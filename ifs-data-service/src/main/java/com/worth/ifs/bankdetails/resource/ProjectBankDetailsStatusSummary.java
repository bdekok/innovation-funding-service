package com.worth.ifs.bankdetails.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

import static com.worth.ifs.application.resource.ApplicationResource.formatter;

public class ProjectBankDetailsStatusSummary {
    private Long competitionId;
    private Long projectId;
    private List<BankDetailsStatusResource> bankDetailsStatusResources;

    public ProjectBankDetailsStatusSummary() {
    }

    public ProjectBankDetailsStatusSummary(Long competitionId, Long projectId, List<BankDetailsStatusResource> bankDetailsStatusResources) {
        this.competitionId = competitionId;
        this.projectId = projectId;
        this.bankDetailsStatusResources = bankDetailsStatusResources;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public String getFormattedCompetitionId() {
        return formatter.format(competitionId);
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getFormattedProjectId() {
        return formatter.format(projectId);
    }

    public List<BankDetailsStatusResource> getBankDetailsStatusResources() {
        return bankDetailsStatusResources;
    }

    public void setBankDetailsStatusResources(List<BankDetailsStatusResource> bankDetailsStatusResources) {
        this.bankDetailsStatusResources = bankDetailsStatusResources;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectBankDetailsStatusSummary that = (ProjectBankDetailsStatusSummary) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(projectId, that.projectId)
                .append(bankDetailsStatusResources, that.bankDetailsStatusResources)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(projectId)
                .append(bankDetailsStatusResources)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("competitionId", competitionId)
                .append("projectId", projectId)
                .append("bankDetailsStatusResources", bankDetailsStatusResources)
                .toString();
    }
}
