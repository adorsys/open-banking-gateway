package de.adorsys.opba.core.protocol.controller.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class ApiPaths {

    public static final String ACCOUNTS = "/accounts";
    public static final String CONSENTS = "/consents";
    public static final String TRANSACTIONS = "/transactions";
    public static final String BANKS = "/banks";

    public static final String MORE_PARAMETERS = "/parameters/provide-more";
    public static final String MORE_PARAMETERS_PSU_PASSWORD = "/parameters/provide-psu-password";
    public static final String MORE_PARAMETERS_SELECT_SCA_METHOD = "/parameters/select-sca-method";
    public static final String MORE_PARAMETERS_REPORT_SCA_RESULT = "/parameters/report-sca-result";
}
