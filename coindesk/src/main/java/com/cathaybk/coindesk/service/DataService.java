package com.cathaybk.coindesk.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.cathaybk.coindesk.dao.CurrencyDao;
import com.cathaybk.coindesk.dao.RecordRateDao;
import com.cathaybk.coindesk.entity.CurrencyEntity;
import com.cathaybk.coindesk.entity.RecordRateEntity;
import com.cathaybk.coindesk.form.UpdateRequest;
import com.cathaybk.coindesk.model.BitcoinData;
import com.cathaybk.coindesk.model.CurrencyData;
import com.cathaybk.coindesk.model.Message;
import com.cathaybk.coindesk.model.QueryModel;
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
	public BitcoinData transJsonToModel() throws JsonMappingException, JsonProcessingException {
		String jsonData = getCoinDeskData();
		ObjectMapper objectMapper = new ObjectMapper();
		BitcoinData result = objectMapper.readValue(jsonData, BitcoinData.class);
		return result;
	}

	// 幣別 DB 維護功能。
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Message insert() throws ParseException, JsonMappingException, JsonProcessingException {
		Message message = new Message();
		BitcoinData rawData = transJsonToModel();
		// 更新時間
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date updateTime = null;
		if (rawData != null) {
			Map<String, String> time = rawData.getTime();
			if (time != null) {
				for (Map.Entry<String, String> entry : time.entrySet()) {
					String key = entry.getKey();
					if (key.equals("updatedISO")) {
						String dateString = entry.getValue();
						updateTime = dateFormat.parse(dateString);
					}
				}
			}

			Map<String, CurrencyData> bpi = rawData.getBpi();
			if (bpi != null) {
				for (Map.Entry<String, CurrencyData> entry : bpi.entrySet()) {
					String key = entry.getKey();
					CurrencyData value = entry.getValue();
					// 若有資料則不新增
					List<CurrencyEntity> existCurrency = currencyDao.findByCurrencyName(key);
					if (existCurrency.isEmpty()) {
						// currency
						CurrencyEntity currency = new CurrencyEntity();
						// 幣別
						currency.setCurrencyName(key);
						// 幣別說明
						currency.setDescription(value.getDescription());
						// 幣別中文
						if (key.equals("USD")) {
							currency.setCurrecnyChiName("美元");
						} else if (key.equals("GBP")) {
							currency.setCurrecnyChiName("英鎊");
						} else if (key.equals("EUR")) {
							currency.setCurrecnyChiName("歐元");
						}
						currencyDao.save(currency);

						message.setSuccess(true);
					} else {
						message.setMessage("對應中文資料表已有資料");
					}
					// 幣別與時間為唯一值
					List<RecordRateEntity> existRecord = recordRateDao.findByCurrencyNameAndUpdateTime(key, updateTime);
					if (existRecord.isEmpty()) {
						// record
						RecordRateEntity record = new RecordRateEntity();
						// 幣別
						record.setCurrencyName(key);
						// 匯率
						BigDecimal rate = new BigDecimal(value.getRate().replace(",", ""));
						record.setRate(rate);
						// 更新時間
						record.setUpdateTime(updateTime);
						recordRateDao.save(record);

						message.setSuccess(true);
					}
				}
			}
			// 回傳邏輯判斷
			if (message.getMessage() == null && message.isSuccess() == true) {
				message.setMessage("執行成功");
				return message;
			} else if (message.getMessage() != null && message.isSuccess() == true) {
				return message;
			} else {
				message.setSuccess(false);
				message.setMessage("已有資料請稍後在試");
				return message;
			}
		}
		return null;
	}

	/**
	 * 更新資料、手動新增資料
	 * @param UpdateRequest
	 * @param currencyName 英文幣別
	 * @param rate 匯率，格式(小數點後四):31228.1943	
	 * @param updateTimeString 時間格式: "2023-11-20 11:07:00"
	 * @param currecnyChiName 中文幣別
	 * @param description 幣別說明
	 * @return Message
	 * @throws ParseException
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Message update(UpdateRequest updateRequest) throws ParseException {
		Message message = new Message();
		if(updateRequest != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date updateTime = dateFormat.parse(updateRequest.getUpdateTimeString());
			List<RecordRateEntity> existRecord = recordRateDao.findByCurrencyNameAndUpdateTime(updateRequest.getCurrencyName(), updateTime);
			if(!existRecord.isEmpty()) {
				existRecord.get(0).setRate(updateRequest.getRate());
				recordRateDao.save(existRecord.get(0));
				message.setSuccess(true);
				message.setMessage("修改匯率資料表成功");
				return message;
			}else {
				RecordRateEntity insertRecordEntity = new RecordRateEntity();
				insertRecordEntity.setRate(updateRequest.getRate());
				insertRecordEntity.setCurrencyName(updateRequest.getCurrencyName());
				insertRecordEntity.setUpdateTime(updateTime);
				recordRateDao.save(insertRecordEntity);
				
				List<CurrencyEntity> existCurrency = currencyDao.findByCurrencyName(updateRequest.getCurrencyName());
				if(existCurrency.isEmpty()) {
					CurrencyEntity insetCurrencyEntity = new CurrencyEntity();
					insetCurrencyEntity.setCurrencyName(updateRequest.getCurrencyName());
					insetCurrencyEntity.setCurrecnyChiName(updateRequest.getCurrecnyChiName());
					insetCurrencyEntity.setDescription(updateRequest.getDescription());
					currencyDao.save(insetCurrencyEntity);
				}
				
				message.setSuccess(true);
				message.setMessage("手動新增成功");
				return message;
			}
		}
		return null;
		
	}

	/**
	 * 刪除匯率紀錄
	 * @param currencyName 英文幣別
	 * @param updateTimeString 時間格式: "2023-11-20 11:07:00"
	 * @return Message
	 * @throws ParseException
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Message deleteRecordRate(String currencyName, String updateTimeString) throws ParseException {
		Message message = new Message();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			// 依據使用者輸入
			if(currencyName != null && updateTimeString != null) {
				Date updateTime = dateFormat.parse(updateTimeString);
				recordRateDao.deleteByCurrencyNameAndUpdateTime(currencyName, updateTime);
				message.setSuccess(true);
				message.setMessage("刪除成功");				
			}else if(currencyName != null && updateTimeString == null) {
				recordRateDao.deleteByCurrencyName(currencyName);
				message.setSuccess(true);
				message.setMessage("刪除成功");	
			}else if(currencyName == null && updateTimeString != null) {
				Date updateTime = dateFormat.parse(updateTimeString);
				recordRateDao.deleteByUpdateTime(updateTime);
				message.setSuccess(true);
				message.setMessage("刪除成功");		
			}else {
				message.setSuccess(false);
				message.setMessage("請輸入參數");
			}
		} catch (Exception e) {
			message.setSuccess(false);
			message.setMessage("刪除失敗");
		}
		return message;
	}

	public  List<QueryModel> read() throws ParseException {
		List<QueryModel> result = new ArrayList<QueryModel>();
		List<Object[]> getRawData = recordRateDao.findRecordsWithLeftJoin();
		
		if(getRawData != null && getRawData.size() > 0) {
			for(Object[] object : getRawData) {
				QueryModel model = new QueryModel();
				model.setCurrencyName(object[0].toString());
				model.setCurrecnyChiName(object[1].toString());
				model.setRate(new BigDecimal(object[2].toString()));
				
				// 資料庫轉出資料為 2023-11-19 14:56:00.0
				String updateTimeString = object[3].toString();
				 if (updateTimeString.endsWith(".0")) {
	                updateTimeString = updateTimeString.substring(0, updateTimeString.length() - 2);
	                model.setUpdateTime(updateTimeString);
	            }else {
	            	model.setUpdateTime(updateTimeString);
	            }
				 
				result.add(model);
			}
		}
		return result;
		
	}

}
