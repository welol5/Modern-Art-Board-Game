package core;

import java.util.ArrayList;
import java.util.HashMap;

import io.BasicIO;
import io.CommandLine;
import io.IOType;
import player.Player;

/**
 * The GameDriver class is the main functional class of this program. It deals with keeping track of the rules and executing them when needed.
 * It also holds onto a GameState object that holds the data of the state of the game.
 * @author William Elliman
 *
 */
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
				//get the painting that people will bid on
				Card card = players[turn].chooseCard();

				//check for a double auction
				Card second = null;
				if(card.getAuctionType() == AuctionType.DOUBLE) {
					second = card;
					card = players[turn].chooseSecondCard(second.getArtist());
					//if card is null then other players should be asked
					if(card == null) {
						card = getSecondCard(turn, second.getArtist());
					}
					
					//if card is still null, no one put in a second and a standard auction should occur
					if(card == null) {
						card = second;
						second = null;//need to remove pointer
					}
				}

				top3 = state.sell(card.getArtist());
				if(top3 == null) {//this checks if the season is over by asking GameState
					//the bidding can now begin
					Bid winningBid;
					if(card.getAuctionType() == AuctionType.ONCE_AROUND) {
						System.out.println("Once Around");//debug
						winningBid = onceAround(turn,card);
					} else if(card.getAuctionType() == AuctionType.FIXED_PRICE){
						System.out.println("Fixed");//debug
						winningBid = fixedPrice(turn,card,players[turn].getFixedPrice(card));
					} else if(card.getAuctionType() == AuctionType.SEALED){
						System.out.println("Sealed");//debug
						winningBid = sealed(card);
					} else {
						winningBid = standardBidding(turn, card);
					}

					//execute the bid
					if(winningBid.index == turn) {
						players[winningBid.index].pay(winningBid.price);
						players[winningBid.index].givePainting(card);
						if(second != null) {
							players[winningBid.index].givePainting(second);
						}
					} else {
						players[winningBid.index].pay(winningBid.price);
						players[turn].recive(winningBid.price);
						players[winningBid.index].givePainting(card);
						if(second != null) {
							players[winningBid.index].givePainting(second);
						}
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

	private Card getSecondCard(int turn, Artist artist) {
		Card card = null;
		for(int i = 0; i < players.length; i++) {
			int playerTurn = (turn + i + 1)%players.length;
			card = players[playerTurn].chooseSecondCard(artist);
		}
		return null;
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
		int stillBidding = 0;//used for checking how many players are bidding
		do {
			//hasWinner will only be true if only one player has not backed out of bidding
			for(int biddingTurn = 0; biddingTurn < players.length; biddingTurn++) {
				//skip people who are no longer in
				if(!bidding[(turn+biddingTurn+1)%players.length]) {
					continue;
				}

				//get the price a player is willing to pay
				int bid = players[(turn+biddingTurn+1)%players.length].getBid(card, highestBid);
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
					System.out.print(bidding[b] + " ");
				}
				System.out.println(" " + stillBidding);
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
	private Bid onceAround(int turn, Card card) {
		int highestBid = 0;
		int highestBidder = turn;
		for(int i = 0; i < players.length; i++) {
			int biddingTurn = (turn+i+1)%players.length;
			int bid = players[biddingTurn].getBid(card, highestBid);
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
	private Bid fixedPrice(int turn, Card card, int price) {
		for(int i = 0; i < players.length; i++) {
			int biddingTurn = (turn+i+1)%players.length;
			if(players[biddingTurn].buy(card, price)) {
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
	private Bid sealed(Card card) {
		int highestBidder = -1;
		int highestPrice = -1;
		for(int i = 0; i < players.length; i++) {
			int bid = players[i].getBid(card, -1);
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
