package com.rafaelleite.mastermind.service;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class GameServiceTest {

	GameService service;
	
	@Before
	public void setUp() throws Exception {
		service = new GameService(null, null, null);
	}
	
	@Test
	public void testCalculateExact() {
		assertEquals(service.calculateResult("1234", "1233").exact, 3);
		assertEquals(service.calculateResult("1234", "0000").exact, 0);
		assertEquals(service.calculateResult("1234", "1").exact, 1);
		assertEquals(service.calculateResult("1234", "1234").exact, 4);
		assertEquals(service.calculateResult("1234", "12345").exact, 4);
	}

	@Test
	public void testCalculateNear() {
		assertEquals(service.calculateResult("1234", "1234").near, 0);
		assertEquals(service.calculateResult("1234", "1233").near, 0);
		assertEquals(service.calculateResult("1234", "1230").near, 0);
		assertEquals(service.calculateResult("1234", "1243").near, 2);
		assertEquals(service.calculateResult("1234", "4321").near, 4);
		assertEquals(service.calculateResult("1234", "133").near, 0);
		assertEquals(service.calculateResult("1234", "12543").near, 1);
	}


}
