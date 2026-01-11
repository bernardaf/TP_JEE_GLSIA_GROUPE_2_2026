package com.ega.ega.dto;

import com.ega.ega.model.TypeCompte;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompteDetailDTO {
    private Long id;
    private String numeroCompte;
    private TypeCompte typeCompte;
    private LocalDateTime dateCreation;
    private Double solde;
    private Long proprietaireId;
    private String nomCompletProprietaire;
    private List<TransactionDTO> transactions;
}
