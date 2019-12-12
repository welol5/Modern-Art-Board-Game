import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import mlaiplayers.NNPlayer;
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

	/**
	 * This is the amount of threads that will be in the tread pool.
	 * I found that this should be far larger than the actual amount of
	 * threads that a computer has.
	 */
	private static final int threadCount = 64;

	/**
	 * The set of lists of AIs that will play the games.
	 */
	private static ArrayList<ArrayList<PlayerType>> allGames = new ArrayList<ArrayList<PlayerType>>();
	
	/**
	 * This keeps the counts of how many games each player has won.
	 */
	private static HashMap<String,Integer> wins;
	
	private static String NNPlayerFile = "NNWeightsFile.txt";
	private static NNPlayerParts NNPlayerWeights = null;

	public static void main(String[] args) {

		/**
		 * The list of players.
		 * This is what should be changed when testing AIs.
		 */
		ArrayList<PlayerType> playerTypeList = new ArrayList<PlayerType>();
		playerTypeList.add(PlayerType.RANDOM);
		playerTypeList.add(PlayerType.REACTIVE_AI);
//		playerTypeList.add(PlayerType.MEMORY_AI);
//		playerTypeList.add(PlayerType.BASIC_PREDICTIVE_AI);
		playerTypeList.add(PlayerType.BASIC_PREDICTIVE_AI_V2);
		playerTypeList.add(PlayerType.HIGH_ROLLER);
		playerTypeList.add(PlayerType.MERCHANT);
		playerTypeList.add(PlayerType.HAND_STATE_CARD_PICKER);
		playerTypeList.add(PlayerType.BASIC_PREDICTIVE_AI_V3);
		
		playerTypeList.add(PlayerType.NNPlayer);
		
		if(playerTypeList.contains(PlayerType.NNPlayer)) {
			Scanner fileInput;
			try {
				fileInput = new Scanner(new File(NNPlayerFile));
				NNPlayerWeights = loadNNPlayerPieces(fileInput);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Loading NNPlayer failed");
//				System.exit(0);
			}
		}

		//init
		wins = new HashMap<String,Integer>();
		//init to 0s
		for(PlayerType type : playerTypeList) {
			wins.put(type.toString(), 0);
		}

		/**
		 * This block creates all of the lists of players that will play in the games.
		 * It creates copies of the same order so that the best AI has many chances to win.
		 * Right now it is set up for 4 player games
		 */
		for(int playerOneSlot = 0; playerOneSlot < playerTypeList.size(); playerOneSlot++) {
			for(int playerTwoSlot = 0; playerTwoSlot < playerTypeList.size(); playerTwoSlot++) {
				for(int playerThreeSlot = 0; playerThreeSlot < playerTypeList.size(); playerThreeSlot++) {
//					for(int playerFourSlot = 0; playerFourSlot < playerTypeList.size(); playerFourSlot++) {

						//run each order for (trials) iterations
						for(int i = 0; i < trials; i++) {

							//create a new list to use
							ArrayList<PlayerType> currentPlayerTypes = new ArrayList<PlayerType>();
							//add the players to the list
							currentPlayerTypes.add(playerTypeList.get(playerOneSlot));
							currentPlayerTypes.add(playerTypeList.get(playerTwoSlot));
							currentPlayerTypes.add(playerTypeList.get(playerThreeSlot));
//							currentPlayerTypes.add(playerTypeList.get(playerFourSlot));

							//add the list to the set of lists
							allGames.add(currentPlayerTypes);

						}
//					}
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

		//wait for all games to be taken
		//this prints out the remaining games after every 5-ish seconds
		while(allGames.size() > 0) {
			//print remaining games
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
			//set the thread to stop
			gr.stopRunner();
			try {
				//wait for the thread to stop
				gr.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//keeping track of the remaining threads
			threadsRemaining--;
			System.out.println("Threads remaining : " + threadsRemaining);
		}

		//print results
		for(PlayerType type : playerTypeList) {
			System.out.println(type + " :: " + wins.get(type.toString()));
		}
	}

	/**
	 * Add one win to the win counts for a player.
	 * @param winner the winner of the game.
	 */
	public static void addWin(Player winner) {
		synchronized(wins) {
			wins.put(winner.name, wins.get(winner.name) + 1);
		}
	}

	/**
	 * Retrieves a list of players for a {@link GameRunner} to use.
	 * @return a list of {@link PlayerType} that should play a game.
	 */
	public static ArrayList<PlayerType> getPlayerList() {
		synchronized(allGames) {
			try {
				return allGames.remove(0);
			} catch (IndexOutOfBoundsException e) {
				return null;
			}
		}
	}
	
	private static NNPlayerParts loadNNPlayerPieces(Scanner fileInput) {
		System.out.println("loading player");
		String checkNetworkFile = fileInput.nextLine();
//		System.out.println(checkNetworkFile);
		if(!checkNetworkFile.equalsIgnoreCase("--network start--")) {
			throw new IllegalArgumentException("File scanner did not start at the begining of a network");
		} else {
			//assumes network settings match current settings
			String inputNodeCount = fileInput.nextLine().split(":")[0];
			String hiddenLayerNodes = fileInput.nextLine().split(":")[0];
			String hiddenLayers = fileInput.nextLine().split(":")[0];
			String alpha = fileInput.nextLine().split(":")[0];
			fileInput.nextLine();//read in the bidding network start line
			double[][][] biddingWeights = NNPlayer.loadWeights(fileInput);
			//			fileInput.nextLine();//read in the bidding network end line
			//			fileInput.nextLine();//read in the picking network start line
			double[][][] pickingWeights = NNPlayer.loadWeights(fileInput);
			//			fileInput.nextLine();//read in the picking network end line
			
			return new NNPlayerParts(biddingWeights,pickingWeights);
		}
	}
	
	public static class NNPlayerParts{
		private double[][][] biddingWeights;
		private double[][][] pickingWeights;
		
		public NNPlayerParts(double[][][] bWeights, double[][][] pWeights) {
			biddingWeights = bWeights;
			pickingWeights = pWeights;
		}
		
		public double[][][] getBiddingWeights(){
			return biddingWeights;
		}
		
		public double[][][] getPickingWeights(){
			return pickingWeights;
		}
	}
	
	public static NNPlayerParts getNNPlayerParts() {
		return NNPlayerWeights;
	}
}
