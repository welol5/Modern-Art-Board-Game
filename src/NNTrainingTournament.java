import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import core.GameState;
import core.ObservableGameState;
import fxmlgui.GameDriver;
import mlaiplayers.LearningAI;
import mlaiplayers.MemoizerAIPlayer;
import mlaiplayers.MemoizerAIPlayerDB;
import mlaiplayers.NNPlayer;
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

public class NNTrainingTournament {

	/**
	 * The amount of games that the MLAI will play.
	 */
	private static int games = 100;

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

	private static int playerCount = 3;

	private static HashMap<String, Integer> wins;

	private static int maxRounds = 1000;

	public static void main(String[] args) {

		wins = new HashMap<String, Integer>();

		types = new ArrayList<PlayerType>();
		names = new ArrayList<String>();
		for(int i = 0; i < playerCount; i++) {
			types.add(PlayerType.NNPlayer);
			names.add("" + PlayerType.NNPlayer + " " + i);
		}

		Player[] players = makePlayers(names, types, null);

		for(int round = 0; round < maxRounds; round++) {
			for(Player p : players) {
				wins.put(p.name, 0);
			}
			for(int game = 0; game < games; game++) {


				GameState state = new GameState(playerCount);
				ObservableGameState OGS = new ObservableGameState(state);

				for(Player p : players) {
					NNPlayer nnp = ((NNPlayer)p);
					nnp.updateOGS(OGS);
				}

				//shuffle player order
				for(int i = 0; i < 20; i++) {
					int slot = (int)(Math.random()*2)+1;
					Player p = players[0];
					players[0] = players[slot];
					players[slot] = p;
				}

				GameDriver driver = new GameDriver(players, state, OGS, false);

				Player winner = driver.playGame();

				int winnerWins = wins.get(winner.name).intValue();
//				System.out.println(winnerWins);
				wins.put(winner.name, winnerWins+1);
				System.out.println(game + " : " + winner.name + " : " + wins.get(winner.name) + " : " + winner.getMoney());
			}
			
			System.out.println("Round over");
			
			//after the round is over the update train the AIs
			int bestIndex = -1;
			int bestScore = -1;
			for(int i = 0; i < players.length; i++) {
				System.out.println(players[i].name + " : " + wins.get(players[i].name));
				if(bestScore < wins.get(players[i].name)) {
					bestScore = wins.get(players[i].name);
					bestIndex = i;
				}
			}
			
			//allow time to see results
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ArrayList<NNPlayer.Move> bestMoves = ((NNPlayer)players[bestIndex]).getMoves();
			
			for(int i = 0; i < players.length; i++) {
				((NNPlayer)players[i]).learn(bestMoves);
				((NNPlayer)players[i]).clearMoves();
			}
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
			} else if(types.get(i) == PlayerType.NNPlayer) {
				players[i] = new NNPlayer(names.get(i),OGS,players.length,i);
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
