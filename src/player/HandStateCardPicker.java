package player;

import java.util.ArrayList;

import core.Artist;
import core.ArtistCount;
import core.AuctionType;
import core.Card;
import core.ObservableGameState;

public class HandStateCardPicker extends HighRoller {

	private Artist[] artistOrder = null;

	public HandStateCardPicker(String name, ObservableGameState state, int playerCount, int turnIndex) {
		super(name, state, playerCount, turnIndex);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Card chooseCard() {

		//return null if the players hand is empty
		if(hand.size() == 0) {
			return null;
		}

		if(artistOrder == null) {
			//if artistOrder is null, that means that the season has started recently

			boolean first = true;//var to say if this AI is the first player of the season
			for(ArtistCount ac : state.getSeasonValues()) {
				if(ac.getCount() > 0) {
					first = false;
					break;
				}
			}

			//if this player is the first of the season it should play the artist that it has the most of in its hand
			if(first) {
				Artist top = null;
				int topCount = -1;
				for(Artist a : Artist.values()) {
					int count = 0;
					for(Card c : hand) {
						if(c.getArtist() == a) {
							count++;
						}
					}

					if(count > topCount) {
						top = a;
						topCount = count;
					}
				}

				//the best has been found
				//play a double if possible
				//the season just started so new cards have been dealt so the AI should not need to worry about not having a second card
				for(Card c: hand) {
					if(c.getArtist() == top && c.getAuctionType() == AuctionType.DOUBLE) {
						hand.remove(c);
						return c;
					}
				}
				//no doubles found, return a normal card
				for(Card c : hand) {
					if(c.getArtist() == top) {
						hand.remove(c);
						return c;
					}
				}
			} else {
				//here the AI knows it was not the first to play in the season
				//it should now look at the season counts and its hand to find an order to play
				//this will fill the artistOrder array

				ArrayList<ArtistCount> acList = new ArrayList<ArtistCount>();
				for(Artist a : Artist.values()) {
					int count = state.getSeasonValue(a);
					//add in the hand counts
					for(Card c : hand) {
						if(c.getArtist() == a) {
							count++;
						}
					}

					acList.add(new ArtistCount(a,count));
				}
				//The list has been made
				//sort it
				acList.sort((ArtistCount a, ArtistCount b) -> a.compareTo(b));

				//use the sorted list to make the artistOrder list
				artistOrder = new Artist[Artist.values().length];
				for(int i = 0; i < acList.size(); i++) {
					artistOrder[i] = acList.get(i).getArtist();
				}

				//the order has been created, but the season is still early
				//The AI should play its top card to start forcing it to the top or boosting it higher above others
				for(Card c: hand) {
					if(c.getArtist() == artistOrder[0] && c.getAuctionType() == AuctionType.DOUBLE) {
						hand.remove(c);
						return c;
					}
				}
				//no doubles found, return a normal card
				for(Card c : hand) {
					if(c.getArtist() == artistOrder[0]) {
						hand.remove(c);
						return c;
					}
				}
			}
		}

		//At this point, the AI knows it is later in the season
		//It should now make sure its top artist is not at risk of losing the season
		for(Artist a : Artist.values()) {
			if(state.getSeasonValue(a) > 2 && a != artistOrder[0]) {
				//here it knows it must end the season
				//try to find a card that will help this. pref a double
				for(Card c: hand) {
					if(c.getArtist() == artistOrder[0] && c.getAuctionType() == AuctionType.DOUBLE) {
						hand.remove(c);
						return c;
					}
				}
				//no doubles found, return a normal card
				for(Card c : hand) {
					if(c.getArtist() == artistOrder[0]) {
						hand.remove(c);
						return c;
					}
				}
				//no card was found that would work
			}
		}

		//if the top is not at risk of losing or there is nothing the AI can do about it, enforce the order
		//the magical three is because the AI should only care about the top 3 artists
		for(int i = 0; i < 3; i++) {
			if(state.getTopSeasonValues()[i] != artistOrder[i]) {
				//search the hand for the best card
				//check to see how bad it is out of order
				if(state.getSeasonValues()[i].getCount()-state.getSeasonValue(artistOrder[i]) > 1) {
					//the order is out by more than 1 so pref a double
					for(Card c: hand) {
						if(c.getArtist() == artistOrder[i] && c.getAuctionType() == AuctionType.DOUBLE) {
							hand.remove(c);
							return c;
						}
					}
					//no doubles found, return a normal card
					for(Card c : hand) {
						if(c.getArtist() == artistOrder[i]) {
							hand.remove(c);
							return c;
						}
					}
				} else {
					//no doubles needed
					for(Card c : hand) {
						if(c.getArtist() == artistOrder[i] && c.getAuctionType() != AuctionType.DOUBLE) {
							hand.remove(c);
							return c;
						}
					}
				}
			}
		}

		//the order is good or nothing could be done for it by this point
		//the goal past here is just top make money, so it will sell the highest valued card (not double)
		ArrayList<ArtistCount> values = new ArrayList<ArtistCount>();
		for(Artist a : Artist.values()) {
			values.add(new ArtistCount(a, state.getArtistValue(a)));
		}
		values.sort((ArtistCount a, ArtistCount b) -> a.compareTo(b));
		//values is a sorted list of artists and their prices
		
		for(ArtistCount v : values) {
			for(Card c : hand) {
				if(c.getArtist() == v.getArtist() && c.getAuctionType() != AuctionType.DOUBLE) {
					hand.remove(c);
					return c;
				}
			}
		}
		
		//something should have fit the criteria by now, but just in case
		return hand.remove(0);
	}
	
	public void announceSeasonEnd(int season) {
		super.announceSeasonEnd(season);
		artistOrder = null;
	}
	
	protected int getValue() {

		if(artistOrder != null) {
			//if the AI has an order, follow it
			for(int i = 0; i < 3; i++) {
				if(artistOrder[i] == biddingCard.getArtist()) {
					return state.getArtistValue(artistOrder[i]) + 30-(i*10);
				}
			}
			return 0;
		} else {
			//if there is no order bid based off of the current values
			for(int i = 0; i < 3; i++) {
				if(state.getTopSeasonValues()[i] == biddingCard.getArtist()) {
					return state.getArtistValue(artistOrder[i]) + 30-(i*10);
				}
			}
			return 0;
		}
	}
}
