package org.innovateuk.ifs.invite.domain;


import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.user.domain.Organisation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ProjectInviteTest {

    ProjectInvite setInvite;
    ProjectInvite constructedInvite;
    Long inviteId;
    String name;
    String email;
    Project project;
    String hash;
    Organisation organisation;
    InviteStatus status;

    @Before
    public void setUp() throws Exception {
        inviteId = 1L;
        name = "testnameforProject";
        email = "testproject-invite@email.test";
        project = new Project();
        hash = "123abcdef";
        organisation = new Organisation();

        setInvite = new ProjectInvite();
        setInvite.setId(inviteId);
        setInvite.setName(name);
        setInvite.setEmail(email);
        setInvite.setTarget(project);
        setInvite.setHash(hash);
        setInvite.setOrganisation(organisation);

        constructedInvite = new ProjectInvite(name, email,hash, organisation,project, status);
    }


    @Test
    public void constructedProjectInviteShouldReturnCorrectAttributes() throws Exception {
        Assert.assertEquals(name, constructedInvite.getName());
        Assert.assertEquals(email, constructedInvite.getEmail());
        Assert.assertEquals(project, constructedInvite.getTarget());
        Assert.assertEquals(hash, constructedInvite.getHash());
        Assert.assertEquals(organisation, constructedInvite.getOrganisation());
        Assert.assertEquals(status, constructedInvite.getStatus());
    }

    @Test
    public void gettingProjectInviteAnyAttributeAfterSettingShouldReturnCorrectValue() throws Exception {
        Assert.assertEquals(inviteId, setInvite.getId());
        Assert.assertEquals(name, setInvite.getName());
        Assert.assertEquals(email, setInvite.getEmail());
        Assert.assertEquals(project, setInvite.getTarget());
        Assert.assertEquals(hash, setInvite.getHash());
        Assert.assertEquals(organisation, setInvite.getOrganisation());

    }

}
