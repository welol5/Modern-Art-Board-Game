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
	public int getBid(Card card) {
		return io.getBid(this);
	}

	@Override
	public int getFixedPrice(Card card) {
		return io.getFixedPrice(card);
	}

	@Override
	public boolean buy(Card card, int price) {
		return io.askPlayertoBuy(card, price);
	}

	@Override
	public Card chooseSecondCard(Artist artist) {
		io.showHand(this, hand, artist);
		int index = io.getHandIndex(hand, artist);
		if(index == -1) {
			return null;
		} else {
			return hand.get(index);
		}
	}

}
