package com.worth.ifs.project.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.resource.ProjectResource;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class ProjectResourceBuilder extends BaseBuilder<ProjectResource, ProjectResourceBuilder> {

    private ProjectResourceBuilder(List<BiConsumer<Integer, ProjectResource>> multiActions) {
        super(multiActions);
    }

    public static ProjectResourceBuilder newProjectResource() {
        return new ProjectResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ProjectResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectResource>> actions) {
        return new ProjectResourceBuilder(actions);
    }

    @Override
    protected ProjectResource createInitial() {
        return new ProjectResource();
    }

    public ProjectResourceBuilder withId(Long... ids) {
        return withArray((id, project) -> setField("id", id, project), ids);
    }

    public ProjectResourceBuilder withTargetStartDate(LocalDate... dates) {
        return withArray((date, project) -> project.setTargetStartDate(date), dates);
    }

    public ProjectResourceBuilder withAddress(Long name) {
        return with(project -> project.setAddress(name));
    }

    public ProjectResourceBuilder withProjectManager(Long projectManager) {
        return with(project -> project.setProjectManager(projectManager));
    }

    public ProjectResourceBuilder withDuration(Long... durations) {
        return withArray((duration, project) -> project.setDurationInMonths(duration), durations);
    }
}