package com.cathaybk.coindesk.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cathaybk.coindesk.entity.RecordRateEntity;

@Repository
public interface RecordRateDao extends CrudRepository<RecordRateEntity, Integer>{

}
