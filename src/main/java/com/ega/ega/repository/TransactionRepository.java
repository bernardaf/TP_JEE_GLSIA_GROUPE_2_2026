package com.ega.ega.repository;

import com.ega.ega.model.Transaction;
import com.ega.ega.model.TypeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 🔎 Recherche par référence
    Optional<Transaction> findByReference(String reference);

    // 📤 Transactions d'un compte source
    List<Transaction> findByCompteSourceId(Long compteSourceId);

    // 📥 Transactions d'un compte destination
    List<Transaction> findByCompteDestinationId(Long compteDestinationId);

    // 📊 Transactions par type
    List<Transaction> findByTypeTransaction(TypeTransaction typeTransaction);

    // 📅 Transactions entre deux dates
    @Query("SELECT t FROM Transaction t WHERE t.dateTransaction BETWEEN ?1 AND ?2")
    List<Transaction> findByDateTransactionBetween(LocalDateTime dateDebut, LocalDateTime dateFin);

    // 📂 Historique complet d'un compte (émises + reçues)
    @Query("SELECT t FROM Transaction t WHERE t.compteSource.id = ?1 OR t.compteDestination.id = ?1 ORDER BY t.dateTransaction DESC")
    List<Transaction> findHistoriqueCompte(Long compteId);

    // 💰 Total des transactions par type pour un compte
    @Query("SELECT SUM(t.montant) FROM Transaction t WHERE t.compteSource.id = ?1 AND t.typeTransaction = ?2")
    Double calculerTotalParType(Long compteId, TypeTransaction typeTransaction);

    // 🔢 Compter les transactions d'un compte (AJOUTÉ)
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.compteSource.id = :compteId OR t.compteDestination.id = :compteId")
    long countByCompteId(@Param("compteId") Long compteId);

    // 💰 Somme des montants par type et compte (AJOUTÉ)
    @Query("""
        SELECT SUM(t.montant)
        FROM Transaction t
        WHERE (t.compteSource.id = :compteId OR t.compteDestination.id = :compteId)
          AND t.typeTransaction = :type
    """)
    Double sumMontantByCompteIdAndType(@Param("compteId") Long compteId,
                                       @Param("type") TypeTransaction type);
}