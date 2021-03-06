package org.innovateuk.ifs.application.team.controller;

import org.innovateuk.ifs.application.team.form.ApplicantInviteForm;
import org.innovateuk.ifs.application.team.form.ApplicationTeamUpdateForm;
import org.innovateuk.ifs.application.team.populator.ApplicationTeamManagementModelPopulator;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementApplicantRowViewModel;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.invite.service.InviteOrganisationRestService;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ServiceResult.processAnyFailuresOrSucceed;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * This controller will handle all requests that are related to the management of application participants
 */
@Controller
@RequestMapping("/application/{applicationId}/team/update")
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationTeamManagementController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private InviteOrganisationRestService inviteOrganisationRestService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private ApplicationTeamManagementModelPopulator applicationTeamManagementModelPopulator;

    @GetMapping(params = {"organisation"})
    public String getUpdateOrganisation(Model model,
                                        @PathVariable("applicationId") long applicationId,
                                        @RequestParam(name = "organisation") long organisationId,
                                        UserResource loggedInUser,
                                        @ModelAttribute(name = FORM_ATTR_NAME, binding = false) ApplicationTeamUpdateForm form) {
        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            ApplicationTeamManagementViewModel viewModel = applicationTeamManagementModelPopulator.populateModelByOrganisationId(
                    applicationId, organisationId, loggedInUser.getId());
            model.addAttribute("model", viewModel);
            addExistingApplicantsToForm(viewModel, form);
            return "application-team/edit-org";
        });
    }

    @GetMapping(params = {"inviteOrganisation"})
    public String getUpdateOrganisationByInviteOrganisation(Model model,
                                                            @PathVariable("applicationId") long applicationId,
                                                            @RequestParam(name = "inviteOrganisation") long inviteOrganisationId,
                                                            UserResource loggedInUser,
                                                            @ModelAttribute(name = FORM_ATTR_NAME, binding = false) ApplicationTeamUpdateForm form) {
        return validateOrganisationInviteAndApplicationId(applicationId, inviteOrganisationId, () -> {
            ApplicationTeamManagementViewModel viewModel = applicationTeamManagementModelPopulator.populateModelByInviteOrganisationId(
                    applicationId, inviteOrganisationId, loggedInUser.getId());
            model.addAttribute("model", viewModel);
            addExistingApplicantsToForm(viewModel, form);
            return "application-team/edit-org";
        });
    }

    @PostMapping(params = {"updateOrganisation", "organisation"})
    public String submitUpdateOrganisation(Model model,
                                           @PathVariable("applicationId") Long applicationId,
                                           @RequestParam(name = "organisation") long organisationId,
                                           UserResource loggedInUser,
                                           @Valid @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form,
                                           @SuppressWarnings("unused") BindingResult bindingResult,
                                           ValidationHandler validationHandler) {
        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            validateUniqueEmails(form, bindingResult);
            Supplier<String> failureView = () -> getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form);

            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                ServiceResult<InviteResultsResource> updateResult = updateInvitesByOrganisation(organisationId, form, applicationId);

                return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                        .failNowOrSucceedWith(failureView, () -> format("redirect:/application/%s/team", applicationId));
            });
        }) ;
    }

    @PostMapping(params = {"updateOrganisation", "inviteOrganisation"})
    public String submitUpdateOrganisationByInviteOrganisation(Model model,
                                                               @PathVariable("applicationId") Long applicationId,
                                                               @RequestParam(name = "inviteOrganisation") long inviteOrganisationId,
                                                               UserResource loggedInUser,
                                                               @Valid @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form,
                                                               @SuppressWarnings("unused") BindingResult bindingResult,
                                                               ValidationHandler validationHandler) {
        return validateOrganisationInviteAndApplicationId(applicationId, inviteOrganisationId, () -> {
            validateUniqueEmails(form, bindingResult);
            Supplier<String> failureView = () -> getUpdateOrganisationByInviteOrganisation(model, applicationId, inviteOrganisationId, loggedInUser, form);

            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                ServiceResult<InviteResultsResource> updateResult = updateInvitesByInviteOrganisation(inviteOrganisationId, form, applicationId);

                return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                        .failNowOrSucceedWith(failureView, () -> format("redirect:/application/%s/team", applicationId));
            });
        });
    }

    @PostMapping(params = {"addApplicant", "organisation"})
    public String addApplicant(Model model,
                               @PathVariable("applicationId") long applicationId,
                               @RequestParam(name = "organisation") long organisationId,
                               UserResource loggedInUser,
                               @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form) {
        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            form.getApplicants().add(new ApplicantInviteForm());
            return getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form);
        });
    }

    @PostMapping(params = {"addApplicant", "inviteOrganisation"})
    public String addApplicantByInviteOrganisation(Model model,
                                                   @PathVariable("applicationId") long applicationId,
                                                   @RequestParam(name = "inviteOrganisation") long inviteOrganisationId,
                                                   UserResource loggedInUser,
                                                   @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form) {
        return validateOrganisationInviteAndApplicationId(applicationId, inviteOrganisationId, () -> {
            form.getApplicants().add(new ApplicantInviteForm());
            return getUpdateOrganisationByInviteOrganisation(model, applicationId, inviteOrganisationId, loggedInUser, form);
        });
    }

    @PostMapping(params = {"removeApplicant", "organisation"})
    public String removeApplicant(Model model,
                                  @PathVariable("applicationId") long applicationId,
                                  @RequestParam(name = "organisation") long organisationId,
                                  UserResource loggedInUser,
                                  @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form,
                                  @RequestParam(name = "removeApplicant") Integer position) {

        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            form.getApplicants().remove(position.intValue());
            return getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form);
        });
    }

    @PostMapping(params = {"removeApplicant", "inviteOrganisation"})
    public String removeApplicantByInviteOrganisation(Model model,
                                                      @PathVariable("applicationId") long applicationId,
                                                      @RequestParam(name = "inviteOrganisation") long inviteOrganisationId,
                                                      UserResource loggedInUser,
                                                      @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form,
                                                      @RequestParam(name = "removeApplicant") Integer position) {
        return validateOrganisationInviteAndApplicationId(applicationId, inviteOrganisationId, () -> {
            form.getApplicants().remove(position.intValue());
            return getUpdateOrganisationByInviteOrganisation(model, applicationId, inviteOrganisationId, loggedInUser, form);
        });
    }

    @PostMapping(params = {"markForRemoval", "organisation"})
    public String markForRemoval(Model model,
                                 @PathVariable("applicationId") long applicationId,
                                 @RequestParam(name = "organisation") long organisationId,
                                 UserResource loggedInUser,
                                 @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form,
                                 @RequestParam(name = "markForRemoval") long applicationInviteId) {

        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            form.getMarkedForRemoval().add(applicationInviteId);
            return getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form);
        });
    }

    @PostMapping(params = {"markForRemoval", "inviteOrganisation"})
    public String markForRemovalByInviteOrganisation(Model model,
                                                     @PathVariable("applicationId") long applicationId,
                                                     @RequestParam(name = "inviteOrganisation") long inviteOrganisationId,
                                                     UserResource loggedInUser,
                                                     @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form,
                                                     @RequestParam(name = "markForRemoval") long applicationInviteId) {
        return validateOrganisationInviteAndApplicationId(applicationId, inviteOrganisationId, () -> {
            form.getMarkedForRemoval().add(applicationInviteId);
            return getUpdateOrganisationByInviteOrganisation(model, applicationId, inviteOrganisationId, loggedInUser, form);
        });
    }

    @GetMapping(params = {"deleteOrganisation", "organisation"})
    public String confirmDeleteOrganisation(Model model,
                                            @PathVariable("applicationId") long applicationId,
                                            @RequestParam("organisation") long organisationId,
                                            UserResource loggedInUser) {

        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            model.addAttribute("model", applicationTeamManagementModelPopulator.populateModelByOrganisationId(
                    applicationId,
                    organisationId,
                    loggedInUser.getId()
            ));

            return "application-team/delete-org";
        });
    }

    @GetMapping(params = {"deleteOrganisation", "inviteOrganisation"})
    public String confirmDeleteInviteOrganisation(Model model,
                                                  @PathVariable("applicationId") long applicationId,
                                                  @RequestParam("inviteOrganisation") long inviteOrganisationId,
                                                  UserResource loggedInUser) {
        return validateOrganisationInviteAndApplicationId(applicationId, inviteOrganisationId, () -> {
            model.addAttribute("model", applicationTeamManagementModelPopulator.populateModelByInviteOrganisationId(
                    applicationId,
                    inviteOrganisationId,
                    loggedInUser.getId()
            ));

            return "application-team/delete-org";
        });
    }

    @PostMapping(params = {"deleteOrganisation", "organisation"})
    public String deleteOrganisation(Model model,
                                     @PathVariable("applicationId") Long applicationId,
                                     @RequestParam("organisation") long organisationId,
                                     UserResource loggedInUser,
                                     @Valid @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form) {

        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            List<Long> existingApplicantIds = inviteOrganisationRestService.getByOrganisationIdWithInvitesForApplication(organisationId, applicationId)
                    .toOptionalIfNotFound()
                    .getSuccessObjectOrThrowException()
                    .map(organisation -> organisation.getInviteResources().stream()
                            .map(ApplicationInviteResource::getId)
                            .collect(Collectors.toList())
                    )
                    .orElse(Collections.emptyList());

            return processAnyFailuresOrSucceed(simpleMap(existingApplicantIds, applicationService::removeCollaborator))
                    .handleSuccessOrFailure(
                            failure -> getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form),
                            success -> format("redirect:/application/%s/team", applicationId)
                    );
        });
    }

    @PostMapping(params = {"deleteOrganisation", "inviteOrganisation"})
    public String deleteInviteOrganisation(Model model,
                                           @PathVariable("applicationId") Long applicationId,
                                           @RequestParam("inviteOrganisation") long inviteOrganisationId,
                                           UserResource loggedInUser,
                                           @Valid @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form) {
        return validateOrganisationInviteAndApplicationId(applicationId, inviteOrganisationId, () -> {
            List<Long> existingApplicantIds = inviteOrganisationRestService.getById(inviteOrganisationId)
                    .getSuccessObjectOrThrowException()
                    .getInviteResources().stream()
                    .map(ApplicationInviteResource::getId)
                    .collect(Collectors.toList());

            return processAnyFailuresOrSucceed(simpleMap(existingApplicantIds, applicationService::removeCollaborator))
                    .handleSuccessOrFailure(
                            failure -> getUpdateOrganisationByInviteOrganisation(model, applicationId, inviteOrganisationId, loggedInUser, form),
                            success -> format("redirect:/application/%s/team", applicationId)
                    );
        });
    }

    private ServiceResult<InviteResultsResource> updateInvitesByOrganisation(long organisationId,
                                                                             ApplicationTeamUpdateForm form,
                                                                             long applicationId) {
        List<ApplicationInviteResource> invites = createInvites(form, applicationId);

        return processAnyFailuresOrSucceed(simpleMap(form.getMarkedForRemoval(), applicationService::removeCollaborator))
                .andOnSuccess(() -> invites.isEmpty() ?
                        serviceSuccess(new InviteResultsResource()) :
                        inviteRestService.createInvitesByOrganisation(organisationId, invites).toServiceResult()
                );
    }

    private ServiceResult<InviteResultsResource> updateInvitesByInviteOrganisation(long inviteOrganisationId,
                                                                                   ApplicationTeamUpdateForm form,
                                                                                   long applicationId) {
        List<ApplicationInviteResource> invites = createInvites(form, applicationId, inviteOrganisationId);

        return processAnyFailuresOrSucceed(simpleMap(form.getMarkedForRemoval(), applicationService::removeCollaborator))
                .andOnSuccess(() -> invites.isEmpty() ?
                        serviceSuccess(new InviteResultsResource()) :
                        inviteRestService.saveInvites(invites).toServiceResult()
                );
    }

    private List<ApplicationInviteResource> createInvites(ApplicationTeamUpdateForm applicationTeamUpdateForm,
                                                          long applicationId) {
        return createInvites(applicationTeamUpdateForm, applicationId, null);
    }

    private List<ApplicationInviteResource> createInvites(ApplicationTeamUpdateForm applicationTeamUpdateForm,
                                                          long applicationId,
                                                          Long inviteOrganisationId) {
        return simpleMap(
                applicationTeamUpdateForm.getApplicants(),
                applicantInviteForm -> createInvite(applicantInviteForm, applicationId, inviteOrganisationId)
        );
    }

    private ApplicationInviteResource createInvite(ApplicantInviteForm applicantInviteForm,
                                                   long applicationId,
                                                   Long inviteOrganisationId) {
        ApplicationInviteResource applicationInviteResource = new ApplicationInviteResource(
                applicantInviteForm.getName(),
                applicantInviteForm.getEmail(),
                applicationId
        );
        applicationInviteResource.setInviteOrganisation(inviteOrganisationId);

        return applicationInviteResource;
    }

    private void validateUniqueEmails(ApplicationTeamUpdateForm form, BindingResult bindingResult) {
        Set<String> emails = new HashSet<>(form.getExistingApplicants());

        forEachWithIndex(form.getApplicants(), (index, applicantInviteForm) -> {
            if (!emails.add(applicantInviteForm.getEmail())) {
                bindingResult.rejectValue(format("applicants[%s].email", index), "email.already.in.invite",
                        "You have used this email address for another applicant.");
            }
        });
    }

    // Detecting duplicate applicants when the form is submitted requires the pre-population of existing applicants for reference.
    private void addExistingApplicantsToForm(ApplicationTeamManagementViewModel viewModel, ApplicationTeamUpdateForm form) {
        List<ApplicationTeamManagementApplicantRowViewModel> applicants = viewModel.getApplicants();
        form.setExistingApplicants(simpleMap(applicants, row -> row.getEmail()));
    }


    private String validateOrganisationAndApplicationIds(Long applicationId, Long organisationId, Supplier<String> supplier) {
        List<ProcessRoleResource> processRoles = processRoleService.getByApplicationId(applicationId);
        if (processRoles.stream().anyMatch(processRoleResource -> organisationId.equals(processRoleResource.getOrganisationId()))) {
            return supplier.get();
        }
        throw new ObjectNotFoundException("Organisation id not found in application id provided.", Collections.emptyList());
    }

    private String validateOrganisationInviteAndApplicationId(Long applicationId, Long organisationInviteId, Supplier<String> supplier) {
        InviteOrganisationResource organisation = inviteOrganisationRestService.getById(organisationInviteId).getSuccessObjectOrThrowException();
        if(organisation.getInviteResources().stream().anyMatch(applicationInviteResource -> applicationInviteResource.getApplication().equals(applicationId))) {
            return supplier.get();
        }
        throw new ObjectNotFoundException("Organisation invite id not found in application id provided.", Collections.emptyList());
    }
}
