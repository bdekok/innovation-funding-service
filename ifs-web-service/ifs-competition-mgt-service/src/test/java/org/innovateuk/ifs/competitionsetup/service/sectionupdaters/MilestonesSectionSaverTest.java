package org.innovateuk.ifs.competitionsetup.service.sectionupdaters;

import org.apache.commons.collections4.map.LinkedMap;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.MilestoneRowForm;
import org.innovateuk.ifs.competitionsetup.form.MilestonesForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupMilestoneService;
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.*;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MilestonesSectionSaverTest {

    @InjectMocks
    private MilestonesSectionSaver service;

    @Mock
    private CompetitionSetupMilestoneService competitionSetupMilestoneService;

    @Mock
    private MilestoneRestService milestoneRestService;

    @Test
    public void testSaveMilestone() {
        MilestonesForm competitionSetupForm = new MilestonesForm();

        ZonedDateTime milestoneDate = ZonedDateTime.of(2017, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault());

        CompetitionResource competition = newCompetitionResource()
                .withMilestones(singletonList(1L))
                .withId(1L).build();

        MilestoneResource milestoneresource = newMilestoneResource()
                .withId(1L)
                .withName(MilestoneType.OPEN_DATE)
                .withDate(milestoneDate)
                .withCompetitionId(1L).build();

        List<MilestoneResource> resourceList = new ArrayList<>();
        resourceList.add(milestoneresource);

        competitionSetupForm.setMilestoneEntries(populateMilestoneFormEntry(resourceList));

        when(competitionSetupMilestoneService.updateMilestonesForCompetition(anyList(), anyMap(), anyLong())).thenReturn(serviceSuccess());
        when(milestoneRestService.getAllMilestonesByCompetitionId(anyLong())).thenReturn(restSuccess(resourceList));
        service.saveSection(competition, competitionSetupForm);
        List<Long> milestones = competition.getMilestones();

        assertEquals(1L, milestones.get(0).longValue());
        assertTrue(resourceList.get(0).getCompetitionId() == 1L);
        assertNotNull(resourceList.get(0).getDate());
        assertTrue(resourceList.get(0).getType().equals(MilestoneType.OPEN_DATE));
    }


    @Test
    public void testSaveMilestoneSetupComplete() {
        MilestonesForm competitionSetupForm = new MilestonesForm();

        ZonedDateTime pastDate = ZonedDateTime.now().minusDays(1);
        ZonedDateTime futureDate = ZonedDateTime.now().plusDays(1);

        CompetitionResource competition = newCompetitionResource()
                .withMilestones(Arrays.asList(1L, 2L))
                .withSetupComplete(true)
                .withStartDate(ZonedDateTime.now().minusDays(1))
                .withFundersPanelDate(ZonedDateTime.now().plusDays(1))
                .withId(1L).build();

        MilestoneResource milestonePast = newMilestoneResource()
                .withId(1L)
                .withName(MilestoneType.OPEN_DATE)
                .withDate(pastDate)
                .withCompetitionId(1L).build();
        MilestoneResource milestoneFuture = newMilestoneResource()
                .withId(2L)
                .withName(MilestoneType.BRIEFING_EVENT)
                .withDate(futureDate)
                .withCompetitionId(1L).build();

        List<MilestoneResource> resourceList = asList(milestonePast, milestoneFuture);

        competitionSetupForm.setMilestoneEntries(populateMilestoneFormEntry(resourceList));
        when(competitionSetupMilestoneService.updateMilestonesForCompetition(anyList(), anyMap(), anyLong())).thenReturn(serviceSuccess());
        when(milestoneRestService.getAllMilestonesByCompetitionId(anyLong())).thenReturn(restSuccess(resourceList));

        service.saveSection(competition, competitionSetupForm);

        //verify update was only called once (for the future date)
        ArgumentCaptor<Map> argumentCaptor = ArgumentCaptor.forClass(Map.class);
        verify(competitionSetupMilestoneService).updateMilestonesForCompetition(eq(resourceList), argumentCaptor.capture(), eq(1L));

        assertThat(argumentCaptor.getValue().size(), equalTo(1));
        assertThat(argumentCaptor.getValue().get(MilestoneType.BRIEFING_EVENT.name()), notNullValue());
        assertThat(argumentCaptor.getValue().get(MilestoneType.OPEN_DATE.name()), nullValue());

    }



    private LinkedMap<String, MilestoneRowForm> populateMilestoneFormEntry(List<MilestoneResource> resources) {
        LinkedMap<String, MilestoneRowForm>  milestoneList = new LinkedMap<>();

        resources.forEach(milestoneResource -> {
            MilestoneRowForm milestone = new MilestoneRowForm(milestoneResource.getType(), milestoneResource.getDate());
            milestoneList.put(milestoneResource.getType().name(), milestone);
        });

        return milestoneList;
    }

    @Test
    public void testAutoSaveCompetitionSetupSection() {
        String fieldName =  "milestoneEntries[BRIEFING_EVENT].milestoneType";
        when(milestoneRestService.getMilestoneByTypeAndCompetitionId(MilestoneType.BRIEFING_EVENT, 1L)).thenReturn(restSuccess(getBriefingEventMilestone()));
        MilestonesForm form = new MilestonesForm();

        CompetitionResource competition = newCompetitionResource().withId(1L).build();
        competition.setMilestones(Arrays.asList(10L));
        when(competitionSetupMilestoneService.isMilestoneDateValid(20, 10, 2020)).thenReturn(Boolean.TRUE);

        MilestoneResource expectedMilestoneMatcher = createLambdaMatcher(milestoneResource -> {
            assertEquals(TimeZoneUtil.fromUkTimeZone(2020, 10, 20, 0), milestoneResource.getDate());
        });
        when(milestoneRestService.updateMilestone(expectedMilestoneMatcher)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.autoSaveSectionField(competition, form, fieldName, "20-10-2020", Optional.empty());

        assertTrue(result.isSuccess());
    }

    @Test
    public void testAutoSaveTimeCompetitionSetupSection() {
        String fieldName =  "milestoneEntries[BRIEFING_EVENT].time";
        when(milestoneRestService.getMilestoneByTypeAndCompetitionId(MilestoneType.BRIEFING_EVENT, 2L))
                .thenReturn(restSuccess(getBriefingEventMilestone()));
        MilestonesForm form = new MilestonesForm();

        CompetitionResource competition = newCompetitionResource().withId(2L).build();
        competition.setMilestones(Arrays.asList(10L));
        when(competitionSetupMilestoneService.isMilestoneDateValid(1, 12, ZonedDateTime.now().plusYears(1).getYear())).thenReturn(Boolean.TRUE);

        MilestoneResource expectedMilestoneMatcher = createLambdaMatcher(milestoneResource -> {
            assertEquals(TimeZoneUtil.fromUkTimeZone(ZonedDateTime.now().plusYears(1).getYear(), 12, 1, 15), milestoneResource.getDate());
        });
        when(milestoneRestService.updateMilestone(expectedMilestoneMatcher)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.autoSaveSectionField(competition, form, fieldName, "THREE_PM", Optional.empty());

        assertTrue(result.isSuccess());
    }

    @Test
    public void testAutoSaveTimeWithEmptyDateCompetitionSetupSection() {
        String fieldName =  "milestoneEntries[BRIEFING_EVENT].time";
        when(milestoneRestService.getMilestoneByTypeAndCompetitionId(MilestoneType.BRIEFING_EVENT, 2L)).thenReturn(restSuccess(newMilestoneResource().build()));
        MilestonesForm form = new MilestonesForm();

        CompetitionResource competition = newCompetitionResource().withId(2L).build();
        competition.setMilestones(Arrays.asList(10L));
        when(competitionSetupMilestoneService.isMilestoneDateValid(null, null, null)).thenReturn(Boolean.FALSE);

        ServiceResult<Void> result = service.autoSaveSectionField(competition, form, fieldName, "THREE_PM", Optional.empty());

        assertTrue(result.isFailure());
    }

    @Test
    public void testAutoSaveCompetitionSetupSectionDateNotInFuture() {
        String fieldName =  "milestoneEntries[BRIEFING_EVENT].milestoneType";
        when(milestoneRestService.getMilestoneByTypeAndCompetitionId(MilestoneType.BRIEFING_EVENT, 1L)).thenReturn(restSuccess(getBriefingEventMilestone()));
        MilestonesForm form = new MilestonesForm();
        CompetitionResource competition = newCompetitionResource().withId(1L).build();
        competition.setMilestones(Arrays.asList(10L));
        when(competitionSetupMilestoneService.isMilestoneDateValid(20, 10, 2015)).thenReturn(Boolean.FALSE);

        ServiceResult<Void> result = service.autoSaveSectionField(competition, form, fieldName, "20-10-2015", null);

        assertTrue(!result.isSuccess());
        assertEquals(result.getErrors().get(0).getErrorKey(), "error.milestone.invalid");
    }

    private MilestoneResource getBriefingEventMilestone(){
        return newMilestoneResource()
                .withId(1L)
                .withName(MilestoneType.OPEN_DATE)
                .withDate(ZonedDateTime.of(ZonedDateTime.now().plusYears(1).getYear(), 12, 1, 0, 0, 0, 0, ZoneId.systemDefault()))
                .withCompetitionId(1L).build();
    }

    @Test
    public void testsSupportsForm() {
        assertTrue(service.supportsForm(MilestonesForm.class));
        assertFalse(service.supportsForm(CompetitionSetupForm.class));
    }
}
