package de.adorsys.opba.consent.rest.api.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Category of the PSU message category
 */
public enum PsuMessageCategory {
  ERROR("ERROR"), WARNING("WARNING"), INFO("INFO");
  private String value;

  PsuMessageCategory(String value) {
    this.value = value;
  }

  @JsonCreator
  public static PsuMessageCategory fromValue(String text) {
    for (PsuMessageCategory b : PsuMessageCategory.values()) {
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
