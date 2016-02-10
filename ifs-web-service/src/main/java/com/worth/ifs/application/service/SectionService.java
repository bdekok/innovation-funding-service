package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.domain.Section;

import java.util.*;
import java.util.concurrent.Future;

/**
 * Interface for CRUD operations on {@link Section} related data.
 */
public interface SectionService {
    Section getById(Long sectionId);
    List<Long> getCompleted(Long applicationId, Long organisationId);
    List<Long> getInCompleted(Long applicationId);
    Map<Long, Set<Long>> getCompletedSectionsByOrganisation(Long applicationId);
    Boolean allSectionsMarkedAsComplete(Long applicationId);
    List<Section> getParentSections(List<Section> sections);
    Section getByName(String name);
    void removeSectionsQuestionsWithType(Section section, String name);
    List<Long> getUserAssignedSections(List<Section> sections, HashMap<Long, QuestionStatus> questionAssignees, Long currentProcessRoleId);
    Future<Section> getPreviousSection(Optional<Section> sectionId);
    Future<Section> getNextSection(Optional<Section> sectionId);
    Section getSectionByQuestionId(Long questionId);
}
