package de.adorsys.opba.consent.embedded.rest.api.domain;

import java.util.ArrayList;
import java.util.List;

public class PsuMessageBearingResponse {

	private List<PsuMessage> psuMessages = new ArrayList<>();

	public List<PsuMessage> getPsuMessages() {
		return psuMessages;
	}

	public void setPsuMessages(List<PsuMessage> psuMessages) {
		this.psuMessages = psuMessages;
	}
}
