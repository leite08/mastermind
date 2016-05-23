package com.rafaelleite.mastermind.service;

import static java.lang.String.format;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rafaelleite.mastermind.domain.Game;
import com.rafaelleite.mastermind.domain.Guess;
import com.rafaelleite.mastermind.repository.GameRepository;
import com.rafaelleite.mastermind.repository.GuessRepository;

@Service
public class GameService {

	private static final Logger log = LoggerFactory.getLogger(GameService.class);

	// Maximum game duration, IN MINUTES
	public static final long EXPIRATION_TIME = 30;

	private final GameRepository gameRepo;
	private final GuessRepository guessRepo;
	private final ConfigService configService;

	private SecureRandom random = new SecureRandom();

	public String nextSessionId() {
		return RandomStringUtils.random(68, 0, 0, true, true, null, random);
	}

	@Autowired
	public GameService(GameRepository gr, GuessRepository gsr, ConfigService cs) {
		gameRepo = gr;
		guessRepo = gsr;
		configService = cs;
	}

	/**
	 * Create a new game for the given user.
	 * 
	 * @param userName
	 * @return a newly created game
	 */
	public Game newGame(String userName) {
		return newGame(userName, 1);
	}
	/**
	 * Create a new game for the given user.
	 * If the game is in multiplayer mode, each user has to play in order for the
	 * first user might play again. 
	 * 
	 * @param userName
	 * @param numberOfPlayers indicates the number of players for multiplayer mode.
	 * @return a newly created game
	 */
	public Game newGame(String userName, int numberOfPlayers) {
		if (numberOfPlayers <= 0)
			numberOfPlayers = 1;

		// Create the game
		Game game = new Game();
		game.setUserName(userName);
		game.setNumberOfPlayers(numberOfPlayers);
		game.setCreatedAt(LocalDateTime.now());
		game.setKey(nextSessionId());
		game.setSolution(generateRandomSolution());
		game.getOptions().addAll(configService.getConfig().getOptions());

		// Add it to the repository
		gameRepo.saveAndFlush(game);

		return game;
	}

	/**
	 * Return a random solution to be used on a game. 
	 */
	private String generateRandomSolution() {
		Collection<Character> options = configService.getConfig().getOptions();
		char[] chars = ArrayUtils.toPrimitive(options.toArray(new Character[] {}), ' ');
		return RandomStringUtils.random(options.size(), 0, options.size(), true, true, chars, random);
	}

	/**
	 * Enum that represents the different results for a guess method. 
	 */
	public enum GuessResult {
		OK, GAME_NOT_FOUND, GAME_OVER_SOLVED, GAME_OVER_NUM_ATTEMPS, 
		GAME_OVER_EXPIRED, MULTIPLAYER_WAIT_TURN, MULTIPLAYER_MISSING_USER, 
		MULTIPLAYER_FULL;
		
		private Guess guess;

		public GuessResult setGuess(Guess val) {
			guess = val;
			return this;
		}
		public Guess getGuess() {
			return guess;
		}
	}
	
	/**
	 * Process a new user's guess.
	 * 
	 * @param gameKey key that identifies the game 
	 * @param code the combination the user is guessing
	 * @return GuessResult indicating the result of the guess
	 */
	public GuessResult guess(String gameKey, String code) {
		return guess(gameKey, code, null);
	}
	/**
	 * Process a new user's guess.
	 * 
	 * @param gameKey key that identifies the game 
	 * @param code the combination the user is guessing
	 * @param userName the name of the user trying to guess the solution. Might 
	 * 		be null for single-player.
	 * @return GuessResult indicating the result of the guess
	 */
	@Transactional
	public GuessResult guess(String gameKey, String code, String userName) {

		Guess guess = new Guess();
		guess.setCreatedAt(LocalDateTime.now());
		guess.setGuess(code);

		Game game = gameRepo.findByKey(gameKey);
		if (game == null) {
			log.debug(format("Didn't find a game with key '%s'", gameKey));
			return GuessResult.GAME_NOT_FOUND.setGuess(guess);
		}
		
		guess.setGame(game);
		
		// Multiplayer mode, make sure everyone plays the same amount of turns
		if (game.getNumberOfPlayers() > 1) {
			if (userName == null || userName.trim().isEmpty()) {
				log.debug(format("User trying to play without informing the username on game '%s'", gameKey));
				return GuessResult.MULTIPLAYER_MISSING_USER.setGuess(guess);
			}
			if (!userHasAccess(game, userName)) {
				log.debug(format("User '%s' trying to play but game is full - game '%s'", userName, gameKey));
				return GuessResult.MULTIPLAYER_FULL.setGuess(guess);
			}
			if (!userCanPlay(game, userName)) {
				log.debug(format("User trying to play before others '%s' on game '%s'", userName, gameKey));
				return GuessResult.MULTIPLAYER_WAIT_TURN.setGuess(guess);
			}
			guess.setUserName(userName);
		}

		// TODO: check the number of attempts
		// TODO: store the game result in the repository
		if (game.isSolved()) {
			log.debug(format("Tried to play a solved game '%s'", gameKey));
			return GuessResult.GAME_OVER_SOLVED.setGuess(guess);
		}
		if (isExpired(game)) {
			log.debug(format("Game '%s' expired", gameKey));
			return GuessResult.GAME_OVER_EXPIRED.setGuess(guess);
		}

		Result result = calculateResult(game.getSolution(), code);
		guess.setExact(result.exact);
		guess.setNear(result.near);
		if (guess.getExact() >= game.getSolution().length()) {
			game.setSolved(true);
			game.setTimeTaken(Duration.between(game.getCreatedAt(), LocalDateTime.now()).getSeconds());
		}
		guess = guessRepo.save(guess);
		game.getGuesses().add(guess);
		game = gameRepo.save(game);

		return GuessResult.OK.setGuess(guess);
	}

	private boolean userHasAccess(Game game, String userName) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean userCanPlay(Game game, String userPlaying) {
		
		// If no one guessed yet, let the user guess!
		if (game.getGuesses().isEmpty())
			return true;
		
		// If the user was the last to guess, let others play
		if (lastGuessPlayer(game).equals(userPlaying))
			return false;
		
		// Create a map with the number of guesses per user
		Map<String, Integer> guessesPerUser = new HashMap<>();
		for (Guess guess : game.getGuesses()) {
			Integer numGuesses = guessesPerUser.get(guess.getUserName());
			if (numGuesses == null) {
				numGuesses = 0;
			}
			guessesPerUser.put(guess.getUserName(), ++numGuesses);
		}
		
		// Check if every user guessed at least once
		if (game.getNumberOfPlayers() > guessesPerUser.keySet().size()) {
			// Someone didn't play
			// If this user already did, return false
			if (guessesPerUser.get(userPlaying) != null) {
				return false;
			}
		} else {
			// Everyone played at least once
			// If there is no user with preference over the user playing, he/she can play now.
			// Those with fewer guesses have preference over those who already guessed.
			int userPlayingNumGuesses = guessesPerUser.get(userPlaying);
			for (String userInLoop : guessesPerUser.keySet()) {
				if (!userInLoop.equals(userPlaying) 
						&& guessesPerUser.get(userInLoop) < userPlayingNumGuesses) {
					// Someone played fewer times than the current user
					return false;
				}
			}
		}
		return true;
	}

	private String lastGuessPlayer(Game game) {
	    final Comparator<Guess> comp = (g1, g2) -> g1.getCreatedAt().compareTo(g2.getCreatedAt());
	    return game.getGuesses().stream()
	                              .max(comp)
	                              .get()
	                              .getUserName();
	}

	/**
	 * Return whether this game is expired.
	 * If there were guess(es), use the last guess to check the expiration.
	 * Otherwise, use the game's creation date to check for expiration.
	 */
	public static boolean isExpired(Game game) {
		if (game.getGuesses() != null && game.getGuesses().size() > 0) {
			if (Duration.between(getLastGuess(game).getCreatedAt(), LocalDateTime.now()).toMinutes() > EXPIRATION_TIME)
				return true;
		} else {
			if (Duration.between(game.getCreatedAt(), LocalDateTime.now()).toMinutes() > EXPIRATION_TIME)
				return true;
		}
		return false;
	}

	/**
	 * Returns the last guess done for a particular game
	 */
	private static Guess getLastGuess(Game game) {
		Guess lastGuess = null;
		for (Guess guess : game.getGuesses()) {
			if (lastGuess == null || guess.getCreatedAt().isAfter(lastGuess.getCreatedAt())) {
				lastGuess = guess;
			}
		}
		return lastGuess;
	}

	class Result {
		int exact;
		int near;

		Result(int e, int n) {
			exact = e;
			near = n;
		}
	}

	/**
	 * Calculate the result of the guess, checking how many colors
	 * the user got in the right position, as well as how many he/she
	 * got chose right on the wrong position.
	 *  
	 * @param solution the solution of the game to compare the guess against
	 * @param guess the combination the user is trying
	 * @return Result indicating the number of exact hits and the number of
	 * near/close hits
	 */
	protected Result calculateResult(String solution, String guess) {
		if (solution.equals(guess))
			return new Result(solution.length(), 0);

		boolean[] keyUsed = new boolean[solution.length()];
		boolean[] guessUsed = new boolean[guess.length()];

		int correct = 0;
		int match = 0;

		// Compare correct color and position
		for (int i = 0; i < solution.length(); i++) {
			if (i < guess.length() && solution.charAt(i) == guess.charAt(i)) {
				correct++;
				keyUsed[i] = guessUsed[i] = true;
			}
		}

		// Compare matching colors for "pins" that were not used
		for (int i = 0; i < solution.length(); i++) {
			for (int j = 0; j < guess.length(); j++) {
				if (!keyUsed[i] && !guessUsed[j] && solution.charAt(i) == guess.charAt(j)) {
					match++;
					keyUsed[i] = guessUsed[j] = true;
					break;
				}
			}
		}
		return new Result(correct, match);
	}

}
