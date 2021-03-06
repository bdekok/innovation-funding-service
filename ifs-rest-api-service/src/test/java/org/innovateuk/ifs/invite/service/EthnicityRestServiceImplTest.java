package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.user.resource.EthnicityResource;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.ethnicityResourceListType;

import static java.lang.String.format;
import static org.junit.Assert.assertSame;
import static org.springframework.http.HttpStatus.OK;

public class EthnicityRestServiceImplTest extends BaseRestServiceUnitTest<EthnicityRestServiceImpl> {

    private static final String ethnicityRestUrl = "/ethnicity";

    @Override
    protected EthnicityRestServiceImpl registerRestServiceUnderTest() {
        EthnicityRestServiceImpl ethnicityRestService = new EthnicityRestServiceImpl();
        ethnicityRestService.setEthnicityRestUrl(ethnicityRestUrl);
        return ethnicityRestService;
    }

    @Test
    public void findAllActive() throws Exception {
        List<EthnicityResource> expected = Arrays.asList(1,2,3).stream().map(i -> new EthnicityResource()).collect(Collectors.toList());

        setupGetWithRestResultAnonymousExpectations(format("%s/findAllActive", ethnicityRestUrl), ethnicityResourceListType(), expected, OK);
        List<EthnicityResource> response = service.findAllActive().getSuccessObject();
        assertSame(expected, response);
    }
}
