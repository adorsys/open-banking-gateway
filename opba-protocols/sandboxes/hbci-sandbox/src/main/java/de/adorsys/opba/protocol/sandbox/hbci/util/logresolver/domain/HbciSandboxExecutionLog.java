package de.adorsys.opba.protocol.sandbox.hbci.util.logresolver.domain;

import lombok.Data;


@Data
public class HbciSandboxExecutionLog {

    private String id;
    private String processInstanceId;
    private String rootProcessInstanceId;
    private String parentId;

}
