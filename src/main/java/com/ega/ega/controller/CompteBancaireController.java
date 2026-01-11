package com.ega.ega.controller;

import com.ega.ega.model.CompteBancaire;
import com.ega.ega.model.CompteCourant;
import com.ega.ega.model.CompteEpargne;
import com.ega.ega.service.CompteBancaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comptes")
@RequiredArgsConstructor
public class CompteBancaireController {

    private final CompteBancaireService compteService;

    // ➕ Créer compte courant
    @PostMapping("/courant/{clientId}")
    public ResponseEntity<CompteCourant> creerCompteCourant(
            @PathVariable Long clientId,
            @RequestParam(required = false) Double decouvert) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(compteService.creerCompteCourant(clientId, decouvert));
    }

    // ➕ Créer compte épargne
    @PostMapping("/epargne/{clientId}")
    public ResponseEntity<CompteEpargne> creerCompteEpargne(
            @PathVariable Long clientId,
            @RequestParam(required = false) Double tauxInteret) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(compteService.creerCompteEpargne(clientId, tauxInteret));
    }

    // 📄 Tous les comptes
    @GetMapping
    public ResponseEntity<List<CompteBancaire>> listerComptes() {
        return ResponseEntity.ok(compteService.listerComptes());
    }

    // 📄 Comptes d’un client
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<CompteBancaire>> comptesClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(compteService.listerComptesClient(clientId));
    }

    // 🔍 Compte par ID
    @GetMapping("/{id}")
    public ResponseEntity<CompteBancaire> obtenirCompte(@PathVariable Long id) {
        return ResponseEntity.ok(compteService.obtenirCompte(id));
    }

    // 🔍 Par numéro
    @GetMapping("/search")
    public ResponseEntity<CompteBancaire> rechercherParNumero(@RequestParam String numero) {
        return ResponseEntity.ok(compteService.rechercherParNumero(numero));
    }

    // 💰 Créditer
    @PostMapping("/{id}/credit")
    public ResponseEntity<CompteBancaire> crediter(
            @PathVariable Long id,
            @RequestParam Double montant) {
        return ResponseEntity.ok(compteService.crediterCompte(id, montant));
    }

    // 💸 Débiter
    @PostMapping("/{id}/debit")
    public ResponseEntity<CompteBancaire> debiter(
            @PathVariable Long id,
            @RequestParam Double montant) {
        return ResponseEntity.ok(compteService.debiterCompte(id, montant));
    }

    // ❌ Supprimer
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerCompte(@PathVariable Long id) {
        compteService.supprimerCompte(id);
        return ResponseEntity.noContent().build();
    }
}

