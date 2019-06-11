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
 * TODO rewrite this
 * @author William Elliman
 *
 */
public class BasicPredictiveAIPlayerV2 extends MemoryAIPlayer{

	private Random random = new Random();

	//memory
	//private Artist[] favoredArtists = new Artist[3];//keeps track of what artists this AI want to win during the season
	private ArrayList<Artist> favoredArtists = new ArrayList<Artist>();

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
	public int getBid(int highestBid, int highestValue) {

		//if it a once around return the max value this AI will pay
		if((biddingCard.getAuctionType() == AuctionType.ONCE_AROUND || biddingCard.getAuctionType() == AuctionType.SEALED) && highestValue > highestBid) {
			return highestValue;
		} else if (biddingCard.getAuctionType() == AuctionType.ONCE_AROUND || biddingCard.getAuctionType() == AuctionType.SEALED) {
			return -1;
		}

		//try to buy the painting for the lowest possible price
		//System.out.println(maxValue);
		//System.out.println("here");
		if(highestValue > highestBid) {
			int t = 1;
			while (highestValue-players.length*t-highestBid > 0) {
				t++;
			}
			System.out.println("Highest bid : " + highestBid);
			System.out.println("Bid         : " + (highestValue-1-players.length*(t-1)-highestBid));
			System.out.println("Value       : " + highestValue);
			return highestBid + highestValue-1-players.length*(t-1)-highestBid;
		} else {
			return -1;
		}
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
		for(int i = 0; i < favoredArtists.size(); i++) {
			favoredArtists.clear();
		}
	}

	/**
	 * 
	 * @return the favored artist
	 */
	protected Artist chooseFavordArtist() {

		//print all values considered for favored artist
//		System.out.println("Hand");
//		for(Card c : hand) {
//			System.out.println(c);
//		}
//		System.out.println("Season values");
//		for(ArtistCount c : state.getSeasonValues()) {
//			System.out.println(c);
//		}
//		System.out.println("Winnings");
//		for(Card c : winnings) {
//			System.out.println(c);
//		}

		if(favoredArtists.size() < 3) {
			if(winnings.size() > 0) {
				//sort winnings to be able to choose the best order
				ArrayList<ArtistCount> sortedWinnings = new ArrayList<ArtistCount>();
				for(Artist a : Artist.values()) {
					int count = 0;
					for(Card c : winnings) {
						if(c.getArtist() == a) {
							count++;
						}
					}
					sortedWinnings.add(new ArtistCount(a,count));
				}
				sortedWinnings.sort((ArtistCount a, ArtistCount b) -> a.compareTo(b));//love this

				//add these in as needed
				if(favoredArtists.size() == 0) {
					favoredArtists.add(sortedWinnings.get(0).getArtist());
				} else if(favoredArtists.size() == 1) {
					for(ArtistCount c : sortedWinnings) {
						//add if the artist is not already added and if the AI has won at least 1
						if(c.getArtist() != favoredArtists.get(0) && c.getCount() > 0) {
							favoredArtists.add(c.getArtist());
							break;
						}
					}
				} else if(favoredArtists.size() == 2) {
					for(ArtistCount c : sortedWinnings) {
						//add if the artist is not already added and if the AI has won at least 1
						if(c.getArtist() != favoredArtists.get(0) && c.getArtist() != favoredArtists.get(0) && c.getCount() > 0) {
							favoredArtists.add(c.getArtist());
							break;
						}
					}
				}
			} else {
				//nothing has been won yet, play the most common artist in hand
				Artist bestArtist = null;
				int bestCount = 0;
				for(Artist a : Artist.values()) {
					int count = 0;;
					for(Card c : hand) {
						if(c.getArtist() == a) {
							count++;
						}
					}
					if(count > bestCount) {
						bestCount = count;
						bestArtist = a;
					}
				}
				//bestArtist here is the most common one in hand
//				System.out.println("Best " + bestArtist);
				return bestArtist;
			}
		}
		
//		System.out.println("Favored Artists");
//		for(Artist a : favoredArtists) {
//			System.out.println(a);
//		}

		//here the favored artist list is good, so an artist should be chosen from that
		//the artist that should be chosen as favored is one that would make the season values get closer to favored artist list
		if( favoredArtists.size() > 0 && !(favoredArtists.get(0) == state.getSeasonValues()[0].getArtist())) {
			//need to check if the hand has a card
			for(Card c : hand) {
				if(c.getArtist() == favoredArtists.get(0) && c.getAuctionType() != AuctionType.DOUBLE) {
					return c.getArtist();//return the good artist
				}
			}
			
		}
		
		if(favoredArtists.size() > 1 && !(favoredArtists.get(1) == state.getSeasonValues()[1].getArtist())) {
			//need to check if the hand has a card
			for(Card c : hand) {
				if(c.getArtist() == favoredArtists.get(1) && c.getAuctionType() != AuctionType.DOUBLE) {
					return c.getArtist();//return the good artist
				}
			}
		}
		
		if( favoredArtists.size() > 2 && !(favoredArtists.get(2) == state.getSeasonValues()[2].getArtist())) {
			//need to check if the hand has a card
			for(Card c : hand) {
				if(c.getArtist() == favoredArtists.get(2) && c.getAuctionType() != AuctionType.DOUBLE) {
					return c.getArtist();//return the good artist
				}
			}
		}
		//the above only returns something if something is out of place
		
		//return the most favored artist that is in hand (not double)
		for(int i = 0; i < favoredArtists.size(); i++) {
			for(Card c : hand) {
				if(c.getArtist() == favoredArtists.get(i) && c.getAuctionType() != AuctionType.DOUBLE) {
					return c.getArtist();
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
