package player;

import java.util.ArrayList;
import java.util.Random;

import core.Artist;
import core.AuctionType;
import core.Card;
import core.ObservableGameState;
import core.ArtistCount;
import io.BasicIO;

/**
 * WIP
 * @author William Elliman
 *
 */
public class MemoryAIPlayer extends Player{

	private Random random = new Random();

	//memory
	//hand keeps track of the cards in the players hand
	private ArrayList<ArtistCount> playedCards = new ArrayList<ArtistCount>();//this could probably be an array
	private ArtistPlayChance[] chances = new ArtistPlayChance[Artist.values().length];

	public MemoryAIPlayer(String name, BasicIO io) {
		super(name);

		//init playedCards to 0s
		for(Artist artist : Artist.values()) {
			playedCards.add(new ArtistCount(artist));
		}

		//init ArtistPlayChances
		for(int i = 0; i < Artist.values().length; i++) {
			chances[i] = new ArtistPlayChance(Artist.values()[i]);
		}
	}

	@Override
	public Card chooseCard(ObservableGameState state) {
		//go through the artists in terms of most to least favored
		for(int f = 0; f < Artist.values().length; f++) {
			Artist favored = chooseFavordArtist(state, f);
			Card bestCard = null;

			//if a card that is a double auction of the favored artist can be found, play it
			//requires a second card to be present
			for(Card card : hand) {
				if(card.getArtist() == favored && card.getAuctionType() == AuctionType.DOUBLE) {
					bestCard = card;
				}
			}
			//bestCard will be null if there are no double or if none exist
			if(bestCard != null) {
				hand.remove(hand.indexOf(bestCard));
				for(Card card : hand) {
					if(card.getArtist() == bestCard.getArtist()) {
						return bestCard;
					}
				}
				//no other cards had a matching artist
			} else {
				//no doubles exist so return the first one if any exist
				for(Card card : hand) {
					if(card.getArtist() == favored) {
						hand.remove(hand.indexOf(card));
						return card;
					}
				}
			}
		}

		//this will be left here until the full method is implemented
		if(hand.size() == 0) {
			return null;
		}
		return hand.remove(random.nextInt(hand.size()));
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
	public int getBid(ObservableGameState state, boolean isDouble) {

		//first thing to do is to find the max the ai is willing to pay
		//starting with it always being half its est. value 
		int value  = getValue(state);
		if(isDouble) {
			value *=2;
		}

		int maxValue = value/2;

		if(state.highestBid < maxValue && money > maxValue+1) {
			return state.highestBid+1;
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
		for(int i = 0; i < playedCards.size(); i++) {
			if(playedCards.get(i).getArtist() == card.getArtist()) {
				playedCards.get(i).auction(isDouble);
			}
		}
		
		//find the total number of unknown cards
		int total = 0;
		for(ArtistCount c : playedCards) {
			total += c.getCount();
		}
		total += hand.size();
		
		//update all of the chances
		for(int i = 0; i < chances.length; i++) {
			//find how many cards have been played
			int artistPlayedCards = 0;
			for(int k = 0; k < playedCards.size(); k++) {
				if(playedCards.get(k).getArtist() == chances[i].artist) {
					artistPlayedCards = playedCards.get(k).getCount();
				}
			}
			
			//add in cards in hand
			for(Card handCard : hand) {
				if(handCard.getArtist() == chances[i].artist) {
					artistPlayedCards++;
				}
			}
			
			//update
			chances[i].updateChance(artistPlayedCards, total);
		}
	}

	/**
	 * 
	 * @param state state of the game
	 * @param favor if this is 0 it will return the most favored, 1 is second most favored and so on
	 * @return the favored artist
	 */
	private Artist chooseFavordArtist(ObservableGameState state, int favor) {

		//the favored artist will be the one with the fewest cards needed to complete the set
		//this also requires the cards needed to be in hand
		//if no set can be completed with this it will choose the one with the closest to completing a complete set

		ArtistCount[] artistCounts = state.getSeasonValues();

		int highestCount = -1;//used for picking if no set can be made
		Artist highestArtist = null;//used for picking if no set can be made
		for(int i = favor; i < artistCounts.length; i++) {
			int count = artistCounts[i].getCount();
			//include the card if one is being played
			if(state.card != null && state.card.getArtist() == artistCounts[i].getArtist()) {
				count++;
			}
			for(Card card : hand) {
				if(card.getArtist() == artistCounts[i].getArtist()) {
					count++;
				}
				if(count >= 5) {
					return artistCounts[i].getArtist();
				} else if(count > highestCount){
					highestCount = count;
					highestArtist = artistCounts[i].getArtist();
				}
			}

		}

		return highestArtist;
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

	private class ArtistPlayChance{
		private double chance = 0;
		public final Artist artist;
		public final double cardCount;

		public ArtistPlayChance(Artist artist) {
			this.artist = artist;

			int cardCount = 16;
			for(int i = 0; i < 5; i++) {
				if(artist == Artist.values()[i]) {
					cardCount = 12+i;
				}
			}
			this.cardCount = cardCount;
		}

		public void updateChance(int playedCards, int totalPlayedCards) {
			//this assumes an even distribution
			chance = (cardCount-playedCards)/(70-totalPlayedCards);
		}

		public double getChance() {
			return chance;
		}
	}
}
