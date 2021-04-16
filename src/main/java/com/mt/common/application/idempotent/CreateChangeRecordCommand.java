package com.mt.common.application.idempotent;

import lombok.Data;

@Data
public class CreateChangeRecordCommand {

    private String changeId;
    private String entityType;
    private String returnValue;
}
