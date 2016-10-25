package com.worth.ifs.assessment.controller.dashboard;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.assessment.model.AssessorCompetitionDashboardModelPopulator;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.assessment.viewmodel.AssessorCompetitionDashboardApplicationViewModel;
import com.worth.ifs.assessment.viewmodel.AssessorCompetitionDashboardViewModel;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.user.resource.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessorCompetitionDashboardControllerTest extends BaseControllerMockMVCTest<AssessorCompetitionDashboardController> {

    @Spy
    @InjectMocks
    private AssessorCompetitionDashboardModelPopulator assessorCompetitionDashboardModelPopulator;

    @Mock
    private AssessmentService assessmentService;

    @Override
    protected AssessorCompetitionDashboardController supplyControllerUnderTest() {
        return new AssessorCompetitionDashboardController();
    }

    @Test
    public void competitionDashboard() throws Exception {
        Long competitionId = 1L;
        Long userId = 1L;

        UserResource leadTechnologist = newUserResource()
                .withFirstName("Competition")
                .withLastName("Technologist")
                .build();

        LocalDateTime assessorAcceptsDate = LocalDateTime.now().minusDays(2);
        LocalDateTime assessorDeadlineDate = LocalDateTime.now().plusDays(4);

        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withName("Juggling Craziness")
                .withDescription("Juggling Craziness (CRD3359)")
                .withLeadTechnologist(leadTechnologist.getId())
                .withAssessorAcceptsDate(assessorAcceptsDate)
                .withAssessorDeadlineDate(assessorDeadlineDate)
                .build();

        List<AssessmentResource> assessments = newAssessmentResource()
                .withId(9L, 10L)
                .withApplication(8L, 14L)
                .withCompetition(competitionId)
                .build(2);
        ApplicationResource application1 = newApplicationResource().withId(8L).withName("Juggling is fun").build();
        ApplicationResource application2 = newApplicationResource().withId(14L).withName("Juggling is word that sounds funny to say").build();
        List<ApplicationResource> applications = asList(application1, application2);

        RoleResource role = newRoleResource().withType(UserRoleType.LEADAPPLICANT).build();
        List<ProcessRoleResource> users = newProcessRoleResource().withRole(role).withOrganisation(1L, 2L).build(2);
        List<OrganisationResource> organisations = newOrganisationResource()
                .withId(1L, 2L)
                .withName("The Best Juggling Company", "Mo Juggling Mo Problems Ltd")
                .build(2);

        when(competitionService.getById(competitionId)).thenReturn(competition);
        when(userService.findById(leadTechnologist.getId())).thenReturn(leadTechnologist);
        when(assessmentService.getByUserAndCompetition(userId, competitionId)).thenReturn(assessments);
        when(applicationService.getById(8L)).thenReturn(applications.get(0));
        when(applicationService.getById(14L)).thenReturn(applications.get(1));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(0).getId())).thenReturn(asList(users.get(0)));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(1).getId())).thenReturn(asList(users.get(1)));
        when(organisationRestService.getOrganisationById(1L)).thenReturn(restSuccess(organisations.get(0)));
        when(organisationRestService.getOrganisationById(2L)).thenReturn(restSuccess(organisations.get(1)));

        MvcResult result = mockMvc.perform(get("/assessor/dashboard/competition/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-dashboard"))
                .andReturn();

        InOrder inOrder = inOrder(competitionService, userService, assessmentService);
        inOrder.verify(competitionService).getById(competitionId);
        inOrder.verify(userService).findById(leadTechnologist.getId());
        inOrder.verify(assessmentService).getByUserAndCompetition(userId, competitionId);
        inOrder.verifyNoMoreInteractions();

        assessments.stream().forEach(assessment -> {
            InOrder inOrderByAssessment = inOrder(applicationService, processRoleService, organisationRestService);
            inOrderByAssessment.verify(applicationService).getById(assessment.getApplication());
            inOrderByAssessment.verify(processRoleService).findProcessRolesByApplicationId(assessment.getApplication());
            inOrderByAssessment.verify(organisationRestService).getOrganisationById(isA(Long.class));
        });

        List<AssessorCompetitionDashboardApplicationViewModel> expectedApplications = asList(
                new AssessorCompetitionDashboardApplicationViewModel(8L, 9L, "Juggling is fun", "The Best Juggling Company"),
                new AssessorCompetitionDashboardApplicationViewModel(14L, 10L, "Juggling is word that sounds funny to say", "Mo Juggling Mo Problems Ltd")
        );

        AssessorCompetitionDashboardViewModel model = (AssessorCompetitionDashboardViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("Juggling Craziness", model.getCompetitionTitle());
        assertEquals("Juggling Craziness (CRD3359)", model.getCompetition());
        assertEquals("Competition Technologist", model.getLeadTechnologist());
        assertEquals(assessorAcceptsDate, model.getAcceptDeadline());
        assertEquals(assessorDeadlineDate, model.getSubmitDeadline());
        assertEquals(expectedApplications, model.getApplications());
    }
}