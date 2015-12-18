package com.worth.ifs.assessment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * AssessmentRestServiceImpl is a utility for CRUD operations on {@link Assessment}.
 * This class connects to the {@link com.worth.ifs.assessment.controller.AssessmentController}
 * through a REST call.
 */
@Service
public class AssessmentRestServiceImpl extends BaseRestService implements AssessmentRestService {

    @Value("${ifs.data.service.rest.assessment}")
    String assessmentRestURL;


    public List<Assessment> getAllByAssessorAndCompetition(Long assessorId, Long competitionId) {
        return Arrays.asList(restGet(assessmentRestURL +"/findAssessmentsByCompetition/" + assessorId + "/" + competitionId , Assessment[].class));
    }

    public Assessment getOneByProcessRole(Long processRoleId) {
        return restGet(assessmentRestURL +"/findAssessmentByProcessRole/" + processRoleId, Assessment.class);
    }


    public Integer getTotalAssignedByAssessorAndCompetition(Long assessorId, Long competitionId) {
        return restGet(assessmentRestURL +"/totalAssignedAssessmentsByCompetition/" + assessorId + "/" + competitionId, Integer.class);
    }


    public Integer getTotalSubmittedByAssessorAndCompetition(Long assessorId, Long competitionId) {
        return restGet(assessmentRestURL +"/totalSubmittedAssessmentsByCompetition/" + assessorId + "/" + competitionId, Integer.class);
    }

    public Boolean respondToAssessmentInvitation(Long assessorId, Long applicationId, Boolean decision, String reason, String observations) {

        //builds the node with the response form fields data
        ObjectNode node =  new ObjectMapper().createObjectNode();
        node.put("assessorId", assessorId);
        node.put("applicationId", applicationId);
        node.put("decision", decision);
        node.put("reason", reason);
        node.put("observations", HtmlUtils.htmlEscape(observations));

        return restPost(assessmentRestURL +"/respondToAssessmentInvitation/", node, Boolean.class);
    }

    public Boolean saveAssessmentSummary(Long assessorId, Long applicationId, String suitableValue, String suitableFeedback, String comments) {
        //builds the node with the response form fields data
        ObjectNode node =  new ObjectMapper().createObjectNode();
        node.put("assessorId", assessorId);
        node.put("applicationId", applicationId);
        node.put("suitableValue", suitableValue);
        node.put("suitableFeedback", HtmlUtils.htmlEscape(suitableFeedback));
        node.put("comments",  HtmlUtils.htmlEscape(comments));
        return restPost(assessmentRestURL +"/saveAssessmentSummary/", node, Boolean.class);
    }

    public Boolean submitAssessments(Long assessorId, Set<Long> assessmentIds ) {
        //builds the node with the response form fields data
        ObjectNode node =  new ObjectMapper().createObjectNode();
        node.put("assessorId", assessorId);
        ArrayNode assessmentsToSubmit = new ObjectMapper().valueToTree(assessmentIds);
        node.putArray("assessmentsToSubmit").addAll(assessmentsToSubmit);
        return restPost(assessmentRestURL +"/submitAssessments/", node,  Boolean.class);
    }

    @Override
    public void acceptAssessmentInvitation(Long processId, Assessment assessment) {
        restPost(assessmentRestURL + "/acceptAssessmentInvitation/" + processId, assessment, String.class);
    }

    @Override
    public void rejectAssessmentInvitation(Long processId, ProcessOutcome processOutcome) {
        restPost(assessmentRestURL + "/rejectAssessmentInvitation/" + processId, processOutcome, String.class);
    }

    @Override
    public int getScore(Long id) {
        return restGet(assessmentRestURL + "/" + id + "/score", Integer.class);
    }


}
