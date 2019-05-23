package core;

import java.util.ArrayList;
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
		state = new GameState(io.getPlayers(), io);
		//get the players list to use later
		players = state.getPlayers();
		int turn = 0;//keeps track of whose turn it is

		////////////////////////////////////////////////////////////////////
		//everything past this point is incomplete

		//the game is now ready for the first season
		for(int i = 1; i <= 4; i++) {
			io.startSeason(i);
			//this is setup for the end of the season
			Artist[] top3;

			//deal out cards
			for(int d = 0; d < state.dealAmounts[i]; d++) {
				for(Player player : players) {
					player.deal(state.drawCard());
				}
			}

			for(;true;turn = (turn+1)%players.length) {
				Card card = players[turn].chooseCard();
				top3 = state.sell(card.getArtist());
				if(top3 == null) {//this checks if the season is over by asking GameState
					//the bidding can now begin
					Bid winningBid = standardBidding(turn, card);

					//execute the bid
					if(winningBid.index == turn) {
						players[winningBid.index].pay(winningBid.price);
						players[winningBid.index].givePainting(card);
					} else {
						players[winningBid.index].pay(winningBid.price);
						players[turn].recive(winningBid.price);
						players[winningBid.index].givePainting(card);
					}
				} else {
					//break out of the season once it is over
					break;
				}
			}

			//the season is over by this point
			for(Player player: players) {
				ArrayList<Card> paintings = player.getWinnings();
				for(Card card : paintings) {
					if(top3[0] == card.getArtist() || top3[1] == card.getArtist() || top3[2] == card.getArtist()) {
						player.recive(state.getArtistValue(card.getArtist()));
					}
				}
				//reset the players winnings
				player.clearWinnings();
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
		int stillBidding;//used for checking how many players are bidding
		do {
			stillBidding = 0;
			//hasWinner will only be true if only one player has not backed out of bidding
			for(int biddingTurn = 0; biddingTurn < players.length; biddingTurn++) {
				int bid = players[(turn+biddingTurn)%players.length].getBid(card);
				if(bid==-1 || bid < highestBid) {
					bidding[(turn+biddingTurn)%players.length] = false;
				} else {
					highestBid = bid;
					highestBidder = (turn+biddingTurn)%players.length;
				}
			}

			//checks for winner
			for(int b = 0; b < bidding.length; b++) {
				if(bidding[b]) {
					stillBidding++;
				}
			}

		} while(stillBidding > 1);
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
