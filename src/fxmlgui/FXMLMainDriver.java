package fxmlgui;

import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;

import core.Artist;
import core.AuctionType;
import core.Card;
import core.GameState;
import core.ObservableGameState;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import player.BasicPredictiveAIPlayer;
import player.BasicPredictiveAIPlayerV2;
import player.HumanPlayer;
import player.MemoryAIPlayer;
import player.Player;
import player.PlayerType;
import player.RandomPlayer;
import player.ReactiveAIPlayer;

public class FXMLMainDriver extends Application{
	
	private Player[] players;
	private GameState state;
	private ObservableGameState OGS;
	
	private int humanPlayerIndex = -1;
	
	public static void main(String[] agrs) {
		launch();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		VBox root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
		
		double height = Toolkit.getDefaultToolkit().getScreenSize().getHeight()*3.0/4.0;
		double width = Toolkit.getDefaultToolkit().getScreenSize().getWidth()*3.0/4.0;
		root.setPrefWidth(width);
		root.setPrefHeight(height);
		
		//add options to the combo boxes
		for(Node node : ((VBox)root.getChildren().get(1)).getChildren()) {
			ComboBox box = (ComboBox)((HBox)node).getChildren().get(2);
			
			box.getItems().addAll("None","Human", "Random","Reactive","Memory");
		}
		
		((Button)root.getChildren().get(2)).setOnAction((e) -> {
			try {
				startGame(root, primaryStage);
			} catch (IOException e1) {
				System.out.println("It broke");
				System.exit(0);
			}
		});
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void startGame(Node mainMenu, Stage stage) throws IOException {
		
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<PlayerType> types = new ArrayList<PlayerType>();
		
		for(Node node : ((VBox)((VBox)mainMenu).getChildren().get(1)).getChildren()) {
			System.out.println("node: " + ((HBox)node).getChildren().get(2));
			String boxSelection = ((String)((ComboBox)((HBox)node).getChildren().get(2)).getValue());
			//ignore empty spaces
			if(boxSelection == null) {
				continue;
			}
			if(boxSelection.equalsIgnoreCase("Human")) {
				names.add(((TextField)((HBox)node).getChildren().get(1)).getText());
				types.add(PlayerType.HUMAN);
			} else if(boxSelection.equalsIgnoreCase("Random")) {
				names.add(((TextField)((HBox)node).getChildren().get(1)).getText());
				types.add(PlayerType.RANDOM);
			} else if(boxSelection.equalsIgnoreCase("Reactive")) {
				names.add(((TextField)((HBox)node).getChildren().get(1)).getText());
				types.add(PlayerType.REACTIVE_AI);
			} else if(boxSelection.equalsIgnoreCase("Memory")) {
				names.add(((TextField)((HBox)node).getChildren().get(1)).getText());
				types.add(PlayerType.MEMORY_AI);
			}
		}
		
		//prep the game vars
		state = new GameState(names.size());
		OGS = new ObservableGameState(state);
		
		//this is awkward for now //TODO
		
		//TODO testing stuff
		HBox playerView = FXMLLoader.load(getClass().getResource("PlayerView.fxml"));
		
		Scene playerScene = new Scene(playerView);
		stage.setScene(playerScene);
	}
	
	private void makePlayers(ArrayList<String> names, ArrayList<PlayerType> types) {
		players = new Player[names.size()];
		
		for(int i = 0; i < players.length; i++) {
			//System.out.println(types[i]);
			if(types.get(i) == PlayerType.HUMAN) {
				players[i] = new HumanPlayer(names.get(i), OGS);
				if(humanPlayerIndex == -1) {
					humanPlayerIndex = i;
				}
			} else if(types.get(i) == PlayerType.RANDOM) {
				players[i] = new RandomPlayer(names.get(i));
			} else if(types.get(i) == PlayerType.REACTIVE_AI) {
				players[i] = new ReactiveAIPlayer(names.get(i),OGS);
			} else if(types.get(i) == PlayerType.MEMORY_AI) {
				players[i] = new MemoryAIPlayer(names.get(i),OGS, names.size(), i);
			} else if(types.get(i) == PlayerType.BASIC_PREDICTIVE_AI) {
				players[i] = new BasicPredictiveAIPlayer(names.get(i),OGS, names.size(), i);
			} else if(types.get(i) == PlayerType.BASIC_PREDICTIVE_AI_V2) {
				players[i] = new BasicPredictiveAIPlayerV2(names.get(i),OGS, names.size(), i);
			} 
		}
	}
}
