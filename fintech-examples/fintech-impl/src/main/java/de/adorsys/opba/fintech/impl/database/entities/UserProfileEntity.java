package de.adorsys.opba.fintech.impl.database.entities;

import de.adorsys.opba.fintech.api.model.generated.UserProfile;
import lombok.AllArgsConstructor;
import lombok.ToString;

import javax.persistence.Embeddable;

@Embeddable
@AllArgsConstructor
@ToString
public class UserProfileEntity extends UserProfile {
}
