package fxmlgui;

import java.util.Arrays;

import core.Artist;
import core.AuctionType;
import core.Card;
import core.GameState;
import core.ObservableGameState;
import player.Player;

/**
 * This is the class that controls the logic of the game. takes in the set of players and the initialized state of the game
 * and will start going to players and asking them to preform actions.
 * @author William Elliman
 *
 */
public class GameDriver {

	private Player[] players;
	private GameState state;
	private ObservableGameState OGS;
	private boolean debugPrinting = true;

	/**
	 * 
	 * @param players The list of players that will be playing the game.
	 * @param state The state of the game, freshly created.
	 * @param OGS The observable game state.
	 */
	public GameDriver(Player[] players, GameState state, ObservableGameState OGS) {
		this.players = players;
		this.state = state;
		this.OGS = OGS;
	}
	
	/**
	 * 
	 * @param players The list of players that will be playing the game.
	 * @param state The state of the game, freshly created.
	 * @param OGS The observable game state.
	 * @param debugPrinting Should be set to true if the debug statements should be printed.
	 */
	public GameDriver(Player[] players, GameState state, ObservableGameState OGS, boolean debugPrinting) {
		this.players = players;
		this.state = state;
		this.OGS = OGS;
		this.debugPrinting = debugPrinting;
	}
	
	/**
	 * Resets game to be like it never happened.
	 * This does not seem to work.
	 */
	public void resetGame(boolean sealedOnly) {
		for(Player p : players) {
			p.reset();
		}
		state.reset(sealedOnly);
	}

	/**
	 * This function actually starts running the game.
	 */
	public Player playGame() {

		for(int season = 0; season < 4; season++) {

			//deal out cards
			for(int i = 0; i < state.dealAmounts[season]; i++) {
				for(Player p : players) {
					p.deal(state.drawCard());
				}
			}
			
			//debug
			//show money amounts
			for(Player p : players) {
//				if(debugPrinting)
				//System.out.println(p.name + " : " + p.getMoney());
			}

			state.resetSeason();

			int turnCount = 0;
			for(int turn = 0; true; turn = (turn+1)%players.length) {
				
				turnCount++;
				if(turnCount > 1000) {
					System.exit(0);
				}
				
				//have a player play a card
				Card card = players[turn].chooseCard();
				Card second = null;
				
				//debug
//				System.out.println(players[turn].name + " played " + card);
				
				if(card == null) {
					int hasHands = 0;
					for(Player p : players) {
						if(p.getHand().size() <= 0) {
							hasHands++;
						}
					}
					//debug
//					System.out.println("hasHands = " + hasHands);
//					if(hasHands == 1) {
//						for(int i = 0; i < players.length; i++) {
//							if(players[i].getHand().size() > 0) {
//								System.out.println(players[i].name);
//								System.out.println(players[i].getHand());
//							}
//						}
//					}
					//if no players have anything in their hand
					if(hasHands == 0) {
						endSeason();
						break;//break out of the season
					}
					continue;
				}

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
				//System.out.println(state.seasonEnded());
				if(state.seasonEnded()) {
					endSeason();
					break;//this break will break out of the season
				}

				//announce card(s)
				for(Player p : players) {
					p.announceCard(card, !(second == null));
				}
				if(debugPrinting)
				System.out.println("Player : " + players[turn].name + " :: " + card + " :: " + second);

				//card(s) are ready for auction
				Bid winningBid = null;
				if(card.getAuctionType() == AuctionType.FIXED_PRICE) {
					winningBid = fixedPrice(turn, players[turn].getFixedPrice());
				} else if(card.getAuctionType() == AuctionType.ONCE_AROUND) {
					winningBid = onceAround(turn);
				} else if(card.getAuctionType() == AuctionType.SEALED) {
					winningBid = sealed(turn);
				} else {
					winningBid = standardBidding(turn);
				}

				//let everybody know who won the auction
				for(Player p : players) {
					p.announceAuctionWinner(winningBid.index, players[winningBid.index].name, winningBid.price);
				}

				//System.out.println("Auction winner: " + players[winningBid.index].name + ":: Price : " + winningBid.price);

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
		
		//debug
		//show money amounts
		for(Player p : players) {
			if(debugPrinting)
			System.out.println(p.name + " : " + p.getMoney());
		}
		
		Player winner = null;
		for(Player p : players) {
			if(winner == null || winner.getMoney() < p.getMoney()) {
				winner = p;
			}
		}
		
		return winner;
	}
	
	private void endSeason() {
		//state.updateTopThree(state.getTopThree());
		if(debugPrinting)
		System.out.println("Season Ended");

		//give players what they have won
		for(Player p : players) {
			Artist[] top3 = state.getTopThree();
			if(debugPrinting)
			System.out.println(p.name);

			for(int i = 0; i < top3.length; i++) {
				for(Card c : p.getWinnings()) {
					if(c.getArtist() == top3[i]) {
						if(debugPrinting)
						System.out.println(c + " : " + state.getArtistValue(top3[i]));
						p.recive(state.getArtistValue(top3[i]));
					}
				}
			}

			//clear the players winnings
			p.clearWinnings();
		}

	}

	////////////////////////////////////////////////////////////////////////////////
	//Bidding stuff is below here

	/**
	 * Runs the standard bidding option
	 * @param turn the index of the player who played the card
	 * @return a Bid object that hold the index of the winner and the price they will pay.
	 */
	private Bid standardBidding(int turn) {
		boolean[] bidding = new boolean[players.length];//used to tell how many players are still bidding
		for(int i = 0; i < bidding.length; i++) {//all players are bidding
			bidding[i] = true;
		}

		int highestBid = 0;
		int highestBidder = turn;
		//while there is more than 1 player bidding
		int stillBidding = players.length;//used for checking how many players are bidding
		for(int biddingTurn = (turn+1)%players.length; stillBidding > 1; biddingTurn = (biddingTurn+1)%players.length) {
			//skip people who are no longer in
			if(!bidding[biddingTurn]) {
				continue;
			}

			//get the price a player is willing to pay
			OGS.stillBidding = Arrays.copyOf(bidding, bidding.length);//reset this so players can mess with it if they want
			int bid = players[biddingTurn].getBid(highestBid);
			if(bid==-1 || bid <= highestBid) {
				bidding[biddingTurn] = false;
			} else {
				highestBid = bid;
				highestBidder = biddingTurn;
			}

			//checks for winner
			stillBidding = 0;
			for(int b = 0; b < bidding.length; b++) {
				if(bidding[b]) {
					stillBidding++;
				}
				//System.out.print(bidding[b] + " ");//debug
			}
		}

		return new Bid(highestBidder,highestBid);
	}

	/**
	 * Goes to each player once and asks them for a bid, the highest wins
	 * @param turn the player index who played the card
	 * @return a Bid object that hold the index of the winner and the price they will pay.
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
	 * @param price the price the card is being sold at
	 * @return a Bid object that hold the index of the winner and the price they will pay.
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
	 * @param turn the index of the player selling the card
	 * @return a Bid object that hold the index of the winner and the price they will pay.
	 */
	private Bid sealed(int turn) {
		int highestBidder = turn;
		int highestPrice = 0;
		for(int i = 0; i < players.length; i++) {
			int bid = players[(i+turn+1)%players.length].getBid(-1);
			if(bid > highestPrice) {
				highestPrice = bid;
				highestBidder = (i+turn+1)%players.length;
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
		/**
		 * @param index of the player in the players array.
		 * @param price the player is willing to pay.
		 */
		public Bid(int index, int price) {
			this.index = index;
			this.price = price;
		}
	}
}
