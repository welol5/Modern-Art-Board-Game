import java.util.ArrayList;
import java.util.HashMap;

import player.Player;
import player.PlayerType;

/**
 * This main class is used to run a tournament with all of the
 * AIs that are specified in the {@link PlayerType} ArrayList.
 * The main method works by creating all of the different lists
 * of players that will be in the games, then it creates {@link GameRunner}
 * Threads in a pool to take the games and run them as fast as they can.
 * 
 * @author William Elliman
 *
 */
public class TournamentGame {

	/**
	 * The amount of times an ordering of the AIs will be played.
	 * Multiple games are run here so that the best AI will prove
	 * it by winning most of the games.
	 */
	private static final int trials = 100;
	
	/**
	 * The time a {@link GameRunner} Thread will allow for a game to be
	 * played. This exists because sometimes the games do not finish.
	 * It is rarely used but the {@link Random} AIs will trigger it.
	 */
	private static final int timeout = 10000;

	private static final int threadCount = 1024;

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
		playerTypeList.add(PlayerType.BASIC_PREDICTIVE_AI_V3);

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
					for(int playerFourSlot = 0; playerFourSlot < playerTypeList.size(); playerFourSlot++) {

						//run each order for (trials) iterations
						for(int i = 0; i < trials; i++) {

							ArrayList<PlayerType> currentPlayerTypes = new ArrayList<PlayerType>();
							currentPlayerTypes.add(playerTypeList.get(playerOneSlot));
							currentPlayerTypes.add(playerTypeList.get(playerTwoSlot));
							currentPlayerTypes.add(playerTypeList.get(playerThreeSlot));
							currentPlayerTypes.add(playerTypeList.get(playerFourSlot));

							allGames.add(currentPlayerTypes);

						}
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
		int threadsRemaining = threadCount;
		for(GameRunner gr : runnerPool) {
			gr.stopRunner();
			try {
				gr.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			threadsRemaining--;
			System.out.println("Threads remaining : " + threadsRemaining);
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
