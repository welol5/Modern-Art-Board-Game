import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import core.GameState;
import core.ObservableGameState;
import fxmlgui.GameDriver;
import mlaiplayers.LearningAI;
import mlaiplayers.MemoizerAIPlayer;
import mlaiplayers.MemoizerAIPlayerDB;
import player.BasicPredictiveAIPlayer;
import player.BasicPredictiveAIPlayerV2;
import player.BasicPredictiveAIPlayerV3;
import player.HandStateCardPicker;
import player.HighRoller;
import player.MemoryAIPlayer;
import player.Merchant;
import player.Player;
import player.PlayerType;
import player.RandomPlayer;
import player.ReactiveAIPlayer;

public class TrainingTournament {

	/**
	 * The amount of games that the MLAI will play.
	 */
	private static int games = 1000;

	/**
	 * This array holds the list of players the MLAI player will be training with.
	 */
	private ArrayList<Player> players = new ArrayList<Player>();

	/**
	 * The list of names the players will be using. This list should be
	 * in the same order as the {@link PlayerType} list.
	 */
	private static ArrayList<String> names;

	/**
	 * The list of {@link PlayerType} that will be playing in this game
	 * in the order that they will be during the game.
	 */
	private static ArrayList<PlayerType> types = null;

	/**
	 * The MLAI being trained.
	 */
	private static LearningAI MLAIPlayer;
	private static PlayerType MLAIType = PlayerType.GENETIC_AI;
	private static String MLAIFileName = "MemoizerDatabase.txt";
	private static File MLAIFile = new File(MLAIFileName);

	private static MemoizerAIPlayerDB database;

	public static void main(String[] args) {

		//load MLAI data
		try {
			database = new MemoizerAIPlayerDB(MLAIFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("an error occured with the file.");
			System.exit(0);
		}

		types = new ArrayList<PlayerType>();
		types.add(MLAIType);//add the MLAI
		types.add(PlayerType.MERCHANT);
		types.add(PlayerType.BASIC_PREDICTIVE_AI_V2);
		types = randomizePlayerOrder(types);

		int MLAIWins = 0;

		for(int i = 0; i < games; i++) {
			//make the game
			GameState state = new GameState(types.size(), true);
			ObservableGameState OGS = new ObservableGameState(state);

			//make the players
			names = new ArrayList<String>();
			for(PlayerType type : types) {
				names.add(type.toString());
			}
			Player[] players = null;

			players = makePlayers(names, types, OGS);


			//make the driver
			GameDriver driver = new GameDriver(players, state, OGS, false);

			//start the game
			Player winner = driver.playGame();
			System.out.println("Game " + i + " winner : " + winner.name);
			if(winner.name == MLAIType.toString()) {
				MLAIWins++;
			}

			//update the MLAI
			if(winner.name == MLAIType.toString()) {
				MLAIPlayer.learn(true);
			} else {
				MLAIPlayer.learn(false);
			}


		}

		System.out.println(MLAIWins);

		//save the database
		database.saveData(MLAIFileName);
		System.out.println("MLAI data saved");

	}

	/**
	 * Helper method to take a list of player properties and make
	 * the actual players from them.
	 * @param names of the players that will be playing in the game.
	 * @param types of the players that will be playing in the game.
	 * @param OGS The observable game state. See the class for details.
	 * @return The list of players that will be playing in the game.
	 */
	private static Player[] makePlayers(ArrayList<String> names, ArrayList<PlayerType> types, ObservableGameState OGS) {
		Player[] players = new Player[names.size()];

		for(int i = 0; i < players.length; i++) {
			if(types.get(i) == PlayerType.RANDOM) {
				players[i] = new RandomPlayer(names.get(i));
			} else if(types.get(i) == PlayerType.REACTIVE_AI) {
				players[i] = new ReactiveAIPlayer(names.get(i),OGS);
			} else if(types.get(i) == PlayerType.MEMORY_AI) {
				players[i] = new MemoryAIPlayer(names.get(i),OGS, players.length,i);
			} else if(types.get(i) == PlayerType.BASIC_PREDICTIVE_AI) {
				players[i] = new BasicPredictiveAIPlayer(names.get(i),OGS, players.length,i);
			} else if(types.get(i) == PlayerType.BASIC_PREDICTIVE_AI_V2) {
				players[i] = new BasicPredictiveAIPlayerV2(names.get(i),OGS, players.length,i);
			} else if(types.get(i) == PlayerType.HIGH_ROLLER) {
				players[i] = new HighRoller(names.get(i),OGS, players.length,i);
			} else if(types.get(i) == PlayerType.MERCHANT) {
				players[i] = new Merchant(names.get(i),OGS, players.length,i);
			} else if(types.get(i) == PlayerType.HAND_STATE_CARD_PICKER) {
				players[i] = new HandStateCardPicker(names.get(i),OGS, players.length,i);
			} else if(types.get(i) == PlayerType.BASIC_PREDICTIVE_AI_V3) {
				players[i] = new BasicPredictiveAIPlayerV3(names.get(i),OGS, players.length,i);
			} else if(types.get(i) == PlayerType.MEMOIZER_AI) {
				MLAIPlayer = new MemoizerAIPlayer(names.get(i), OGS, players.length, i, database, 0.01, 0.5);
				players[i] = (Player) MLAIPlayer;
			} else {
				players[i] = new RandomPlayer(names.get(i));
			}
		}
		return players;
	}

	private static ArrayList<PlayerType> randomizePlayerOrder(ArrayList<PlayerType> players){

		for(int i = 0; i < 100; i++) {
			PlayerType player = players.remove((int) Math.random()*players.size());
			players.add(player);
		}

		return players;
	}
}
