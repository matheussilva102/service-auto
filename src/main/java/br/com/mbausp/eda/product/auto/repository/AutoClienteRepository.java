package br.com.mbausp.eda.product.auto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.mbausp.eda.product.auto.entity.AutoClienteEntity;

public interface AutoClienteRepository extends JpaRepository<AutoClienteEntity, Long> {
	
}
