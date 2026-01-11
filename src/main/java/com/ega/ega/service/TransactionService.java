package com.ega.ega.service;

import com.ega.ega.model.CompteBancaire;
import com.ega.ega.model.Transaction;
import com.ega.ega.model.TypeTransaction;
import com.ega.ega.repository.CompteBancaireRepository;
import com.ega.ega.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CompteBancaireRepository compteRepository;

    /**
     * ➕ EFFECTUER UN VERSEMENT
     */
    public Transaction effectuerVersement(Long compteId, Double montant, String description) {
        // Validation
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }

        CompteBancaire compte = compteRepository.findById(compteId)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé avec l'ID: " + compteId));

        // Créer la transaction
        Transaction transaction = Transaction.builder()
                .reference(genererReference())
                .montant(montant)
                .typeTransaction(TypeTransaction.VERSEMENT)
                .compteDestination(compte)
                .dateTransaction(LocalDateTime.now())
                .description(description != null ? description : "Versement")
                .build();

        // Mettre à jour le solde
        compte.setSolde(compte.getSolde() + montant);
        compteRepository.save(compte);

        return transactionRepository.save(transaction);
    }

    /**
     * ➖ EFFECTUER UN RETRAIT
     */
    public Transaction effectuerRetrait(Long compteId, Double montant, String description) {
        // Validation
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }

        CompteBancaire compte = compteRepository.findById(compteId)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé avec l'ID: " + compteId));

        // Vérifier le solde
        if (compte.getSolde() < montant) {
            throw new RuntimeException("Solde insuffisant. Solde actuel: " + compte.getSolde());
        }

        // Créer la transaction
        Transaction transaction = Transaction.builder()
                .reference(genererReference())
                .montant(montant)
                .typeTransaction(TypeTransaction.RETRAIT)
                .compteSource(compte)
                .dateTransaction(LocalDateTime.now())
                .description(description != null ? description : "Retrait")
                .build();

        // Mettre à jour le solde
        compte.setSolde(compte.getSolde() - montant);
        compteRepository.save(compte);

        return transactionRepository.save(transaction);
    }

    /**
     * 🔁 EFFECTUER UN VIREMENT
     */
    public Transaction effectuerVirement(Long sourceId, Long destinationId,
                                         Double montant, String description) {
        // Validation
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }

        if (sourceId.equals(destinationId)) {
            throw new IllegalArgumentException("Les comptes source et destination doivent être différents");
        }

        CompteBancaire source = compteRepository.findById(sourceId)
                .orElseThrow(() -> new RuntimeException("Compte source non trouvé avec l'ID: " + sourceId));

        CompteBancaire destination = compteRepository.findById(destinationId)
                .orElseThrow(() -> new RuntimeException("Compte destination non trouvé avec l'ID: " + destinationId));

        // Vérifier le solde
        if (source.getSolde() < montant) {
            throw new RuntimeException("Solde insuffisant. Solde actuel: " + source.getSolde());
        }

        // Créer la transaction
        Transaction transaction = Transaction.builder()
                .reference(genererReference())
                .montant(montant)
                .typeTransaction(TypeTransaction.VIREMENT)
                .compteSource(source)
                .compteDestination(destination)
                .dateTransaction(LocalDateTime.now())
                .description(description != null ? description : "Virement")
                .build();

        // Mettre à jour les soldes
        source.setSolde(source.getSolde() - montant);
        destination.setSolde(destination.getSolde() + montant);

        compteRepository.save(source);
        compteRepository.save(destination);

        return transactionRepository.save(transaction);
    }

    /**
     * 📄 CONSULTER L'HISTORIQUE D'UN COMPTE
     */
    public List<Transaction> consulterHistorique(Long compteId) {
        if (!compteRepository.existsById(compteId)) {
            throw new RuntimeException("Compte non trouvé avec l'ID: " + compteId);
        }
        return transactionRepository.findHistoriqueCompte(compteId);
    }

    /**
     * 📋 LISTER TOUTES LES TRANSACTIONS
     */
    public List<Transaction> listerTransactions() {
        return transactionRepository.findAll();
    }

    /**
     * 🔍 OBTENIR UNE TRANSACTION PAR ID
     */
    public Transaction obtenirTransaction(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction non trouvée avec l'ID: " + id));
    }

    /**
     * 🔍 RECHERCHER PAR RÉFÉRENCE
     */
    public Transaction rechercherParReference(String reference) {
        return transactionRepository.findByReference(reference)
                .orElseThrow(() -> new RuntimeException("Transaction non trouvée avec la référence: " + reference));
    }

    /**
     * 📅 RECHERCHER PAR PÉRIODE
     */
    public List<Transaction> rechercherParPeriode(LocalDateTime debut, LocalDateTime fin) {
        if (debut.isAfter(fin)) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }
        return transactionRepository.findByDateTransactionBetween(debut, fin);
    }

    /**
     * 🔢 GÉNÉRER UNE RÉFÉRENCE UNIQUE
     */
    private String genererReference() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 📊 COMPTER LES TRANSACTIONS D'UN COMPTE
     */
    public long compterTransactions(Long compteId) {
        return transactionRepository.countByCompteId(compteId);
    }

    /**
     * 💰 CALCULER LE TOTAL PAR TYPE
     */
    public Double calculerTotal(Long compteId, TypeTransaction type) {
        Double total = transactionRepository.sumMontantByCompteIdAndType(compteId, type);
        return total != null ? total : 0.0;
    }
}