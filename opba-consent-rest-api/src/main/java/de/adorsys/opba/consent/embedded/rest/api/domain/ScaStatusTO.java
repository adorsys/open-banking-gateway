package de.adorsys.opba.consent.embedded.rest.api.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ScaStatusTO {
	RECEIVED("received"), 
	PSUIDENTIFIED("psuIdentified"), 
	PSUAUTHENTICATED("psuAuthenticated"),
	SCAMETHODSELECTED("scaMethodSelected"), 
	STARTED("started"), 
	FINALISED("finalised"), 
	FAILED("failed"),
	EXEMPTED("exempted");

	private String value;

	ScaStatusTO(String value) {
        this.value = value;
    }

	@JsonCreator
	public static ScaStatusTO fromValue(String text) {
		for (ScaStatusTO b : ScaStatusTO.values()) {
			if (String.valueOf(b.value).equals(text)) {
				return b;
			}
		}
		return null;
	}

	@Override
	@JsonValue
	public String toString() {
		return String.valueOf(value);
	}
}
