package player;

import java.util.Random;

import core.Artist;
import core.Card;
import io.BasicIO;

public class RandomPlayer extends Player {
	
	Random random = new Random();
	BasicIO io;

	public RandomPlayer(String name, BasicIO io) {
		super(name);
		this.io = io;
	}

	@Override
	public Card chooseCard() {
		return hand.remove(random.nextInt(hand.size()));
	}

	@Override
	public Card chooseSecondCard(Artist artist) {
		//check if the hand contains the artist
		boolean contains = false;
		for(Card c : hand) {
			if(c.getArtist() == artist) {
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
		while(card == null || card.getArtist() != artist) {
			index = random.nextInt(hand.size());
			card = hand.get(index);
		}
		hand.remove(index);
		return card;
	}

	@Override
	public int getBid(Card card, int highestSoFar) {
		if(highestSoFar == -1) {
			return random.nextInt(money);
		} else if(highestSoFar >= money){
			return -1;
		} else {
			//randomly decides not to bid half the time
			if(random.nextDouble() < 0.5) {
				return -1;
			}
			return random.nextInt(money-highestSoFar)+highestSoFar;
		}
	}

	@Override
	public int getFixedPrice(Card card) {
		return random.nextInt(money);
	}

	@Override
	public boolean buy(Card card, int price) {
		if(price < money) {
			return random.nextBoolean();
		} else {
			return false;
		}
	}

}
