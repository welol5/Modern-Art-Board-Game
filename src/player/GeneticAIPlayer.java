package player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import core.Artist;
import core.AuctionType;
import core.Card;
import core.ObservableGameState;
import core.ArtistCount;
import io.BasicIO;

/**
 * This player takes advantage of keeping track of the major actions made in the game. It keeps track of who
 * won what card and how much they bid on it. It then uses that infomation to calculate how much it can bid
 * so that it will make a profit and the best other player will not.
 * @author William Elliman
 *
 */
public class GeneticAIPlayer extends Player{

	private Random random = new Random();

	//memory
	private GeneticAIPlayerDB dataBase;
	
	//keep track of other players
	private Player[] players;
	private final int turnIndex;//keep track of where itself is in the list of turns
	private int turn = 0;
	
	//memory during bidding
	//private Card biddingCard;
	
	public GeneticAIPlayer(String name, BasicIO io, int playerCount, int turnIndex, GeneticAIPlayerDB dataBase) {
		super(name);
		
		//init player array
		players = new Player[playerCount];
		for(int i = 0; i < players.length; i++) {
			players[i] = new RandomPlayer(null,null);
		}
		
		this.turnIndex = turnIndex;
	}

	@Override
	public Card chooseCard(ObservableGameState state) {
		
		int cardToPlay = 0;
		double bestNextStateValue = Double.NEGATIVE_INFINITY;
		for(int i = 0; i < hand.size(); i++) {
			ArrayList<Card> tempHand = (ArrayList<Card>) hand.clone();
			tempHand.remove(tempHand.size());
			
			if(i == 0) {
				bestNextStateValue = dataBase.getValue(dataBase.new StateData(tempHand, state.getSeasonValues()));
			} else if(dataBase.getValue(dataBase.new StateData(tempHand, state.getSeasonValues())) > bestNextStateValue) {
				bestNextStateValue = dataBase.getValue(dataBase.new StateData(tempHand, state.getSeasonValues()));
				cardToPlay = i;
			}
		}
		
		return hand.remove(cardToPlay);
	}

	@Override
	public Card chooseSecondCard(Artist artist, ObservableGameState state) {
		//check if the hand contains the artist
		boolean contains = false;
		for(Card c : hand) {
			if(c.getArtist() == artist && c.getAuctionType() != AuctionType.DOUBLE) {
				contains = true;
				break;
			}
		}
		if(!contains) {
			return null;
		}

		//the player has a card that will work, choose one randomly
		Card card = null;
		int index = 0;
		while(card == null || (card.getArtist() != artist && card.getAuctionType() != AuctionType.DOUBLE)) {
			index = random.nextInt(hand.size());
			card = hand.get(index);
		}
		hand.remove(index);
		return card;
	}

	@Override
	public int getBid(ObservableGameState state) {
		int bestPlayer = -1;
		int bestPlayerMoney = -1;
		
		//first it to find the player who is doing the best that is not this AI
		for(int i = 0; i < players.length; i++) {
			//skip this player
			if(i == turnIndex) {
				continue;
			}
			
			//the best player will be found by finding the est values for each player
			//first calculate the players est value
			Artist[] top3 = state.getTopSeasonValues();
			int value = players[i].getMoney();
			for(Card c : players[i].getWinnings()) {
				if(c.getArtist() == top3[0]) {
					value += state.getArtistValue(top3[0]) + 30;
				} else if(c.getArtist() == top3[1]) {
					value += state.getArtistValue(top3[0]) + 20;
				} else if(c.getArtist() == top3[2]) {
					value += state.getArtistValue(top3[0]) + 10;
				}
			}
			
			//value now holds the players value
			if(value > bestPlayerMoney) {
				bestPlayerMoney = value;
				bestPlayer = i;
			}
		}
		
		//now the best other player has been found
		
		//find that players value
		Artist[] top3 = state.getTopSeasonValues();
		for(Card card : players[bestPlayer].getWinnings()) {
			if(card.getArtist() == top3[0]) {
				bestPlayerMoney += state.getArtistValue(card.getArtist()) + 30;
			} else if(card.getArtist() == top3[1]) {
				bestPlayerMoney += state.getArtistValue(card.getArtist()) + 20;
			} else if(card.getArtist() == top3[2]) {
				bestPlayerMoney += state.getArtistValue(card.getArtist()) + 10;
			}
		}
		//bestPlayerMoney holds the highest value another player has
		
		//calculate this players value
		int AIvalue = money;
		for(Card card : getWinnings()) {
			if(card.getArtist() == top3[0]) {
				AIvalue += state.getArtistValue(card.getArtist()) + 30;
			} else if(card.getArtist() == top3[1]) {
				AIvalue += state.getArtistValue(card.getArtist()) + 20;
			} else if(card.getArtist() == top3[2]) {
				AIvalue += state.getArtistValue(card.getArtist()) + 10;
			}
		}
		
		//AIvalue and BestPlayerMoney hold money values
		
		//set the maxValue to the
		int maxValue = getValue(state);
		if(state.isDouble) {
			maxValue*=2;
		}
		//need to know if the player is still bidding
		//if the player is still bidding, do not let them win if it will make a profit
		if((state.card.getAuctionType() == AuctionType.ONCE_AROUND || state.card.getAuctionType() == AuctionType.SEALED) || state.stillBidding[bestPlayer]) {
			//find the cards value
			//if the card is the bestPlayes, they will profit more from this AI if more than half the value is paid
			if(turn == bestPlayer) {
				maxValue /= 2;
			}
			//if a profit can be made, don't let them win
		} else {
			maxValue /= 2;
		}
		
		//if it a once around return the max value this AI will pay
		if((state.card.getAuctionType() == AuctionType.ONCE_AROUND || state.card.getAuctionType() == AuctionType.SEALED) && maxValue > state.highestBid) {
			return maxValue;
		} else if (state.card.getAuctionType() == AuctionType.ONCE_AROUND || state.card.getAuctionType() == AuctionType.SEALED) {
			return -1;
		}
		
		//try to buy the painting for the lowest possible price
		//System.out.println(maxValue);
		if(maxValue > state.highestBid) {
			return state.highestBid + 1;
		} else {
			return -1;
		}
	}

	@Override
	public int getFixedPrice(ObservableGameState state) {
		int maxValue = getValue(state)/2;
		if(maxValue < money) {
			return maxValue;
		} else {
			return money;
		}
	}

	@Override
	public boolean buy(ObservableGameState state) {

		int value = getValue(state);

		if(state.highestBid < value/2 && money > state.highestBid) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void announceCard(Card card, boolean isDouble) {
		
	}

	/**
	 * Gets the value of the card/artist in a specific state
	 * @param state that contains the card being bid on
	 * @return the value of the artist of the card
	 */
	private int getValue(ObservableGameState state) {
		int value = 0;
		boolean inTop3 = false;
		int index = -1;
		for(int i = 0; i < state.getTopSeasonValues().length; i++) {
			if(state.getTopSeasonValues()[i] == state.card.getArtist()) {
				inTop3 = true;
				index = i;
			}
		}

		if(inTop3) {
			value = state.getArtistValue(state.card.getArtist()) + (30-(10*index));
		} else {
			value = 0;
		}
		return value;
	}
	
	@Override
	public void announceSeasonEnd(int season, ObservableGameState state) {
		Artist[] top3 = state.getTopSeasonValues();
		for(int i = 0; i < players.length; i++) {
			for(Player player : players) {
				for(Card c : player.getWinnings()) {
					if(top3[0] == c.getArtist() || top3[1] == c.getArtist() || top3[2] == c.getArtist()) {
						player.recive(state.getArtistValue(c.getArtist()));
					}
				}
			}
		}
	}
	
	@Override
	public void announceAuctionWinner(int turn, String name, int price) {
		
	}
}
