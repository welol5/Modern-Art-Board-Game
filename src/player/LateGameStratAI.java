package player;

import java.util.ArrayList;

import core.Artist;
import core.ArtistCount;
import core.AuctionType;
import core.Card;
import core.ObservableGameState;
import io.BasicIO;

public class LateGameStratAI extends BasicPredictiveAIPlayerV2{

	protected int season = 1;
	protected ArrayList<ArtistCount> unknownCards = new ArrayList<ArtistCount>();
	int cardCount = 70;


	public LateGameStratAI(String name, BasicIO io, ObservableGameState state, int playerCount, int turnIndex) {
		super(name, io, state, playerCount, turnIndex);

		//hardcode
		for(int i = 15-Artist.values().length; i > 15; i++) {
			unknownCards.add(new ArtistCount(Artist.values()[i-15+Artist.values().length],i));//this is confusing and I hate it
		}
	}

	@Override
	public Card chooseCard() {
		//go through the artists in terms of most to least favored
		Artist favored = chooseFavordArtist();
		Card bestCard = null;

		//if a card that is a double auction of the favored artist can be found, play it
		//requires a second card to be present
		for(Card card : hand) {
			if(card.getArtist() == favored && card.getAuctionType() == AuctionType.DOUBLE) {
				bestCard = card;
			}
		}
		//bestCard will be null if there are no double or if none exist
		if(bestCard != null) {
			hand.remove(hand.indexOf(bestCard));
			for(Card card : hand) {
				if(card.getArtist() == bestCard.getArtist()) {
					return bestCard;
				}
			}
			//no other cards had a matching artist
		} else {
			//no doubles exist so return the first one if any exist
			for(Card card : hand) {
				if(card.getArtist() == favored) {
					hand.remove(hand.indexOf(card));
					return card;
				}
			}
		}


		//this will be left here until the full method is implemented
		if(hand.size() == 0) {
			return null;
		}
		return hand.remove(random.nextInt(hand.size()));
	}

	public void announceSeasonEnd(int season) {
		//keep track of player money
		Artist[] top3 = state.getTopSeasonValues();
		for(Player player : players) {
			for(Card c : player.getWinnings()) {
				if(top3[0] == c.getArtist() || top3[1] == c.getArtist() || top3[2] == c.getArtist()) {
					player.recive(state.getArtistValue(c.getArtist()));
				}
			}
		}

		//reset favoredArtists
		for(int i = 0; i < favoredArtists.size(); i++) {
			favoredArtists.clear();
		}

		season++;
	}

	@Override
	public void announceCard(Card card, boolean isDouble) {

		//prep for bidding
		biddingCard = card;
		this.isDouble = isDouble;
		getBestOtherPlayer();

		//keep track of unknown cards
		for(ArtistCount c : unknownCards) {
			if(c.getArtist() == card.getArtist()) {
				c.removeCard();
				cardCount--;
				if(isDouble) {
					c.removeCard();
					cardCount--;
				}
			}
		}
	}

	@Override
	public void deal(Card card) {
		hand.add(card);
		//players got delt another card
		for(int i = 0; i < playerCardCounts.length; i++) {
			playerCardCounts[i]++;
		}

		//keep track of unknown cards
		for(ArtistCount c : unknownCards) {
			if(c.getArtist() == card.getArtist()) {
				c.removeCard();
				cardCount--;
			}
		}
	}
}
