package com.worth.ifs.project;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.builder.ProjectResourceBuilder;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import com.worth.ifs.project.viewmodel.ProjectSetupStatusViewModel;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static com.worth.ifs.project.builder.ProjectLeadStatusResourceBuilder.newProjectLeadStatusResource;
import static com.worth.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static com.worth.ifs.project.constant.ProjectActivityStates.*;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.resource.UserRoleType.PARTNER;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProjectSetupStatusControllerTest extends BaseControllerMockMVCTest<ProjectSetupStatusController> {
	
    @Override
    protected ProjectSetupStatusController supplyControllerUnderTest() {
        return new ProjectSetupStatusController();
    }

    private static final Predicate<ProjectSetupStatusViewModel> projectDetailsSubmitted = ProjectSetupStatusViewModel::isProjectDetailsSubmitted;
    private static final Predicate<ProjectSetupStatusViewModel> projectDetailsProcessCompleted = ProjectSetupStatusViewModel::isProjectDetailsProcessCompleted;
    private static final Predicate<ProjectSetupStatusViewModel> awaitingProjectDetailsActionFromOtherPartners = ProjectSetupStatusViewModel::isAwaitingProjectDetailsActionFromOtherPartners;
    private static final Predicate<ProjectSetupStatusViewModel> monitoringOfficerAssigned = ProjectSetupStatusViewModel::isMonitoringOfficerAssigned;
    private static final Predicate<ProjectSetupStatusViewModel> bankDetailsActionRequired = ProjectSetupStatusViewModel::isBankDetailsActionRequired;
    private static final Predicate<ProjectSetupStatusViewModel> bankDetailsComplete = ProjectSetupStatusViewModel::isBankDetailsComplete;
    private static final Predicate<ProjectSetupStatusViewModel> allBankDetailsApprovedOrNotRequired = ProjectSetupStatusViewModel::isAllBankDetailsApprovedOrNotRequired;
    private static final Predicate<ProjectSetupStatusViewModel> allFinanceChecksApproved = ProjectSetupStatusViewModel::isAllFinanceChecksApproved;

    private static final List<Predicate<ProjectSetupStatusViewModel>> partnerProgressFlagChecks = asList(
            projectDetailsSubmitted, projectDetailsProcessCompleted, awaitingProjectDetailsActionFromOtherPartners,
            monitoringOfficerAssigned, bankDetailsActionRequired, allBankDetailsApprovedOrNotRequired,
            allFinanceChecksApproved);

    private static final boolean monitoringOfficerNotExpected = false;
    private static final boolean monitoringOfficerExpected = true;

    private CompetitionResource competition = newCompetitionResource().build();
    private ApplicationResource application = newApplicationResource().withCompetition(competition.getId()).build();
    private ProjectResourceBuilder projectBuilder = newProjectResource().withApplication(application);

    private ProjectResource project = projectBuilder.build();
    private OrganisationResource organisationResource = newOrganisationResource().build();

    private BankDetailsResource bankDetailsResource = newBankDetailsResource().build();
    private RestResult<BankDetailsResource> bankDetailsFoundResult = restSuccess(bankDetailsResource);
    private RestResult<BankDetailsResource> bankDetailsNotFoundResult = restFailure(notFoundError(BankDetailsResource.class, 123L));

    private MonitoringOfficerResource monitoringOfficer = newMonitoringOfficerResource().build();
    private Optional<MonitoringOfficerResource> monitoringOfficerFoundResult = Optional.of(monitoringOfficer);
    private Optional<MonitoringOfficerResource> monitoringOfficerNotFoundResult = Optional.empty();

    @Test
    public void testViewProjectSetupStatus() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withOrganisationId(organisationResource.getId()).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerNotExpected);
        partnerProgressFlagChecks.forEach(check -> assertFalse(check.test(viewModel)));
    }

    @Test
    public void testViewProjectSetupStatusWithProjectDetailsSubmittedButFinanceContactNotYetSubmittedAsLead() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectLeadStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerNotExpected);
        assertPartnerProgressFlagsCorrect(viewModel, projectDetailsSubmitted);
    }

    @Test
    public void testViewProjectSetupStatusForNonLeadPartnerWithFinanceContactNotSubmitted() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectLeadStatusResource()
                        .withOrganisationId(999L)
                        .withProjectDetailsStatus(COMPLETE)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .withOrganisationId(organisationResource.getId())
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerNotExpected);
        assertPartnerProgressFlagsCorrect(viewModel, projectDetailsSubmitted);
    }

    @Test
    public void testViewProjectSetupStatusWithProjectDetailsSubmittedAndFinanceContactSubmitted() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withFinanceContactStatus(COMPLETE).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerNotExpected);
        assertPartnerProgressFlagsCorrect(viewModel, projectDetailsSubmitted, projectDetailsProcessCompleted);
    }

    @Test
    public void testViewProjectSetupStatusWhenAwaitingProjectDetailsActionFromOtherPartners() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource()
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withOrganisationId(organisationResource.getId())
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerNotExpected);
        assertPartnerProgressFlagsCorrect(viewModel, projectDetailsSubmitted, awaitingProjectDetailsActionFromOtherPartners);
    }

    @Test
    public void testViewProjectSetupStatusWithMonitoringOfficerAssigned() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsNotFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerExpected);
        assertPartnerProgressFlagsCorrect(viewModel, projectDetailsSubmitted, projectDetailsProcessCompleted, monitoringOfficerAssigned);
    }

    @Test
    public void testViewProjectSetupStatusWithBankDetailsEntered() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsNotFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerExpected);
        assertPartnerProgressFlagsCorrect(viewModel, projectDetailsSubmitted, projectDetailsProcessCompleted, monitoringOfficerAssigned,
                bankDetailsComplete, allBankDetailsApprovedOrNotRequired);
    }

    @Test
    public void testViewProjectSetupStatusWithAllBankDetailsCompleteOrNotRequired() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(NOT_REQUIRED).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsNotFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerExpected);
        assertPartnerProgressFlagsCorrect(viewModel, projectDetailsSubmitted, projectDetailsProcessCompleted, monitoringOfficerAssigned,
                bankDetailsComplete, allBankDetailsApprovedOrNotRequired);
    }

    @Test
    public void testViewProjectSetupStatusWithAllFinanceChecksApproved() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerExpected);
        assertPartnerProgressFlagsCorrect(viewModel, projectDetailsSubmitted, projectDetailsProcessCompleted, monitoringOfficerAssigned,
                bankDetailsComplete, allBankDetailsApprovedOrNotRequired, allFinanceChecksApproved);
    }

    private ProjectSetupStatusViewModel performViewProjectStatusCallAndAssertBasicDetails(boolean expectedMonitoringOfficer) throws Exception {

        MvcResult result = performViewProjectStatusCall();

        ProjectSetupStatusViewModel viewModel = (ProjectSetupStatusViewModel) result.getModelAndView().getModel().get("model");
        assertStandardViewModelValuesCorrect(viewModel, expectedMonitoringOfficer);
        return viewModel;
    }

    private MvcResult performViewProjectStatusCall() throws Exception {
        return mockMvc.perform(get("/project/{id}", project.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("project/setup-status"))
                .andReturn();
    }

    private void setupLookupProjectDetailsExpectations(Optional<MonitoringOfficerResource> monitoringOfficerResult, RestResult<BankDetailsResource> bankDetailsResult, ProjectTeamStatusResource teamStatus) {

        when(applicationService.getById(application.getId())).thenReturn(application);
        when(projectService.getById(project.getId())).thenReturn(project);
        when(competitionService.getById(application.getCompetition())).thenReturn(competition);
        when(projectService.getMonitoringOfficerForProject(project.getId())).thenReturn(monitoringOfficerResult);
        when(projectService.getOrganisationByProjectAndUser(project.getId(), loggedInUser.getId())).thenReturn(organisationResource);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(newProjectUserResource().
                withUser(loggedInUser.getId())
                .withOrganisation(organisationResource.getId())
                .withRoleName(PARTNER).build(1));
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(project.getId(), organisationResource.getId())).thenReturn(bankDetailsResult);
        when(projectService.getProjectTeamStatus(project.getId(), Optional.empty())).thenReturn(teamStatus);
    }

    private void assertStandardViewModelValuesCorrect(ProjectSetupStatusViewModel viewModel, boolean existingMonitoringOfficerExpected) {
        assertEquals(project.getId(), viewModel.getProjectId());
        assertEquals(project.getName(), viewModel.getProjectName());
        assertEquals(competition.getName(), viewModel.getCompetitionName());
        assertEquals(application.getId(), viewModel.getApplicationId());
        assertEquals(organisationResource.getId(), viewModel.getOrganisationId());

        if (existingMonitoringOfficerExpected) {
            assertEquals(monitoringOfficer.getFullName(), viewModel.getMonitoringOfficerName());
        } else {
            assertEquals("", viewModel.getMonitoringOfficerName());
        }
    }

    @SafeVarargs
    private final void assertPartnerProgressFlagsCorrect(ProjectSetupStatusViewModel viewModel, Predicate<ProjectSetupStatusViewModel>... expectedTrueFlags) {
        assertPartnerProgressFlagsCorrect(viewModel, asList(expectedTrueFlags));
    }

    private void assertPartnerProgressFlagsCorrect(ProjectSetupStatusViewModel viewModel, List<Predicate<ProjectSetupStatusViewModel>> expectedTrueFlags) {
        partnerProgressFlagChecks.forEach(check -> assertEquals(expectedTrueFlags.contains(check), check.test(viewModel)));
    }
}
