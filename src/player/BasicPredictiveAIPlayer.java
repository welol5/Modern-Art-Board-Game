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
 * This player takes advantage of keeping track of the major actions made in the game. It keeps track of who
 * won what card and how much they bid on it. It then uses that infomation to calculate how much it can bid
 * so that it will make a profit and the best other player will not.
 * 
 * NEW STUFF
 * This AI will try to predict what other players will play based on the cards that they have
 * @author William Elliman
 *
 */
public class BasicPredictiveAIPlayer extends MemoryAIPlayer{

	private Random random = new Random();

	//memory
	
	//keep track of other players
	private int[] playerCardCounts;
	
	public BasicPredictiveAIPlayer(String name,ObservableGameState state, int playerCount, int turnIndex) {
		super(name,state,playerCount,turnIndex);
		
		//init playerCardCounts
		playerCardCounts = new int[playerCount];
		//init everything to 0 because cards are all delt at the same time
		for(int i = 0; i < playerCount; i++) {
			playerCardCounts[i] = 0;
		}
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

	/**
	 * 
	 * @param state state of the game
	 * @param favor if this is 0 it will return the most favored, 1 is second most favored and so on
	 * @return the favored artist
	 */
	protected Artist chooseFavordArtist(int favor) {
		//TODO replace this method with one that takes into consideration what other players have won
		//this will be done so that a search problem can start to be formulated

		//the favored artist will be the one with the fewest cards needed to complete the set
		//this also requires the cards needed to be in hand
		//if no set can be completed with this it will choose the one with the closest to completing a complete set
		
		//this AI will also try to predict what the other players will do
		//it will do it by just adding 1 to the season values of the other players favored artists
		//their favored artist will just be what they have won the most of

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
	
	//Override superclass implemented method
	@Override
	public void deal(Card card) {
		hand.add(card);
		//players got delt another card
		for(int i = 0; i < playerCardCounts.length; i++) {
			playerCardCounts[i]++;
		}
	}
}
