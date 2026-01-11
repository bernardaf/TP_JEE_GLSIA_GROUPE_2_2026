package com.ega.ega.repository;

import com.ega.ega.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    // 📧 Recherche par email
    Optional<Client> findByCourriel(String courriel);

    // 📱 Recherche par téléphone
    Optional<Client> findByTelephone(String telephone);

    // ✅ Vérifier si un email existe
    boolean existsByCourriel(String courriel);

    // ✅ Vérifier si un téléphone existe
    boolean existsByTelephone(String telephone);

    // 👤 Recherche par nom et prénom (insensible à la casse)
    @Query("SELECT c FROM Client c WHERE LOWER(c.nom) = LOWER(?1) AND LOWER(c.prenom) = LOWER(?2)")
    List<Client> findByNomAndPrenomIgnoreCase(String nom, String prenom);

    // 🌍 Recherche par nationalité
    List<Client> findByNationalite(String nationalite);

    // 💼 Clients avec comptes
    @Query("SELECT DISTINCT c FROM Client c LEFT JOIN FETCH c.comptes WHERE c.id = ?1")
    Optional<Client> findByIdWithComptes(Long id);

    // 🔍 Recherche par nom OU prénom (contenant, insensible à la casse) - AJOUTÉ
    List<Client> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);

    // 🔍 Alternative avec @Query pour plus de flexibilité - OPTIONNEL
    @Query("SELECT c FROM Client c WHERE LOWER(c.nom) LIKE LOWER(CONCAT('%', :recherche, '%')) OR LOWER(c.prenom) LIKE LOWER(CONCAT('%', :recherche, '%'))")
    List<Client> searchByNomOrPrenom(@Param("recherche") String recherche);
}