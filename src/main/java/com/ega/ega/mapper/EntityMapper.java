package com.ega.ega.mapper;

import com.ega.ega.dto.*;
import com.ega.ega.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityMapper {

    // ==================== CLIENT ====================

    public ClientDTO toClientDTO(Client client) {
        if (client == null) return null;

        return ClientDTO.builder()
                .id(client.getId())
                .nom(client.getNom())
                .prenom(client.getPrenom())
                .dateNaissance(client.getDateNaissance())
                .sexe(client.getSexe())
                .adresse(client.getAdresse())
                .telephone(client.getTelephone())
                .courriel(client.getCourriel())
                .nationalite(client.getNationalite())
                .dateCreation(client.getDateCreation())
                .comptes(client.getComptes() != null ?
                        client.getComptes().stream()
                                .map(this::toCompteDTO)
                                .collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }

    // ==================== COMPTE ====================

    public CompteDTO toCompteDTO(CompteBancaire compte) {
        if (compte == null) return null;

        return CompteDTO.builder()
                .id(compte.getId())
                .numeroCompte(compte.getNumeroCompte())
                .typeCompte(compte.getTypeCompte())
                .dateCreation(compte.getDateCreation())
                .solde(compte.getSolde())
                .proprietaireId(compte.getProprietaire() != null ?
                        compte.getProprietaire().getId() : null)
                .nomCompletProprietaire(compte.getProprietaire() != null ?
                        compte.getProprietaire().getNom() + " " +
                                compte.getProprietaire().getPrenom() : null)
                .build();
    }

    public CompteDetailDTO toCompteDetailDTO(CompteBancaire compte) {
        if (compte == null) return null;

        List<Transaction> allTransactions = new ArrayList<>();
        if (compte.getTransactionsEmises() != null) {
            allTransactions.addAll(compte.getTransactionsEmises());
        }
        if (compte.getTransactionsRecues() != null) {
            allTransactions.addAll(compte.getTransactionsRecues());
        }

        return CompteDetailDTO.builder()
                .id(compte.getId())
                .numeroCompte(compte.getNumeroCompte())
                .typeCompte(compte.getTypeCompte())
                .dateCreation(compte.getDateCreation())
                .solde(compte.getSolde())
                .proprietaireId(compte.getProprietaire() != null ?
                        compte.getProprietaire().getId() : null)
                .nomCompletProprietaire(compte.getProprietaire() != null ?
                        compte.getProprietaire().getNom() + " " +
                                compte.getProprietaire().getPrenom() : null)
                .transactions(allTransactions.stream()
                        .map(this::toTransactionDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    // ==================== TRANSACTION ====================

    public TransactionDTO toTransactionDTO(Transaction transaction) {
        if (transaction == null) return null;

        return TransactionDTO.builder()
                .id(transaction.getId())
                .typeTransaction(transaction.getTypeTransaction())
                .montant(transaction.getMontant())
                .dateTransaction(transaction.getDateTransaction())
                .description(transaction.getDescription())
                .numeroCompteSource(transaction.getCompteSource() != null ?
                        transaction.getCompteSource().getNumeroCompte() : null)
                .numeroCompteDestination(transaction.getCompteDestination() != null ?
                        transaction.getCompteDestination().getNumeroCompte() : null)
                .build();
    }

    // ==================== LISTE CONVERSIONS ====================

    public List<ClientDTO> toClientDTOList(List<Client> clients) {
        if (clients == null) return new ArrayList<>();
        return clients.stream()
                .map(this::toClientDTO)
                .collect(Collectors.toList());
    }

    public List<CompteDTO> toCompteDTOList(List<CompteBancaire> comptes) {
        if (comptes == null) return new ArrayList<>();
        return comptes.stream()
                .map(this::toCompteDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> toTransactionDTOList(List<Transaction> transactions) {
        if (transactions == null) return new ArrayList<>();
        return transactions.stream()
                .map(this::toTransactionDTO)
                .collect(Collectors.toList());
    }
}