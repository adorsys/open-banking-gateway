package de.adorsys.opba.protocol.api.services.scoped;

/**
 * Interface to assign current {@link RequestScoped} general services.
 */
// TODO - consider migration to custom spring scope
public interface UsesRequestScoped {

    /**
     * Set {@link RequestScoped} general services
     * @param scoped General services that were constructed from secret key.
     */
    void setRequestScoped(RequestScoped scoped);
}
