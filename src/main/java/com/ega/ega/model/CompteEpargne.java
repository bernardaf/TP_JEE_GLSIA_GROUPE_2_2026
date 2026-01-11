package com.ega.ega.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("EPARGNE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CompteEpargne extends CompteBancaire {

    @Column(nullable = false)
    private Double tauxInteret = 2.5; // 2.5% par défaut

    @Column(nullable = false)
    private Double plafondRetrait = 1000.0; // Plafond de retrait par opération

    @Override
    public TypeCompte getTypeCompte() {
        return TypeCompte.COMPTE_EPARGNE;
    }

    @Override
    public void debiter(Double montant) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        if (montant > plafondRetrait) {
            throw new IllegalArgumentException(
                    "Dépassement du plafond de retrait : " + plafondRetrait
            );
        }
        if (getSolde() < montant) {
            throw new IllegalArgumentException("Solde insuffisant");
        }
        setSolde(getSolde() - montant);
    }
}

