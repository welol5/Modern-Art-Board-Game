package player;

import java.util.ArrayList;

import core.Artist;
import core.ArtistCount;
import core.AuctionType;
import core.Card;
import core.ObservableGameState;

/**
 * This AI is one that only sells cards. Its goal is to sell the highest valued cards so that other players will pay
 * and it does not buy other cards so it will not make any money at the end of the season.
 * @author William Elliman
 *
 */
public class Merchant extends MemoryAIPlayer{

	/**
	 * See {@link MemoryAIPlayer} constructor for details.
	 * @param name
	 * @param state
	 * @param playerCount
	 * @param turnIndex
	 */
	public Merchant(String name, ObservableGameState state, int playerCount, int turnIndex) {
		super(name, state, playerCount, turnIndex);
	}

	@Override
	public int getBid(int higestBid) {
		return -1;
	}
	
	@Override
	public int getBid(int highestBid, int maxVal) {
		return -1;
	}

	@Override
	public int getFixedPrice() {
		return (int) (getValue()*((((double)players.length)-1)/((double)players.length)));
	}
	
	@Override
	public boolean buy(int price) {
		return false;
	}
	
	@Override
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
