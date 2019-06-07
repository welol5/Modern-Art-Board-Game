package player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import core.Artist;
import core.AuctionType;
import core.Card;
import core.ObservableGameState;
import core.ArtistCount;
import io.BasicIO;

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
public class BasicPredictiveAIPlayerV2 extends MemoryAIPlayer{

	private Random random = new Random();

	//memory
	private Artist[] favoredArtists = new Artist[3];//keeps track of what artists this AI want to win during the season

	//keep track of other players
	private int[] playerCardCounts;

	public BasicPredictiveAIPlayerV2(String name, BasicIO io,ObservableGameState state, int playerCount, int turnIndex) {
		super(name,io,state,playerCount,turnIndex);

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

	@Override
	public void announceCard(Card card, boolean isDouble) {

		//prep for bidding
		biddingCard = card;
		this.isDouble = isDouble;
		getBestOtherPlayer();
	}

	@Override
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
		for(int i = 0; i < favoredArtists.length; i++) {
			favoredArtists[i] = null;
		}
	}

	/**
	 * 
	 * @return the favored artist
	 */
	protected Artist chooseFavordArtist() {

		if(favoredArtists[0] == null) {//only want to reset favored artists if a new season has started
			//favored artists are going to be ones that the AI owns cards of both by winning auctions and in hand
			//first find how many different artists the AI has won paintings of
			boolean[] diffArtists = new boolean[Artist.values().length];
			for(int i = 0; i < diffArtists.length; i++) {
				for(Card c : winnings) {
					if(c.getArtist() == Artist.values()[i]) {
						diffArtists[i] = true;
						break;
					}
				}
			}
			//count how many artists
			int count = 0;
			for(int i = 0; i < diffArtists.length; i++) {
				if(diffArtists[i]) {
					count++;
				}
			}

			//After this first check, favored artists may be able to be chosen
			if(count >= 3) {
				//if there are 3 or more artists, the top 3 should be the favored ones
				ArrayList<ArtistCount> orderedFavoredList = new ArrayList<ArtistCount>();
				for(Artist artist : Artist.values()) {
					count = 0;
					for(Card c : winnings) {
						if(c.getArtist() == artist) {
							count++;
						}
					}
					orderedFavoredList.add(new ArtistCount(artist,count));
				}
				//sort the list
				orderedFavoredList.sort((ArtistCount a, ArtistCount b) -> a.compareTo(b));
				//set favored artists to the top 3 of these
				for(int i = 0; i < favoredArtists.length; i++) {
					favoredArtists[i] = orderedFavoredList.get(i).getArtist();
				}
			} else {
				//the favored artists list cannot be made yet, so it should continue being null
				//the one favored artist should be one that the AI has many of in hand and it has not won
				//TODO
			}
		}

		//here the favored artist list is good, so an artist should be chosen from that
		//the artist that should be chosen as favored is one that would make the season values get closer to favored artist list
		if(!(favoredArtists[0] == state.getSeasonValues()[0].getArtist())) {
			//need to check if the hand has a card
			for(Card c : hand) {
				if(c.getArtist() == favoredArtists[0] && c.getAuctionType() != AuctionType.DOUBLE) {
					return c.getArtist();//return the good artist
				}
			}
		} else if(!(favoredArtists[1] == state.getSeasonValues()[1].getArtist())) {
			//need to check if the hand has a card
			for(Card c : hand) {
				if(c.getArtist() == favoredArtists[1] && c.getAuctionType() != AuctionType.DOUBLE) {
					return c.getArtist();//return the good artist
				}
			}
		} else if(!(favoredArtists[2] == state.getSeasonValues()[2].getArtist())) {
			//need to check if the hand has a card
			for(Card c : hand) {
				if(c.getArtist() == favoredArtists[2] && c.getAuctionType() != AuctionType.DOUBLE) {
					return c.getArtist();//return the good artist
				}
			}
		}


		//If noting can be done, return the artist of the first non double card in hand
		for(Card c : hand) {
			if(c.getAuctionType() != AuctionType.DOUBLE) {
				return c.getArtist();
			}
		}

		//if all this fails return null
		//this should never happen unless hand is empty or only contains doubles
		//try returning a double artist
		for(Card c : hand) {
			return c.getArtist();
		}
		return null;
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
