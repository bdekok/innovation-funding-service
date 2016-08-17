package com.worth.ifs.competition.security;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import com.google.common.collect.Lists;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.user.builder.RoleResourceBuilder;
import com.worth.ifs.user.builder.UserResourceBuilder;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Test;

import com.worth.ifs.BasePermissionRulesTest;

/**
 * Tests the logic within the individual CompetitionPermissionRules methods that secures basic Competition details
 */
public class CompetitionPermissionRulesTest extends BasePermissionRulesTest<CompetitionPermissionRules> {

	@Override
	protected CompetitionPermissionRules supplyPermissionRulesUnderTest() {
		return new CompetitionPermissionRules();
	}
	
    @Test
    public void testAnyoneCanViewACompetition() {
        //null user cannot see competition in setup.
        assertFalse(rules.anyoneCanViewOpenCompetitions(newCompetitionResource().withCompetitionStatus(CompetitionResource.Status.COMPETITION_SETUP).build(), null));
        //null user can see open competitions
        assertTrue(rules.anyoneCanViewOpenCompetitions(newCompetitionResource().withCompetitionStatus(CompetitionResource.Status.OPEN).build(), null));
        //Comp admin can see competition in setup
        assertTrue(rules.anyoneCanViewOpenCompetitions(newCompetitionResource().withCompetitionStatus(CompetitionResource.Status.COMPETITION_SETUP).build(),
                UserResourceBuilder.newUserResource().withRolesGlobal(
                        Lists.newArrayList(RoleResourceBuilder.newRoleResource().withType(UserRoleType.COMP_ADMIN).build())).build()));
    }
    
}
