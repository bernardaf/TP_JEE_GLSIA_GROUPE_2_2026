package com.ega.ega.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comptes_bancaires")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "type_compte",
        discriminatorType = DiscriminatorType.STRING
)
@Access(AccessType.FIELD) // 🔴 IMPORTANT : évite le double mapping Hibernate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class CompteBancaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 34)
    private String numeroCompte;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(nullable = false)
    @Min(value = 0, message = "Le solde ne peut pas être négatif")
    private Double solde = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proprietaire_id", nullable = false)
    private Client proprietaire;

    @OneToMany(
            mappedBy = "compteSource",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<Transaction> transactionsEmises = new ArrayList<>();

    @OneToMany(
            mappedBy = "compteDestination",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<Transaction> transactionsRecues = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
        if (this.solde == null) {
            this.solde = 0.0;
        }
    }

    // ---- Méthodes métier ----

    public void crediter(Double montant) {
        if (montant == null || montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        this.solde += montant;
    }

    public abstract void debiter(Double montant);

    public abstract TypeCompte getTypeCompte();
}
