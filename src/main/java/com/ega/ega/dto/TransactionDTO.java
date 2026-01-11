package com.ega.ega.dto;

import com.ega.ega.model.TypeTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {
    private Long id;
    private TypeTransaction typeTransaction;
    private Double montant;
    private LocalDateTime dateTransaction;
    private String description;
    private String numeroCompteSource;
    private String numeroCompteDestination;
}
