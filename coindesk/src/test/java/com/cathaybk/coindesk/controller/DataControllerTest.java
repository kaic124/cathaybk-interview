package com.cathaybk.coindesk.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
public class DataControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void getCoinDeskData() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.get("/getData");
		MvcResult mvcResult = mockMvc.perform(requestBuilder)
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		
		String body = mvcResult.getResponse().getContentAsString();
		System.out.println("呼叫 coindesk API 回傳結果為:" + body);
	}

	@Test
	public void transJsonToModel() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.get("/transData");
		MvcResult mvcResult = mockMvc.perform(requestBuilder)
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		
		String body = mvcResult.getResponse().getContentAsString();
		System.out.println("資料轉換回傳結果為:" + body);
	}

	@Test
	@Transactional
	public void insertData() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.get("/insert");
		
		mockMvc.perform(requestBuilder)
				.andDo(print())
				.andExpect(status().isOk());
		
	}

	@Test
	@Transactional
	public void deleteRecord() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.delete("/deleteRecord")
				.param("currencyName", "GBP");
		
		mockMvc.perform(requestBuilder)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	public void readData() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.get("/read");
		MvcResult mvcResult = mockMvc.perform(requestBuilder)
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		
		String body = mvcResult.getResponse().getContentAsString();
		System.out.println("查詢回傳結果為:" + body);
	}

	@Test
	@Transactional
	public void updateData() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post("/update")
				.contentType(MediaType.APPLICATION_JSON)
				// 1. currencyName 與 updateTimeString 要找到唯一值，匯率表才會更新(須改當下測試時間)
				.content("{\r\n"
						+ "    \"currencyName\":\"EUR\",\r\n"
						+ "    \"rate\":\"31111.1111\",\r\n"
						+ "    \"updateTimeString\":\"2023-11-20 15:23:00\",\r\n"
						+ "    \"currecnyChiName\":\"歐元\",\r\n"
						+ "    \"description\":\"Euro\"\r\n"
						+ "}");
		
				 // 2. 若沒有找到唯一值則會新增匯率資料與時間，中文對照若英文currencyName重複，則不更新對照表DB
				
		MvcResult mvcResult = mockMvc.perform(requestBuilder)
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		
		String body = mvcResult.getResponse().getContentAsString();
		System.out.println("資料更新回傳結果為:" + body);
	}

}
