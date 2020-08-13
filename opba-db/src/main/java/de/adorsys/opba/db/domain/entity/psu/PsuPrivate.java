package de.adorsys.opba.db.domain.entity.psu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.sql.Blob;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PsuPrivate {

    @Id
    private UUID id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private Blob data;
}
