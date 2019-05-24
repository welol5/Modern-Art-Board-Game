package player;

import core.Artist;
import core.Card;

public class RandomPlayer extends Player {

	public RandomPlayer(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Card chooseCard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Card chooseSecondCard(Artist artist) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBid(Card card, int highestSoFar) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFixedPrice(Card card) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean buy(Card card, int price) {
		// TODO Auto-generated method stub
		return false;
	}

}
