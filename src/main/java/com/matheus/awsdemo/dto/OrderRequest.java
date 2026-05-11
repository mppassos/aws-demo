package com.matheus.awsdemo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @NotBlank(message = "Customer ID é obrigatório")
    private String customerId;

    @Positive(message = "Valor deve ser positivo")
    private Double totalAmount;
}