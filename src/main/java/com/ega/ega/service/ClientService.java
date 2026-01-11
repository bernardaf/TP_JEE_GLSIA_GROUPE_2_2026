package com.ega.ega.service;

import com.ega.ega.exception.ResourceNotFoundException;
import com.ega.ega.exception.DuplicateResourceException;
import com.ega.ega.exception.BusinessException;
import com.ega.ega.model.Client;
import com.ega.ega.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;

    /**
     * Create a new client
     */
    @Transactional
    public Client createClient(Client client) {
        log.info("Creating new client: {}", client.getCourriel());

        // Business validation
        if (clientRepository.existsByCourriel(client.getCourriel())) {
            log.warn("Attempt to create client with existing email: {}", client.getCourriel());
            throw new DuplicateResourceException("Ce courriel est déjà utilisé : " + client.getCourriel());
        }

        if (clientRepository.existsByTelephone(client.getTelephone())) {
            log.warn("Attempt to create client with existing phone: {}", client.getTelephone());
            throw new DuplicateResourceException("Ce téléphone est déjà utilisé : " + client.getTelephone());
        }

        Client savedClient = clientRepository.save(client);
        log.info("Client created successfully with ID: {}", savedClient.getId());
        return savedClient;
    }

    /**
     * Get client by ID
     */
    @Transactional(readOnly = true)
    public Client getClientById(Long id) {
        log.debug("Fetching client with ID: {}", id);
        return clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Client not found with ID: {}", id);
                    return new ResourceNotFoundException("Client", "id", id);
                });
    }

    /**
     * Get client with accounts
     */
    @Transactional(readOnly = true)
    public Client getClientWithAccounts(Long id) {
        log.debug("Fetching client with accounts, ID: {}", id);
        return clientRepository.findByIdWithComptes(id)
                .orElseThrow(() -> {
                    log.error("Client not found with ID: {}", id);
                    return new ResourceNotFoundException("Client", "id", id);
                });
    }

    /**
     * Get all clients
     */
    @Transactional(readOnly = true)
    public List<Client> getAllClients() {
        log.debug("Fetching all clients");
        return clientRepository.findAll();
    }

    /**
     * Search by email
     */
    @Transactional(readOnly = true)
    public Client getClientByEmail(String courriel) {
        log.debug("Searching client by email: {}", courriel);
        return clientRepository.findByCourriel(courriel)
                .orElseThrow(() -> {
                    log.error("Client not found with email: {}", courriel);
                    return new ResourceNotFoundException("Client", "courriel", courriel);
                });
    }

    /**
     * Update a client
     */
    @Transactional
    public Client updateClient(Long id, Client clientModifie) {
        log.info("Updating client with ID: {}", id);

        Client client = getClientById(id);

        // Check email uniqueness (if modified)
        if (!client.getCourriel().equals(clientModifie.getCourriel())) {
            if (clientRepository.existsByCourriel(clientModifie.getCourriel())) {
                log.warn("Attempt to update with existing email: {}", clientModifie.getCourriel());
                throw new DuplicateResourceException("Ce courriel est déjà utilisé : " + clientModifie.getCourriel());
            }
        }

        // Check phone uniqueness (if modified)
        if (!client.getTelephone().equals(clientModifie.getTelephone())) {
            if (clientRepository.existsByTelephone(clientModifie.getTelephone())) {
                log.warn("Attempt to update with existing phone: {}", clientModifie.getTelephone());
                throw new DuplicateResourceException("Ce téléphone est déjà utilisé : " + clientModifie.getTelephone());
            }
        }

        // Update fields
        client.setNom(clientModifie.getNom());
        client.setPrenom(clientModifie.getPrenom());
        client.setDateNaissance(clientModifie.getDateNaissance());
        client.setSexe(clientModifie.getSexe());
        client.setAdresse(clientModifie.getAdresse());
        client.setTelephone(clientModifie.getTelephone());
        client.setCourriel(clientModifie.getCourriel());
        client.setNationalite(clientModifie.getNationalite());

        Client updated = clientRepository.save(client);
        log.info("Client updated successfully: {}", updated.getId());
        return updated;
    }

    /**
     * Delete a client
     */
    @Transactional
    public void deleteClient(Long id) {
        log.info("Deleting client with ID: {}", id);

        Client client = getClientById(id);

        // Check that client has no active accounts
        if (client.getComptes() != null && !client.getComptes().isEmpty()) {
            log.warn("Attempt to delete client with active accounts: {}", id);
            throw new BusinessException("Impossible de supprimer un client avec des comptes actifs. Veuillez d'abord fermer tous les comptes.");
        }

        clientRepository.deleteById(id);
        log.info("Client deleted successfully: {}", id);
    }

    /**
     * Search by nationality
     */
    @Transactional(readOnly = true)
    public List<Client> getClientsByNationality(String nationalite) {
        log.debug("Searching clients by nationality: {}", nationalite);
        return clientRepository.findByNationalite(nationalite);
    }

    /**
     * Check if client exists
     */
    @Transactional(readOnly = true)
    public boolean clientExists(Long id) {
        return clientRepository.existsById(id);
    }

    /**
     * Search by name or first name
     */
    @Transactional(readOnly = true)
    public List<Client> searchClientsByNameOrFirstName(String recherche) {
        log.debug("Searching clients by name or first name: {}", recherche);
        return clientRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(recherche, recherche);
    }

    /**
     * Count total clients
     */
    @Transactional(readOnly = true)
    public long countClients() {
        return clientRepository.count();
    }
}
