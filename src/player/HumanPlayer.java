package player;

import core.Artist;
import core.Card;
import core.ObservableGameState;
import io.BasicIO;

/**
 * The class that allows human players to play by asking an IO object for all the humans decisions.
 * 
 * This may not work for now and it is not my priority.
 * 
 * @author William Elliman
 *
 */
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
	public int getBid(int highestBid) {
		
		return io.getBid(name, money, highestBid);
	}

	@Override
	public int getFixedPrice() {
		return io.getFixedPrice(null);//TODO this needs to be fixed for human players
	}

	@Override
	public boolean buy(int price) {
		//does not allow player to go into debt
		if(price > money) {
			return false;
		}
		return io.askPlayertoBuy(null, price);//TODO fix for human players
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
		return hand.remove(io.getHandIndex(hand, artist));
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
