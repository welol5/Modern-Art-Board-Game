package fxmlgui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import core.Artist;
import core.GameState;
import core.ObservableGameState;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import player.BasicPredictiveAIPlayer;
import player.BasicPredictiveAIPlayerV2;
import player.HumanPlayer;
import player.MemoryAIPlayer;
import player.Player;
import player.PlayerType;
import player.RandomPlayer;
import player.ReactiveAIPlayer;

public class GUIDriver implements Initializable, Observer{
	
	@FXML Button startGame;
	
	@FXML TextField player1Name;
	@FXML TextField player2Name;
	@FXML TextField player3Name;
	@FXML TextField player4Name;
	@FXML TextField player5Name;
	
	@FXML ComboBox<PlayerType> player1Type;
	@FXML ComboBox<PlayerType> player2Type;
	@FXML ComboBox<PlayerType> player3Type;
	@FXML ComboBox<PlayerType> player4Type;
	@FXML ComboBox<PlayerType> player5Type;
	
	private PlayerView playerView;
	private HBox playerfxml;
	
	private Service<Void> gameThread;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		for(PlayerType type : PlayerType.values()) {
			player1Type.getItems().add(type);
			player2Type.getItems().add(type);
			player3Type.getItems().add(type);
			player4Type.getItems().add(type);
			player5Type.getItems().add(type);
		}
		
	}
	
	@FXML private void startGame(ActionEvent event) {
		
		//get the stage so the scene can be changed
		Stage mainStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
		
		//change the scene
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("PlayerView.fxml"));
			playerfxml = loader.load();
			playerView = (PlayerView)loader.getController();
			mainStage.setScene(new Scene(playerfxml));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("PlayerView.fxml file is missing or corrupted");
			System.exit(0);
		}
		
		//get all the names and types
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<PlayerType> types = new ArrayList<PlayerType>();
		if(!player1Name.getText().equals("")) {
			names.add(player1Name.getText());
			types.add(player1Type.getValue());
		}
		if(!player2Name.getText().equals("")) {
			names.add(player2Name.getText());
			types.add(player2Type.getValue());
		}
		if(!player3Name.getText().equals("")) {
			names.add(player3Name.getText());
			types.add(player3Type.getValue());
		}
		if(!player4Name.getText().equals("")) {
			names.add(player4Name.getText());
			types.add(player4Type.getValue());
		}
		if(!player5Name.getText().equals("")) {
			names.add(player5Name.getText());
			types.add(player5Type.getValue());
		}
		
		//make the game states that the players need to see
		GameState state = new GameState(names.size());
		ObservableGameState OGS = new ObservableGameState(state);
		
		playerView.bindPrices(
				OGS.getArtistValueString(Artist.LITE_METAL),
				OGS.getArtistValueString(Artist.YOKO),
				OGS.getArtistValueString(Artist.CHRISTIN_P),
				OGS.getArtistValueString(Artist.KARL_GITTER),
				OGS.getArtistValueString(Artist.KRYPTO));
		
		playerView.bindCounts(
				OGS.getArtistCountString(Artist.LITE_METAL),
				OGS.getArtistCountString(Artist.YOKO),
				OGS.getArtistCountString(Artist.CHRISTIN_P),
				OGS.getArtistCountString(Artist.KARL_GITTER),
				OGS.getArtistCountString(Artist.KRYPTO));
		
		Player[] players = makePlayers(names,types,OGS);
		
		playerView.setPlayer(players[0]);
		
		gameThread = new Service<Void>() {
			
			protected Task<Void> createTask(){
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {

						//the game starts now
						GameDriver driver = new GameDriver(players, state, OGS);
						driver.playGame();
						
						return null;
					}
				};
			}
		};
		
		
		
		gameThread.restart();
	}
	
	/////////////////////////////////////////////
	//helper stuff
	
	private PlayerType getPlayerType(String type) {
		if(type == null || type.equalsIgnoreCase("Random")) {
			return PlayerType.RANDOM;
		} else if(type.equalsIgnoreCase("Human")) {
			return PlayerType.HUMAN;
		} else if(type.equalsIgnoreCase("Reactive")) {
			return PlayerType.REACTIVE_AI;
		} else if(type.equalsIgnoreCase("Memory")) {
			return PlayerType.MEMORY_AI;
		} else if(type.equalsIgnoreCase("Basic Predictive V1")) {
			return PlayerType.BASIC_PREDICTIVE_AI;
		} else if(type.equalsIgnoreCase("Basic Predictive V2")) {
			return PlayerType.BASIC_PREDICTIVE_AI_V2;
		}
		
		//if all else fails, return random
		return PlayerType.RANDOM;
	}
	
	private Player[] makePlayers(ArrayList<String> names, ArrayList<PlayerType> types, ObservableGameState OGS) {
		Player[] players = new Player[names.size()];
		
		for(int i = 0; i < players.length; i++) {
			if(types.get(i) == PlayerType.HUMAN) {
				players[i] = new HumanPlayer(names.get(i), playerView, OGS);
			} else if(types.get(i) == PlayerType.RANDOM) {
				players[i] = new RandomPlayer(names.get(i));
			} else if(types.get(i) == PlayerType.REACTIVE_AI) {
				players[i] = new ReactiveAIPlayer(names.get(i),OGS);
			} else if(types.get(i) == PlayerType.MEMORY_AI) {
				players[i] = new MemoryAIPlayer(names.get(i),OGS, players.length,i);
			} else if(types.get(i) == PlayerType.BASIC_PREDICTIVE_AI) {
				players[i] = new BasicPredictiveAIPlayer(names.get(i),OGS, players.length,i);
			} else if(types.get(i) == PlayerType.BASIC_PREDICTIVE_AI_V2) {
				players[i] = new BasicPredictiveAIPlayerV2(names.get(i),OGS, players.length,i);
			} else {
				players[i] = new RandomPlayer(names.get(i));
			}
		}
		
		return players;
	}

	@Override
	public void update(Observable o, Object arg) {
		
	}
}
