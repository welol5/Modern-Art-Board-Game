package player;

import java.util.ArrayList;

import core.Artist;
import core.ArtistCount;
import core.AuctionType;
import core.Card;
import core.ObservableGameState;

public class Merchant extends MemoryAIPlayer{

	public Merchant(String name, ObservableGameState state, int playerCount, int turnIndex) {
		super(name, state, playerCount, turnIndex);
		// TODO Auto-generated constructor stub
	}

	public int getBid(int higestBid) {
		return -1;
	}

	public int getFixedPrice() {
		return (int) (getValue()*((((double)players.length)-1)/((double)players.length)));
	}

	public Card chooseCard() {

		if(hand.size() == 0) {
			return null;
		}

		ArrayList<ArtistCount> values = new ArrayList<ArtistCount>();
		for(Artist a : Artist.values()) {
			values.add(new ArtistCount(a, state.getArtistValue(a)));
		}
		values.sort((ArtistCount a, ArtistCount b) -> a.compareTo(b));

		for(ArtistCount ac : values) {
			Artist a = ac.getArtist();//just for easier access
			
			//check for double auctions
			for(int d = 0; d < hand.size(); d++) {
				if(hand.get(d).getArtist() == a && hand.get(d).getAuctionType() == AuctionType.DOUBLE) {
					//double auction found, check for a second card
					for(int i = 0; i < hand.size(); i++) {
						if(i == d) {
							continue;
						}
						
						if(hand.get(i).getArtist() == a && hand.get(i).getAuctionType() != AuctionType.DOUBLE) {
							return hand.remove(d);
						}
					}
					
				}
			}
			
			//play highest valued card
			for(int i = 0; i < hand.size(); i++) {
				if(hand.get(i).getArtist() == a && hand.get(i).getAuctionType() != AuctionType.DOUBLE) {
					return hand.remove(i);
				}
			}
		}

		return null;
	}
}
