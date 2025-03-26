package de.adorsys.opba.db.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatasafeConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "datasafe_config_id_generator")
    @SequenceGenerator(name = "datasafe_config_id_generator", sequenceName = "datasafe_config_sequence")
    private Long id;

    private String config;
}
