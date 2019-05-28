package player;

import core.Artist;
import core.Card;
import io.BasicIO;

public class HumanPlayer extends Player{
	
	BasicIO io;

	public HumanPlayer(String name, BasicIO io) {
		super(name);
		this.io = io;
	}

	@Override
	public Card chooseCard() {
		
		io.showHand(this, hand);
		int index = io.getHandIndex(hand.size());
		return hand.remove(index);
	}

	@Override
	public int getBid(Card card, int highestSoFar) {
		
		return io.getBid(name, money, highestSoFar);
	}

	@Override
	public int getFixedPrice(Card card) {
		return io.getFixedPrice(card);
	}

	@Override
	public boolean buy(Card card, int price) {
		//does not allow player to go into debt
		if(price > money) {
			return false;
		}
		return io.askPlayertoBuy(card, price);
	}

	@Override
	public Card chooseSecondCard(Artist artist) {
		
		//skip asking for a second card if a player does not have one
		boolean contains = false;
		for(Card card : hand) {
			if(card.getArtist() == artist) {
				contains = true;
			}
		}
		if(!contains) {
			return null;
		}
		
		io.showHand(this, hand, artist);
		return hand.get(io.getHandIndex(hand, artist));
	}

}
