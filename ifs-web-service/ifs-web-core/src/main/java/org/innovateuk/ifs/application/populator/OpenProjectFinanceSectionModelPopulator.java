package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.finance.view.ProjectFinanceOverviewModelManager;
import org.innovateuk.ifs.application.finance.viewmodel.FinanceViewModel;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.QuestionType;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.viewmodel.BaseSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.OpenFinanceSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.SectionApplicationViewModel;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for populating the model for the "Your Finances" section
 */
@Component
public class OpenProjectFinanceSectionModelPopulator extends BaseOpenFinanceSectionModelPopulator {
    public static final String MODEL_ATTRIBUTE_FORM = "form";

    @Autowired
    private QuestionService questionService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ProjectFinanceOverviewModelManager projectFinanceOverviewModelManager;

    @Autowired
    private FinanceHandler financeHandler;

    @Autowired
    private ProjectService projectService;

    @Override
    public BaseSectionViewModel populateModel(ApplicationForm form, Model model, BindingResult bindingResult, ApplicantSectionResource applicantSection) {
        ProjectResource projectResource = projectService.getByApplicationId(applicantSection.getApplication().getId());

        OpenFinanceSectionViewModel openFinanceSectionViewModel = new OpenFinanceSectionViewModel(addNavigation(applicantSection.getSection(), applicantSection.getApplication().getId()),
                applicantSection.getSection(), true, applicantSection.getSection().getId(), applicantSection.getCurrentUser(), isSubFinanceSection(applicantSection.getSection()));
        SectionApplicationViewModel sectionApplicationViewModel = new SectionApplicationViewModel();

        sectionApplicationViewModel.setCurrentApplication(applicantSection.getApplication());
        sectionApplicationViewModel.setCurrentCompetition(applicantSection.getCompetition());

        sectionApplicationViewModel.setAllReadOnly(calculateAllReadOnly(openFinanceSectionViewModel, applicantSection)
                || SectionType.FINANCE.equals(applicantSection.getSection().getType()));

        addQuestionsDetails(openFinanceSectionViewModel, applicantSection, form);
        addApplicationAndSections(openFinanceSectionViewModel, sectionApplicationViewModel, form, applicantSection);
        addOrganisationAndUserProjectFinanceDetails(openFinanceSectionViewModel, form, applicantSection, projectResource);
        addFundingSection(openFinanceSectionViewModel, applicantSection);

        form.setBindingResult(bindingResult);
        form.setObjectErrors(bindingResult.getAllErrors());

        openFinanceSectionViewModel.setSectionApplicationViewModel(sectionApplicationViewModel);

        FinanceViewModel financeViewModel = (FinanceViewModel) openFinanceSectionViewModel.getFinance();
        populateSubSectionMenuOptions(openFinanceSectionViewModel, applicantSection.allSections().map(ApplicantSectionResource::getSection).collect(Collectors.toList()), openFinanceSectionViewModel.getSectionApplicationViewModel().getUserOrganisation().getId(), financeViewModel.getOrganisationGrantClaimPercentage());

        model.addAttribute(MODEL_ATTRIBUTE_FORM, form);

        return openFinanceSectionViewModel;
    }

    private void addOrganisationAndUserProjectFinanceDetails(OpenFinanceSectionViewModel financeSectionViewModel, ApplicationForm form, ApplicantSectionResource applicantSection, ProjectResource projectResource) {
        List<ApplicantQuestionResource> costsQuestions = applicantSection.questionsWithType(QuestionType.COST);

        financeSectionViewModel.setFinanceOverviewViewModel(projectFinanceOverviewModelManager.getFinanceDetailsViewModel(applicantSection.getCompetition().getId(), projectResource.getId()));
        financeSectionViewModel.setFinanceViewModel(financeHandler.getProjectFinanceModelManager(applicantSection.getCurrentApplicant().getOrganisation().getOrganisationType()).getFinanceViewModel(projectResource.getId(), costsQuestions.stream().map(ApplicantQuestionResource::getQuestion).collect(Collectors.toList()), applicantSection.getCurrentApplicant().getProcessRole().getUser(), form, applicantSection.getCurrentApplicant().getOrganisation().getId()));
    }
}