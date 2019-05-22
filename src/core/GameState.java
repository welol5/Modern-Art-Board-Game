package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * 
 * @author William Elliman
 * 
 * This class is used to hold the higher level data about the game, as well as data that no players know.
 *
 */
public class GameState {
	private Player[] players;//The players
	private ArrayList<Card> deck = new ArrayList<Card>();//the deck of cards that no palyer can see
	
	private HashMap<Artist, Integer> seasonValues;//contains counts of how many times an artists paintings have been sold for the season
	private HashMap<Artist, Integer> artistValues;//contains the values for each artists paintings
	
	/**
	 * This is used to setup a new game. It resets and shuffles the deck, resets players and painting values.
	 * @param players
	 */
	public GameState(String[] players) {
		//first all the new players need to be created
		this.players = new Player[players.length];
		for(int i = 0; i < players.length; i++) {
			this.players[i] = new Player(players[i]);
		}
		
		//now the deck needs to be created and shuffled
		makeDeck();
		shuffleDeck();
		printDeck();
		
		seasonValues = new HashMap<Artist, Integer>();
		artistValues = new HashMap<Artist, Integer>();
	}
	
	/**
	 * Call this on a painting being sold to keep track of how many are sold from that artist
	 * @param artist
	 * @return true of the season has ended
	 */
	public boolean sell(Artist artist) {
		seasonValues.put(artist, seasonValues.get(artist)+1);
		
		if(seasonValues.get(artist) == 5) {
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * This method gets the count of how many times an artists painting has been sold during the current season.
	 * @return a HashMap of the values
	 */
	public HashMap<Artist, Integer> getSeasonValues(){
		return seasonValues;
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
		deck.add(new Card(Artist.LITE_METAL,AuctionType.SEALED));
		deck.add(new Card(Artist.LITE_METAL,AuctionType.SEALED));
		deck.add(new Card(Artist.LITE_METAL,AuctionType.DOUBLE));
		deck.add(new Card(Artist.LITE_METAL,AuctionType.DOUBLE));
		deck.add(new Card(Artist.LITE_METAL,AuctionType.ONCE_AROUND));
		deck.add(new Card(Artist.LITE_METAL,AuctionType.ONCE_AROUND));
		deck.add(new Card(Artist.LITE_METAL,AuctionType.ONCE_AROUND));
		deck.add(new Card(Artist.LITE_METAL,AuctionType.FIXED_PRICE));
		deck.add(new Card(Artist.LITE_METAL,AuctionType.FIXED_PRICE));
		deck.add(new Card(Artist.LITE_METAL,AuctionType.STANDARD));
		deck.add(new Card(Artist.LITE_METAL,AuctionType.STANDARD));
		deck.add(new Card(Artist.LITE_METAL,AuctionType.STANDARD));
		
		//add the Yoko paintings
		deck.add(new Card(Artist.YOKO,AuctionType.SEALED));
		deck.add(new Card(Artist.YOKO,AuctionType.SEALED));
		deck.add(new Card(Artist.YOKO,AuctionType.SEALED));
		deck.add(new Card(Artist.YOKO,AuctionType.DOUBLE));
		deck.add(new Card(Artist.YOKO,AuctionType.DOUBLE));
		deck.add(new Card(Artist.YOKO,AuctionType.ONCE_AROUND));
		deck.add(new Card(Artist.YOKO,AuctionType.ONCE_AROUND));
		deck.add(new Card(Artist.YOKO,AuctionType.FIXED_PRICE));
		deck.add(new Card(Artist.YOKO,AuctionType.FIXED_PRICE));
		deck.add(new Card(Artist.YOKO,AuctionType.FIXED_PRICE));
		deck.add(new Card(Artist.YOKO,AuctionType.STANDARD));
		deck.add(new Card(Artist.YOKO,AuctionType.STANDARD));
		deck.add(new Card(Artist.YOKO,AuctionType.STANDARD));
		
		//add the Karl Gitter paintings
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.SEALED));
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.SEALED));
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.SEALED));
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.DOUBLE));
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.DOUBLE));
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.DOUBLE));
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.ONCE_AROUND));
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.ONCE_AROUND));
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.ONCE_AROUND));
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.FIXED_PRICE));
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.FIXED_PRICE));
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.FIXED_PRICE));
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.STANDARD));
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.STANDARD));
		deck.add(new Card(Artist.KARL_GITTER,AuctionType.STANDARD));
		
		//add the Krypto paintings
		deck.add(new Card(Artist.KRYPTO,AuctionType.SEALED));
		deck.add(new Card(Artist.KRYPTO,AuctionType.SEALED));
		deck.add(new Card(Artist.KRYPTO,AuctionType.SEALED));
		deck.add(new Card(Artist.KRYPTO,AuctionType.DOUBLE));
		deck.add(new Card(Artist.KRYPTO,AuctionType.DOUBLE));
		deck.add(new Card(Artist.KRYPTO,AuctionType.DOUBLE));
		deck.add(new Card(Artist.KRYPTO,AuctionType.ONCE_AROUND));
		deck.add(new Card(Artist.KRYPTO,AuctionType.ONCE_AROUND));
		deck.add(new Card(Artist.KRYPTO,AuctionType.ONCE_AROUND));
		deck.add(new Card(Artist.KRYPTO,AuctionType.FIXED_PRICE));
		deck.add(new Card(Artist.KRYPTO,AuctionType.FIXED_PRICE));
		deck.add(new Card(Artist.KRYPTO,AuctionType.FIXED_PRICE));
		deck.add(new Card(Artist.KRYPTO,AuctionType.STANDARD));
		deck.add(new Card(Artist.KRYPTO,AuctionType.STANDARD));
		deck.add(new Card(Artist.KRYPTO,AuctionType.STANDARD));
		deck.add(new Card(Artist.KRYPTO,AuctionType.STANDARD));
		
		//add the Christin P. paintings
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.SEALED));
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.SEALED));
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.SEALED));
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.DOUBLE));
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.DOUBLE));
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.ONCE_AROUND));
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.ONCE_AROUND));
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.ONCE_AROUND));
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.FIXED_PRICE));
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.FIXED_PRICE));
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.FIXED_PRICE));
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.STANDARD));
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.STANDARD));
		deck.add(new Card(Artist.CHRISTIN_P,AuctionType.STANDARD));
	}
}
