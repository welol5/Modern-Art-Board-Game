package player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import core.Artist;
import core.AuctionType;
import core.Card;
import io.BasicIO;

/**
 * WIP
 * @author William Elliman
 *
 */
public class BasicAIPlayer extends Player{

	private Random random = new Random();
	private BasicIO io;
	
	//memory
	//hand keeps track of the cards in the players hand
	private ArrayList<SeasonValue> playedCards = new ArrayList<SeasonValue>();//this could probably be an array
	private HashMap<Artist,Integer> artistValues = new HashMap<Artist,Integer>();

	public BasicAIPlayer(String name, BasicIO io) {
		super(name);
		this.io = io;
		
		//init artist values to 0 at the beginning of the game
		for(Artist artist : Artist.values()) {
			artistValues.put(artist, 0);
		}
		//init playedCards to 0s
		for(Artist artist : Artist.values()) {
			playedCards.add(new SeasonValue(artist));
		}
	}

	@Override
	public Card chooseCard() {
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
	public int getBid(Card card, int highestSoFar, boolean isDouble) {
		if(highestSoFar == -1) {
			return random.nextInt(money);
		} else if(highestSoFar >= money){
			return -1;
		} else {
			//randomly decides not to bid half the time
			if(random.nextDouble() < 0.5) {
				return -1;
			}
			return random.nextInt(money-highestSoFar)+highestSoFar;
		}
	}

	@Override
	public int getFixedPrice(Card card) {
		return random.nextInt(money);
	}

	@Override
	public boolean buy(Card card, int price) {
		if(price < money) {
			return random.nextBoolean();
		} else {
			return false;
		}
	}
	
	//below here are nested classes used for storing data
	
	/**
	 *  Holds the count of how many of each artist painting has been sold during the current season.
	 *  This is here so the AI has memory
	 * @author William Elliman
	 *
	 */
	private class SeasonValue implements Comparable<SeasonValue>{
		
		private final Artist artist;
		private int count = 0;
		
		public SeasonValue(Artist artist) {
			this.artist = artist;
		}
		
		public void auction(boolean isDouble) {
			count++;
			if(isDouble) {
				count++;
			}
		}
		
		public int getCount() {
			return count;
		}
		
		public Artist getArtist() {
			return artist;
		}
		
		/**
		 * Resets the count for a new season
		 */
		public void reset() {
			count = 0;
		}
		
		public String toString() {
			return artist + " : " + count;
		}

		@Override
		public int compareTo(SeasonValue o) {
			int diff = count-o.getCount();
			if(diff != 0) {
				return -diff;
			} else {
				for(Artist artist : Artist.values()) {
					if(artist == this.artist) {
						return -1;
					} else if(artist == o.getArtist()){
						return 1;
					}
				}
			}
			return 0;
		}
	}

	@Override
	public void announceCard(Card card, boolean isDouble) {
		for(int i = 0; i < playedCards.size(); i++) {
			if(playedCards.get(i).getArtist() == card.getArtist()) {
				playedCards.get(i).auction(isDouble);
			}
		}
	}
}
