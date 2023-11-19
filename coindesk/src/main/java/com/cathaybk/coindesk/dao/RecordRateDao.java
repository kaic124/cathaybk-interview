package com.cathaybk.coindesk.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cathaybk.coindesk.entity.RecordRateEntity;

@Repository
public interface RecordRateDao extends CrudRepository<RecordRateEntity, Integer>{

	RecordRateEntity findByCurrencyNameAndUpdateTime(String currencyName, Date updateTime);
	
	@Transactional
	void deleteByCurrencyNameAndUpdateTime(String currencyName, Date updateTime);
	
    @Transactional
    void deleteByCurrencyName(String currencyName);

    @Transactional
    void deleteByUpdateTime(Date updateTime);
	
	@Query("SELECT r.currencyName, c.currecnyChiName, r.rate, r.updateTime " +
	        "FROM RecordRateEntity r " +
	        "LEFT JOIN CurrencyEntity c ON c.currencyName = r.currencyName " +
	        "ORDER BY r.updateTime DESC")
	List<Object[]> findRecordsWithLeftJoin();
	
}
