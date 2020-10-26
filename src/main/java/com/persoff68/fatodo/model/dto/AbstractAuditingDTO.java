package com.persoff68.fatodo.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractAuditingDTO extends AbstractDTO {

    protected Date createdBy;
    protected Instant createdAt;
    protected Date lastModifiedBy;
    protected Instant lastModifiedAt;

}
