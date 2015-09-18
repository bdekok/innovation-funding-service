package com.worth.ifs.user.controller;

import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/processrole")
public class ProcessRoleController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    ProcessRoleRepository processRoleRepository;

    @RequestMapping("/findByUserApplication/{userId}/{applicationId}")
    public ProcessRole findByUserApplication(@PathVariable("userId") final Long userId,
                                                     @PathVariable("applicationId") final Long applicationId) {
        return processRoleRepository.findByUserIdAndApplicationId(userId, applicationId);
    }

    @RequestMapping("/findByApplicationId/{applicationId}")
    public List<ProcessRole> findByUserApplication(@PathVariable("applicationId") final Long applicationId) {
        return processRoleRepository.findByApplicationId(applicationId);
    }

    @RequestMapping("/findByUserId/{userId}")
    public List<ProcessRole> findByUser(@PathVariable("userId") final Long userId) {
        return processRoleRepository.findByUserId(userId);
    }

    @RequestMapping("/findAssignable/{applicationId}")
    public Set<ProcessRole> findAssignable(@PathVariable("applicationId") final Long applicationId) {
        List<ProcessRole> processRoles = processRoleRepository.findByApplicationId(applicationId);
        Set<ProcessRole> assignableProcessRoles = processRoles.stream()
                .filter(r -> r.getRole().getName().equals("leadapplicant") || r.getRole().getName().equals("collaborator"))
                .collect(Collectors.toSet());
        return assignableProcessRoles;
    }
}
