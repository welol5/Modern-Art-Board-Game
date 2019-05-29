package player;

import java.util.Random;

import core.Artist;
import core.AuctionType;
import core.Card;
import io.BasicIO;

/**
 * WIP
 * @author William Elliman
 *
 */
public class BasicAIPlayer extends Player{

	Random random = new Random();
	BasicIO io;

	public BasicAIPlayer(String name, BasicIO io) {
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
	public int getBid(Card card, int highestSoFar, boolean isDouble) {
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

	private Artist getFavoredArtist() {
		int yoko = 0;
		int krypto = 0;
		int christin = 0;
		int lite = 0;
		int karl = 0;
		
		for(Card card : winnings) {
			if(card.getArtist() == Artist.YOKO) {
				yoko++;
			} else if(card.getArtist() == Artist.KRYPTO) {
				krypto++;
			} else if(card.getArtist() == Artist.CHRISTIN_P) {
				christin++;
			} else if(card.getArtist() == Artist.LITE_METAL) {
				lite++;
			} else {
				karl++;
			}
		}
		
		if(yoko > krypto && yoko > christin && yoko > lite && yoko > karl) {
			return Artist.YOKO;
		} //else if(krypto > )
		return null;
	}
}
