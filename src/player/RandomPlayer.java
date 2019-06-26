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

	public RandomPlayer(String name) {
		super(name);
	}

	@Override
	public void chooseCard() {
		if(hand.size() == 0) {
			chosenCard = null;
			return;
		}
		chosenCard = hand.remove(random.nextInt(hand.size()));
	}

	@Override
	public void chooseSecondCard(Artist artist) {
		//check if the hand contains the artist
		boolean contains = false;
		for(Card c : hand) {
			if(c.getArtist() == artist && c.getAuctionType() != AuctionType.DOUBLE) {
				contains = true;
				break;
			}
		}
		if(!contains) {
			chosenCard = null;
			return;
		}
		
		//the player has a card that will work, choose one randomly
		Card card = null;
		int index = 0;
		while(card == null || (card.getArtist() != artist && card.getAuctionType() != AuctionType.DOUBLE)) {
			index = random.nextInt(hand.size());
			card = hand.get(index);
		}
		chosenCard = hand.remove(index);
	}

	@Override
	public void getBid(int highestBid) {
		
		if(highestBid == -1) {
			bid = random.nextInt(money);
			return;
		} else if(highestBid >= money){
			bid = -1;
			return;
		} else {
			//randomly decides not to bid half the time
			if(random.nextDouble() < 0.5) {
				bid = -1;
				return;
			}
			bid = random.nextInt(money-highestBid)+highestBid;
			return;
		}
	}

	@Override
	public void getFixedPrice() {
		bid = random.nextInt(money);
	}

	@Override
	public void buy(int price) {
		if(price < money) {
			buy = random.nextBoolean();
		} else {
			buy = false;
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
