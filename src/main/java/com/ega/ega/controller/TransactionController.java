package com.ega.ega.controller;

import com.ega.ega.model.Transaction;
import com.ega.ega.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // ➕ Versement
    @PostMapping("/versement")
    public ResponseEntity<Transaction> versement(
            @RequestParam Long compteId,
            @RequestParam Double montant,
            @RequestParam(required = false) String description) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.effectuerVersement(compteId, montant, description));
    }

    // ➖ Retrait
    @PostMapping("/retrait")
    public ResponseEntity<Transaction> retrait(
            @RequestParam Long compteId,
            @RequestParam Double montant,
            @RequestParam(required = false) String description) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.effectuerRetrait(compteId, montant, description));
    }

    // 🔁 Virement
    @PostMapping("/virement")
    public ResponseEntity<Transaction> virement(
            @RequestParam Long sourceId,
            @RequestParam Long destinationId,
            @RequestParam Double montant,
            @RequestParam(required = false) String description) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.effectuerVirement(
                        sourceId, destinationId, montant, description));
    }

    // 📄 Historique d’un compte
    @GetMapping("/compte/{compteId}")
    public ResponseEntity<List<Transaction>> historique(@PathVariable Long compteId) {
        return ResponseEntity.ok(transactionService.consulterHistorique(compteId));
    }

    // 📄 Toutes les transactions
    @GetMapping
    public ResponseEntity<List<Transaction>> listerTransactions() {
        return ResponseEntity.ok(transactionService.listerTransactions());
    }

    // 🔍 Transaction par ID
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> obtenirTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.obtenirTransaction(id));
    }

    // 🔍 Par référence
    @GetMapping("/search")
    public ResponseEntity<Transaction> rechercherParReference(@RequestParam String reference) {
        return ResponseEntity.ok(transactionService.rechercherParReference(reference));
    }

    // 📅 Par période
    @GetMapping("/periode")
    public ResponseEntity<List<Transaction>> rechercherParPeriode(
            @RequestParam LocalDateTime debut,
            @RequestParam LocalDateTime fin) {

        return ResponseEntity.ok(transactionService.rechercherParPeriode(debut, fin));
    }
}

