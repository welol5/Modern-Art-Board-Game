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
	private static final int timeout = 10000;
	
	private static final int threadCount = 8;

	private static ArrayList<ArrayList<PlayerType>> allGames = new ArrayList<ArrayList<PlayerType>>();
	private static HashMap<String,Integer> wins;

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

		wins = new HashMap<String,Integer>();
		//init to 0s
		for(PlayerType type : playerTypeList) {
			wins.put(type.toString(), 0);
		}

		int iterationIndex = 0;
		int totalIterations = playerTypeList.size()*playerTypeList.size()*playerTypeList.size()*trials;

		for(int playerOneSlot = 0; playerOneSlot < playerTypeList.size(); playerOneSlot++) {
			for(int playerTwoSlot = 0; playerTwoSlot < playerTypeList.size(); playerTwoSlot++) {
				for(int playerThreeSlot = 0; playerThreeSlot < playerTypeList.size(); playerThreeSlot++) {

					//run each order for (trials) iterations
					for(int i = 0; i < trials; i++) {
						
						ArrayList<PlayerType> currentPlayerTypes = new ArrayList<PlayerType>();
						currentPlayerTypes.add(playerTypeList.get(playerOneSlot));
						currentPlayerTypes.add(playerTypeList.get(playerTwoSlot));
						currentPlayerTypes.add(playerTypeList.get(playerThreeSlot));

						allGames.add(currentPlayerTypes);

					}
				}
			}
		}

		//games have been created, time to run through them
		//use a thread pool
		GameRunner[] runnerPool = new GameRunner[threadCount];
		for(int i = 0; i < runnerPool.length; i++) {
			//start the games
			runnerPool[i] = new GameRunner(timeout);
			runnerPool[i].start();
		}
//		System.out.println("Threads statred");

		//wait for all games to be taken
		while(allGames.size() > 0) {
			//print remaing games
			System.out.println(allGames.size());
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//wait for all threads to finish
		for(GameRunner gr : runnerPool) {
			gr.stopRunner();
			try {
				gr.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//print results
		for(PlayerType type : playerTypeList) {
			System.out.println(type + " :: " + wins.get(type.toString()));
		}
	}

	public static void addWin(Player winner) {
		synchronized(wins) {
			wins.put(winner.name, wins.get(winner.name) + 1);
		}
	}
	
	public static ArrayList<PlayerType> getPlayerList() {
		synchronized(allGames) {
			try {
				return allGames.remove(0);
			} catch (IndexOutOfBoundsException e) {
				return null;
			}
		}
	}
}
