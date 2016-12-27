package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.invite.builder.InviteResourceBuilder;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class InviteOrganisationPermissionRulesTest extends BasePermissionRulesTest<InviteOrganisationPermissionRules> {

    private ApplicationResource applicationResource;
    private UserResource leadApplicant;
    private UserResource collaborator;
    private UserResource otherApplicant;

    @Override
    protected InviteOrganisationPermissionRules supplyPermissionRulesUnderTest() {
        return new InviteOrganisationPermissionRules();
    }

    @Before
    public void setUp() throws Exception {
        applicationResource = newApplicationResource().build();

        leadApplicant = newUserResource().build();
        collaborator = newUserResource().build();
        otherApplicant = newUserResource().build();

        when(processRoleRepositoryMock.findByUserIdAndApplicationId(leadApplicant.getId(), applicationResource.getId())).thenReturn(newProcessRole().withRole(getRole(LEADAPPLICANT)).build());
        when(processRoleRepositoryMock.findByUserIdAndApplicationId(collaborator.getId(), applicationResource.getId())).thenReturn(newProcessRole().withRole(getRole(COLLABORATOR)).build());
    }

    @Test
    public void testLeadApplicantCanInviteAnOrganisationToTheApplication() throws Exception {
        final List<ApplicationInviteResource> inviteResource = InviteResourceBuilder.newInviteResource().withApplication(applicationResource.getId()).build(5);
        final InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResource).build();

        assertTrue(rules.leadApplicantCanInviteAnOrganisationToTheApplication(inviteOrganisationResource, leadApplicant));
        assertFalse(rules.leadApplicantCanInviteAnOrganisationToTheApplication(inviteOrganisationResource, collaborator));
        assertFalse(rules.leadApplicantCanInviteAnOrganisationToTheApplication(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void testLeadApplicantCanViewOrganisationInviteToTheApplication() throws Exception {
        final List<ApplicationInviteResource> inviteResource = InviteResourceBuilder.newInviteResource().withApplication(applicationResource.getId()).build(5);
        final InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResource).build();

        assertTrue(rules.leadApplicantCanViewOrganisationInviteToTheApplication(inviteOrganisationResource, leadApplicant));
        assertFalse(rules.leadApplicantCanViewOrganisationInviteToTheApplication(inviteOrganisationResource, collaborator));
        assertFalse(rules.leadApplicantCanViewOrganisationInviteToTheApplication(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void testCollaboratorCanViewOrganisationInviteToTheApplication() throws Exception {
        final List<ApplicationInviteResource> inviteResource = InviteResourceBuilder.newInviteResource().withApplication(applicationResource.getId()).build(5);
        final InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResource).build();

        assertTrue(rules.collaboratorCanViewOrganisationInviteToTheApplication(inviteOrganisationResource, collaborator));
        assertFalse(rules.collaboratorCanViewOrganisationInviteToTheApplication(inviteOrganisationResource, leadApplicant));
        assertFalse(rules.collaboratorCanViewOrganisationInviteToTheApplication(inviteOrganisationResource, otherApplicant));
    }

    @Test
    public void testLeadApplicantCanSaveInviteAnOrganisationToTheApplication() throws Exception {
        final List<ApplicationInviteResource> inviteResource = InviteResourceBuilder.newInviteResource().withApplication(applicationResource.getId()).build(5);
        final InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResource).build();

        assertTrue(rules.leadApplicantCanSaveInviteAnOrganisationToTheApplication(inviteOrganisationResource, leadApplicant));
        assertFalse(rules.leadApplicantCanSaveInviteAnOrganisationToTheApplication(inviteOrganisationResource, collaborator));
        assertFalse(rules.leadApplicantCanSaveInviteAnOrganisationToTheApplication(inviteOrganisationResource, otherApplicant));
    }

}