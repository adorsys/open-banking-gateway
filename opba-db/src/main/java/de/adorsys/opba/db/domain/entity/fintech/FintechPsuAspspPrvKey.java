package de.adorsys.opba.db.domain.entity.fintech;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FintechPsuAspspPrvKey extends FintechPsuAspspKey {
}
