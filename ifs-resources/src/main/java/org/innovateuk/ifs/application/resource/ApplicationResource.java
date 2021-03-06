package org.innovateuk.ifs.application.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;
import org.innovateuk.ifs.commons.validation.constraints.FutureLocalDate;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;

@FieldRequiredIf(required = "previousApplicationNumber", argument = "resubmission", predicate = true, message = "{validation.application.previous.application.number.required}")
@FieldRequiredIf(required = "previousApplicationTitle", argument = "resubmission", predicate = true, message = "{validation.application.previous.application.title.required}")
public class ApplicationResource {
    private static final int MIN_DURATION_IN_MONTHS = 1;

    private static final int MAX_DURATION_IN_MONTHS = 36;

    private static final List<CompetitionStatus> PUBLISHED_ASSESSOR_FEEDBACK_COMPETITION_STATES = singletonList(PROJECT_SETUP);

    private static final List<CompetitionStatus> EDITABLE_ASSESSOR_FEEDBACK_COMPETITION_STATES = asList(FUNDERS_PANEL, ASSESSOR_FEEDBACK);
    private static final List<CompetitionStatus> SUBMITTABLE_COMPETITION_STATES = asList(OPEN);
    private static final List<ApplicationState> SUBMITTED_APPLICATION_STATES =
            asList(ApplicationState.SUBMITTED, ApplicationState.APPROVED, ApplicationState.REJECTED);
    private Long id;

    @NotBlank(message ="{validation.project.name.must.not.be.empty}")
    private String name;

    @FutureLocalDate(message = "{validation.project.start.date.not.in.future}")
    private LocalDate startDate;

    private ZonedDateTime submittedDate;
    @Min(value=MIN_DURATION_IN_MONTHS, message ="{validation.application.details.duration.in.months.max.digits}")
    @Max(value=MAX_DURATION_IN_MONTHS, message ="{validation.application.details.duration.in.months.max.digits}")
    @NotNull
    private Long durationInMonths;

    private ApplicationState applicationState;
    private Long competition;
    private String competitionName;
    private CompetitionStatus competitionStatus;
    private BigDecimal completion;
    private Boolean stateAidAgreed;
    @NotNull(message="{validation.application.must.indicate.resubmission.or.not}")
    private Boolean resubmission;

    private String previousApplicationNumber;
    private String previousApplicationTitle;
    @NotNull(message="{validation.application.research.category.required}")
    private ResearchCategoryResource researchCategory;

    @NotNull(message="{validation.application.innovationarea.category.required}")
    private InnovationAreaResource innovationArea;

    private boolean noInnovationAreaApplicable;

    private IneligibleOutcomeResource ineligibleOutcome;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Long getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(Long durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public ApplicationState getApplicationState() {
        return applicationState;
    }

    public void setApplicationState(ApplicationState applicationState) {
        this.applicationState = applicationState;
    }

    public Long getCompetition() {
        return competition;
    }

    public void setCompetition(Long competition) {
        this.competition = competition;
    }

    public Boolean getResubmission() {
        return resubmission;
    }

    public void setResubmission(Boolean resubmission) { this.resubmission = resubmission; }

    public String getPreviousApplicationNumber() {
        return previousApplicationNumber;
    }

    public void setPreviousApplicationNumber(String previousApplicationNumber) { this.previousApplicationNumber = previousApplicationNumber; }

    public String getPreviousApplicationTitle() {
        return previousApplicationTitle;
    }

    public void setPreviousApplicationTitle(String previousApplicationTitle) { this.previousApplicationTitle = previousApplicationTitle; }

    @JsonIgnore
    public boolean isOpen(){
        return applicationState == ApplicationState.OPEN || applicationState == ApplicationState.CREATED;
    }

    public IneligibleOutcomeResource getIneligibleOutcome() {
        return ineligibleOutcome;
    }

    public void setIneligibleOutcome(final IneligibleOutcomeResource ineligibleOutcome) {
        this.ineligibleOutcome = ineligibleOutcome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationResource that = (ApplicationResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(startDate, that.startDate)
                .append(durationInMonths, that.durationInMonths)
                .append(applicationState, that.applicationState)
                .append(ineligibleOutcome, that.ineligibleOutcome)
                .append(competition, that.competition)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(startDate)
                .append(durationInMonths)
                .append(applicationState)
                .append(ineligibleOutcome)
                .append(competition)
                .toHashCode();
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public ZonedDateTime getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(ZonedDateTime submittedDate) {
        this.submittedDate = submittedDate;
    }

    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }

    public void setCompetitionStatus(CompetitionStatus competitionStatus) {
        this.competitionStatus = competitionStatus;
    }

    @JsonIgnore
    public boolean isInPublishedAssessorFeedbackCompetitionState() {
        return PUBLISHED_ASSESSOR_FEEDBACK_COMPETITION_STATES.contains(competitionStatus);
    }

    @JsonIgnore
    public boolean isInEditableAssessorFeedbackCompetitionState() {
        return EDITABLE_ASSESSOR_FEEDBACK_COMPETITION_STATES.contains(competitionStatus);
    }

    @JsonIgnore
    public boolean isSubmittable() {
        return isInSubmittableCompetitionState() && !hasBeenSubmitted();
    }

    @JsonIgnore
    public boolean hasBeenSubmitted() {
        return SUBMITTED_APPLICATION_STATES.contains(applicationState);
    }

    private boolean isInSubmittableCompetitionState() {
        return SUBMITTABLE_COMPETITION_STATES.contains(competitionStatus);
    }

    public BigDecimal getCompletion() {
        return completion;
    }

    public void setCompletion(final BigDecimal completion) {
        this.completion = completion;
    }

    public Boolean getStateAidAgreed() {
        return stateAidAgreed;
    }

    public void setStateAidAgreed(Boolean stateAidAgreed) {
        this.stateAidAgreed = stateAidAgreed;
    }

    public ResearchCategoryResource getResearchCategory() {
        return researchCategory;
    }

    public void setResearchCategory(ResearchCategoryResource researchCategory) {
        this.researchCategory = researchCategory;
    }

    public InnovationAreaResource getInnovationArea() {
        return innovationArea;
    }

    public void setInnovationArea(InnovationAreaResource innovationArea) {
        this.innovationArea = innovationArea;
    }

    public boolean getNoInnovationAreaApplicable() {
        return noInnovationAreaApplicable;
    }

    public void setNoInnovationAreaApplicable(boolean noInnovationAreaApplicable) {
        this.noInnovationAreaApplicable = noInnovationAreaApplicable;
    }
}