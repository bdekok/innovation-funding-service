package org.innovateuk.ifs.application.populator.section;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.section.YourFundingSectionViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;

/**
 * Your funding populator section view models.
 */
@Component
public class YourFundingSectionPopulator extends AbstractSectionPopulator<YourFundingSectionViewModel> {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private FormInputViewModelGenerator formInputViewModelGenerator;

    @Override
    protected void populate(ApplicantSectionResource section, ApplicationForm form, YourFundingSectionViewModel viewModel, Model model, BindingResult bindingResult) {
        List<Long> completedSectionIds = sectionService.getCompleted(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId());
        viewModel.setComplete(completedSectionIds.contains(section.getSection().getId()));
    }

    @Override
    protected YourFundingSectionViewModel createNew(ApplicantSectionResource section, ApplicationForm form) {
        List<Long> completedSectionIds = sectionService.getCompleted(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId());
        return new YourFundingSectionViewModel(section, formInputViewModelGenerator.fromSection(section, section, form), getNavigationViewModel(section), completedSectionIds.contains(section.getSection().getId()));
    }

    @Override
    public SectionType getSectionType() {
        return SectionType.FUNDING_FINANCES;
    }
}

