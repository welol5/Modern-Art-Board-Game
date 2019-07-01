package player;

import core.Artist;
import core.AuctionType;
import core.Card;
import core.ObservableGameState;
import fxmlgui.GUICard;
import fxmlgui.PlayerView;
import io.BasicIO;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

	public HumanPlayer(String name, PlayerView view, ObservableGameState OGS) {
		super(name,view, OGS);
	}

	@Override
	public Card chooseCard() {
		updateGUI();
		return hand.remove(playerView.getCard(null));
	}

	@Override
	public int getBid(int highestBid) {
		return playerView.getBid();
	}

	@Override
	public int getFixedPrice() {
		return playerView.getBid();
	}

	@Override
	public boolean buy(int price) {
		//does not allow player to go into debt
		if(price > money) {
			return false;
		}
		return true;
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

//		io.showHand(this, hand, artist);
//		chosenCard = hand.remove(io.getHandIndex(hand, artist));
		
		return null;
	}

	@Override
	public void announceCard(Card card, boolean isDouble) {
		biddingCard = card;
		this.isDouble = isDouble;

		playerView.announceCard(card, isDouble);
	}

	@Override
	public void announceSeasonEnd(int season) {
		// TODO Auto-generated method stub
//		io.startSeason(season+1);
	}

	@Override
	public void announceAuctionWinner(int turn, String name, int price) {
		biddingCard = null;
		isDouble = false;
//		io.auctionWinner(name, price);
	}

}
