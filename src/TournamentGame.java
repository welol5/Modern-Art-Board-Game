import java.util.ArrayList;
import java.util.HashMap;

import core.GameState;
import core.ObservableGameState;
import fxmlgui.GameDriver;
import player.BasicPredictiveAIPlayer;
import player.BasicPredictiveAIPlayerV2;
import player.HandStateCardPicker;
import player.HighRoller;
import player.HumanPlayer;
import player.MemoryAIPlayer;
import player.Merchant;
import player.Player;
import player.PlayerType;
import player.RandomPlayer;
import player.ReactiveAIPlayer;

public class TournamentGame {
	
	private static final int trials = 100;

	public static void main(String[] args) {
		
		ArrayList<PlayerType> playerTypeList = new ArrayList<PlayerType>();
		playerTypeList.add(PlayerType.RANDOM);
		playerTypeList.add(PlayerType.REACTIVE_AI);
		playerTypeList.add(PlayerType.MEMORY_AI);
		playerTypeList.add(PlayerType.BASIC_PREDICTIVE_AI);
		playerTypeList.add(PlayerType.BASIC_PREDICTIVE_AI_V2);
		playerTypeList.add(PlayerType.HIGH_ROLLER);
		playerTypeList.add(PlayerType.MERCHANT);
		playerTypeList.add(PlayerType.HAND_STATE_CARD_PICKER);
		
		ArrayList<String> nameList = new ArrayList<String>();
		nameList.add("Random");
		nameList.add("Reactive");
		nameList.add("Memory");
		nameList.add("V1");
		nameList.add("V2");
		nameList.add("HighRoller");
		nameList.add("Merchant");
		nameList.add("HSCPAI");
		
		HashMap<String,Integer> wins = new HashMap<String,Integer>();
		//init to 0s
		for(String s : nameList) {
			wins.put(s, 0);
		}
		
		int iterationIndex = 0;
		int totalIterations = playerTypeList.size()*playerTypeList.size()*playerTypeList.size()*trials;
		
		for(int playerOneSlot = 0; playerOneSlot < playerTypeList.size(); playerOneSlot++) {
			for(int playerTwoSlot = 0; playerTwoSlot < playerTypeList.size(); playerTwoSlot++) {
				for(int playerThreeSlot = 0; playerThreeSlot < playerTypeList.size(); playerThreeSlot++) {
					
					ArrayList<PlayerType> currentPlayerTypes = new ArrayList<PlayerType>();
					currentPlayerTypes.add(playerTypeList.get(playerOneSlot));
					currentPlayerTypes.add(playerTypeList.get(playerTwoSlot));
					currentPlayerTypes.add(playerTypeList.get(playerThreeSlot));
					
					ArrayList<String> currentPlayerNames = new ArrayList<String>();
					currentPlayerNames.add(nameList.get(playerOneSlot));
					currentPlayerNames.add(nameList.get(playerTwoSlot));
					currentPlayerNames.add(nameList.get(playerThreeSlot));
					
					//run each order for (trials) iterations
					for(int i = 0; i < trials; i++) {
						GameState state = new GameState(3);
						ObservableGameState OGS = new ObservableGameState(state);
						Player[] players = makePlayers(currentPlayerNames, currentPlayerTypes, OGS);
						GameDriver driver = new GameDriver(players,state,OGS, false);
						Player winner = driver.playGame();
						wins.put(winner.name, wins.get(winner.name) + 1);
						iterationIndex++;
						System.out.println(iterationIndex + " / " + totalIterations);
					}
				}
			}
		}
		
		for(String s : nameList) {
			System.out.println(s + " : " + wins.get(s));
		}
	}
	
	private static Player[] makePlayers(ArrayList<String> names, ArrayList<PlayerType> types, ObservableGameState OGS) {
		Player[] players = new Player[names.size()];
		
		for(int i = 0; i < players.length; i++) {
			boolean add = true;
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
			} else {
				players[i] = new RandomPlayer(names.get(i));
			}
		}
		
		return players;
	}
}
