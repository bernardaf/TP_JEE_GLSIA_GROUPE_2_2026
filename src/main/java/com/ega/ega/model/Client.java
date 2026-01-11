package com.ega.ega.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false, length = 100)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Column(nullable = false, length = 100)
    private String prenom;

    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être dans le passé")
    @Column(nullable = false)
    private LocalDate dateNaissance;

    @NotBlank(message = "Le sexe est obligatoire")
    @Pattern(regexp = "M|F|Autre", message = "Le sexe doit être M, F ou Autre")
    @Column(nullable = false, length = 10)
    private String sexe;

    @NotBlank(message = "L'adresse est obligatoire")
    @Column(nullable = false, length = 255)
    private String adresse;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Format de téléphone invalide")
    @Column(nullable = false, unique = true, length = 20)
    private String telephone;

    @NotBlank(message = "Le courriel est obligatoire")
    @Email(message = "Format d'email invalide")
    @Column(nullable = false, unique = true, length = 100)
    private String courriel;

    @NotBlank(message = "La nationalité est obligatoire")
    @Column(nullable = false, length = 50)
    private String nationalite;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @OneToMany(mappedBy = "proprietaire", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CompteBancaire> comptes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}