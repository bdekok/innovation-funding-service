package com.worth.ifs.registration;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.commons.resource.ResourceStatusEnvelope;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.Resource;

import java.util.ArrayList;

import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RegistrationControllerTest extends BaseUnitTest {
    @InjectMocks
    private RegistrationController registrationController;

    @Before
    public void setUp(){
        super.setup();
        setupUserRoles();

        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(registrationController)
                .setViewResolvers(viewResolver())
                .build();
    }

    @Test
    public void onGetRequestRegistrationViewIsReturned() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);

        mockMvc.perform(get("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
        ;
    }

    @Test
    public void emptyFormInputsShouldReturnError() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "")
                        .param("password", "")
                        .param("retypedPassword", "")
                        .param("title", "")
                        .param("firstName", "")
                        .param("lastName", "")
                        .param("phoneNumber", "")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "password"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "retypedPassword"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "title"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "firstName"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "lastName"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "phoneNumber"))
        ;
    }

    @Test
    public void invalidEmailFormatShouldReturnError() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "invalid email format")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"))
        ;
    }

    @Test
    public void incorrectPasswordSizeShouldReturnError() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("password", "12345")
                        .param("retypedPassword", "123456789012345678901234567890123")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "password"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "retypedPassword"))
        ;
    }

    @Test
    public void unmatchedPasswordAndRetypePasswordShouldReturnError() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("password", "12345678")
                        .param("retypedPassword", "123456789")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "retypedPassword"))
        ;
    }

    //@Test
    public void validFormInputShouldInitiateCreateUserServiceCall() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();

        UserResource userDto = new UserResource();
        userDto.setPassword("testtest");
        userDto.setFirstName("firstName");
        userDto.setLastName("lastName");
        userDto.setTitle("Mr");
        userDto.setPhoneNumber("0123456789");
        userDto.setEmail("test@test.test");

        ResourceStatusEnvelope<UserResource> envelope = new ResourceStatusEnvelope<>("OK", new ArrayList<>(), userDto);


        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);
        when(userService.createUserForOrganisation(userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getPassword(),
                userDto.getEmail(),
                userDto.getTitle(),
                userDto.getPhoneNumber(),
                1L,
                "applicant")).thenReturn(envelope);

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", userDto.getEmail())
                        .param("password", userDto.getPassword())
                        .param("retypedPassword", userDto.getPassword())
                        .param("title", userDto.getTitle())
                        .param("firstName", userDto.getFirstName())
                        .param("lastName", userDto.getLastName())
                        .param("phoneNumber", userDto.getPhoneNumber())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"))
        ;
    }

    @Test
    public void correctOrganisationNameIsAddedToModel() throws Exception {
        Organisation organisation = newOrganisation().withId(4L).withName("uniqueOrganisationName").build();

        when(organisationService.getOrganisationById(4L)).thenReturn(organisation);
        mockMvc.perform(post("/registration/register?organisationId=4")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        ).andExpect(model().attribute("organisationName", "uniqueOrganisationName"));
    }
}