package com.rafaelleite.mastermind;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;
import com.rafaelleite.mastermind.domain.Game;
import com.rafaelleite.mastermind.repository.GameRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MasterMindApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class MasterMindApplicationTests {

    private static final String GAME_KEY_ACTIVE = "TESTING_ACTIVE";
    // TODO: implement tests for expired game in the database
    private static final String GAME_KEY_EXPIRED = "TESTING_EXPIRED";
	private static final String USER_NAME = "rafael";
	private static final Character[] OPTIONS = {'R','G','B','Y','O','P','C','M'};

	@Autowired
    GameRepository repository;
	
	Collection<String> optionsStr = Arrays.asList(OPTIONS).stream()
			.map(c -> c.toString())
            .collect(Collectors.toList());
 
    @Value("${local.server.port}")
    int port;
 
    @Before
    public void setUp() {
		String solution = Arrays.asList(OPTIONS).stream()
			.map(i -> i.toString())
			.collect(Collectors.joining(""));

    	// Make sure we have a non expired game in the repository
    	Game game = repository.findByKey(GAME_KEY_ACTIVE);
    	if (game == null) {
    		// The game was not in the database, create it
	    	game = new Game();
	    	game.setKey(GAME_KEY_ACTIVE);
	    	game.setSolution(solution);
	    	game.getOptions().addAll(Arrays.asList(OPTIONS));
	    	game.setUserName(USER_NAME);
    	} else {
    		// If the game was already there, clean it
    		game.setSolved(false);
    		game.getGuesses().clear();
    	}
    	// Either way, set the creation time as now
    	game.setCreatedAt(LocalDateTime.now());
    	repository.save(game);
 
        RestAssured.port = port;
    }
 
    @Test
    public void createGame() {
        ValidatableResponse then = given().
        	contentType(ContentType.JSON).
        	body("{\"user\":\""+USER_NAME+"\"}").
    	when().
    		post("/new_game").
        then();
        then.
        	statusCode(HttpStatus.SC_OK).
        	contentType(ContentType.JSON).
            body("user", is(USER_NAME)).
            body("solved", is(false)).
            body("colors", hasItems(optionsStr.toArray())).
            body("num_guesses", is(0)).
            body("key", notNullValue());
    }
 
    @Test
    public void createGameNoUser() {
    	when().
    		post("/new_game").
    	then().
        	statusCode(HttpStatus.SC_BAD_REQUEST);
    }
 
    @Test
    public void createGameEmptyUser() {
    	given().
    		body("{\"user\":\"\"}").
    	when().
    		post("/new_game").
    	then().
        	statusCode(HttpStatus.SC_BAD_REQUEST);
    }
 
    @Test
    public void makeGuess() {
        String guess = "000000";
		ValidatableResponse then = given().
			contentType(ContentType.JSON).
        	body("{\"code\":\""+ guess +"\", \"game_key\":\""+ GAME_KEY_ACTIVE +"\"}").
    	when().
    		post("/guess").
        then();
        then.
        	statusCode(HttpStatus.SC_OK).
        	contentType(ContentType.JSON).
            body("game_key", is(GAME_KEY_ACTIVE)).
            body("solved", is(false)).
            body("num_guesses", notNullValue()).
            body("past_results", notNullValue()).
            body("guess", is(guess)).
            body("colors", hasItems(optionsStr.toArray())).
            body("code_length", is(optionsStr.size())).
            body("num_guesses", greaterThan(0)).
            body("result", notNullValue());
    }
    
    // TODO: Make more guessing tests...
 
}
