package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.user.resource.EthnicityResource;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EthnicityControllerTest extends BaseControllerMockMVCTest<EthnicityController> {

    @Override
    protected EthnicityController supplyControllerUnderTest() {
        return new EthnicityController();
    }

    @Test
    public void findAllActive() throws Exception {

        List<EthnicityResource> expected = newEthnicityResource().build(2);

        when(ethnicityServiceMock.findAllActive()).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/ethnicity/findAllActive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));

        verify(ethnicityServiceMock, only()).findAllActive();
    }
}
