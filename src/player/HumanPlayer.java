package player;

import core.Card;
import core.Player;
import io.BasicIO;

public class HumanPlayer extends Player{
	
	BasicIO io;

	public HumanPlayer(String name, BasicIO io) {
		super(name);
		this.io = io;
	}

	@Override
	public Card chooseCard() {
		
		io.showHand(hand);
		int index = io.getHandIndex(hand.size());
		return hand.remove(index);
	}

	@Override
	public int getBid(Card card) {
		return io.getBid(this);
	}

}
