package player;

import java.util.ArrayList;
import java.util.Random;

import core.Artist;
import core.AuctionType;
import core.Card;
import core.ObservableGameState;
import core.ArtistCount;

/**
 * This AI is has very solid rules for bidding and it will try to end the season as quickly as possible.
 * The rule it uses for bidding is to always only bid up to half what the painting is currently worth.
 * The rule it uses for choosing which card to play is it will try to end the season as fast as possible.
 * @author William Elliman
 *
 */
public class ReactiveAIPlayer extends Player{

	private Random random = new Random();

	/**
	 * The card that is currently being auctioned.
	 */
	protected Card biddingCard = null;
	
	/**
	 * True if the current auction is a double auction
	 */
	protected boolean isDouble = false;

	/**
	 * The state of the game.
	 */
	protected ObservableGameState state;

	/**
	 * 
	 * @param name of the player
	 * @param state the observable game state
	 */
	public ReactiveAIPlayer(String name, ObservableGameState state) {
		super(name);
		this.state = state;
		OGS = this.state;
	}

	@Override
	public Card chooseCard() {
		//go through the artists in terms of most to least favored
		for(int f = 0; f < Artist.values().length; f++) {
			Artist favored = chooseFavordArtist(f);
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
					if(card.getArtist() == favored && card.getAuctionType() != AuctionType.DOUBLE) {
						hand.remove(hand.indexOf(card));
						return card;
					}
				}
			}
		}

		//If all else fails return null for no card or a random one
		if(hand.size() == 0) {
			return null;
		}
		return hand.remove(random.nextInt(hand.size()));
	}

	@Override
	public Card chooseSecondCard(Artist artist) {
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
	public int getBid(int highestBid) {
		return getBid(highestBid, getValue()/2);
	}

	/**
	 * This is here for simplicity. It takes the highest value and the highest bid and determines what to bid.
	 * It also takes into account what kind of auction it is.
	 * @param highestBid the highest bid another player has made.
	 * @param highestValue the highest this AI is willing to pay.
	 * @return The price this AI will bid on the painting.
	 */
	protected int getBid(int highestBid, int highestValue) {

		//if it a once around return the max value this AI will pay
		if((biddingCard.getAuctionType() == AuctionType.ONCE_AROUND || biddingCard.getAuctionType() == AuctionType.SEALED) && highestValue > highestBid) {
			return highestValue;
		} else if (biddingCard.getAuctionType() == AuctionType.ONCE_AROUND || biddingCard.getAuctionType() == AuctionType.SEALED) {
			return -1;
		}

		//try to buy the painting for the lowest possible price
		//System.out.println(maxValue);
		if(highestValue > highestBid) {
			return highestBid + 1;
		} else {
			return -1;
		}
	}

	@Override
	public int getFixedPrice() {
		int maxValue = getValue()/2;
		if(maxValue < money) {
			return maxValue;
		} else {
			return money;
		}
	}

	@Override
	public boolean buy(int price) {

		int value = getValue();

		if(price < value/2 && money > price) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void announceCard(Card card, boolean isDouble) {
		this.biddingCard = card;
		this.isDouble = isDouble;
	}

	/**
	 * This will return the artist that this AI will try to play.
	 * @param favor if this is 0 it will return the most favored, 1 is second most favored and so on
	 * @return the favored artist
	 */
	protected Artist chooseFavordArtist(int favor) {

		//the favored artist will be the one with the fewest cards needed to complete the set
		//this also requires the cards needed to be in hand
		//if no set can be completed with this it will choose the one with the closest to completing a complete set

		ArtistCount[] artistCounts = state.getSeasonValues();

		int highestCount = -1;//used for picking if no set can be made
		Artist highestArtist = null;//used for picking if no set can be made
		for(int i = favor; i < artistCounts.length; i++) {
			int count = artistCounts[i].getCount();
			//include the card if one is being played
			if(biddingCard != null && biddingCard.getArtist() == artistCounts[i].getArtist()) {
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
	protected int getValue() {
		int value = 0;
		boolean inTop3 = false;
		int index = -1;
		for(int i = 0; i < state.getTopSeasonValues().length; i++) {
			if(state.getTopSeasonValues()[i] == biddingCard.getArtist()) {
				inTop3 = true;
				index = i;
			}
		}

		if(inTop3) {
			value = state.getArtistValue(biddingCard.getArtist()) + (30-(10*index));
		} else {
			value = 0;
		}
		return value;
	}

	@Override
	public void announceSeasonEnd(int season) {

	}

	@Override
	public void announceAuctionWinner(int turn, String name, int price) {
		biddingCard = null;
		isDouble = false;
	}
}
