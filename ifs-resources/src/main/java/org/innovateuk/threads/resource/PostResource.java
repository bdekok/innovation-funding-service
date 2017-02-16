package org.innovateuk.threads.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.time.LocalDateTime;
import java.util.List;

public class PostResource {
    public final Long id;
    public final UserResource author;
    public final String body;
    public final List<FileEntryResource> attachments;
    public final LocalDateTime createdOn;

    @JsonCreator
    public PostResource(@JsonProperty("id") Long id, @JsonProperty("author") UserResource author,
                        @JsonProperty("body") String body, @JsonProperty("attachments") List<FileEntryResource> attachments,
                        @JsonProperty("createdOn") LocalDateTime createdOn) {
        this.id = id;
        this.author = author;
        this.body = body;
        this.attachments = attachments;
        this.createdOn = createdOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PostResource that = (PostResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(id, that.id)
                .append(author, that.author)
                .append(body, that.body)
                .append(attachments, that.attachments)
                .append(createdOn, that.createdOn)
                .isEquals();
    }
}