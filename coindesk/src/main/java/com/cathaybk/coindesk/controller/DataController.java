package com.cathaybk.coindesk.controller;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.cathaybk.coindesk.form.UpdateRequest;
import com.cathaybk.coindesk.model.BitcoinData;
import com.cathaybk.coindesk.model.Message;
import com.cathaybk.coindesk.model.QueryModel;
import com.cathaybk.coindesk.service.DataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Controller
public class DataController {

	@Autowired
	private DataService dataService;

	@GetMapping(value = "/getData", produces = "application/json")
	public ResponseEntity<String> getCoinDeskData() {
		String result = dataService.getCoinDeskData();
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@GetMapping(value = "/transData", produces = "application/json")
	public ResponseEntity<BitcoinData> transJsonToModel() throws JsonMappingException, JsonProcessingException {
		BitcoinData result = dataService.transJsonToModel();
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@DeleteMapping(value = "/deleteRecord", produces = "application/json")
	public ResponseEntity<?> deleteRecord(@RequestParam String currencyName, @RequestParam String updateTimeString)
			throws ParseException {
		Message message = dataService.deleteRecordRate(currencyName, updateTimeString);
		if (message.isSuccess() == true) {
			return new ResponseEntity<>(message, HttpStatus.OK);
		}
		return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping(value = "/insert", produces = "application/json")
	public ResponseEntity<?> insertData() throws ParseException, JsonMappingException, JsonProcessingException {
		Message message = dataService.insert();
		if (message.isSuccess() == true) {
			return new ResponseEntity<>(message, HttpStatus.OK);
		}
		return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping(value = "/read", produces = "application/json")
	public ResponseEntity<List<QueryModel>> readData() throws ParseException {
		List<QueryModel> result = dataService.read();
		if (result != null) {
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@PostMapping(value = "/update", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> updateData(@RequestBody UpdateRequest updateRequest) throws ParseException {
		Message message = dataService.update(updateRequest);
		if (message.isSuccess() == true) {
			return new ResponseEntity<>(message, HttpStatus.OK);
		}
		return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
