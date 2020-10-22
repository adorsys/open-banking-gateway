package de.adorsys.opba.db.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;

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

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] config;
}
