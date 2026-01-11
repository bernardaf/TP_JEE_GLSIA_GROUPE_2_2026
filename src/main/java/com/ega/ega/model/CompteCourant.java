package com.ega.ega.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("COURANT")
@Access(AccessType.FIELD) // 🔴 CRUCIAL pour éviter DuplicateMappingException
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CompteCourant extends CompteBancaire {

    @Column(name = "decouvert_autorise", nullable = false)
    private Double decouvertAutorise = 500.0;

    @Column(name = "frais_tenue", nullable = false)
    private Double fraisTenue = 5.0;

    @Column(name = "plafond_retrait", nullable = false)
    private Double plafondRetrait = 1000.0;

    @Override
    public TypeCompte getTypeCompte() {
        return TypeCompte.COMPTE_COURANT;
    }

    @Override
    public void debiter(Double montant) {
        if (montant == null || montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }

        if (montant > plafondRetrait) {
            throw new IllegalArgumentException(
                    "Plafond de retrait dépassé : " + plafondRetrait
            );
        }

        if ((getSolde() - montant) < -decouvertAutorise) {
            throw new IllegalArgumentException(
                    "Dépassement du découvert autorisé : " + decouvertAutorise
            );
        }

        setSolde(getSolde() - montant);
    }
}

