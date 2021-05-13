package de.adorsys.opba.protocol.xs2a.util.logresolver.domain;

import lombok.Data;


@Data
public class Xs2aExecutionLog {

    private String id;
    private String processInstanceId;
    private String rootProcessInstanceId;
    private String parentId;

}
