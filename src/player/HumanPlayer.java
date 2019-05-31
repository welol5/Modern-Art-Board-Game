package player;

import core.Artist;
import core.Card;
import core.ObservableGameState;
import io.BasicIO;

/**
 * The class that allows human players to play by asking an IO object for all the humans decisions
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
	public Card chooseCard(ObservableGameState state) {
		
		io.showHand(this, hand);
		int index = io.getHandIndex(hand.size());
		return hand.remove(index);
	}

	@Override
	public int getBid(ObservableGameState state) {
		
		return io.getBid(name, money, state.highestBid);
	}

	@Override
	public int getFixedPrice(ObservableGameState state) {
		return io.getFixedPrice(state.card);
	}

	@Override
	public boolean buy(ObservableGameState state) {
		//does not allow player to go into debt
		if(state.highestBid > money) {
			return false;
		}
		return io.askPlayertoBuy(state.card, state.highestBid);
	}

	@Override
	public Card chooseSecondCard(Artist artist, ObservableGameState state) {
		
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
	public void announceSeasonEnd(int season, ObservableGameState state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void announceAuctionWinner(int turn, String name, int price) {
		// TODO Auto-generated method stub
		
	}

}
