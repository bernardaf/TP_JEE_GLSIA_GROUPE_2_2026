package com.ega.ega.repository;

import com.ega.ega.model.CompteBancaire;
import com.ega.ega.model.TypeCompte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompteBancaireRepository extends JpaRepository<CompteBancaire, Long> {

    // Recherche par numéro de compte
    Optional<CompteBancaire> findByNumeroCompte(String numeroCompte);

    // Vérifier si un numéro de compte existe
    boolean existsByNumeroCompte(String numeroCompte);

    // Tous les comptes d'un client
    List<CompteBancaire> findByProprietaireId(Long proprietaireId);

    // Comptes par type
    @Query("SELECT c FROM CompteBancaire c WHERE TYPE(c) = ?1")
    List<CompteBancaire> findByTypeCompte(Class<?> typeCompte);

    // Comptes avec solde supérieur à un montant
    @Query("SELECT c FROM CompteBancaire c WHERE c.solde >= ?1")
    List<CompteBancaire> findComptesAvecSoldeMinimum(Double montantMinimum);

    // Compte avec transactions
    @Query("SELECT c FROM CompteBancaire c LEFT JOIN FETCH c.transactionsEmises WHERE c.id = ?1")
    Optional<CompteBancaire> findByIdWithTransactions(Long id);
}
