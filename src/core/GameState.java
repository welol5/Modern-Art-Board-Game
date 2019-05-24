package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import io.BasicIO;
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
	
	private HashMap<Artist, Integer> seasonValues;//contains counts of how many times an artists paintings have been sold for the season
	private HashMap<Artist, Integer> artistValues;//contains the values for each artists paintings
	
	/**
	 * This is used to setup a new game. It resets and shuffles the deck, resets players and painting values.
	 * @param players
	 */
	public GameState(String[] players, BasicIO io) {
		//first all the new players need to be created
		this.players = new Player[players.length];
		this.players[0] = new HumanPlayer(players[0], io);
		for(int i = 1; i < players.length; i++) {
			this.players[i] = new RandomPlayer(players[i]);
		}
		
		//now the deck needs to be created and shuffled
		makeDeck();
		shuffleDeck();
		printDeck();//debug
		
		seasonValues = new HashMap<Artist, Integer>();
		artistValues = new HashMap<Artist, Integer>();
		for(Artist artist: Artist.values()) {
			artistValues.put(artist, 0);
		}
		
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
		for(Artist artist : Artist.values()) {
			seasonValues.put(artist, 0);
		}
	}
	
	/**
	 * Call this on a painting being sold to keep track of how many are sold from that artist.
	 * Also if the season has ended it will update the values of the paintings.
	 * @param artist
	 * @return an array of the top 3 artists if the season has ended
	 */
	public Artist[] sell(Artist artist) {
		//the code for this is really weird and could probably be improved quite a bit
		try {
			seasonValues.put(artist, seasonValues.get(artist)+1);
		} catch(NullPointerException e) {
			seasonValues.put(artist, 1);
		}
		
		if(seasonValues.get(artist) == 5) {
			//update the highest artist
			artistValues.put(artist, artistValues.get(artist)+30);
			//update the second highest
			Artist artist2 = null;;
			for(Artist artistTemp : Artist.values()) {
				//the line below checks if the artist is not the highest amount sold
				//if there is an artist in second place
				//if the artist in second (currently) should not be there
				if(artistTemp != artist && (artist2 == null || seasonValues.get(artistTemp) > seasonValues.get(artist2))) {
					artist2 = artistTemp;
				}
			}
			artistValues.put(artist2, artistValues.get(artist2)+20);
			//update the third highest
			Artist artist3 = null;;
			for(Artist artistTemp : Artist.values()) {
				//the line below checks if the artist is not the highest amount sold
				//the artist should not be the second highest
				//if there is an artist in second place
				//if the artist in second (currently) should not be there
				if(artistTemp != artist && artistTemp != artist2 && (artist3 == null || seasonValues.get(artistTemp) > seasonValues.get(artist3))) {
					artist3 = artistTemp;
				}
			}
			artistValues.put(artist3,artistValues.get(artist3)+10);
			Artist[] retVal = {artist,artist2,artist3};
			return retVal;
		} else {
			return null;
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
