package com.ega.ega.service;

import com.ega.ega.exception.BusinessException;
import com.ega.ega.exception.ResourceNotFoundException;
import com.ega.ega.model.*;
import com.ega.ega.repository.ClientRepository;
import com.ega.ega.repository.CompteBancaireRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompteBancaireService {

    private final CompteBancaireRepository compteRepository;
    private final ClientRepository clientRepository;

    /**
     * Create a checking account (compte courant)
     */
    @Transactional
    public CompteCourant creerCompteCourant(Long clientId, Double decouvertAutorise) {
        log.info("Creating checking account for client ID: {}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> {
                    log.error("Client not found with ID: {}", clientId);
                    return new ResourceNotFoundException("Client", "id", clientId);
                });

        CompteCourant compte = new CompteCourant();
        compte.setProprietaire(client);
        compte.setNumeroCompte(genererNumeroCompte());
        compte.setDecouvertAutorise(decouvertAutorise != null ? decouvertAutorise : 500.0);
        compte.setFraisTenue(5.0); // ✅ AJOUTÉ
        compte.setPlafondRetrait(1000.0); // ✅ AJOUTÉ
        compte.setSolde(0.0);

        CompteCourant saved = compteRepository.save(compte);
        log.info("Checking account created successfully: {}", saved.getNumeroCompte());
        return saved;
    }

    /**
     * Create a checking account with custom parameters
     */
    @Transactional
    public CompteCourant creerCompteCourantPersonnalise(Long clientId, Double decouvertAutorise,
                                                        Double fraisTenue, Double plafondRetrait) {
        log.info("Creating customized checking account for client ID: {}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> {
                    log.error("Client not found with ID: {}", clientId);
                    return new ResourceNotFoundException("Client", "id", clientId);
                });

        CompteCourant compte = new CompteCourant();
        compte.setProprietaire(client);
        compte.setNumeroCompte(genererNumeroCompte());
        compte.setDecouvertAutorise(decouvertAutorise != null ? decouvertAutorise : 500.0);
        compte.setFraisTenue(fraisTenue != null ? fraisTenue : 5.0);
        compte.setPlafondRetrait(plafondRetrait != null ? plafondRetrait : 1000.0);
        compte.setSolde(0.0);

        CompteCourant saved = compteRepository.save(compte);
        log.info("Customized checking account created successfully: {}", saved.getNumeroCompte());
        return saved;
    }

    /**
     * Create a savings account (compte épargne)
     */
    @Transactional
    public CompteEpargne creerCompteEpargne(Long clientId, Double tauxInteret) {
        log.info("Creating savings account for client ID: {}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> {
                    log.error("Client not found with ID: {}", clientId);
                    return new ResourceNotFoundException("Client", "id", clientId);
                });

        CompteEpargne compte = new CompteEpargne();
        compte.setProprietaire(client);
        compte.setNumeroCompte(genererNumeroCompte());
        compte.setTauxInteret(tauxInteret != null ? tauxInteret : 2.5);
        compte.setSolde(0.0);

        CompteEpargne saved = compteRepository.save(compte);
        log.info("Savings account created successfully: {}", saved.getNumeroCompte());
        return saved;
    }

    /**
     * Get account by ID
     */
    @Transactional(readOnly = true)
    public CompteBancaire obtenirCompte(Long id) {
        log.debug("Fetching account with ID: {}", id);
        return compteRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Account not found with ID: {}", id);
                    return new ResourceNotFoundException("Compte", "id", id);
                });
    }

    /**
     * Search by account number
     */
    @Transactional(readOnly = true)
    public CompteBancaire rechercherParNumero(String numeroCompte) {
        log.debug("Searching account by number: {}", numeroCompte);
        return compteRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> {
                    log.error("Account not found with number: {}", numeroCompte);
                    return new ResourceNotFoundException("Compte", "numeroCompte", numeroCompte);
                });
    }

    /**
     * List all accounts
     */
    @Transactional(readOnly = true)
    public List<CompteBancaire> listerComptes() {
        log.debug("Fetching all accounts");
        return compteRepository.findAll();
    }

    /**
     * List client's accounts
     */
    @Transactional(readOnly = true)
    public List<CompteBancaire> listerComptesClient(Long clientId) {
        log.debug("Fetching accounts for client ID: {}", clientId);

        // Verify client exists
        if (!clientRepository.existsById(clientId)) {
            log.error("Client not found with ID: {}", clientId);
            throw new ResourceNotFoundException("Client", "id", clientId);
        }

        return compteRepository.findByProprietaireId(clientId);
    }

    /**
     * Credit an account
     */
    @Transactional
    public CompteBancaire crediterCompte(Long compteId, Double montant) {
        log.info("Crediting account ID: {} with amount: {}", compteId, montant);

        if (montant == null || montant <= 0) {
            log.error("Invalid credit amount: {}", montant);
            throw new BusinessException("Le montant doit être positif");
        }

        CompteBancaire compte = obtenirCompte(compteId);
        compte.crediter(montant);

        CompteBancaire updated = compteRepository.save(compte);
        log.info("Account credited successfully. New balance: {}", updated.getSolde());
        return updated;
    }

    /**
     * Debit an account
     */
    @Transactional
    public CompteBancaire debiterCompte(Long compteId, Double montant) {
        log.info("Debiting account ID: {} with amount: {}", compteId, montant);

        if (montant == null || montant <= 0) {
            log.error("Invalid debit amount: {}", montant);
            throw new BusinessException("Le montant doit être positif");
        }

        CompteBancaire compte = obtenirCompte(compteId);

        try {
            compte.debiter(montant);
        } catch (IllegalArgumentException e) {
            log.error("Debit failed for account {}: {}", compteId, e.getMessage());
            throw new BusinessException(e.getMessage());
        }

        CompteBancaire updated = compteRepository.save(compte);
        log.info("Account debited successfully. New balance: {}", updated.getSolde());
        return updated;
    }

    /**
     * Check balance
     */
    @Transactional(readOnly = true)
    public Double consulterSolde(Long compteId) {
        log.debug("Checking balance for account ID: {}", compteId);
        return obtenirCompte(compteId).getSolde();
    }

    /**
     * Delete an account
     */
    @Transactional
    public void supprimerCompte(Long compteId) {
        log.info("Deleting account ID: {}", compteId);

        CompteBancaire compte = obtenirCompte(compteId);

        if (compte.getSolde() != null && compte.getSolde() != 0) {
            log.warn("Attempt to delete account with non-zero balance: {}", compteId);
            throw new BusinessException("Impossible de supprimer un compte avec un solde non nul. Solde actuel: " + compte.getSolde());
        }

        compteRepository.deleteById(compteId);
        log.info("Account deleted successfully: {}", compteId);
    }

    /**
     * Generate unique account number
     */
    private String genererNumeroCompte() {
        String numero;
        do {
            numero = "CPT-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        } while (compteRepository.existsByNumeroCompte(numero));

        log.debug("Generated account number: {}", numero);
        return numero;
    }

    /**
     * Check if account exists
     */
    @Transactional(readOnly = true)
    public boolean accountExists(Long id) {
        return compteRepository.existsById(id);
    }

    /**
     * Count total accounts
     */
    @Transactional(readOnly = true)
    public long countAccounts() {
        return compteRepository.count();
    }
}