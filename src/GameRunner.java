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

public class GameRunner extends Thread{

	private Player winner;
	private boolean done = false;
	private final int timeout;
	private GameDriver driver;
	
	private ArrayList<PlayerType> types = null;
	private ArrayList<String> names;
	
	private boolean stop = false;

	public GameRunner(int timeout) {
		super();
		this.timeout = timeout;
	}

	@Override
	public void run() {

		while(!stop) {
			done = false;
			
			//get a list of players
			types = TournamentGame.getPlayerList();

			if(types == null) {
				continue;
			}

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
						winner = driver.playGame();
//						System.out.println("done");
						done = true;
					}
				});
				
				runner.start();

				try {
					runner.join(timeout);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			TournamentGame.addWin(winner);
		}
	}
	
	public void stopRunner() {
		stop = true;
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
			} else if(types.get(i) == PlayerType.BASIC_PREDICTIVE_AI_V3) {
				players[i] = new BasicPredictiveAIPlayerV3(names.get(i),OGS, players.length,i);
			} else {
				players[i] = new RandomPlayer(names.get(i));
			}
		}

		return players;
	}
}
