package core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Observable;
import java.util.Random;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 
 * @author William Elliman
 * 
 * This class is used to hold the higher level data about the game, as well as data that no players know.
 *
 */
public class GameState extends Observable{

	/**
	 * Holds the deal amounts for 3 player games.
	 */
	private static final int[] DEAL_3_PLAYERS = {10,6,6,0};
	
	/**
	 * Holds the deal amounts for 4 player games.
	 */
	private static final int[] DEAL_4_PLAYERS = {9,4,4,0};
	
	/**
	 * Holds the deal amounts for 5 player games.
	 */
	private static final int[] DEAL_5_PLAYERS = {8,3,3,0};
	
	/**
	 * The var used to hold the deal amounts.
	 */
	public final int[] dealAmounts;

	/**
	 * The amount of players in the game.
	 */
	public final int playerCount;

	/**
	 * The deck of painting cards.
	 */
	private ArrayList<Card> deck = new ArrayList<Card>();

	/**
	 * The list of artist and the prices their paintings will sell for
	 */
	private HashMap<Artist, Integer> artistValues;
	
	/**
	 * String values for the artistValues. This is used by the GUI by binding {@link Text} objects to them
	 */
	private HashMap<Artist, StringProperty> artistValuesStrings;

	/**
	 * Keeps track of how many of each artist has been sold during the season.
	 */
	private ArrayList<ArtistCount> seasonCounts;
	
	/**
	 * String values for the seasonCounts. This is used by the GUI by binding {@link Text} objects to them
	 */
	private static Comparator<ArtistCount> valuesComparitor = new Comparator<ArtistCount>() {
		@Override
		public int compare(ArtistCount o1, ArtistCount o2) {
			return o1.compareTo(o2);
		}

	};
	private HashMap<Artist, StringProperty> seasonCountsStrings;

	/**
	 * This is used to setup a new game. It resets and shuffles the deck, resets players and painting values.
	 * @param players
	 */
	public GameState(int playerCount) {
		this.playerCount = playerCount;

		reset();

		//set the deal amounts
		if(playerCount == 3) {
			dealAmounts = DEAL_3_PLAYERS;
		} else if(playerCount == 4) {
			dealAmounts = DEAL_4_PLAYERS;
		} else {
			dealAmounts = DEAL_5_PLAYERS;
		}

		//update the strings for observers
		updateStrings();
	}

	public void reset() {
		//now the deck needs to be created and shuffled
		makeDeck();
		shuffleDeck();
		//printDeck();//debug
		//System.out.println("Deck sixe : " + deck.size());

		artistValues = new HashMap<Artist, Integer>();
		for(Artist artist: Artist.values()) {
			artistValues.put(artist, 0);
		}

		//setup for observers
		artistValuesStrings = new HashMap<Artist, StringProperty>();
		for(Artist a : Artist.values()) {
			artistValuesStrings.put(a, new SimpleStringProperty());
		}

		//init seasonCounts with a way to compare values
		//this is a special arraylist that sorts the list after a new item is added
		seasonCounts = new ArrayList<ArtistCount>();
		for(Artist artist : Artist.values()) {
			seasonCounts.add(new ArtistCount(artist));
		}
		seasonCounts.sort(valuesComparitor);

		seasonCountsStrings = new HashMap<Artist, StringProperty>();
		for(Artist a : Artist.values()) {
			seasonCountsStrings.put(a, new SimpleStringProperty());
		}

		//update the strings for observers
		updateStrings();
	}

	/**
	 * Resets the counts of the paintings sold
	 */
	public void resetSeason() {
		for(ArtistCount value : seasonCounts) {
			value.reset();
		}
		seasonCounts.sort(valuesComparitor);

		//update the strings for observers
		updateStrings();
	}

	/**
	 * Call this on a painting being sold to keep track of how many are sold from that artist.
	 * @param artist
	 * @return an array of the top 3 artists if the season has ended
	 */
	public boolean sell(Artist artist, boolean doubleAuction) {

		for(ArtistCount value : seasonCounts) {
			if(value.getArtist() == artist) {
				value.auction(doubleAuction);
			}
		}
		seasonCounts.sort(valuesComparitor);

		//		for(ArtistCount ac : seasonCounts) {
		//			System.out.println(ac);
		//		}

		if(seasonCounts.get(0).getCount() >= 5) {
			updateTopThree(getTopThree());
			//update the strings for observers
			updateStrings();
			return true;
		} else {
			//update the strings for observers
			updateStrings();
			return false;
		}
	}

	public boolean seasonEnded() {
		if(seasonCounts.get(0).getCount() >= 5) {
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

	/**
	 * Goes to the top 3 artists and updates their prices
	 * @param top3 a list of the top 3 artists
	 */
	public void updateTopThree(Artist[] top3) {
		//System.out.println("update");
		int increase = 30;
		for(Artist artist : top3) {
			artistValues.put(artist, artistValues.get(artist)+increase);
			increase-=10;
		}

		//update the strings for observers
		updateStrings();
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
	 * @return returns the list of the artists and their counts ordered by value
	 */
	public ArtistCount[] getSeasonValues() {
		ArtistCount[] counts = new ArtistCount[seasonCounts.size()];
		return seasonCounts.toArray(counts);
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
	private void makeDeck(boolean sealedOnly) {
		deck.clear();

		if(!sealedOnly) {
			makeDeck();
			return;
		} else {
			for(int i = 0; i < 12; i++) {
				deck.add(new Card(Artist.LITE_METAL, AuctionType.SEALED));
			}
			for(int i = 0; i < 13; i++) {
				deck.add(new Card(Artist.YOKO, AuctionType.SEALED));
			}
			for(int i = 0; i < 14; i++) {
				deck.add(new Card(Artist.CHRISTIN_P, AuctionType.SEALED));
			}
			for(int i = 0; i < 15; i++) {
				deck.add(new Card(Artist.KARL_GITTER, AuctionType.SEALED));
			}
			for(int i = 0; i < 16; i++) {
				deck.add(new Card(Artist.KRYPTO, AuctionType.SEALED));
			}
		}
		
	}
	
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

	///////////////////////////////////////////////////////
	//update strings

	/**
	 * Updates the string values to match their numerical counterparts.
	 */
	private void updateStrings() {
		for(Artist artist : Artist.values()) {
			artistValuesStrings.get(artist).set(artistValues.get(artist).toString());
			for(ArtistCount c : seasonCounts) {
				if(c.getArtist() == artist) {
					seasonCountsStrings.get(artist).set("" + c.getCount());
				}
			}
		}

		//this is here so that the GUI thread is not spammed
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param artist that the string will be for.
	 * @return the {@link ReadOnlyStringProperty} for the amount of the artists paintings have been sold that season.
	 */
	public ReadOnlyStringProperty getArtistCountString(Artist artist) {
		return seasonCountsStrings.get(artist);
	}
	
	/**
	 * @param artist that the string will be for.
	 * @return the {@link ReadOnlyStringProperty} for the current value of an artits paintings.
	 */
	public ReadOnlyStringProperty getArtistValueString(Artist artist) {
		return artistValuesStrings.get(artist);
	}
}
