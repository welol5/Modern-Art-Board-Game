package player;

import java.util.ArrayList;
import java.util.HashMap;

import core.Artist;
import core.ArtistCount;
import core.Card;
import core.ObservableGameState;

public class BasicPredictiveAIPlayerV3 extends BasicPredictiveAIPlayerV2 {
	
	protected int deckSize;
	protected HashMap<Artist,Integer> artistRemaining;

	public BasicPredictiveAIPlayerV3(String name, ObservableGameState state, int playerCount, int turnIndex) {
		super(name, state, playerCount, turnIndex);
		deckSize = 70;
		
		//init artistRemaining
		artistRemaining = new HashMap<Artist,Integer>();
		artistRemaining.put(Artist.LITE_METAL, 12);
		artistRemaining.put(Artist.YOKO, 13);
		artistRemaining.put(Artist.CHRISTIN_P, 14);
		artistRemaining.put(Artist.KARL_GITTER, 15);
		artistRemaining.put(Artist.KRYPTO, 16);
	}

	protected boolean setNextFavoredArtist() {
		//TODO this could be improved
		//make decisions based off of more than just the winnings
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
//			if(favoredArtists.size() == 0) {
//				
//				favoredArtists.add(sortedWinnings.get(0).getArtist());
//			} else if(favoredArtists.size() == 1) {
//				for(ArtistCount c : sortedWinnings) {
//					//add if the artist is not already added and if the AI has won at least 1
//					if(c.getArtist() != favoredArtists.get(0) && c.getCount() > 0) {
//						favoredArtists.add(c.getArtist());
//						break;
//					}
//				}
//			} else if(favoredArtists.size() == 2) {
//				for(ArtistCount c : sortedWinnings) {
//					//add if the artist is not already added and if the AI has won at least 1
//					if(c.getArtist() != favoredArtists.get(0) && c.getArtist() != favoredArtists.get(0) && c.getCount() > 0) {
//						favoredArtists.add(c.getArtist());
//						break;
//					}
//				}
//			}
			
			if(favoredArtists.size() == 0) {
				//find the first artist that can win
				for(ArtistCount ac : sortedWinnings) {
					if(ac.getCount() == 0) {
						return false;//nothing can be added
					} else if(artistRemaining.get(ac.getArtist()) < 5) {
						continue;//this artist will not win
					} else {
						favoredArtists.add(ac.getArtist());
						break;
					}
				}
			} else if(favoredArtists.size() == 1) {
				for(ArtistCount ac : sortedWinnings) {
					if(ac.getCount() == 0) {
						return true;//nothing can be added but favored there is a favored artist
					} else if(artistRemaining.get(ac.getArtist()) < 2) {//I dont like the magic number here
						continue;//this artist will not win
					} else if(ac.getArtist() != favoredArtists.get(0)){
						//the AI has won at least one of this artist
						//the artist could come in second for the season
						//the artist has not already been added
						favoredArtists.add(ac.getArtist());
						break;
					}
				}
			} else if(favoredArtists.size() == 2) {
				for(ArtistCount ac : sortedWinnings) {
					if(ac.getCount() == 0) {
						return true;//nothing can be added but favored there is a favored artist
					} else if(artistRemaining.get(ac.getArtist()) < 1) {//I dont like the magic number here
						continue;//this artist will not win
					} else if(ac.getArtist() != favoredArtists.get(0) && ac.getArtist() != favoredArtists.get(1)){
						//the AI has won at least one of this artist
						//the artist could come in third for the season
						//the artist has not already been added
						favoredArtists.add(ac.getArtist());
						break;
					}
				}
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	public void deal(Card c) {
		super.deal(c);
		//every player is dealt out the same amount of cards
		deckSize -= players.length;
	}
	
	public void announceCard(Card card, boolean isDouble) {
		super.announceCard(card, isDouble);
		
		artistRemaining.put(card.getArtist(), artistRemaining.get(card.getArtist())-1);
	}
}
