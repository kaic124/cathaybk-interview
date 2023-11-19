package com.cathaybk.coindesk.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cathaybk.coindesk.entity.CurrencyEntity;

@Repository
public interface CurrencyDao extends CrudRepository<CurrencyEntity, Integer>{

	CurrencyEntity findByCurrencyName(String currencyName);
	
	@Transactional
	void deleteByCurrencyName(String currencyName);
}
