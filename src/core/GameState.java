package core;

import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * @author William Elliman
 * 
 * This class is used to hold the higher level data about the game, as well as data that no players know.
 *
 */
public class GameState {
	private Player[] players;
	private ArrayList<Card> deck = new ArrayList<Card>();
	
	/**
	 * This is used to setup a new game.
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
	}
	
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
