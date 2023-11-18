package com.cathaybk.coindesk.controller;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.cathaybk.coindesk.model.BitcoinData;
import com.cathaybk.coindesk.service.DataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Controller
public class DataController {
	
	@Autowired
	private DataService dataService;
	
	@GetMapping("/getData")
	public ResponseEntity<String> getCoinDeskData () {
		String result = dataService.getCoinDeskData();
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@GetMapping("/transData")
	public ResponseEntity<BitcoinData> transJsonToModel () throws JsonMappingException, JsonProcessingException {
		BitcoinData result = dataService.transJsonToModel();
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@GetMapping("/insert")
	public ResponseEntity<?> insertData () throws ParseException, JsonMappingException, JsonProcessingException {
		boolean success = dataService.insert();
		if(success == true) {
			return new ResponseEntity<>(success, HttpStatus.OK);
		}
		return new ResponseEntity<>(success, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
