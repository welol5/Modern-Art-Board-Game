package player;

import java.util.Random;

import core.Artist;
import core.AuctionType;
import core.Card;
import core.ObservableGameState;
import io.BasicIO;

/**
 * This is a player that makes all of its decisions randomly
 * @author William Elliman
 *
 */
public class RandomPlayer extends Player {
	
	Random random = new Random();
	BasicIO io;

	public RandomPlayer(String name, BasicIO io) {
		super(name);
		this.io = io;
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
	public int getBid(int highestBid) {
		if(highestBid == -1) {
			return random.nextInt(money);
		} else if(highestBid >= money){
			return -1;
		} else {
			//randomly decides not to bid half the time
			if(random.nextDouble() < 0.5) {
				return -1;
			}
			return random.nextInt(money-highestBid)+highestBid;
		}
	}

	@Override
	public int getFixedPrice() {
		return random.nextInt(money);
	}

	@Override
	public boolean buy(int price) {
		if(price < money) {
			return random.nextBoolean();
		} else {
			return false;
		}
	}

	@Override
	public void announceCard(Card card, boolean isDouble) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void announceSeasonEnd(int season) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void announceAuctionWinner(int turn, String name, int price) {
		// TODO Auto-generated method stub
		
	}

}
