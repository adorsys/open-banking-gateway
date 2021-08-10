package de.adorsys.opba.protocol.hbci.util.logresolver.domain;

import lombok.Data;


@Data
public class HbciExecutionLog {

    private String id;
    private String processInstanceId;
    private String rootProcessInstanceId;
    private String parentId;

}
