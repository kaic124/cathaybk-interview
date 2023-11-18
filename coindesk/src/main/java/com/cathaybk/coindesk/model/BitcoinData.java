package com.cathaybk.coindesk.model;

import java.util.Map;

public class BitcoinData {
	private Map<String, String> time;
    private String disclaimer;
    private String chartName;
    private Map<String, CurrencyData> bpi;
    
	public Map<String, String> getTime() {
		return time;
	}
	public void setTime(Map<String, String> time) {
		this.time = time;
	}
	public String getDisclaimer() {
		return disclaimer;
	}
	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}
	public String getChartName() {
		return chartName;
	}
	public void setChartName(String chartName) {
		this.chartName = chartName;
	}
	public Map<String, CurrencyData> getBpi() {
		return bpi;
	}
	public void setBpi(Map<String, CurrencyData> bpi) {
		this.bpi = bpi;
	}
    
    
}
