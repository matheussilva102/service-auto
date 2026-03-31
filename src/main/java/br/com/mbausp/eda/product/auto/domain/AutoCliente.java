package br.com.mbausp.eda.product.auto.domain;

import java.time.LocalDateTime;

public record AutoCliente(Integer id, Integer contractNumber, Integer seqContract, String idCliente, LocalDateTime dataCriacao, boolean flAtivo, LocalDateTime dataAlteracao, AutoStatus status) {

}