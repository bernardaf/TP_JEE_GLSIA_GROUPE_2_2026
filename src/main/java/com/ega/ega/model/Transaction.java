package com.ega.ega.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_date_transaction", columnList = "dateTransaction"),
        @Index(name = "idx_compte_source", columnList = "compte_source_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeTransaction typeTransaction;

    @NotNull(message = "Le montant est obligatoire")
    @Min(value = 0, message = "Le montant doit être positif")
    @Column(nullable = false)
    private Double montant;

    @Column(nullable = false)
    private LocalDateTime dateTransaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compte_source_id", nullable = false)
    private CompteBancaire compteSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compte_destination_id")
    private CompteBancaire compteDestination;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, unique = true, length = 50)
    private String reference;

    @PrePersist
    protected void onCreate() {
        dateTransaction = LocalDateTime.now();
        if (reference == null) {
            reference = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}

