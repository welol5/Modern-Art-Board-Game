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
public class GameRunner extends Thread{

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
	 * The list of {@link PlayerType} that will be playing in this game
	 * in the order that they will be during the game.
	 */
	private ArrayList<PlayerType> types = null;

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

	public GameRunner(int timeout) {
		super();
		this.timeout = timeout;
	}

	@Override
	public void run() {

		//while the program is not done
		while(!stop) {
			//reset the done var
			done = false;

			//get a list of players
			types = TournamentGame.getPlayerList();

			//if the list is null, try again
			if(types == null) {
				continue;
			}

			//while a game has not been completed
			while(!done) {

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

				//make the thread to run the game
				Thread runner = new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							//run the game
							winner = driver.playGame();
							//set this to true if the game finished
							done = true;
						} catch (Exception e){
							e.printStackTrace();
						}
					}
				});

				//Start the thread that actually runs the game
				runner.start();

				try {
					//wait for the game to be either done or to fail
					runner.join(timeout);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//update the wins in the tournament
			TournamentGame.addWin(winner);
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
			} else {
				players[i] = new RandomPlayer(names.get(i));
			}
		}

		return players;
	}
}
