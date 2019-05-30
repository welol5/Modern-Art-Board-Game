package player;

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
 * WIP
 * @author William Elliman
 *
 */
public class BasicAIPlayer extends Player{

	private Random random = new Random();
	private BasicIO io;
	
	//memory
	//hand keeps track of the cards in the players hand
	private ArrayList<ArtistCount> playedCards = new ArrayList<ArtistCount>();//this could probably be an array
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
			playedCards.add(new ArtistCount(artist));
		}
	}

	@Override
	public Card chooseCard(ObservableGameState state) {
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
		if(state.highestBid == -1) {
			return random.nextInt(money);
		} else if(state.highestBid >= money){
			return -1;
		} else {
			//randomly decides not to bid half the time
			if(random.nextDouble() < 0.5) {
				return -1;
			}
			return random.nextInt(money-state.highestBid)+state.highestBid;
		}
	}

	@Override
	public int getFixedPrice(ObservableGameState state) {
		return random.nextInt(money);
	}

	@Override
	public boolean buy(ObservableGameState state) {
		if(state.highestBid < money) {
			return random.nextBoolean();
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
	}
	
	private Artist chooseFavordArtist() {
		//need to know cards in hand and the season values
		return null;
	}
	
	//below here are nested classes used for storing data
}
