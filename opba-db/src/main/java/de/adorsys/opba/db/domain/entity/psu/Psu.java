package de.adorsys.opba.db.domain.entity.psu;

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

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Psu {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "psu_id_generator")
    @SequenceGenerator(name = "psu_id_generator", sequenceName = "psu_id_sequence")
    private Long id;


    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] keystore;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] pubKeys;
}
