package player;

import java.util.Random;

import core.Artist;
import core.Card;

public class RandomPlayer extends Player {
	
	Random random = new Random();

	public RandomPlayer(String name) {
		super(name);
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
		} else if(highestSoFar > money){
			return -1;
		} else {
			return random.nextInt(money-highestSoFar)+money;
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