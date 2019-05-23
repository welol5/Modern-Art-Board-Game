package core;

import java.util.HashMap;

import io.BasicIO;
import io.CommandLine;
import io.IOType;

public class GameDriver implements Runnable{
	
	//IO var
	private BasicIO io;
	
	//GameState var
	GameState state;
	Player[] players;
	
	/**
	 * Setup the program by giving it a IO type.
	 * IO types are public constant ints in the GameDriver class.
	 * @param gameType
	 */
	public GameDriver(IOType type){
		if(type == IOType.COMMAND_LINE) {
			io = new CommandLine();
		}
	}

	@Override
	public void run() {
		//setup the game state
		state = new GameState(io.getPlayers());
		//get the players list to use later
		players = state.getPlayers();
		int turn = 0;//keeps track of whose turn it is
		
		////////////////////////////////////////////////////////////////////
		//everything past this point is incomplete
		
		//the game is now ready for the first season
		for(int i = 1; i <= 4; i++) {
			io.startSeason(i);
			//TODO check if the season is over
			for(;true;turn = (turn+1)%players.length) {
				Card card = players[turn].chooseCard();
				//the bidding can now begin
				Bid winningBid = standardBidding(turn, card);
				
				//execute the bid
				if(winningBid.index == turn) {
					players[winningBid.index].pay(winningBid.price);
				} else {
					players[winningBid.index].pay(winningBid.price);
					players[turn].recive(winningBid.price);
				}
			}
		}
		
	}
	
	/**
	 * Runs the standard bidding option
	 * @param turn the index of the player who played the card
	 * @param card the card being bid on
	 * @return the index of the winner
	 */
	private Bid standardBidding(int turn, Card card) {
		boolean[] bidding = new boolean[players.length];//used to tell how many players are still bidding
		for(int i = 0; i < bidding.length; i++) {//all players are bidding
			bidding[i] = true;
		}
		
		int highestBid = 0;
		int highestBidder = turn;
		//while there is more than 1 player bidding
		while(true) {
			//checks for winner
			boolean hasWinner = false;
			for(int b = 0; b < bidding.length; b++) {
				hasWinner = hasWinner^bidding[b];
			}
			//break out if there is a winner
			if(hasWinner) {
				break;
			}
			
			//hasWinner will only be true if only one player has not backed out of bidding
			for(int biddingTurn = 0; biddingTurn < players.length; biddingTurn++) {
				int bid = players[(turn+biddingTurn)%players.length].getBid(card);
				if(bid==-1 || bid < highestBid) {
					bidding[(turn+biddingTurn)%players.length] = false;
				}
				highestBid = bid;
				highestBidder = (turn+biddingTurn)%players.length;
			}
		}
		return new Bid(highestBidder,highestBid);
	}
	
	private class Bid{
		public final int index,price;
		public Bid(int index, int price) {
			this.index = index;
			this.price = price;
		}
	}
}
