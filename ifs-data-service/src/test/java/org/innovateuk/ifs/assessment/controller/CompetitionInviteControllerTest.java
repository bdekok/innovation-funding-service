package org.innovateuk.ifs.assessment.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestErrorResponse;
import org.innovateuk.ifs.invite.domain.CompetitionInvite;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static java.util.Collections.nCopies;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInvitePageResourceBuilder.newAssessorCreatedInvitePageResource;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInviteResourceBuilder.newAssessorCreatedInviteResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewPageResourceBuilder.newAssessorInviteOverviewPageResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewResourceBuilder.newAssessorInviteOverviewResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteSendResourceBuilder.newAssessorInviteSendResource;
import static org.innovateuk.ifs.invite.builder.AssessorInvitesToSendResourceBuilder.newAssessorInvitesToSendResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorPageResourceBuilder.newAvailableAssessorPageResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorResourceBuilder.newAvailableAssessorResource;
import static org.innovateuk.ifs.invite.builder.CompetitionInviteStatisticsResourceBuilder.newCompetitionInviteStatisticsResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteListResourceBuilder.newNewUserStagedInviteListResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteResourceBuilder.newNewUserStagedInviteResource;
import static org.innovateuk.ifs.invite.builder.RejectionReasonResourceBuilder.newRejectionReasonResource;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.fromJson;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionInviteControllerTest extends BaseControllerMockMVCTest<CompetitionInviteController> {

    private final long COMPETITION_ID = 1L;

    @Override
    protected CompetitionInviteController supplyControllerUnderTest() {
        return new CompetitionInviteController();
    }

    @Test
    public void getAllInvitesToSend() throws Exception {
        AssessorInvitesToSendResource resource = newAssessorInvitesToSendResource().build();

        when(competitionInviteServiceMock.getAllInvitesToSend(COMPETITION_ID)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/competitioninvite/getAllInvitesToSend/{competitionId}", COMPETITION_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(competitionInviteServiceMock, only()).getAllInvitesToSend(COMPETITION_ID);
    }

    @Test
    public void getInviteToSend() throws Exception {
        AssessorInvitesToSendResource resource = new AssessorInvitesToSendResource();
        long inviteId = 1L;

        when(competitionInviteServiceMock.getInviteToSend(inviteId)).thenReturn(serviceSuccess(resource));
        mockMvc.perform(get("/competitioninvite/getInviteToSend/{inviteId}", inviteId)).andExpect(status().isOk());

        verify(competitionInviteServiceMock, only()).getInviteToSend(inviteId);
    }

    @Test
    public void getInvite() throws Exception {
        CompetitionInviteResource resource = new CompetitionInviteResource();

        when(competitionInviteServiceMock.getInvite("hash")).thenReturn(serviceSuccess(resource));
        mockMvc.perform(get("/competitioninvite/getInvite/{inviteHash}", "hash").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(competitionInviteServiceMock, only()).getInvite("hash");
    }

    @Test
    public void openInvite() throws Exception {
        CompetitionInviteResource resource = new CompetitionInviteResource();

        when(competitionInviteServiceMock.openInvite("hash")).thenReturn(serviceSuccess(resource));
        mockMvc.perform(post("/competitioninvite/openInvite/{inviteHash}", "hash").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(competitionInviteServiceMock, only()).openInvite("hash");
    }

    @Test
    public void openInvite_hashNotExists() throws Exception {
        when(competitionInviteServiceMock.openInvite("hashNotExists")).thenReturn(serviceFailure(notFoundError(CompetitionInvite.class, "hashNotExists")));

        MvcResult result = mockMvc.perform(post("/competitioninvite/openInvite/{inviteHash}", "hashNotExists").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        RestErrorResponse response = fromJson(result.getResponse().getContentAsString(), RestErrorResponse.class);
        assertEqualsUpNoIncludingStatusCode(response, notFoundError(CompetitionInvite.class, "hashNotExists"));

        verify(competitionInviteServiceMock, only()).openInvite("hashNotExists");
    }

    @Test
    public void acceptInvite() throws Exception {
        UserResource userResource = newUserResource().withId(7L).build();
        login(userResource);
        when(competitionInviteServiceMock.acceptInvite("hash", userResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competitioninvite/acceptInvite/{inviteHash}", "hash").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(competitionInviteServiceMock, only()).acceptInvite("hash", userResource);
    }

    @Test
    public void acceptInvite_hashNotExists() throws Exception {
        UserResource userResource = newUserResource().withId(7L).build();
        login(userResource);

        when(competitionInviteServiceMock.acceptInvite("hashNotExists", userResource)).thenReturn(serviceFailure(notFoundError(CompetitionParticipant.class, "hashNotExists")));

        MvcResult result = mockMvc.perform(post("/competitioninvite/acceptInvite/{inviteHash}", "hashNotExists")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        RestErrorResponse response = fromJson(result.getResponse().getContentAsString(), RestErrorResponse.class);
        assertEqualsUpNoIncludingStatusCode(response, notFoundError(CompetitionParticipant.class, "hashNotExists"));

        verify(competitionInviteServiceMock, only()).acceptInvite("hashNotExists", userResource);
    }

    @Test
    public void acceptInvite_notOpened() throws Exception {
        acceptFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_UNOPENED_INVITE, "Connected digital additive manufacturing"));
    }

    @Test
    public void acceptInvite_alreadyAccepted() throws Exception {
        acceptFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_ALREADY_ACCEPTED_INVITE, "Connected digital additive manufacturing"));
    }

    @Test
    public void acceptInvite_alreadyRejected() throws Exception {
        acceptFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_ALREADY_REJECTED_INVITE, "Connected digital additive manufacturing"));
    }

    private void acceptFailure(Error expectedError) throws Exception {
        UserResource userResource = newUserResource().withId(7L).build();
        login(userResource);

        when(competitionInviteServiceMock.acceptInvite("hash", userResource)).thenReturn(serviceFailure(expectedError));

        mockMvc.perform(post("/competitioninvite/acceptInvite/{inviteHash}", "hash")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(toJson(new RestErrorResponse(expectedError))));

        verify(competitionInviteServiceMock, only()).acceptInvite("hash", userResource);
    }

    @Test
    public void rejectInvite() throws Exception {
        String comment = String.join(" ", nCopies(100, "comment"));
        RejectionReasonResource rejectionReasonResource =
                newRejectionReasonResource()
                        .withId(1L)
                        .build();
        CompetitionRejectionResource rejectionResource = new CompetitionRejectionResource(rejectionReasonResource, comment);

        when(competitionInviteServiceMock.rejectInvite("hash", rejectionReasonResource, of(comment))).thenReturn(serviceSuccess());
        mockMvc.perform(
                post("/competitioninvite/rejectInvite/{inviteHash}", "hash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(rejectionResource))
        ).andExpect(status().isOk());

        verify(competitionInviteServiceMock, only()).rejectInvite("hash", rejectionReasonResource, of(comment));
    }

    @Test
    public void rejectInvite_noReason() throws Exception {
        String comment = String.join(" ", nCopies(100, "comment"));
        CompetitionRejectionResource rejectionResource = new CompetitionRejectionResource(null, comment);

        Error rejectReasonError = fieldError("rejectReason", null, "validation.competitionrejectionresource.rejectReason.required", "");

        mockMvc.perform(
                post("/competitioninvite/rejectInvite/{inviteHash}", "hash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(rejectionResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(rejectReasonError))));
    }

    @Test
    public void rejectInvite_noReasonComment() throws Exception {
        RejectionReasonResource rejectionReasonResource =
                newRejectionReasonResource()
                        .withId(1L)
                        .build();
        CompetitionRejectionResource rejectionResource = new CompetitionRejectionResource(rejectionReasonResource, null);

        when(competitionInviteServiceMock.rejectInvite("hash", rejectionReasonResource, empty())).thenReturn(serviceSuccess());
        mockMvc.perform(
                post("/competitioninvite/rejectInvite/{inviteHash}", "hash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(rejectionResource))
        ).andExpect(status().isOk());

        verify(competitionInviteServiceMock, only()).rejectInvite("hash", rejectionReasonResource, empty());
    }

    @Test
    public void rejectInvite_exceedsCharacterSizeLimit() throws Exception {
        String comment = RandomStringUtils.random(5001);
        CompetitionRejectionResource rejectionResource = new CompetitionRejectionResource(newRejectionReasonResource()
                .withId(1L)
                .build(), comment);

        Error rejectCommentError = fieldError("rejectComment", comment, "validation.field.too.many.characters", "", "5000", "0");

        mockMvc.perform(
                post("/competitioninvite/rejectInvite/{inviteHash}", "hash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(rejectionResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(rejectCommentError))));
    }

    @Test
    public void rejectInvite_exceedsWordLimit() throws Exception {
        String comment = String.join(" ", nCopies(101, "comment"));
        CompetitionRejectionResource rejectionResource = new CompetitionRejectionResource(newRejectionReasonResource()
                .withId(1L)
                .build(), comment);

        Error rejectCommentError = fieldError("rejectComment", comment, "validation.field.max.word.count", "", "100");

        mockMvc.perform(
                post("/competitioninvite/rejectInvite/{inviteHash}", "hash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(rejectionResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(rejectCommentError))));
    }

    @Test
    public void rejectInvite_hashNotExists() throws Exception {
        String comment = String.join(" ", nCopies(100, "comment"));
        RejectionReasonResource rejectionReasonResource =
                newRejectionReasonResource()
                        .withId(1L)
                        .build();
        CompetitionRejectionResource rejectionResource = new CompetitionRejectionResource(rejectionReasonResource, comment);

        when(competitionInviteServiceMock.rejectInvite("hashNotExists", rejectionReasonResource, of(comment))).thenReturn(serviceFailure(notFoundError(CompetitionParticipant.class, "hashNotExists")));

        MvcResult result = mockMvc.perform(
                post("/competitioninvite/rejectInvite/{inviteHash}", "hashNotExists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(rejectionResource))
        ).andExpect(status().isNotFound())
                .andReturn();

        RestErrorResponse response = fromJson(result.getResponse().getContentAsString(), RestErrorResponse.class);
        assertEqualsUpNoIncludingStatusCode(response, notFoundError(CompetitionParticipant.class, "hashNotExists"));

        verify(competitionInviteServiceMock, only()).rejectInvite("hashNotExists", rejectionReasonResource, of(comment));
    }

    @Test
    public void rejectInvite_notOpened() throws Exception {
        rejectFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_UNOPENED_INVITE, "Connected digital additive manufacturing"));
    }

    @Test
    public void rejectInvite_alreadyAccepted() throws Exception {
        rejectFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_ALREADY_ACCEPTED_INVITE, "Connected digital additive manufacturing"));
    }

    @Test
    public void rejectInvite_alreadyRejected() throws Exception {
        rejectFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_ALREADY_REJECTED_INVITE, "Connected digital additive manufacturing"));
    }

    @Test
    public void checkExistingUser() throws Exception {
        when(competitionInviteServiceMock.checkExistingUser("hash")).thenReturn(serviceSuccess(TRUE));

        mockMvc.perform(
                get("/competitioninvite/checkExistingUser/{inviteHash}", "hash"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(competitionInviteServiceMock).checkExistingUser("hash");
    }

    @Test
    public void checkExistingUser_userNotExists() throws Exception {
        when(competitionInviteServiceMock.checkExistingUser("hash")).thenReturn(serviceSuccess(Boolean.FALSE));

        mockMvc.perform(
                get("/competitioninvite/checkExistingUser/{inviteHash}", "hash"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(competitionInviteServiceMock).checkExistingUser("hash");
    }

    @Test
    public void checkExistingUser_hashNotExists() throws Exception {
        when(competitionInviteServiceMock.checkExistingUser("hashNotExists")).thenReturn(serviceFailure(notFoundError(CompetitionInvite.class, "hashNotExists")));

        MvcResult result = mockMvc.perform(
                get("/competitioninvite/checkExistingUser/{inviteHash}", "hashNotExists"))
                .andExpect(status().isNotFound())
                .andReturn();

        RestErrorResponse response = fromJson(result.getResponse().getContentAsString(), RestErrorResponse.class);
        assertEqualsUpNoIncludingStatusCode(response, notFoundError(CompetitionInvite.class, "hashNotExists"));

        verify(competitionInviteServiceMock).checkExistingUser("hashNotExists");
    }

    private void rejectFailure(Error expectedError) throws Exception {
        String comment = String.join(" ", nCopies(100, "comment"));
        RejectionReasonResource rejectionReasonResource =
                newRejectionReasonResource()
                        .withId(1L)
                        .build();
        CompetitionRejectionResource rejectionResource = new CompetitionRejectionResource(rejectionReasonResource, comment);

        when(competitionInviteServiceMock.rejectInvite("hash", rejectionReasonResource, of(comment))).thenReturn(serviceFailure(expectedError));
        mockMvc.perform(
                post("/competitioninvite/rejectInvite/{inviteHash}", "hash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(rejectionResource)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(toJson(new RestErrorResponse(expectedError))));

        verify(competitionInviteServiceMock, only()).rejectInvite("hash", rejectionReasonResource, of(comment));
    }

    @Test
    public void getAvailableAssessors() throws Exception {
        int page = 5;
        int pageSize = 30;

        List<AvailableAssessorResource> expectedAvailableAssessorResources = newAvailableAssessorResource().build(2);

        AvailableAssessorPageResource expectedAvailableAssessorPageResource = newAvailableAssessorPageResource()
                .withContent(expectedAvailableAssessorResources)
                .withNumber(page)
                .withTotalElements(300L)
                .withTotalPages(10)
                .withSize(30)
                .build();

        Pageable pageable = new PageRequest(page, pageSize, new Sort(DESC, "lastName"));
        Optional<Long> innovationArea = of(4L);

        when(competitionInviteServiceMock.getAvailableAssessors(COMPETITION_ID, pageable, innovationArea))
                .thenReturn(serviceSuccess(expectedAvailableAssessorPageResource));

        mockMvc.perform(get("/competitioninvite/getAvailableAssessors/{competitionId}", COMPETITION_ID)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(pageSize))
                .param("sort", "lastName,desc")
                .param("innovationArea", "4"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedAvailableAssessorPageResource)));

        verify(competitionInviteServiceMock, only()).getAvailableAssessors(COMPETITION_ID, pageable, innovationArea);
    }

    @Test
    public void getAvailableAssessors_defaultParameters() throws Exception {
        int page = 0;
        int pageSize = 20;

        List<AvailableAssessorResource> expectedAvailableAssessorResources = newAvailableAssessorResource().build(2);

        AvailableAssessorPageResource expectedAvailableAssessorPageResource = newAvailableAssessorPageResource()
                .withContent(expectedAvailableAssessorResources)
                .withNumber(page)
                .withTotalElements(300L)
                .withTotalPages(10)
                .withSize(30)
                .build();

        Pageable pageable = new PageRequest(page, pageSize, new Sort(ASC, "firstName", "lastName"));
        Optional<Long> innovationArea = empty();

        when(competitionInviteServiceMock.getAvailableAssessors(COMPETITION_ID, pageable, innovationArea))
                .thenReturn(serviceSuccess(expectedAvailableAssessorPageResource));

        mockMvc.perform(get("/competitioninvite/getAvailableAssessors/{competitionId}", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedAvailableAssessorPageResource)));

        verify(competitionInviteServiceMock, only()).getAvailableAssessors(COMPETITION_ID, pageable, innovationArea);
    }

    @Test
    public void getCreatedInvites() throws Exception {
        int page = 5;
        int pageSize = 40;

        List<AssessorCreatedInviteResource> expectedAssessorCreatedInviteResources = newAssessorCreatedInviteResource()
                .build(2);

        AssessorCreatedInvitePageResource expectedPageResource = newAssessorCreatedInvitePageResource()
                .withContent(expectedAssessorCreatedInviteResources)
                .withNumber(page)
                .withTotalElements(200L)
                .withTotalPages(10)
                .withSize(pageSize)
                .build();

        Pageable pageable = new PageRequest(page, pageSize, new Sort(ASC, "email"));

        when(competitionInviteServiceMock.getCreatedInvites(COMPETITION_ID, pageable)).thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/competitioninvite/getCreatedInvites/{competitionId}", COMPETITION_ID)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(pageSize))
                .param("sort", "email,ASC"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedPageResource)));

        verify(competitionInviteServiceMock, only()).getCreatedInvites(COMPETITION_ID, pageable);
    }

    @Test
    public void getCreatedInvites_defaultParameters() throws Exception {
        int page = 0;
        int pageSize = 20;

        List<AssessorCreatedInviteResource> expectedAssessorCreatedInviteResources = newAssessorCreatedInviteResource()
                .build(2);

        AssessorCreatedInvitePageResource expectedPageResource = newAssessorCreatedInvitePageResource()
                .withContent(expectedAssessorCreatedInviteResources)
                .withNumber(page)
                .withTotalElements(200L)
                .withTotalPages(10)
                .withSize(pageSize)
                .build();

        Pageable pageable = new PageRequest(page, pageSize, new Sort(ASC, "name"));

        when(competitionInviteServiceMock.getCreatedInvites(COMPETITION_ID, pageable)).thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/competitioninvite/getCreatedInvites/{competitionId}", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedPageResource)));

        verify(competitionInviteServiceMock, only()).getCreatedInvites(COMPETITION_ID, pageable);
    }

    @Test
    public void getInvitationOverview() throws Exception {
        long competitionId = 1L;
        int page = 2;
        int size = 10;
        Optional<Long> innovationArea = of(3L);
        Optional<ParticipantStatus> status = of(ACCEPTED);
        Optional<Boolean> compliant = of(TRUE);

        AssessorInviteOverviewPageResource expectedPageResource = newAssessorInviteOverviewPageResource()
                .withContent(newAssessorInviteOverviewResource().build(2))
                .build();

        Pageable pageable = new PageRequest(page, size, new Sort(Sort.Direction.ASC, "invite.email"));

        when(competitionInviteServiceMock.getInvitationOverview(competitionId, pageable, innovationArea, status, compliant))
                .thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/competitioninvite/getInvitationOverview/{competitionId}", competitionId)
                .param("page", "2")
                .param("size", "10")
                .param("sort", "invite.email")
                .param("innovationArea", "3")
                .param("status", "ACCEPTED")
                .param("compliant", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedPageResource)));

        verify(competitionInviteServiceMock, only()).getInvitationOverview(competitionId, pageable, innovationArea, status, compliant);
    }

    @Test
    public void getInvitationOverview_defaultParams() throws Exception {
        long competitionId = 1L;
        int page = 0;
        int size = 20;
        Optional<Long> innovationArea = empty();
        Optional<ParticipantStatus> status = empty();
        Optional<Boolean> compliant = empty();

        Pageable pageable = new PageRequest(page, size, new Sort(Sort.Direction.ASC, "invite.name"));

        AssessorInviteOverviewPageResource expectedPageResource = newAssessorInviteOverviewPageResource()
                .withContent(newAssessorInviteOverviewResource().build(2))
                .build();

        when(competitionInviteServiceMock.getInvitationOverview(competitionId, pageable, innovationArea, status, compliant))
                .thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/competitioninvite/getInvitationOverview/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedPageResource)));

        verify(competitionInviteServiceMock, only()).getInvitationOverview(competitionId, pageable, innovationArea, status, compliant);
    }

    @Test
    public void getInviteStatistics() throws Exception {
        long competitionId = 1L;
        CompetitionInviteStatisticsResource expectedCompetitionInviteStatisticsResource = newCompetitionInviteStatisticsResource().build();
        when(competitionInviteServiceMock.getInviteStatistics(competitionId)).thenReturn(serviceSuccess(expectedCompetitionInviteStatisticsResource));
        mockMvc.perform(get("/competitioninvite/getInviteStatistics/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedCompetitionInviteStatisticsResource)));
        verify(competitionInviteServiceMock, only()).getInviteStatistics(competitionId);
    }

    @Test
    public void inviteUser() throws Exception {
        ExistingUserStagedInviteResource existingUserStagedInviteResource = new ExistingUserStagedInviteResource("firstname.lastname@example.com", 1L);
        CompetitionInviteResource expectedCompetitionInviteResource = newCompetitionInviteResource().build();

        when(competitionInviteServiceMock.inviteUser(existingUserStagedInviteResource)).thenReturn(serviceSuccess(expectedCompetitionInviteResource));

        mockMvc.perform(post("/competitioninvite/inviteUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(existingUserStagedInviteResource)))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedCompetitionInviteResource)));

        verify(competitionInviteServiceMock, only()).inviteUser(existingUserStagedInviteResource);
    }

    @Test
    public void inviteNewUser() throws Exception {
        NewUserStagedInviteResource newUserStagedInviteResource = new NewUserStagedInviteResource("test@test.com", 1L, "Test Name", 1L);
        CompetitionInviteResource expectedCompetitionInviteResource = newCompetitionInviteResource().build();

        when(competitionInviteServiceMock.inviteUser(newUserStagedInviteResource)).thenReturn(serviceSuccess(expectedCompetitionInviteResource));

        mockMvc.perform(post("/competitioninvite/inviteNewUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(newUserStagedInviteResource)))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedCompetitionInviteResource)));

        verify(competitionInviteServiceMock, only()).inviteUser(newUserStagedInviteResource);
    }

    @Test
    public void inviteNewUsers() throws Exception {
        List<NewUserStagedInviteResource> newUserStagedInvites = newNewUserStagedInviteResource()
                .withEmail("test1@test.com", "test2@test.com")
                .withName("Test Name 1", "Test Name 2")
                .withInnovationAreaId(1L)
                .build(2);

        NewUserStagedInviteListResource newUserStagedInviteList = newNewUserStagedInviteListResource()
                .withInvites(newUserStagedInvites)
                .build();

        when(competitionInviteServiceMock.inviteNewUsers(newUserStagedInvites, 1L)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competitioninvite/inviteNewUsers/{competitionId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(newUserStagedInviteList)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(competitionInviteServiceMock, only()).inviteNewUsers(newUserStagedInvites, 1L);
    }

    @Test
    public void deleteInvite() throws Exception {
        String email = "firstname.lastname@example.com";
        long competitionId = 1L;

        when(competitionInviteServiceMock.deleteInvite(email, competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/competitioninvite/deleteInvite")
                .param("email", email)
                .param("competitionId", String.valueOf(competitionId)))
                .andExpect(status().isNoContent());

        verify(competitionInviteServiceMock, only()).deleteInvite(email, competitionId);
    }

    @Test
    public void deleteAllInvites() throws Exception {
        long competitionId = 1L;

        when(competitionInviteServiceMock.deleteAllInvites(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/competitioninvite/deleteAllInvites")
                .param("competitionId", String.valueOf(competitionId)))
                .andExpect(status().isNoContent());

        verify(competitionInviteServiceMock).deleteAllInvites(competitionId);
    }

    @Test
    public void sendAllInvites() throws Exception {
        long competitionId = 1L;

        AssessorInviteSendResource assessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject("subject")
                .withContent("content")
                .build();

        when(competitionInviteServiceMock.sendAllInvites(competitionId, assessorInviteSendResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competitioninvite/sendAllInvites/{competitionId}", competitionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessorInviteSendResource)))
                .andExpect(status().isOk());

        verify(competitionInviteServiceMock).sendAllInvites(competitionId, assessorInviteSendResource);
    }

    @Test
    public void resendInvite() throws Exception {
        long inviteId = 1L;
        AssessorInviteSendResource assessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject("subject")
                .withContent("content")
                .build();

        when(competitionInviteServiceMock.resendInvite(inviteId, assessorInviteSendResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competitioninvite/resendInvite/{inviteId}", inviteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessorInviteSendResource)))
                .andExpect(status().isOk());

        verify(competitionInviteServiceMock, only()).resendInvite(inviteId, assessorInviteSendResource);
    }
}