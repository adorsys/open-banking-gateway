package de.adorsys.opba.protocol.bpmnshared.util.logResolver.domain;

import lombok.Data;


@Data
public class ExecutionLog {

    private String id;
    private String processInstanceId;
    private String rootProcessInstanceId;
    private String parentId;

}
