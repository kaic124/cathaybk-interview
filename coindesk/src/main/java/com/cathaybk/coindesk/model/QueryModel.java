package com.cathaybk.coindesk.model;

import java.math.BigDecimal;

public class QueryModel {

	String currencyName;
	String currecnyChiName;
	BigDecimal rate;
	String updateTime;
	
	public String getCurrencyName() {
		return currencyName;
	}
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
	public String getCurrecnyChiName() {
		return currecnyChiName;
	}
	public void setCurrecnyChiName(String currecnyChiName) {
		this.currecnyChiName = currecnyChiName;
	}
	public BigDecimal getRate() {
		return rate;
	}
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

}
