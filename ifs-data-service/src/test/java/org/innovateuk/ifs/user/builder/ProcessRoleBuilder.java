package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.BuilderAmendFunctions;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserRoleType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static java.util.Collections.emptyList;

/**
 * Builder for {@link ProcessRole} entities.
 */
public class ProcessRoleBuilder extends BaseBuilder<ProcessRole, ProcessRoleBuilder> {

    private ProcessRoleBuilder(List<BiConsumer<Integer, ProcessRole>> multiActions) {
        super(multiActions);
    }

    public static ProcessRoleBuilder newProcessRole() {
        return new ProcessRoleBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ProcessRoleBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProcessRole>> actions) {
        return new ProcessRoleBuilder(actions);
    }

    @Override
    protected ProcessRole createInitial() {
        return new ProcessRole();
    }

    public ProcessRoleBuilder withId(Long... ids) {
        return withArray((id, processRole) -> processRole.setId(id), ids);
    }

    public ProcessRoleBuilder withRole(Builder<Role, ?> role) {
        return with(processRole -> processRole.setRole(role.build()));
    }

    public ProcessRoleBuilder withRole(Role... roles) {
        return withArray((role, processRole) -> processRole.setRole(role), roles);
    }

    public ProcessRoleBuilder withRole(UserRoleType... roles) {
        return withArray((role, processRole) -> processRole.setRole(newRole().withType(role).build()), roles);
    }

    public ProcessRoleBuilder withApplication(Builder<Application, ?> application) {
        return withApplication(application.build());
    }

    public ProcessRoleBuilder withApplication(Application... applications) {
        return withArray((application, processRole) -> processRole.setApplication(application), applications);
    }

    public ProcessRoleBuilder withOrganisation(Organisation... organisations) {
        return withArray((organisation, processRole) -> setField("organisation", organisation, processRole), organisations);
    }

    public ProcessRoleBuilder withUser(User... users) {
        return withArray(BuilderAmendFunctions::setUser, users);
    }

    @Override
    public void postProcess(int index, ProcessRole processRole) {

        // now add back-refs where appropriate
        User user = processRole.getUser();
        if (user != null && !user.getProcessRoles().contains(processRole)) {
            user.addUserApplicationRole(processRole);
        }

        Application application = processRole.getApplication();
        if (application != null){
            if (application.getProcessRoles() == null){
                application.setProcessRoles(new ArrayList<>());
            }
            if (!application.getProcessRoles().contains(processRole)){
                application.addUserApplicationRole(processRole);
            }
        }
    }
}