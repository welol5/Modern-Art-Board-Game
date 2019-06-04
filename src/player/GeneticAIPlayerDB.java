package player;

import java.util.ArrayList;
import java.util.HashMap;

import core.Artist;
import core.ArtistCount;
import core.Card;

public class GeneticAIPlayerDB {
	
	private HashMap<StateData,Double> states;
	
	public GeneticAIPlayerDB() {
		states = new HashMap<StateData,Double>();
	}
	
	public double getValue(StateData state) {
		return states.get(state);
	}
	
	public void putValue(StateData state, double value) {
		states.put(state, value);
	}

	public class StateData {
		private ArtistCount[] hand;
		private ArtistCount[] seasonValues;
		
		public StateData(ArrayList<Card> hand, ArtistCount[] seasonValues) {
			ArrayList<ArtistCount> handValues = new ArrayList<ArtistCount>();
			for(Artist artist : Artist.values()) {
				
				//count the number of cards by a given artist.
				int count = 0;
				for(Card card : hand) {
					if(card.getArtist() == artist) {
						count++;
					}
				}
				
				handValues.add(new ArtistCount(artist, count));
			}
			//sort the list
			handValues.sort((a,b) -> {
				return a.compareTo(b);
			});
			
			this.hand = handValues.toArray(this.hand);
			this.seasonValues = seasonValues;
		}
	}
}
