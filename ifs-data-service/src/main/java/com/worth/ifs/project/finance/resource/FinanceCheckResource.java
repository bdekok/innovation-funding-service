package com.worth.ifs.project.finance.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class FinanceCheckResource {
    private Long id;
    private Long project;
    private CostGroupResource costGroup;

    public FinanceCheckResource() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProject() {
        return project;
    }

    public void setProject(Long project) {
        this.project = project;
    }

    public CostGroupResource getCostGroup() {
        return costGroup;
    }

    public void setCostGroup(CostGroupResource costGroup) {
        this.costGroup = costGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FinanceCheckResource that = (FinanceCheckResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(project, that.project)
                .append(costGroup, that.costGroup)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(project)
                .append(costGroup)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("project", project)
                .append("costGroup", costGroup)
                .toString();
    }
}
