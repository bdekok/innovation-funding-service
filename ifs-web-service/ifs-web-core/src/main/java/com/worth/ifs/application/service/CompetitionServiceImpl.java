package com.worth.ifs.application.service;

import com.worth.ifs.competition.resource.CompetitionCountResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.CompetitionSearchResult;
import com.worth.ifs.competition.resource.CompetitionTypeResource;
import com.worth.ifs.competition.service.CompetitionsRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class contains methods to retrieve and store {@link CompetitionResource} related data,
 * through the RestService {@link CompetitionsRestService}.
 */
// TODO DW - INFUND-1555 - get this service to return RestResults
@Service
public class CompetitionServiceImpl implements CompetitionService {

    public static final int COMPETITION_PAGE_SIZE = 20;

    @Autowired
    private CompetitionsRestService competitionsRestService;

    @Override
    public CompetitionResource getById(Long competitionId){
        return competitionsRestService.getCompetitionById(competitionId).getSuccessObjectOrThrowException();
    }

    @Override
    public CompetitionResource create(){
        return competitionsRestService.create().getSuccessObjectOrThrowException();
    }

    @Override
    public List<CompetitionResource> getAllCompetitions() {
        return competitionsRestService.getAll().getSuccessObjectOrThrowException();
    }

    @Override
    public List<CompetitionResource> getAllCompetitionsNotInSetup() {
        List<CompetitionResource> competitions = competitionsRestService.getAll().getSuccessObjectOrThrowException();

        return competitions
                .stream()
                .filter(competition -> competition.getCompetitionStatus() == null || !competition.getCompetitionStatus().equals(CompetitionResource.Status.COMPETITION_SETUP))
                .collect(Collectors.toList());
    }

    public List<CompetitionSetupSection> getCompletedCompetitionSetupSectionStatusesByCompetitionId(Long competitionId) {

        CompetitionResource competition = competitionsRestService.getCompetitionById(competitionId).getSuccessObjectOrThrowException();
        
        return competition.getSectionSetupStatus().entrySet().stream()
        				.filter(entry -> Boolean.TRUE.equals(entry.getValue()))
        				.map(entry -> entry.getKey())
        				.sorted()
        				.collect(Collectors.toList());
    }

    @Override
    public List<CompetitionTypeResource> getAllCompetitionTypes() {
        return competitionsRestService.getCompetitionTypes().getSuccessObjectOrThrowException();
    }

    @Override
    public Map<CompetitionResource.Status, List<CompetitionResource>>getLiveCompetitions() {
        return mapToStatus(competitionsRestService.findLiveCompetitions().getSuccessObjectOrThrowException());
    }

    @Override
    public Map<CompetitionResource.Status, List<CompetitionResource>> getProjectSetupCompetitions() {
        return mapToStatus(competitionsRestService.findProjectSetupCompetitions().getSuccessObjectOrThrowException());
    }

    @Override
    public Map<CompetitionResource.Status, List<CompetitionResource>> getUpcomingCompetitions() {
        return mapToStatus(competitionsRestService.findUpcomingCompetitions().getSuccessObjectOrThrowException());
    }

    @Override
    public CompetitionSearchResult searchCompetitions(String searchQuery, int page) {
        CompetitionSearchResult searchResult = competitionsRestService.searchCompetitions(searchQuery, page, COMPETITION_PAGE_SIZE).getSuccessObjectOrThrowException();
        searchResult.setMappedCompetitions(mapToStatus(searchResult.getContent()));
        return searchResult;
    }

    @Override
    public CompetitionCountResource getCompetitionCounts() {
        return competitionsRestService.countCompetitions().getSuccessObjectOrThrowException();
    }

    private Map<CompetitionResource.Status, List<CompetitionResource>> mapToStatus(List<CompetitionResource> resources) {
        return resources.stream().collect(Collectors.groupingBy(CompetitionResource::getCompetitionStatus));
    }

    @Override
    public void update(CompetitionResource competition) {
        competitionsRestService.update(competition).getSuccessObjectOrThrowException();
    }

    @Override
    public void setSetupSectionMarkedAsComplete(Long competitionId, CompetitionSetupSection section) {
        competitionsRestService.markSectionComplete(competitionId, section).getSuccessObjectOrThrowException();
    }

    @Override
    public void setSetupSectionMarkedAsIncomplete(Long competitionId, CompetitionSetupSection section) {
        competitionsRestService.markSectionInComplete(competitionId, section).getSuccessObjectOrThrowException();
    }

    @Override
    public void initApplicationFormByCompetitionType(Long competitionId, Long competitionTypeId) {
        competitionsRestService.initApplicationForm(competitionId, competitionTypeId);
    }

    @Override
    public String generateCompetitionCode(Long competitionId, LocalDateTime openingDate) {
        String competitionCode = competitionsRestService.generateCompetitionCode(competitionId, openingDate).getSuccessObjectOrThrowException();
        return competitionCode;
    }

    @Override
    public void returnToSetup(Long competitionId) {
        competitionsRestService.returnToSetup(competitionId);
    }

    @Override
    public void markAsSetup(Long competitionId) {
        competitionsRestService.markAsSetup(competitionId);
    }


}
