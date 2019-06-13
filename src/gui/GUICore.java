package gui;

import java.awt.Toolkit;
import java.util.ArrayList;

import core.Artist;
import core.AuctionType;
import core.Card;
import io.BasicIO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
import player.HumanPlayer;
import player.Player;
import player.PlayerType;

public class GUICore extends Application{
	
	private MainMenuPane mainMenuPane;
	private Stage mainStage;
	
	//player and game vars
	private Player[] players;
	
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
		
		//make the list of players
		
		
		//swap to a game screen using the first human player as the view
//		PlayerView view = new PlayerView();
//		Scene playerScene = new Scene(view);
//		mainStage.setScene(playerScene);
	}
	
	public Stage getMainStage() {
		return mainStage;
	}
	
	//helper methods
	
//	private void makePlayers(String[] names, PlayerType[] types) {
//		players = new Player[names.length];
//		for(int i = 0; i < players.length; i++) {
//			if(types[i] == PlayerType.HUMAN) {
//				players[i] = new HumanPlayer(name[i]);
//			}
//		}
//	}
}
