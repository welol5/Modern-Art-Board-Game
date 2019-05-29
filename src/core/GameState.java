package core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import io.BasicIO;
import player.BasicAIPlayer;
import player.HumanPlayer;
import player.Player;
import player.RandomPlayer;

/**
 * 
 * @author William Elliman
 * 
 * This class is used to hold the higher level data about the game, as well as data that no players know.
 *
 */
public class GameState {

	private static final int[] DEAL_3_PLAYERS = {10,6,6,0};
	private static final int[] DEAL_4_PLAYERS = {9,4,4,0};
	private static final int[] DEAL_5_PLAYERS = {8,3,3,0};
	public final int[] dealAmounts;

	private Player[] players;//The players
	private ArrayList<Card> deck = new ArrayList<Card>();//the deck of cards that no palyer can see

	private HashMap<Artist, Integer> artistValues;//contains the values for each artists paintings
	
	private ArrayList<SeasonValue> seasonCounts;//keeps track of how many of each painting has been sold and is sorted
	private static Comparator<SeasonValue> valuesComparitor = new Comparator<SeasonValue>() {
		@Override
		public int compare(SeasonValue o1, SeasonValue o2) {
			return o1.compareTo(o2);
		}
		
	};

	/**
	 * This is used to setup a new game. It resets and shuffles the deck, resets players and painting values.
	 * @param players
	 */
	public GameState(String[] players, BasicIO io) {
		//first all the new players need to be created
		this.players = new Player[players.length];
		//this.players[0] = new HumanPlayer(players[0], io);
		for(int i = 0; i < players.length-1; i++) {
			this.players[i] = new RandomPlayer(players[i], io);
		}
		this.players[players.length-1] = new BasicAIPlayer(players[players.length-1],io);

		//now the deck needs to be created and shuffled
		makeDeck();
		shuffleDeck();
		//printDeck();//debug
		//System.out.println("Deck sixe : " + deck.size());

		artistValues = new HashMap<Artist, Integer>();
		for(Artist artist: Artist.values()) {
			artistValues.put(artist, 0);
		}
		
		//init seasonCounts with a way to compare values
		//this is a special arraylist that sorts the list after a new item is added
		seasonCounts = new ArrayList<SeasonValue>();
		for(Artist artist : Artist.values()) {
			seasonCounts.add(new SeasonValue(artist));
		}
		seasonCounts.sort(valuesComparitor);
		System.out.println(seasonCounts.get(0));

		//set the deal amounts
		if(players.length == 3) {
			dealAmounts = DEAL_3_PLAYERS;
		} else if(players.length == 4) {
			dealAmounts = DEAL_4_PLAYERS;
		} else {
			dealAmounts = DEAL_5_PLAYERS;
		}
	}

	/**
	 * Resets the counts of the paintings sold
	 */
	public void resetSeason() {
		for(SeasonValue value : seasonCounts) {
			value.reset();
		}
		seasonCounts.sort(valuesComparitor);
	}

	/**
	 * Call this on a painting being sold to keep track of how many are sold from that artist.
	 * @param artist
	 * @return an array of the top 3 artists if the season has ended
	 */
	public boolean sell(Artist artist, boolean doubleAuction) {
		
		for(SeasonValue value : seasonCounts) {
			if(value.getArtist() == artist) {
				value.auction(doubleAuction);
			}
		}
		seasonCounts.sort(valuesComparitor);
		for(SeasonValue value : seasonCounts) {
			System.out.println(value);
		}
		
		if(seasonCounts.get(0).getCount() == 5) {
			updateTopThree(getTopThree());
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Gets the top 3 artists of the season
	 * @return the top 3 artist in an array
	 */
	public Artist[] getTopThree() {
		Artist[] top3 = new Artist[3];
		for(int i = 0; i < 3; i++) {
			top3[i] = seasonCounts.get(i).getArtist();
		}
		return top3;
	}
	
	public void updateTopThree(Artist[] top3) {
		int increase = 30;
		for(Artist artist : top3) {
			artistValues.put(artist, artistValues.get(artist)+increase);
			increase-=10;
		}
	}
	
	/**
	 * gets the amount of times an artists painting has been sold during the season
	 * @param artist that the value will be gotten for
	 * @return the count of the artists paintings that have been sold
	 */
	public int getSeasonValue(Artist artist) {
		for(int i = 0; i < seasonCounts.size(); i++) {
			if(seasonCounts.get(i).getArtist() == artist) {
				return seasonCounts.get(i).getCount();
			}
		}
		return -1;
	}

	/**
	 * Get the price of a specific artists paintings
	 * @param artist
	 * @return the value of a painting by the artist
	 */
	public int getArtistValue(Artist artist) {
		return artistValues.get(artist);
	}

	/**
	 * Get the player at an index
	 * @param index
	 * @return the player
	 */
	public Player[] getPlayers() {
		return players;
	}

	/**
	 * Prints the current state of the deck
	 */
	public void printDeck() {
		System.out.println("Deck from top to bottom");
		for(Card card : deck) {
			System.out.println(card);
		}
	}

	/**
	 * Draw a card from the deck
	 * @return
	 */
	public Card drawCard() {
		return deck.remove(0);
	}

	/**
	 * Shuffles the deck of cards
	 */
	private void shuffleDeck() {
		Random random = new Random();
		Card temp;
		//randomly move cards to the top 10000 times
		for(int i = 0; i < 10000; i++) {
			temp = deck.remove(random.nextInt(deck.size()));
			deck.add(temp);
		}
	}

	/**
	 * This clears and adds all of the default cards to the deck
	 */
	private void makeDeck() {
		deck.clear();

		//add the Lite Metal paintings
		deck.add(new Card(Artist.LITE_METAL,AuctionType.SEALED));//
		deck.add(new Card(Artist.LITE_METAL,AuctionType.SEALED));//
		deck.add(new Card(Artist.LITE_METAL,AuctionType.DOUBLE));//
		deck.add(new Card(Artist.LITE_METAL,AuctionType.DOUBLE));//
		deck.add(new Card(Artist.LITE_METAL,AuctionType.ONCE_AROUND));//
		deck.add(new Card(Artist.LITE_METAL,AuctionType.ONCE_AROUND));//
		deck.add(new Card(Artist.LITE_METAL,AuctionType.ONCE_AROUND));//
		deck.add(new Card(Artist.LITE_METAL,AuctionType.FIXED_PRICE));//
		deck.add(new Card(Artist.LITE_METAL,AuctionType.FIXED_PRICE));//
		deck.add(new Card(Artist.LITE_METAL,AuctionType.STANDARD));//
		deck.add(new Card(Artist.LITE_METAL,AuctionType.STANDARD));//
		deck.add(new Card(Artist.LITE_METAL,AuctionType.STANDARD));//

		//add the Yoko paintings
		deck.add(new Card(Artist.YOKO,AuctionType.SEALED));//
		deck.add(new Card(Artist.YOKO,AuctionType.SEALED));//
		deck.add(new Card(Artist.YOKO,AuctionType.SEALED));//
		deck.add(new Card(Artist.YOKO,AuctionType.DOUBLE));//
		deck.add(new Card(Artist.YOKO,AuctionType.DOUBLE));//
		deck.add(new Card(Artist.YOKO,AuctionType.ONCE_AROUND));//
		deck.add(new Card(Artist.YOKO,AuctionType.ONCE_AROUND));//
		deck.add(new Card(Artist.YOKO,AuctionType.FIXED_PRICE));//
		deck.add(new Card(Artist.YOKO,AuctionType.FIXED_PRICE));//
		deck.add(new Card(Artist.YOKO,AuctionType.FIXED_PRICE));//
		deck.add(new Card(Artist.YOKO,AuctionType.STANDARD));//
		deck.add(new Card(Artist.YOKO,AuctionType.STANDARD));//
		deck.add(new Card(Artist.YOKO,AuctionType.STANDARD));//

		//add the Karl Gitter paintings
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.SEALED));//
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.SEALED));//
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.SEALED));//
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.DOUBLE));//
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.DOUBLE));//
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.DOUBLE));//
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.ONCE_AROUND));//
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.ONCE_AROUND));//
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.ONCE_AROUND));//
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.FIXED_PRICE));//
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.FIXED_PRICE));//
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.FIXED_PRICE));//
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.STANDARD));//
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.STANDARD));//
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.STANDARD));//

		//add the Krypto paintings
		deck.add(new Card(Artist.KRYPTO,AuctionType.SEALED));//
		deck.add(new Card(Artist.KRYPTO,AuctionType.SEALED));//
		deck.add(new Card(Artist.KRYPTO,AuctionType.SEALED));//
		deck.add(new Card(Artist.KRYPTO,AuctionType.DOUBLE));//
		deck.add(new Card(Artist.KRYPTO,AuctionType.DOUBLE));//
		deck.add(new Card(Artist.KRYPTO,AuctionType.DOUBLE));//
		deck.add(new Card(Artist.KRYPTO,AuctionType.ONCE_AROUND));//
		deck.add(new Card(Artist.KRYPTO,AuctionType.ONCE_AROUND));//
		deck.add(new Card(Artist.KRYPTO,AuctionType.ONCE_AROUND));//
		deck.add(new Card(Artist.KRYPTO,AuctionType.FIXED_PRICE));//
		deck.add(new Card(Artist.KRYPTO,AuctionType.FIXED_PRICE));//
		deck.add(new Card(Artist.KRYPTO,AuctionType.FIXED_PRICE));//
		deck.add(new Card(Artist.KRYPTO,AuctionType.STANDARD));//
		deck.add(new Card(Artist.KRYPTO,AuctionType.STANDARD));//
		deck.add(new Card(Artist.KRYPTO,AuctionType.STANDARD));//
		deck.add(new Card(Artist.KRYPTO,AuctionType.STANDARD));//

		//add the Christin P. paintings
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.SEALED));//
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.SEALED));//
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.SEALED));//
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.DOUBLE));//
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.DOUBLE));//
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.ONCE_AROUND));//
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.ONCE_AROUND));//
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.ONCE_AROUND));//
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.FIXED_PRICE));//
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.FIXED_PRICE));//
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.FIXED_PRICE));//
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.STANDARD));//
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.STANDARD));//
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.STANDARD));//
	}
	
	/**
	 *  Holds the count of how many of each artist painting has been sold during the current season.
	 *  This exists to make getting the top 3 artist easier
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
}
