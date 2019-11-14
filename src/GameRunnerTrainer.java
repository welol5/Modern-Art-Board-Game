import java.util.ArrayList;

import core.Artist;
import core.GameState;
import core.ObservableGameState;
import fxmlgui.GameDriver;
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
/**
 * This object is the thread that is used in the TournamentGame 
 * main program to run an automated game of Modern Art. It works
 * as part of a thread pool in TournamentGame, getting a list of
 * AI players that will play a game of Modern art. After the game
 * it goes back to TournamentGame and updates the score of the
 * player that won.
 * 
 * @author William Elliman
 *
 */
public class GameRunnerTrainer extends Thread{

	/**
	 * The player that won the game.
	 */
	private Player winner;

	/**
	 * This is used to tell this thread if the game was completed
	 * or if there was an issue and the game should be replayed.
	 */
	private boolean done = false;

	/**
	 * The time in mills that this thread should wait before canceling
	 * because it thinks there was a logical error (infinite loop type
	 * thing).
	 */
	private final int timeout;

	/**
	 * The list of names the players will be using. This list should be
	 * in the same order as the {@link PlayerType} list.
	 */
	private ArrayList<String> names;

	/**
	 * The var used to tell this thread to stop trying to get new games.
	 * This should be changed to false by calling the {@link #stopRunner()}
	 * function.
	 */
	private boolean stop = false;

	/**
	 * How many wins the training player had
	 */
	private double score = 0;

	/**
	 * The players the runner will be dealing with
	 */
	private ArrayList<PlayerType> playerTypes;

	/**
	 * The current weights of the genetic player
	 */
	private double[] geneticPlayerWeights;

	private int gameCount;

	public GameRunnerTrainer(int timeout, int gameCount, ArrayList<PlayerType> types, double[] geneticPlayerWeights) {
		super();
		this.timeout = timeout;
		playerTypes = types;
		this.geneticPlayerWeights = geneticPlayerWeights;
		this.gameCount = gameCount;
	}

	@Override
	public void run() {

		//while the program is not done
		while(!stop) {
			//reset the done var
			done = false;

			for(int game = 0; game < gameCount; game++) {
//				System.out.println(game);

				//make the game
				GameState state = new GameState(playerTypes.size(), false);
				ObservableGameState OGS = new ObservableGameState(state);

				//randomize the list
				playerTypes = GeneticTrainingTournament.randomizePlayerOrder(playerTypes);

				//make the players
				names = new ArrayList<String>();
				for(PlayerType type : playerTypes) {
					names.add(type.toString());
				}
				Player[] players = GeneticTrainingTournament.makePlayers(names, playerTypes, OGS, geneticPlayerWeights);

				//make the driver
				GameDriver driver = new GameDriver(players, state, OGS, false);

				//make the thread to run the game
				Thread runner = new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							//run the game
//							System.out.println("playing game");
							winner = driver.playGame();
							//System.out.println(winner);
						} catch (Exception e){
							e.printStackTrace();
						}
					}
				});

				//Start the thread that actually runs the game
				runner.start();

				try {
					//wait for the game to be either done or to fail
					//TODO re-add timeout
					runner.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(winner.name.equalsIgnoreCase(PlayerType.GENETIC_AI.toString())) {
					score++;
				}

			}

			//all games have been played
			System.out.println("games done");
			score = score/((double)gameCount);
			stop = true;
		}
	}

	/**
	 * Call this method to stop this thread. This should be
	 * called once all of the games in the tournament have either
	 * been competed or taken by other threads.
	 */
	public void stopRunner() {
		stop = true;
	}

	public double[] getGeneticPlayerWeights() {
		return geneticPlayerWeights;
	}

	public double getScore() {
		return score;
	}

}
