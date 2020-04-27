package de.adorsys.opba.protocol.xs2a.service.xs2a.context;

/**
 * The mode of current context. Inner BPMN process (list-accounts as example) is called twice:
 * <ol>
 *     <li>To validate if all necessary parameters are present {@link ContextMode#MOCK_REAL_CALLS}</li>
 *     <li>To actually call ASPSP API {@link ContextMode#REAL_CALLS}</li>
 * </ol>
 */
public enum ContextMode {

    /**
     * Real mode - calling ASPSP API
     */
    REAL_CALLS,

    /**
     * Validation mode - checking if all parameters for ASPSP API calls are present.
     */
    MOCK_REAL_CALLS
}
