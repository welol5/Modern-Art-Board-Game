package fxmlgui;

import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
		primaryStage.setTitle("Modern Art");
		VBox root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
		
		double height = Toolkit.getDefaultToolkit().getScreenSize().getHeight()*3.0/4.0;
		double width = Toolkit.getDefaultToolkit().getScreenSize().getWidth()*3.0/4.0;
		primaryStage.setMinHeight(height);
		primaryStage.setMinWidth(width);
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
			//System.out.println("node: " + ((HBox)node).getChildren().get(2));
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
		
		if(types.size() < 3) {
			return;
		}
		
		//make the players
		makePlayers(names,types);
		//prep the game vars
		state = new GameState(names.size());
		OGS = new ObservableGameState(state);
		
		//this is awkward for now //TODO
		
		//TODO testing stuff
		PlayerView playerView = new PlayerView(players[humanPlayerIndex]);
		
		//make the scene and 
		Scene playerScene = new Scene(playerView);
		stage.setScene(playerScene);

		notifyObservers();
		
		//start the game
		for(int season = 0; season < 4; season++) {
			
			//deal the new cards
			for(int i = 0; i < state.dealAmounts[season]; i++) {
				for(Player p : players) {
					p.deal(state.drawCard());
				}
			}
			notifyObservers();
			
			boolean seasonEnd = false;
			
			
		}
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
	
	private void notifyObservers() {
		for(Player p : players) {
			p.notifyObservers();
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	//driver stuff
	
	/**
	 * Runs the standard bidding option
	 * @param turn the index of the player who played the card
	 * @param card the card being bid on
	 * @return the index of the winner
	 */
	private Bid standardBidding(int turn, Card card, boolean isDouble) {
		boolean[] bidding = new boolean[players.length];//used to tell how many players are still bidding
		for(int i = 0; i < bidding.length; i++) {//all players are bidding
			bidding[i] = true;
		}

		int highestBid = 0;
		int highestBidder = turn;
		//while there is more than 1 player bidding
		int stillBidding = 0;//used for checking how many players are bidding
		do {
			//hasWinner will only be true if only one player has not backed out of bidding
			for(int biddingTurn = 0; biddingTurn < players.length; biddingTurn++) {
				//skip people who are no longer in
				if(!bidding[(turn+biddingTurn+1)%players.length]) {
					continue;
				}

				//get the price a player is willing to pay
				OGS.stillBidding = Arrays.copyOf(bidding, bidding.length);//rsete this so players can mess with it if they want
				int bid = players[(turn+biddingTurn+1)%players.length].getBid(highestBid);
				if(bid==-1 || bid <= highestBid) {
					bidding[(turn+biddingTurn+1)%players.length] = false;
				} else {
					highestBid = bid;
					highestBidder = (turn+biddingTurn+1)%players.length;
				}

				//checks for winner
				stillBidding = 0;
				for(int b = 0; b < bidding.length; b++) {
					if(bidding[b]) {
						stillBidding++;
					}
					//System.out.print(bidding[b] + " ");//debug
				}
				//System.out.println(" " + stillBidding);//
				if(stillBidding < 2) {
					break;
				}
			}

		} while(stillBidding > 1);
		return new Bid(highestBidder,highestBid);
	}

	/**
	 * Goes to each player once and asks them for a bid, the highest wins
	 * @param turn the player index who played the card
	 * @param card the card being played
	 * @return the best bid
	 */
	private Bid onceAround(int turn, Card card, boolean isDouble) {
		int highestBid = 0;
		int highestBidder = turn;
		for(int i = 0; i < players.length; i++) {
			int biddingTurn = (turn+i+1)%players.length;
			int bid = players[biddingTurn].getBid(highestBid);
			if(bid > highestBid) {
				highestBid = bid;
				highestBidder = biddingTurn;
			}
		}

		return new Bid(highestBidder,highestBid);
	}

	/**
	 * Asks each player in turn if they would like to buy the card
	 * @param turn the index of the player selling the card
	 * @param card the card being sold
	 * @param price the price the card is being sold at
	 * @return the winning bidder index and price it was sold for
	 */
	private Bid fixedPrice(int turn, int price) {
		for(int i = 0; i < players.length-1; i++) {
			int biddingTurn = (turn+i+1)%players.length;
			if(players[biddingTurn].buy(price)) {
				return new Bid(biddingTurn, price);
			}
		}
		return new Bid(turn,price);
	}

	/**
	 * Has each player in turn say how much they are willing to pay and keeps track of the highest value
	 * @param card
	 * @return the winning bid
	 */
	private Bid sealed(Card card, boolean isDouble) {
		int highestBidder = -1;
		int highestPrice = -1;
		for(int i = 0; i < players.length; i++) {
			int bid = players[i].getBid(-1);
			if(bid > highestPrice) {
				highestPrice = bid;
				highestBidder = i;
			}
		}
		return new Bid(highestBidder,highestPrice);
	}

	/**
	 * This is just here to help pass around information
	 * @author William Elliman
	 *
	 */
	private class Bid{
		public final int index,price;
		public Bid(int index, int price) {
			this.index = index;
			this.price = price;
		}
	}
}
