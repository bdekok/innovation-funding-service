package org.innovateuk.ifs.finance.security;


import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.resource.cost.AcademicCost;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceRowBuilder.newApplicationFinanceRow;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static java.math.BigDecimal.ZERO;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class FinanceRowPermissionRulesTest extends BasePermissionRulesTest<ApplicationFinanceRowPermissionRules> {

    private ApplicationFinanceRow cost;
    private FinanceRowItem costItem;
    private FinanceRow otherCost;
    private UserResource leadApplicant;
    private UserResource collaborator;
    private UserResource compAdmin;
    private UserResource projectFinance;
    private UserResource assessor;
    private UserResource otherLeadApplicant;

    private UserResource projectUserResource;
    private ProjectResource project;
    private UserResource otherProjectUserResource;
    private ProjectResource otherProject;

    @Override
    protected ApplicationFinanceRowPermissionRules supplyPermissionRulesUnderTest() {
        return new ApplicationFinanceRowPermissionRules();
    }

    @Before
    public void setup() throws Exception {

        // Create a compAdmin
        compAdmin = compAdminUser();
        {
            // Set up users on an organisation and application
            final Long applicationId = 1L;
            final Long organisationId = 2L;
            final Application application = newApplication().with(id(applicationId)).build();
            final Organisation organisation = newOrganisation().with(id(organisationId)).build();
            final ApplicationFinance applicationFinance = newApplicationFinance().withApplication(application).withOrganisationSize(organisation).build();
            cost = newApplicationFinanceRow().withOwningFinance(applicationFinance).build();
            costItem = new AcademicCost(cost.getId(), "", ZERO, "");

            leadApplicant = newUserResource().build();
            collaborator = newUserResource().build();
            when(applicationFinanceRowRepositoryMock.findOne(cost.getId())).thenReturn(cost);
            when(processRoleRepositoryMock.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(leadApplicant.getId(), getRole(LEADAPPLICANT).getId(), applicationId, organisationId)).thenReturn(newProcessRole().build());
            when(processRoleRepositoryMock.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(collaborator.getId(), getRole(COLLABORATOR).getId(), applicationId, organisationId)).thenReturn(newProcessRole().build());
        }

        {
            // Set up different users on an organisation and application to check that there is no bleed through of permissions
            final long otherApplicationId = 3l;
            final long otherOrganisationId = 4l;
            final Organisation otherOrganisation = newOrganisation().with(id(otherOrganisationId)).build();
            final Application otherApplication = newApplication().with(id(otherApplicationId)).build();
            final ApplicationFinance otherApplicationFinance = newApplicationFinance().withOrganisationSize(otherOrganisation).withApplication(otherApplication).build();
            otherCost = newApplicationFinanceRow().withOwningFinance(otherApplicationFinance).build();
            otherLeadApplicant = newUserResource().build();
            when(processRoleRepositoryMock.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(otherLeadApplicant.getId(), getRole(LEADAPPLICANT).getId(), otherApplicationId, otherOrganisationId)).thenReturn(newProcessRole().build());
        }

        // Create project with users for testing getting of partner funding status
        {
            final Long projectId = 1L;
            final Long userId = 1L;

            project = newProjectResource().withId(projectId).build();
            projectUserResource = newUserResource().withId(userId).build();

            when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(projectId, userId, PROJECT_PARTNER)).thenReturn(Collections.singletonList(newProjectUser().withId(userId).build()));
        }

        // Create differnet users with different project
        {
            final Long otherProjectProjectId = 2L;
            final Long otherProjectUserId = 2L;

            otherProject = newProjectResource().withId(otherProjectProjectId).build();
            otherProjectUserResource = newUserResource().withId(otherProjectUserId).build();

            when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(otherProjectProjectId, otherProjectUserId, PROJECT_PARTNER)).thenReturn(Collections.singletonList(newProjectUser().withId(otherProjectUserId).build()));
        }

        projectFinance = projectFinanceUser();
    }

    @Test
    public void testConsortiumCanDeleteACostForTheirApplicationAndOrganis4ation() {
        assertTrue(rules.consortiumCanDeleteACostForTheirApplicationAndOrganisation(cost, leadApplicant));
        assertTrue(rules.consortiumCanDeleteACostForTheirApplicationAndOrganisation(cost, collaborator));

        assertFalse(rules.consortiumCanDeleteACostForTheirApplicationAndOrganisation(cost, otherLeadApplicant));
        assertFalse(rules.consortiumCanDeleteACostForTheirApplicationAndOrganisation(cost, compAdmin));
    }

    @Test
    public void testConsortiumCanUpdateACostForTheirApplicationAndOrganisation() {
        assertTrue(rules.consortiumCanUpdateACostForTheirApplicationAndOrganisation(cost, leadApplicant));
        assertTrue(rules.consortiumCanUpdateACostForTheirApplicationAndOrganisation(cost, collaborator));

        assertFalse(rules.consortiumCanUpdateACostForTheirApplicationAndOrganisation(cost, otherLeadApplicant));
        assertFalse(rules.consortiumCanUpdateACostForTheirApplicationAndOrganisation(cost, compAdmin));
    }

    @Test
    public void testConsortiumCanReadACostForTheirApplicationAndOrganisation() {
        assertTrue(rules.consortiumCanReadACostForTheirApplicationAndOrganisation(cost, leadApplicant));
        assertTrue(rules.consortiumCanReadACostForTheirApplicationAndOrganisation(cost, collaborator));

        assertFalse(rules.consortiumCanReadACostForTheirApplicationAndOrganisation(cost, otherLeadApplicant));
        assertFalse(rules.consortiumCanReadACostForTheirApplicationAndOrganisation(cost, compAdmin));
    }

    @Test
    public void testConsortiumCanReadACostForTheirApplicationAndOrganisation2() {
        assertTrue(rules.consortiumCanReadACostItemForTheirApplicationAndOrganisation(costItem, leadApplicant));
        assertTrue(rules.consortiumCanReadACostItemForTheirApplicationAndOrganisation(costItem, collaborator));

        assertFalse(rules.consortiumCanReadACostItemForTheirApplicationAndOrganisation(costItem, otherLeadApplicant));
        assertFalse(rules.consortiumCanReadACostItemForTheirApplicationAndOrganisation(costItem, compAdmin));
    }

    @Test
    public void testProjectPartnersCanCheckFundingStatusOfTeam(){
        assertFalse(rules.projectPartnersCanCheckFundingStatusOfTeam(project, compAdmin));
        assertFalse(rules.projectPartnersCanCheckFundingStatusOfTeam(project, otherProjectUserResource));
        assertFalse(rules.projectPartnersCanCheckFundingStatusOfTeam(otherProject, projectUserResource));
        assertTrue(rules.projectPartnersCanCheckFundingStatusOfTeam(project, projectUserResource));
        assertTrue(rules.projectPartnersCanCheckFundingStatusOfTeam(otherProject, otherProjectUserResource));
    }

    @Test
    public void testInternalUsersCanCheckFundingStatusOfTeam(){
        assertTrue(rules.internalUsersCanCheckFundingStatusOfTeam(project, compAdmin));
        assertTrue(rules.internalUsersCanCheckFundingStatusOfTeam(project, projectFinance));
    }
}
