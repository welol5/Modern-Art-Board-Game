package gui;

import java.awt.Toolkit;
import java.util.ArrayList;

import core.Artist;
import core.AuctionType;
import core.Card;
import core.GameState;
import core.ObservableGameState;
import io.BasicIO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import player.BasicPredictiveAIPlayer;
import player.BasicPredictiveAIPlayerV2;
import player.HumanPlayer;
import player.MemoryAIPlayer;
import player.Player;
import player.PlayerType;
import player.RandomPlayer;
import player.ReactiveAIPlayer;

/**
 * notes
 * - only 1 human player can be active
 * 
 * @author William Elliman
 *
 */
public class GUICore extends Application{

	//GUI stuff
	private MainMenuPane mainMenuPane;
	private Stage mainStage;
	//Use this for now, but it may be changed later for ease of use
	private Pane playerPane = new Pane();

	//player and game vars
	private Player[] players;
	private GameState state;
	private ObservableGameState OGS;

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		mainStage = primaryStage;

		//setup basic overall stuff
		primaryStage.setTitle("Modern Art");
		primaryStage.setHeight(Toolkit.getDefaultToolkit().getScreenSize().getHeight()*(3.0/4.0));
		primaryStage.setWidth(Toolkit.getDefaultToolkit().getScreenSize().getWidth()*(3.0/4.0));

		showStartup(primaryStage);
		primaryStage.show();


	}



	/////////////////////////////////////////////////
	//below here are helper methods to show specific things

	private void showStartup(Stage stage) {
		mainMenuPane = new MainMenuPane(this,stage.getWidth(),stage.getHeight());
		Scene mainMenuScene = new Scene(mainMenuPane);
		stage.setScene(mainMenuScene);
	}

	public void startGame() {
		String[] names = mainMenuPane.getNames();
		//do nothing if names are bad
		if(names == null) {
			return;
		}
		PlayerType[] types = mainMenuPane.getPlayerTypes();

		//make the game state
		state = new GameState(names.length);
		OGS = new ObservableGameState(state);

		//make the list of players
		//swap to a game screen using the first human player as the view
		makePlayers(names,types);
		//some code here for now and should be clean later
		playerPane.setPrefSize(mainStage.getWidth(), mainStage.getHeight());
		Scene playerScene = new Scene(playerPane);
		mainStage.setScene(playerScene);

		//start up the game
		for(int season = 1; season <= 4; season++) {
			for(int i = 0; i < state.dealAmounts[season-1]; i++) {
				for(Player p : players) {
					p.deal(state.drawCard());
				}
			}
			
			//notify player observers
			for(Player p : players) {
				p.notifyObservers();
			}
		}
	}

	public Stage getMainStage() {
		return mainStage;
	}

	///////////////////////////////////////////////////////
	//helper methods

	private void makePlayers(String[] names, PlayerType[] types) {

		players = new Player[names.length];
		for(int i = 0; i < players.length; i++) {
			//System.out.println(types[i]);
			if(types[i] == PlayerType.HUMAN) {
				players[i] = new HumanPlayer(names[i], playerPane);
			} else if(types[i] == PlayerType.RANDOM) {
				players[i] = new RandomPlayer(names[i]);
			} else if(types[i] == PlayerType.REACTIVE_AI) {
				players[i] = new ReactiveAIPlayer(names[i],OGS);
			} else if(types[i] == PlayerType.MEMORY_AI) {
				players[i] = new MemoryAIPlayer(names[i],OGS, names.length, i);
			} else if(types[i] == PlayerType.BASIC_PREDICTIVE_AI) {
				players[i] = new BasicPredictiveAIPlayer(names[i],OGS, names.length, i);
			} else if(types[i] == PlayerType.BASIC_PREDICTIVE_AI_V2) {
				players[i] = new BasicPredictiveAIPlayerV2(names[i],OGS, names.length, i);
			} 
		}

	}
}
