package com.worth.ifs.assessment.mapper;

import com.worth.ifs.assessment.domain.AssessorFormInputResponse;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.form.mapper.FormInputMapper;
import org.mapstruct.Mapper;

/**
 * Maps between domain and resource DTO for {@link AssessorFormInputResponse}.
 */
@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                AssessmentMapper.class,
                FormInputMapper.class
        }
)
public abstract class AssessorFormInputResponseMapper extends BaseMapper<AssessorFormInputResponse, AssessorFormInputResponseResource, Long> {

    public Long mapAssessmentFormInputResponseToId(final AssessorFormInputResponse object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

}