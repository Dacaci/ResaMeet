package com.resameet.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RessourceDto {
    private Long id;

    @NotBlank(message = "Le nom est requis")
    private String nom;

    @NotNull(message = "La capacité est requise")
    @Min(value = 1, message = "La capacité doit être au moins 1")
    private Integer capacite;

    @NotBlank(message = "La localisation est requise")
    private String localisation;

    private String description;
}
