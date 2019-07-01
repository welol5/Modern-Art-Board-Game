package fxmlgui;

import java.util.Arrays;

import core.Artist;
import core.AuctionType;
import core.Card;
import core.GameState;
import core.ObservableGameState;
import player.Player;

public class GameDriver {

	private Player[] players;
	private GameState state;
	private ObservableGameState OGS;

	public GameDriver(Player[] players, GameState state, ObservableGameState OGS) {
		this.players = players;
		this.state = state;
		this.OGS = OGS;
	}

	public void playGame() {

		for(int season = 0; season < 4; season++) {

			//deal out cards
			for(int i = 0; i < state.dealAmounts[season]; i++) {
				for(Player p : players) {
					p.deal(state.drawCard());
				}
			}

			state.resetSeason();

			for(int turn = 0; true; turn = (turn+1)%players.length) {
				//have a player play a card
				Card card = players[turn].chooseCard();
				Card second = null;

				int secondPlayer = -1;

				if(card.getAuctionType() == AuctionType.DOUBLE) {
					second = card;
					card = players[turn].chooseSecondCard(second.getArtist());

					if(card == null) {
						for(int i = 0; i < players.length-1 && card == null; i++) {
							card = players[(turn+i)%players.length].chooseSecondCard(second.getArtist());
							secondPlayer = (turn+i)%players.length;
						}

						if(card == null) {
							card = second;
							second = null;
							secondPlayer = -1;
						}
					}
				}
				//card(s) selected

				//let the GameState know
				state.sell(card.getArtist(), second != null);

				//check to see if the season has ended
				if(state.seasonEnded()) {
					state.updateTopThree(state.getTopThree());

					//give players what they have won
					for(Player p : players) {
						Artist[] top3 = state.getTopThree();

						for(int i = 0; i < top3.length; i++) {
							for(Card c : p.getWinnings()) {
								if(c.getArtist() == top3[i]) {
									p.recive(state.getArtistValue(top3[i]));
								}
							}
						}

						//clear the players winnings
						p.clearWinnings();
					}

					break;//this break will break out of the season
				}

				//announce card(s)
				for(Player p : players) {
					p.announceCard(card, !(second == null));
				}

				//card(s) are ready for auction
				Bid winningBid = null;
				if(card.getAuctionType() == AuctionType.FIXED_PRICE) {
					winningBid = fixedPrice(turn, players[turn].getFixedPrice());
				} else if(card.getAuctionType() == AuctionType.ONCE_AROUND) {
					winningBid = onceAround(turn);
				} else if(card.getAuctionType() == AuctionType.SEALED) {
					winningBid = sealed();
				} else {
					winningBid = standardBidding(turn);
				}
				
				//let everybody know who won the auction
				for(Player p : players) {
					p.announceAuctionWinner(winningBid.index, players[winningBid.index].name, winningBid.price);
				}

				//The auction has been won, time to execute the order (66)
				if(winningBid.index == turn) {
					players[turn].pay(winningBid.price);
					players[turn].givePainting(card);
					if(second != null) {
						players[turn].givePainting(second);
					}
					if(secondPlayer != -1) {
						players[secondPlayer].recive(winningBid.price/2);
					}
				} else {
					players[winningBid.index].pay(winningBid.price);
					if(secondPlayer != -1) {
						if(secondPlayer != winningBid.index) {
							players[secondPlayer].recive(winningBid.price/2);
						}
						players[turn].recive(winningBid.price/2);
					} else {
						players[turn].recive(winningBid.price);
					}
					players[winningBid.index].givePainting(card);
					if(second != null) {
						players[winningBid.index].givePainting(second);
					}
				}
			}
		}

	}

	////////////////////////////////////////////////////////////////////////////////
	//Bidding stuff is below here

	/**
	 * Runs the standard bidding option
	 * @param turn the index of the player who played the card
	 * @param card the card being bid on
	 * @return the index of the winner
	 */
	private Bid standardBidding(int turn) {
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
	private Bid onceAround(int turn) {
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
	private Bid sealed() {
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
