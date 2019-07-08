package player;

import core.Artist;
import core.Card;
import core.ObservableGameState;
import fxmlgui.PlayerView;

/**
 * The class that allows human players to play by asking an IO object for all the humans decisions.
 * 
 * This may not work for now and it is not my priority.
 * 
 * @author William Elliman
 *
 */
public class HumanPlayer extends Player{

//	private Card biddingCard = null;
//	private boolean isDouble = false;

	public HumanPlayer(String name, PlayerView view, ObservableGameState OGS) {
		super(name,view, OGS);
	}

	@Override
	public Card chooseCard() {
		updateGUI();
		Card retval = hand.remove(playerView.getCard(null));
		updateGUI();
		return retval;
	}

	@Override
	public int getBid(int highestBid) {
		return playerView.getBid(highestBid);
	}

	@Override
	public int getFixedPrice() {
		return playerView.getFixedPrice();
	}

	@Override
	public boolean buy(int price) {
		//does not allow player to go into debt
		if(price > money) {
			return false;
		}
		
		return playerView.askPlayerToBuy(price);
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

		return hand.remove(playerView.getCard(artist));
	}

	@Override
	public void announceCard(Card card, boolean isDouble) {
//		biddingCard = card;
//		this.isDouble = isDouble;

		playerView.announceCard(card, isDouble);
	}

	@Override
	public void announceSeasonEnd(int season) {
		// TODO Auto-generated method stub
//		io.startSeason(season+1);
	}

	@Override
	public void announceAuctionWinner(int turn, String name, int price) {
//		biddingCard = null;
//		isDouble = false;
		
		playerView.announceAuctionWinner(name, price);
		System.out.println(name + " won this auction for " + price);
	}

}
