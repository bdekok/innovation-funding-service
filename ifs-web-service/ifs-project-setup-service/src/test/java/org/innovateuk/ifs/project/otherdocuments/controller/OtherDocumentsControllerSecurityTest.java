package org.innovateuk.ifs.project.otherdocuments.controller;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

public class OtherDocumentsControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<OtherDocumentsController> {

    @Override
    protected Class<? extends OtherDocumentsController> getClassUnderTest() {
        return OtherDocumentsController.class;
    }

    @Test
    public void testViewOtherDocumentsPage() {
        assertSecured(() -> classUnderTest.viewOtherDocumentsPage(123L, null, null),
                permissionRules -> permissionRules.partnerCanAccessOtherDocumentsSection(eq(123L), isA(UserResource.class)));
    }

    @Test
    public void testDownloadCollaborationAgreementFile() {
        assertSecured(() -> classUnderTest.downloadCollaborationAgreementFile(123L),
                permissionRules -> permissionRules.partnerCanAccessOtherDocumentsSection(eq(123L), isA(UserResource.class)));
    }

    @Test
    public void testDownloadExploitationPlanFile() {
        assertSecured(() -> classUnderTest.downloadExploitationPlanFile(123L),
                permissionRules -> permissionRules.partnerCanAccessOtherDocumentsSection(eq(123L), isA(UserResource.class)));
    }

    @Test
    public void testRemoveCollaborationAgreementFile() {
        assertSecured(() -> classUnderTest.removeCollaborationAgreementFile(123L, null, null, null, null, null),
                permissionRules -> permissionRules.partnerCanAccessOtherDocumentsSection(eq(123L), isA(UserResource.class)));
    }

    @Test
    public void testRemoveExploitationPlanFile() {
        assertSecured(() -> classUnderTest.removeExploitationPlanFile(123L, null, null, null, null, null),
                permissionRules -> permissionRules.partnerCanAccessOtherDocumentsSection(eq(123L), isA(UserResource.class)));
    }

    @Test
    public void testSubmitPartnerDocuments() {
        assertSecured(() -> classUnderTest.submitPartnerDocuments(null, 123L),
                permissionRules -> permissionRules.partnerCanAccessOtherDocumentsSection(eq(123L), isA(UserResource.class)));
    }

    @Test
    public void testUploadCollaborationAgreementFile() {
        assertSecured(() -> classUnderTest.uploadCollaborationAgreementFile(123L, null, null, null, null, null),
                permissionRules -> permissionRules.partnerCanAccessOtherDocumentsSection(eq(123L), isA(UserResource.class)));
    }

    @Test
    public void testUploadExploitationPlanFile() {
        assertSecured(() -> classUnderTest.uploadExploitationPlanFile(123L, null, null, null, null, null),
                permissionRules -> permissionRules.partnerCanAccessOtherDocumentsSection(eq(123L), isA(UserResource.class)));
    }

    @Test
    public void testViewConfirmDocumentsPage() {
        assertSecured(() -> classUnderTest.viewConfirmDocumentsPage(123L, null, null),
                permissionRules -> permissionRules.projectManagerCanSubmitOtherDocumentsSection(eq(123L), isA(UserResource.class)));
    }
}
