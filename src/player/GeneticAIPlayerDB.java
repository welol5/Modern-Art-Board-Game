package player;

import java.util.ArrayList;
import java.util.HashMap;

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
		private ArrayList<Card> hand;
		private ArtistCount[] seasonValues;
		public StateData(ArrayList<Card> hand, ArtistCount[] seasonValues) {
			this.hand = hand;
			this.seasonValues = seasonValues;
		}
	}
}
