package core;

import java.util.ArrayList;

import io.BasicIO;
import io.CommandLine;
import io.IOType;
import player.BasicAIPlayer;
import player.HumanPlayer;
import player.Player;
import player.PlayerType;
import player.RandomPlayer;

/**
 * The GameDriver class is the main functional class of this program. It deals with keeping track of the rules and executing them when needed.
 * It also holds onto a GameState object that holds the data of the state of the game.
 * 
 * It also on its on thread so in the future players could have time limits (may be needed for some AI)
 * 
 * @author William Elliman
 *
 */
public class GameDriver implements Runnable{
	
	//multiple games vars
	private int iterations = 1;

	//Defaults to make testing easier
	private static final String[] defaultNames = {"Will","RandomPlayer2","AIPlayer"};
	private static final PlayerType[] defaultTypes = {PlayerType.Human,PlayerType.Random,PlayerType.BasicAI};

	//IO var
	private BasicIO io;

	//GameState var
	GameState state;
	//ObservableGameState OGS;
	Player[] players;
	String[] names;

	/**
	 * Setup the program by giving it a IO type and tell it if it will be training genetic AI.
	 * @param type the type of IO that the game will use
	 * @param aiTraining
	 */
	public GameDriver(IOType type, boolean aiTraining){
		if(type == IOType.COMMAND_LINE) {
			io = new CommandLine();
		}
	}

	/**
	 * Setup the program by giving it a IO type and tell it if it will be training genetic AI.
	 * @param type the type of IO that the game will use
	 * @param aiTraining
	 * @param names that will be used for the players
	 */
	public GameDriver(IOType type, boolean aiTraining, String[] names) {
		if(type == IOType.COMMAND_LINE) {
			io = new CommandLine();
		}

		this.names = names;
	}

	@Override
	public void run() {
		int AIWins = 0;
		//double AIWinRatio = 0;//this can be used later
		for(int game = 0; game < iterations; game++) {
			//setup the game state
			state = new GameState(defaultNames.length);
			//make the observableState

			//Make the list of players
			players = makePlayers(defaultNames, io, defaultTypes);
			int turn = 0;//keeps track of whose turn it is

			//the game is now ready for the first season
			for(int i = 1; i <= 4; i++) {
				io.startSeason(i);
				state.resetSeason();
				//this is setup for the end of the season
				Artist[] top3;

				//deal out cards
				for(int d = 0; d < state.dealAmounts[i-1]; d++) {
					for(Player player : players) {
						player.deal(state.drawCard());
					}
				}

				boolean[] emptyHands = new boolean[players.length];
				for(;true;turn = (turn+1)%players.length) {

					//debug
					//show players money //this sometimes causes issues with io but its a debug thing anyway
					//				for(Player player : players) {
					//					System.out.println(player.name + " : " + player.getMoney());
					//				}

					//get the painting that people will bid on
					Card card = players[turn].chooseCard(new ObservableGameState(players.length, null, -1, state));
					//if null is returned, the player had no cards
					if(card == null) {
						emptyHands[turn] = true;

						//if all the players had no cards the season should end
						boolean allEmpty = true;
						for(int h = 0; h < emptyHands.length && allEmpty; h++) {
							allEmpty = allEmpty && emptyHands[h];
						}
						if(allEmpty) {
							top3 = state.getTopThree();
							state.updateTopThree(top3);
							break;
						}

						continue;
					}

					//check for a double auction
					Card second = null;
					if(card.getAuctionType() == AuctionType.DOUBLE) {
						second = card;
						card = players[turn].chooseSecondCard(second.getArtist(), new ObservableGameState(players.length, second, -1, state));
						//if card is null then other players should be asked
						if(card == null) {
							card = getSecondCard(second,turn, second.getArtist());
						}

						//if card is still null, no one put in a second and a standard auction should occur
						if(card == null) {
							card = second;
							second = null;//need to remove pointer
						}
					}

					boolean seasonEnd = false;
					if(second == null) {
						seasonEnd = state.sell(card.getArtist(), false);
					} else {
						seasonEnd = state.sell(card.getArtist(), true);
					}

					//announce the played card to the players
					for(Player player : players) {
						player.announceCard(card, !(second == null));
					}

					if(!seasonEnd) {//this checks if the season is over by asking GameState
						//the bidding can now begin
						io.announceCard(card);//TODO deal with doubleAuctions better
						Bid winningBid;
						if(card.getAuctionType() == AuctionType.ONCE_AROUND) {
							//System.out.println("Once Around");//debug
							winningBid = onceAround(turn,card,!(second == null));
						} else if(card.getAuctionType() == AuctionType.FIXED_PRICE){
							//System.out.println("Fixed");//debug
							winningBid = fixedPrice(turn,card,players[turn].getFixedPrice(new ObservableGameState(players.length, card, -1, state)),!(second == null));
						} else if(card.getAuctionType() == AuctionType.SEALED){
							//System.out.println("Sealed");//debug
							winningBid = sealed(card,!(second == null));
						} else {
							winningBid = standardBidding(turn, card, !(second == null));
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
						io.auctionWinner(players[winningBid.index], winningBid.price);
					} else {
						//break out of the season once it is over
						break;
					}
				}

				//the season is over by this point
				top3 = state.getTopThree();
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

			//the game is over, time to see who won
			Player winner = null;
			for(Player player : players) {
				if(winner == null) {
					winner = player;
				} else if(winner.getMoney() < player.getMoney()) {
					winner = player;
				}
				//debug
				System.out.println(player.name + " : " + player.getMoney());
			}
			io.announceWinner(winner);
			if(winner.name.equalsIgnoreCase("AIplayer")) {
				AIWins++;
			}
		}
		System.out.println("Final results");
		//System.out.println("AI wins      : " + AIWins);
		System.out.println("AI win % : " + ((double)AIWins)/((double)iterations)*100);
	}

	/**
	 * This is used to take in a list of player names, types, and an io object to make the actual list of players.
	 * @param names of the players
	 * @param io for io
	 * @param types of the players
	 * @return the list of players
	 */
	private Player[] makePlayers(String[] names, BasicIO io, PlayerType[] types) {
		Player[] players = new Player[names.length];
		for(int i = 0; i < players.length; i++) {
			if(types[i] == PlayerType.Human) {
				players[i] = new HumanPlayer(names[i], io);
			} else if(types[i] == PlayerType.BasicAI) {
				if(names[i].matches("[pP][lL][aA][yY][eE][rR]")) {
					players[i] = new BasicAIPlayer("AIPlayer" + i, io);
				} else {
					players[i] = new BasicAIPlayer(names[i], io);
				}
			} else {
				if(names[i].matches("[pP][lL][aA][yY][eE][rR]")) {
					players[i] = new RandomPlayer("RandomPlayer" + i, io);
				} else {
					players[i] = new RandomPlayer(names[i], io);
				}
			}
		}
		return players;
	}

	/**
	 * Gets a second card from a player if the first was a double
	 * @param turn the index of the player
	 * @param artist the type of artist that is required
	 * @return the card the player decides to use
	 */
	private Card getSecondCard(Card firstCard, int turn, Artist artist) {
		Card card = null;
		for(int i = 0; i < players.length; i++) {
			int playerTurn = (turn + i + 1)%players.length;
			card = players[playerTurn].chooseSecondCard(artist, new ObservableGameState(players.length, firstCard, -1, state));
		}
		return card;
	}

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
				int bid = players[(turn+biddingTurn+1)%players.length].getBid(new ObservableGameState(players.length, card, highestBid, state), isDouble);
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
			int bid = players[biddingTurn].getBid(new ObservableGameState(players.length, card, highestBid, state), isDouble);
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
	private Bid fixedPrice(int turn, Card card, int price, boolean isDouble) {
		for(int i = 0; i < players.length-1; i++) {
			int biddingTurn = (turn+i+1)%players.length;
			if(players[biddingTurn].buy(new ObservableGameState(players.length, card, price, state))) {
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
			int bid = players[i].getBid(new ObservableGameState(players.length, card, -1, state), isDouble);
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
