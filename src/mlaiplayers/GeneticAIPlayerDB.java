package mlaiplayers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import core.Artist;
import core.ArtistCount;
import core.Card;

/**
 * This is the DataBase for this {@link GeneticAIPlayer}.
 * @author William Elliman
 *
 */
public class GeneticAIPlayerDB implements Serializable{
	
	private HashMap<StateData,Double> states;
	
	public GeneticAIPlayerDB() {
		states = new HashMap<StateData,Double>();
	}
	
	public double getValue(StateData state) {
		double value = 0;
		try {
			value = states.get(state);
		} catch (NullPointerException e) {
			//this comes here if the state has never been seen before
		}
		return value;
	}
	
	public void putValue(StateData state, double value) {
		states.put(state, value);
	}

	public class StateData implements Serializable{
		
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
			this.hand = new ArtistCount[handValues.size()];
			this.hand = handValues.toArray(this.hand);
			this.seasonValues = seasonValues;
		}
	}
}
