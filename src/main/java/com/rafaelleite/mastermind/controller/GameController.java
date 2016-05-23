package com.rafaelleite.mastermind.controller;

import static java.lang.String.format;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafaelleite.mastermind.controller.dto.GuessRequest;
import com.rafaelleite.mastermind.controller.dto.GuessResponse;
import com.rafaelleite.mastermind.controller.dto.PastResult;
import com.rafaelleite.mastermind.controller.dto.Result;
import com.rafaelleite.mastermind.domain.Game;
import com.rafaelleite.mastermind.domain.Guess;
import com.rafaelleite.mastermind.service.GameService;
import com.rafaelleite.mastermind.service.GameService.GuessResult;

@RestController
public class GameController {
	
	private static final Logger log = LoggerFactory.getLogger(GameController.class);
	
	private final GameService gameService;
	
	@Autowired
	public GameController(GameService gs) {
		gameService = gs;
	}
	
	//#####################################################################################
	//#####################################################################################
	//#####################################################################################

	@RequestMapping(value = "/new_game", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	Game newGame(@RequestBody String body, HttpServletResponse response) throws IOException {
		if (body == null || body.trim().isEmpty()) {
			response.sendError(HttpURLConnection.HTTP_BAD_REQUEST, "Missing body content");
			return null;
		}
		String userName;
		int numberOfPlayers = -1;
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode actualObj = mapper.readTree(body).get("user");
			if (actualObj == null) {
				response.sendError(HttpURLConnection.HTTP_BAD_REQUEST, "Missing user name");
				return null;
			}
			userName = actualObj.asText();
			if (userName == null || userName.trim().isEmpty()) {
				response.sendError(HttpURLConnection.HTTP_BAD_REQUEST, "User name cannot be empty");
				return null;
			}
			
			// Optional - Only for multiplayer
			actualObj = mapper.readTree(body).get("number_of_players");
			if (actualObj != null) {
				numberOfPlayers = actualObj.asInt();
			}
			
		} catch (JsonParseException e) {
			response.sendError(HttpURLConnection.HTTP_BAD_REQUEST, "Malformed body");
			return null;
		}
		log.debug("Creating a new game");
		return gameService.newGame(userName, numberOfPlayers);
	}
	
	//#####################################################################################
	//#####################################################################################

	@RequestMapping(value = "/guess", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	GuessResponse makeGuess(@RequestBody GuessRequest guessParam, HttpServletResponse response) throws IOException {
		if (guessParam == null) {
			response.sendError(HttpURLConnection.HTTP_BAD_REQUEST, "Missing body content");
			return null;
		}
		if (guessParam.game_key == null || guessParam.game_key.trim().isEmpty()) {
			response.sendError(HttpURLConnection.HTTP_BAD_REQUEST, "Missing game key");
			return null;
		}
		if (guessParam.code == null || guessParam.code.trim().isEmpty()) {
			response.sendError(HttpURLConnection.HTTP_BAD_REQUEST, "Missing code/guess");
			return null;
		}
		log.debug(format("Making a new guess on game '%s', user '%s'", guessParam.game_key, guessParam.user));
		String message = null;
		GuessResult guessResult = gameService.guess(guessParam.game_key, guessParam.code, guessParam.user);
		switch (guessResult) {
			case GAME_OVER_SOLVED:
				message = "Game already solved.";
				break;
			case GAME_OVER_EXPIRED:
				message = format("This game has expired (%d minute window). Please start a new game.", GameService.EXPIRATION_TIME);
				break;
			case GAME_NOT_FOUND:
				response.sendError(HttpURLConnection.HTTP_NOT_FOUND, "Game not found");
				return null;
			case MULTIPLAYER_MISSING_USER:
				response.sendError(HttpURLConnection.HTTP_BAD_REQUEST, "Missing user name in multiplayer game");
				return null;
			case MULTIPLAYER_FULL:
				response.sendError(HttpURLConnection.HTTP_UNAUTHORIZED, "Multiplayer game is full");
				return null;
			case MULTIPLAYER_WAIT_TURN:
				message = "Its not your turn just yet... wait for the other user(s) to make their guess(es) too... :)";
				break;
			case OK:
				if (guessResult.getGuess().getGame().isSolved()) {
					message = "Congratulations! You solved the puzzle!";
				}
		}
		GuessResponse guessResponse = toGuessResponse(guessResult.getGuess());
		guessResponse.result.message = message;
		
		return guessResponse;
	}

	private GuessResponse toGuessResponse(Guess guess) {
		GuessResponse r = new GuessResponse();
		r.game_key = guess.getGame().getKey();
		r.num_guesses = guess.getGame().getGuesses().size();
		for (Guess pastGuess : guess.getGame().getGuesses()) {
			// Don't add the current guess in the past guesses collection
			if (guess.equals(pastGuess))
				continue;
			PastResult pastResult = new PastResult();
			pastResult.guess = pastGuess.getGuess();
			pastResult.exact = pastGuess.getExact();
			pastResult.near = pastGuess.getNear();
			pastResult.user = pastGuess.getUserName();
			r.past_results.add(pastResult);
		}
		r.guess = guess.getGuess();
		r.solved = guess.getGame().isSolved();
		if (r.solved && guess.getGame().getTimeTaken() > 0) {
			r.time_taken = guess.getGame().getTimeTaken();
		}
		for (Character option : guess.getGame().getOptions()) {
			r.colors.add(option);
		}
		r.code_length = r.colors.size();
		r.result = new Result();
		r.result.exact = guess.getExact();
		r.result.near = guess.getNear();
		return r;
	}
}
