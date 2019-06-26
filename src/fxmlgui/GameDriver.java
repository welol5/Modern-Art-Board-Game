package fxmlgui;

import core.Artist;
import core.AuctionType;
import core.Card;
import core.GameState;
import core.ObservableGameState;
import player.Player;

public class GameDriver {
	
	class BidRunner implements Runnable{
		
		private Player player;
		
		public BidRunner(Player p) {
			player = p;
		}

		@Override
		public void run() {
			player.chooseCard();
		}
	}
	
	class CardRunner implements Runnable{
		
		private Player player;
		private Artist artist;
		
		public CardRunner(Player p) {
			player = p;
			artist = null;
		}
		
		public CardRunner(Player p, Artist a) {
			player = p;
			artist = a;
		}
		
		@Override
		public void run() {
			if(artist == null) {
				player.chooseCard();
			} else {
				player.chooseSecondCard(artist);
			}
		}
	}
	
	class BuyRunner implements Runnable{
		
		private Player player;
		private int price;
		
		public BuyRunner(Player player, int price) {
			this.player = player;
			this.price = price;
		}

		@Override
		public void run() {
			player.buy(price);
		}
	}
	
	private Player[] players;
	private GameState state;
	private ObservableGameState OGS;
	
	public GameDriver(Player[] players, GameState state, ObservableGameState OGS) {
		this.players = players;
		this.state = state;
		this.OGS = OGS;
	}
	
	public void playGame() throws InterruptedException{
		
		for(int season = 0; season < 4; season++) {
			for(int turn = 0; true; turn = (turn+1)%players.length) {
				
				//have a player choose a card
				Thread cardRunnerThread = new Thread(new CardRunner(players[turn]));
				cardRunnerThread.start();
				//wait for the player to finish
				cardRunnerThread.join();
				
				Card card = players[turn].getChosenCard();
				//if the card is a double, get another card
				Card second = null;
				if(card.getAuctionType() == AuctionType.DOUBLE) {
					second = card;
					//have a player choose a card
					Thread secondCardRunnerThread = new Thread(new CardRunner(players[turn]));
					secondCardRunnerThread.start();
					//wait for the player to finish
					secondCardRunnerThread.join();
					//get the card from the player
					card = players[turn].getChosenCard();
				}
				
				//both cards are now set and second is null if it is not a double auction
				//announce the card to the players
				for(Player p : players) {
					p.announceCard(card, !(second == null));
				}
				
				//run the auctions
			}
		}
	}
	
	//auction subroutines
	
	private Bid sealedAuction(int turn) throws InterruptedException{
		
		int highestBid = 0;
		int bidder = turn;
		
		for(int i = 0; i < players.length; i++) {
			Thread bidRunnerThread = new Thread(new BidRunner(players[i]));
			bidRunnerThread.start();
			//wait for the player to finish
			bidRunnerThread.join();
			
			//compare
			if(players[i].getBid() > highestBid) {
				highestBid = players[i].getBid();
				bidder = i;
			}
		}
		
		return new Bid(bidder,highestBid);
	}
	
	private Bid fixedPriceAuction(int turn) throws InterruptedException{
		players[turn].getFixedPrice();
		int price = players[turn].getBid();//the price will be stored in the bid var
		
		for(int i = 0; i < players.length-1; i++) {
			players[(i+turn)%players.length].buy(price);
			if(players[(i+turn)%players.length].isBuy()) {
				return new Bid((i+turn)%players.length,price);
			}
		}
		
		return new Bid(turn,price);
	}
	
	//nested classes
	
	private class Bid{
		public final int turnIndex;
		public final int price;
		public Bid(int t, int p) {
			turnIndex = t;
			price = p;
		}
	}
}
