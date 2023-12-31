package com.cathaybk.coindesk.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cathaybk.coindesk.entity.CurrencyEntity;

@Repository
public interface CurrencyDao extends CrudRepository<CurrencyEntity, Integer>{

	List<CurrencyEntity> findByCurrencyName(String currencyName);
	
}
