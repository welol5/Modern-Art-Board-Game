import java.util.ArrayList;

import core.GameState;
import core.ObservableGameState;
import fxmlgui.GameDriver;
import mlaiplayers.LearningAI;
import player.BasicPredictiveAIPlayer;
import player.BasicPredictiveAIPlayerV2;
import player.BasicPredictiveAIPlayerV3;
import player.GeneticAIPlayer;
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
	private static int games = 10000;

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
	private LearningAI MLAI; 
	private PlayerType MLAIType = PlayerType.GENETIC_AI;

	public static void main(String[] args) {
		
		types = new ArrayList<PlayerType>();
		types.add(PlayerType.GENETIC_AI);
		types.add(PlayerType.MERCHANT);
		types.add(PlayerType.BASIC_PREDICTIVE_AI_V2);
		
		for(int i = 0; i < games; i++) {
			//make the game
			GameState state = new GameState(types.size());
			ObservableGameState OGS = new ObservableGameState(state);

			//make the players
			names = new ArrayList<String>();
			for(PlayerType type : types) {
				names.add(type.toString());
			}
			Player[] players = makePlayers(names, types, OGS);

			//make the driver
			GameDriver driver = new GameDriver(players, state, OGS, false);
		}
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
			} else if(types.get(i) == PlayerType.GENETIC_AI) {
				//players[i] = new GeneticAIPlayer("MLAI", OGS, players.length, i, dataBase, 0.01, 0.5)
			} else {
				players[i] = new RandomPlayer(names.get(i));
			}
		}
		return players;
	}
}
