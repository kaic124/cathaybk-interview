package com.cathaybk.coindesk.form;

import java.math.BigDecimal;

public class UpdateRequest {
	
	private String currencyName;
	private BigDecimal rate;
	private String updateTimeString;
	private String currecnyChiName;
	private String description;
	
	public String getCurrencyName() {
		return currencyName;
	}
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
	public BigDecimal getRate() {
		return rate;
	}
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	public String getUpdateTimeString() {
		return updateTimeString;
	}
	public void setUpdateTimeString(String updateTimeString) {
		this.updateTimeString = updateTimeString;
	}
	public String getCurrecnyChiName() {
		return currecnyChiName;
	}
	public void setCurrecnyChiName(String currecnyChiName) {
		this.currecnyChiName = currecnyChiName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
