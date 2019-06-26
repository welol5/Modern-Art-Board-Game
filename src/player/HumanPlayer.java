package player;

import core.Artist;
import core.AuctionType;
import core.Card;
import core.ObservableGameState;
import gui.PlayerView;
import io.BasicIO;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * The class that allows human players to play by asking an IO object for all the humans decisions.
 * 
 * This may not work for now and it is not my priority.
 * 
 * @author William Elliman
 *
 */
public class HumanPlayer extends Player{

	private Card biddingCard = null;
	private boolean isDouble = false;
	
	//gui var for interaction
	private Pane guiArea;

	public HumanPlayer(String name, ObservableGameState OGS) {
		super(name);
	}

	@Override
	public void chooseCard() {

		io.showHand(this, hand);
		int index = io.getHandIndex(hand.size());
		chosenCard = hand.remove(index);
	}

	@Override
	public void getBid(int highestBid) {
		bid = io.getBid(name, money, highestBid);
	}

	@Override
	public void getFixedPrice() {
		bid = io.getFixedPrice(biddingCard);
	}

	@Override
	public void buy(int price) {
		//does not allow player to go into debt
		if(price > money) {
			buy = false;
			return;
		}
		buy = io.askPlayertoBuy(biddingCard, price);
	}

	@Override
	public void chooseSecondCard(Artist artist) {

		//skip asking for a second card if a player does not have one
		boolean contains = false;
		for(Card card : hand) {
			if(card.getArtist() == artist) {
				contains = true;
			}
		}
		if(!contains) {
			chosenCard = null;
			return;
		}

		io.showHand(this, hand, artist);
		chosenCard = hand.remove(io.getHandIndex(hand, artist));
	}

	@Override
	public void announceCard(Card card, boolean isDouble) {
		biddingCard = card;
		this.isDouble = isDouble;

		//tell the player what is going on
		io.announceCard(biddingCard);
		if(isDouble) {
			io.announceAuctionType(AuctionType.DOUBLE);
		}
		//io.announceAuctionType(card.getAuctionType());
	}

	@Override
	public void announceSeasonEnd(int season) {
		// TODO Auto-generated method stub
		io.startSeason(season+1);
	}

	@Override
	public void announceAuctionWinner(int turn, String name, int price) {
		biddingCard = null;
		isDouble = false;
		io.auctionWinner(name, price);
	}

}
