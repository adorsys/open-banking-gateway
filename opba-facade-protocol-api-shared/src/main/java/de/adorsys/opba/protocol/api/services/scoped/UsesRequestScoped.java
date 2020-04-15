package de.adorsys.opba.protocol.api.services.scoped;

// TODO - consider migration to custom spring scope
public interface UsesRequestScoped {

    void setRequestScoped(RequestScoped scoped);
}
