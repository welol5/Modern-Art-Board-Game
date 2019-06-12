package core;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import io.BasicIO;
import io.CommandLine;
import io.IOType;
import player.ReactiveAIPlayer;
import player.BasicPredictiveAIPlayer;
import player.BasicPredictiveAIPlayerV2;
import player.GeneticAIPlayer;
import player.GeneticAIPlayerDB;
import player.HumanPlayer;
import player.MemoryAIPlayer;
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
	private static final String[] defaultNames = {"Will", "PredictiveAIPlayer", "PredictiveAIPlayerV2"};
	private static final PlayerType[] defaultTypes = {PlayerType.HUMAN, PlayerType.BASIC_PREDICTIVE_AI, PlayerType.BASIC_PREDICTIVE_AI_V2};

	private GeneticAIPlayerDB database = null;//only need this with geneticAIPlayers

	//IO var
	private BasicIO io;

	//GameState var
	GameState state;
	ObservableGameState OGS;
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
		this(type,aiTraining);
		this.names = names;
	}

	@Override
	public void run() {
		//keep track of wins
		int[] wins = new int[defaultNames.length];
		for(int i = 0; i < wins.length; i++) {
			//set everything to 0
			wins[i] = 0;
		}

		for(int game = 0; game < iterations; game++) {
			//setup the game state
			state = new GameState(defaultNames.length);
			//make the observableState

			//Make the list of players
			OGS = new ObservableGameState(state);
			players = makePlayers(defaultNames, io, defaultTypes, OGS);
			int turn = 0;//keeps track of whose turn it is

			//the game is now ready for the first season
			for(int i = 1; i <= 4; i++) {
				//io.startSeason(i);
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
					Card card = players[turn].chooseCard();
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
						card = players[turn].chooseSecondCard(second.getArtist());
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
					boolean isDouble = !(second == null);
					seasonEnd = state.sell(card.getArtist(), isDouble);

					//announce the played card to the players
					for(Player player : players) {
						player.announceCard(card, isDouble);
					}

					if(!seasonEnd) {//this checks if the season is over by asking GameState
						//the bidding can now begin
						//io.announceCard(card);//TODO deal with doubleAuctions better
						Bid winningBid;
						if(card.getAuctionType() == AuctionType.ONCE_AROUND) {
							//System.out.println("Once Around");//debug
							winningBid = onceAround(turn,card,!(second == null));
						} else if(card.getAuctionType() == AuctionType.FIXED_PRICE){
							//System.out.println("Fixed");//debug
							winningBid = fixedPrice(turn,players[turn].getFixedPrice());
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
						//io.auctionWinner(players[winningBid.index], winningBid.price);
						//tell each player the bid has been won
						for(Player p : players) {
							p.announceAuctionWinner(winningBid.index, players[winningBid.index].name, winningBid.price);
						}
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
			int temp = 0;;
			int winnerTurn = -1;
			for(Player player : players) {
				if(winner == null) {
					winner = player;
					winnerTurn = temp;
				} else if(winner.getMoney() < player.getMoney()) {
					winner = player;
					winnerTurn = temp;
				}
				temp++;
				//debug
				//System.out.println(player.name + " : " + player.getMoney());
			}
			//io.announceWinner(winner);
			wins[winnerTurn] += 1;
			System.out.println("Games played : " + (game+1));
		}

		//show win %
		System.out.println("Final results");		
		for(int i = 0; i < wins.length; i++) {
			System.out.println(players[i].name + " win % : " + ((double)wins[i])/((double)iterations)*100);
		}
	}

	/**
	 * This is used to take in a list of player names, types, and an io object to make the actual list of players.
	 * @param names of the players
	 * @param io for io
	 * @param types of the players
	 * @return the list of players
	 */
	private Player[] makePlayers(String[] names, BasicIO io, PlayerType[] types, ObservableGameState state) {
		Player[] players = new Player[names.length];
		for(int i = 0; i < players.length; i++) {
			if(types[i] == PlayerType.HUMAN) {
				players[i] = new HumanPlayer(names[i], io);
			} else if(types[i] == PlayerType.REACTIVE_AI) {
				if(names[i].matches("[pP][lL][aA][yY][eE][rR]")) {
					players[i] = new ReactiveAIPlayer("ReactiveAIPlayer" + i, io, state);
				} else {
					players[i] = new ReactiveAIPlayer(names[i], io, state);
				}
			} else if(types[i] == PlayerType.MEMORY_AI){
				if(names[i].matches("[pP][lL][aA][yY][eE][rR]")) {
					players[i] = new MemoryAIPlayer("MemoryAIPlayer" + i, io, state, players.length, i);
				} else {
					players[i] = new MemoryAIPlayer(names[i], io, state, players.length, i);
				}
			} else if(types[i] == PlayerType.BASIC_PREDICTIVE_AI){
				if(names[i].matches("[pP][lL][aA][yY][eE][rR]")) {
					players[i] = new BasicPredictiveAIPlayer("PredictiveAIPlayer" + i, io, state, players.length, i);
				} else {
					players[i] = new BasicPredictiveAIPlayer(names[i], io, state, players.length, i);
				}
			} else if(types[i] == PlayerType.BASIC_PREDICTIVE_AI_V2){
				if(names[i].matches("[pP][lL][aA][yY][eE][rR]")) {
					players[i] = new BasicPredictiveAIPlayerV2("PredictiveAIPlayerV2" + i, io, state, players.length, i);
				} else {
					players[i] = new BasicPredictiveAIPlayerV2(names[i], io, state, players.length, i);
				}
			} else if(types[i] == PlayerType.GENETIC_AI){
				//aiTraining = i;//set the index so that learn can be called on this player
				if(names[i].matches("[pP][lL][aA][yY][eE][rR]")) {
					players[i] = new GeneticAIPlayer("GeneticAIPlayer" + i, io,state, players.length, i, database,0.05,0.2);
				} else {
					players[i] = new GeneticAIPlayer(names[i], io,state, players.length, i, database,0.05,0.2);
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
	 * @param turn the index of the player that played the double card
	 * @param artist the type of artist that is required
	 * @return the card the player decides to use
	 */
	private Card getSecondCard(Card firstCard, int turn, Artist artist) {
		Card card = null;
		for(int i = 0; i < players.length; i++) {
			int playerTurn = (turn + i + 1)%players.length;
			card = players[playerTurn].chooseSecondCard(artist);
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
