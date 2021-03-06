package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.assessment.transactional.CompetitionParticipantService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.method.P;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CompetitionParticipantServiceSecurityTest extends BaseServiceSecurityTest<CompetitionParticipantService> {

    private static final int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 3;

    private CompetitionParticipantPermissionRules competitionParticipantPermissionRules;
    private CompetitionParticipantLookupStrategy competitionParticipantLookupStrategy;

    @Override
    protected Class<? extends CompetitionParticipantService> getClassUnderTest() {
        return CompetitionParticipantServiceSecurityTest.TestCompetitionParticipantService.class;
    }


    @Before
    public void setUp() throws Exception {
        competitionParticipantPermissionRules = getMockPermissionRulesBean(CompetitionParticipantPermissionRules.class);
        competitionParticipantLookupStrategy = getMockPermissionEntityLookupStrategiesBean(CompetitionParticipantLookupStrategy.class);
    }

    @Test
    public void getCompetitionParticipants() {
        UserResource assessorUserResource = newUserResource()
                .withRolesGlobal(singletonList(
                        newRoleResource()
                                .withType(UserRoleType.ASSESSOR)
                                .build()
                        )
                ).build();

        setLoggedInUser(assessorUserResource);

        assertTrue(classUnderTest.getCompetitionParticipants(7L, CompetitionParticipantRoleResource.ASSESSOR).getSuccessObject().isEmpty());

        verify(competitionParticipantPermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS)).userCanViewTheirOwnCompetitionParticipation(any(CompetitionParticipantResource.class), eq(assessorUserResource));
    }

    public static class TestCompetitionParticipantService implements CompetitionParticipantService {

        @Override
        public ServiceResult<List<CompetitionParticipantResource>> getCompetitionParticipants(@P("user") Long userId, @P("role") CompetitionParticipantRoleResource role) {
            return serviceSuccess( newCompetitionParticipantResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS) );
        }
    }
}
