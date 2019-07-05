package player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import core.Artist;
import core.AuctionType;
import core.Card;
import core.ObservableGameState;
import core.ArtistCount;
/**
 * This AI now starts to consider what cards other players have won when it determines what card to play.
 * It predicts by taking the winnings of other players and adding to the current season counts. It will expect
 * other players to play the artist that they have won the most of.
 * @author William Elliman
 *
 */
public class BasicPredictiveAIPlayer extends MemoryAIPlayer{

	private Random random = new Random();
	
	/**
	 * See {@link MemoryAIPlayer} for details.
	 * @param name
	 * @param state
	 * @param playerCount
	 * @param turnIndex
	 */
	public BasicPredictiveAIPlayer(String name,ObservableGameState state, int playerCount, int turnIndex) {
		super(name,state,playerCount,turnIndex);
	}

	@Override
	public Card chooseCard() {
		//go through the artists in terms of most to least favored
		for(int f = 0; f < Artist.values().length; f++) {
			Artist favored = chooseFavordArtist(f);
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
		}

		//this will be left here until the full method is implemented
		if(hand.size() == 0) {
			return null;
		}
		return hand.remove(random.nextInt(hand.size()));
	}

	@Override
	protected Artist chooseFavordArtist(int favor) {

		ArtistCount[] artistCounts = state.getSeasonValues();
		
		//add in other players winnings
		for(int i = 0; i < players.length; i++) {
			//skip this player
			if(i == turnIndex) {
				continue;
			}
			//find the highest count artist
			int highestCount = 0;
			Artist highestArtist = null;
			for(Artist artist : Artist.values()) {
				int count = 0;
				for(Card card : players[i].getWinnings()) {
					if(card.getArtist() == artist) {
						count++;
					}
				}
				if(count > highestCount) {
					highestCount = count;
					highestArtist = artist;
				}
			}
			
			//increment seasonValue
			//highestArtist is null if the player won no cards
			if(highestArtist != null) {
				for(ArtistCount c : artistCounts) {
					if(c.getArtist() == highestArtist) {
						c.auction(false);
					}
				}
			}
		}

		int highestCount = -1;//used for picking if no set can be made
		Artist highestArtist = null;//used for picking if no set can be made
		for(int i = favor; i < artistCounts.length; i++) {
			int count = artistCounts[i].getCount();
			//include the card if one is being played
			if(biddingCard != null && biddingCard.getArtist() == artistCounts[i].getArtist()) {
				count++;
			}
			for(Card card : hand) {
				if(card.getArtist() == artistCounts[i].getArtist()) {
					count++;
				}
				if(count >= 5) {
					return artistCounts[i].getArtist();
				} else if(count > highestCount){
					highestCount = count;
					highestArtist = artistCounts[i].getArtist();
				}
			}

		}

		return highestArtist;
	}
}
