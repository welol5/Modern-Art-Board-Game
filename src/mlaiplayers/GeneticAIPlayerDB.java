package mlaiplayers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
public class GeneticAIPlayerDB{
	
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

	public class StateData{
		
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
		
		public String toString() {
			String retVal = "@start\n";
			for(int i = 0; i < hand.length; i++) {
				retVal += hand[i].toString() + "\n";
			}
			for(int i = 0; i < seasonValues.length; i++) {
				retVal += seasonValues[i].toString() + "\n";
			}
			retVal += "@end\n";
			return retVal;
		}
	}
	
	public void saveData(String fileName) {
		String fileString = "";
		for(StateData sd : states.keySet()) {
			fileString += sd.toString();
			fileString += states.get(sd).toString() + "\n";
		}
		File dataFile = new File(fileName);
		try {
			PrintWriter writer = new PrintWriter(dataFile);
			writer.write(fileString);
			writer.flush();
			writer.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void labData(File dataFile) {
//		BufferedReader reader = new BufferedReader(new FileInputStream(dataFile));
	}
}
