package com.cathaybk.coindesk.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.cathaybk.coindesk.dao.CurrencyDao;
import com.cathaybk.coindesk.dao.RecordRateDao;
import com.cathaybk.coindesk.entity.CurrencyEntity;
import com.cathaybk.coindesk.entity.RecordRateEntity;
import com.cathaybk.coindesk.model.BitcoinData;
import com.cathaybk.coindesk.model.CurrencyData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DataService {

	@Autowired
	private CurrencyDao currencyDao;
	@Autowired
	private RecordRateDao recordRateDao;
	
	@Value("${coindesk.url}")
    private String coindeskUrl;

	// 呼叫 coindesk 的 API
	public String getCoinDeskData() {
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(coindeskUrl, String.class);
        return result;
    }
	
	// 呼叫 coindesk 的 API，並進行資料轉換，組成新 API
	public BitcoinData transJsonToModel () throws JsonMappingException, JsonProcessingException {
		String jsonData = getCoinDeskData();
		ObjectMapper objectMapper = new ObjectMapper();
		BitcoinData result = objectMapper.readValue(jsonData, BitcoinData.class);
		return result;
	}
	
	// 幣別 DB 維護功能。
	@Transactional
	public boolean insert () throws ParseException, JsonMappingException, JsonProcessingException {
		
		boolean success = false;
		
		BitcoinData rawData = transJsonToModel();
		// 更新時間
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date updateTime = new Date();
		if(rawData != null) {
			Map<String, String> time = rawData.getTime();
			if(time != null) {
				for (Map.Entry<String, String> entry : time.entrySet()) {
					String key = entry.getKey();
					if(key.equals("updatedISO")) {
						String dateString = entry.getValue();
						updateTime = dateFormat.parse(dateString);
					}
				}
			}
			
			Map<String, CurrencyData> bpi = rawData.getBpi();
			if(bpi != null) {
				for (Map.Entry<String, CurrencyData> entry : bpi.entrySet()) {
					String key = entry.getKey();
					CurrencyData value = entry.getValue();
					// currency
					CurrencyEntity currency = new CurrencyEntity();
					// 幣別
					currency.setCurrencyName(key);
					// 幣別說明
					currency.setDescription(value.getDescription());
					// 幣別中文
					if(key.equals("USD")) {
						currency.setCurrecnyChiName("美元");
					}else if(key.equals("GBP")) {
						currency.setCurrecnyChiName("英鎊");
					}else if(key.equals("EUR")){
						currency.setCurrecnyChiName("歐元");
					}
					CurrencyEntity currencyResult = currencyDao.save(currency);
					
					// record
					RecordRateEntity record = new RecordRateEntity();
					// 幣別
					record.setCurrencyName(key);
					// 匯率
					BigDecimal rate = new BigDecimal(value.getRate().replace(",", ""));
					record.setRate(rate);
					// 更新時間
					record.setUpdateTime(updateTime);
					RecordRateEntity recordRateResult = recordRateDao.save(record);
					
					if(currencyResult != null && recordRateResult != null) {
						success = true;
					}
				}
			}
		}
		return success; 
	}
	
	public String update () {
		CurrencyEntity currencyEntity = new CurrencyEntity();
		currencyDao.save(currencyEntity);
		RecordRateEntity recordEntity = new RecordRateEntity();
		recordRateDao.save(recordEntity);
		return "更新"; 
	}
	
	public String delete () {
		CurrencyEntity currencyEntity = new CurrencyEntity();
		currencyDao.deleteById(currencyEntity.getId());
		
		RecordRateEntity recordEntity = new RecordRateEntity();
		recordRateDao.deleteById(recordEntity.getId());
		return "刪除"; 
	}
	
	public String read () {
		CurrencyEntity currencyEntity = new CurrencyEntity();
		currencyDao.findById(currencyEntity.getId());
		
		RecordRateEntity recordEntity = new RecordRateEntity();
		recordRateDao.findById(recordEntity.getId());
		return "查詢"; 
	}
	
}
