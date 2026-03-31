package br.com.mbausp.eda.product.auto.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AutoClientePayload(@NotNull Integer contractNumber, @NotNull Integer seqContract, @NotBlank String idCliente) {

}
