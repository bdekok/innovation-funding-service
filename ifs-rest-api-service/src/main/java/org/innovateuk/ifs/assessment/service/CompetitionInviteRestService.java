package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.*;

import java.util.Optional;

/**
 * REST service for managing {@link org.innovateuk.ifs.invite.resource.InviteResource} to {@link org.innovateuk.ifs.competition.resource.CompetitionResource }
 */
public interface CompetitionInviteRestService {

    RestResult<AssessorInvitesToSendResource> getAllInvitesToSend(long competitionId);

    RestResult<AssessorInvitesToSendResource> getInviteToSend(long inviteId);

    RestResult<CompetitionInviteResource> getInvite(String inviteHash);

    RestResult<CompetitionInviteResource> openInvite(String inviteHash);

    RestResult<Void> acceptInvite(String inviteHash);

    RestResult<Void> rejectInvite(String inviteHash, CompetitionRejectionResource rejectionReason);

    RestResult<Boolean> checkExistingUser(String inviteHash);

    RestResult<AvailableAssessorPageResource> getAvailableAssessors(long competitionId, int page, Optional<Long> innovationArea);

    RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(long competitionId, int page);

    RestResult<AssessorInviteOverviewPageResource> getInvitationOverview(long competitionId,
                                                                         int page,
                                                                         Optional<Long> innovationArea,
                                                                         Optional<ParticipantStatusResource> participantStatus,
                                                                         Optional<Boolean> compliant);

    RestResult<CompetitionInviteStatisticsResource> getInviteStatistics(long competitionId);

    RestResult<CompetitionInviteResource> inviteUser(ExistingUserStagedInviteResource existingUserStagedInvite);

    RestResult<Void> inviteNewUsers(NewUserStagedInviteListResource newUserStagedInvites, long competitionId);

    RestResult<Void> deleteInvite(String email, long competitionId);

    RestResult<Void> deleteAllInvites(long competitionId);

    RestResult<Void> sendAllInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource);

    RestResult<Void> resendInvite(long inviteId, AssessorInviteSendResource assessorInviteSendResource);
}
