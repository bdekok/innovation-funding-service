package com.worth.ifs.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.commons.error.Errors;
import com.worth.ifs.commons.rest.RestErrorEnvelope;
import com.worth.ifs.commons.rest.RestResult;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.application.builder.ResponseBuilder.newResponse;
import static java.nio.charset.Charset.defaultCharset;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

public class ResponseRestServiceMocksTest extends BaseRestServiceUnitTest<ResponseRestServiceImpl> {

    private static final String responseRestURL = "/response";


    @Override
    protected ResponseRestServiceImpl registerRestServiceUnderTest() {
        ResponseRestServiceImpl responseRestService = new ResponseRestServiceImpl();
        responseRestService.responseRestURL = responseRestURL;
        return responseRestService;
    }

    @Test
    public void test_getResponsesByApplicationId() {
        String expectedUrl = dataServicesUrl + responseRestURL + "/findResponsesByApplication/1";


        Response[] responses = newResponse().buildArray(3, Response.class);
        ResponseEntity<Response[]> response = new ResponseEntity(responses, OK);

        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), Response[].class)).thenReturn(response);
        // now run the method under test
        List<Response> returnedResponses = service.getResponsesByApplicationId(1L);

        // verify
        assertNotNull(returnedResponses);
        assertEquals(3, returnedResponses.size());
        assertEquals(responses[0], returnedResponses.get(0));
        assertEquals(responses[1], returnedResponses.get(1));
        assertEquals(responses[2], returnedResponses.get(2));

    }

    @Test
    public void testSaveQuestionResponseAssessorFeedback() throws JsonProcessingException {
        String expectedUrl = dataServicesUrl + responseRestURL +
                "/saveQuestionResponse/1/assessorFeedback?assessorUserId=2&feedbackValue=value&feedbackText=text";

        ResponseEntity<String> response = new ResponseEntity<>(OK);
        when(mockRestTemplate.exchange(expectedUrl, PUT, httpEntityForRestCall(), String.class)).thenReturn(response);

        // now run the method under test
        RestResult<Void> success = service.saveQuestionResponseAssessorFeedback(2L, 1L, Optional.of("value"), Optional.of("text"));

        // verify
        assertTrue(success.isRight());
    }

    @Test
    public void testSaveQuestionResponseAssessorFeedbackButRestErrorEnvelopeReturned() throws JsonProcessingException {

        String expectedUrl = dataServicesUrl + responseRestURL +
                "/saveQuestionResponse/1/assessorFeedback?assessorUserId=2&feedbackValue=value&feedbackText=text";

        RestErrorEnvelope restErrorEnvelope = new RestErrorEnvelope(asList(Errors.badRequestError("Bad!"), Errors.internalServerErrorError("Bang!")));
        when(mockRestTemplate.exchange(expectedUrl, PUT, httpEntityForRestCall(), String.class)).thenThrow(new HttpServerErrorException(BAD_REQUEST, "Bad!", toJsonBytes(restErrorEnvelope), defaultCharset()));

        // now run the method under test
        RestResult<Void> failure = service.saveQuestionResponseAssessorFeedback(2L, 1L, Optional.of("value"), Optional.of("text"));

        // verify
        assertTrue(failure.isLeft());
        assertEquals(BAD_REQUEST, failure.getStatusCode());
    }

    private String toJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    private byte[] toJsonBytes(Object object) throws JsonProcessingException {
        return toJson(object).getBytes();
    }
//
//    @Test
//    public void test_findById() {
//        String expectedUrl = dataServicesUrl + questionRestURL + "/id/1";
//
//        Question question = newQuestion().build();
//        ResponseEntity<Question> response = new ResponseEntity<>(question, HttpStatus.OK);
//        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), Question.class)).thenReturn(response);
//
//        // now run the method under test
//        Question returnedQuestion = service.findById(1L);
//
//        // verify
//        assertNotNull(returnedQuestion);
//        assertEquals(question, returnedQuestion);
//    }
//
//    @Test
//    public void test_getMarkedAsComplete() {
//        String expectedUrl = dataServicesUrl + questionRestURL + "/getMarkedAsComplete/1/2";
//
//        Long[] questionIds = new Long[]{3L, 4L, 5L};
//        ResponseEntity<Long[]> response = new ResponseEntity<>(questionIds, HttpStatus.OK);
//        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), Long[].class)).thenReturn(response);
//
//        // now run the method under test
//        Set<Long> returnedQuestionIds = service.getMarkedAsComplete(1L, 2L);
//
//        // verify
//        assertNotNull(questionIds);
//        assertEquals(3, returnedQuestionIds.size());
//        assertEquals(new HashSet<>(Arrays.asList(questionIds)), returnedQuestionIds);
//    }
//
//    @Test
//    public void test_markAsComplete() {
//        String expectedUrl = dataServicesUrl + questionRestURL + "/markAsComplete/1/2/3";
//
//        service.markAsComplete(1L, 2L, 3L);
//        verify(mockRestTemplate).exchange(expectedUrl, PUT, httpEntityForRestCall(), Void.class);
//    }
//
//    @Test
//    public void test_markAsInComplete() {
//        String expectedUrl = dataServicesUrl + questionRestURL + "/markAsInComplete/1/2/3";
//
//        service.markAsInComplete(1L, 2L, 3L);
//        verify(mockRestTemplate).exchange(expectedUrl, PUT, httpEntityForRestCall(), Void.class);
//    }
//
//    @Test
//    public void test_updateNotification() {
//        String expectedUrl = dataServicesUrl + questionRestURL + "/updateNotification/1/true";
//        service.updateNotification(1L, true);
//        verify(mockRestTemplate).exchange(expectedUrl, PUT, httpEntityForRestCall(), Void.class);
//    }
}
