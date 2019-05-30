package core;


public class SeasonValue implements Comparable<SeasonValue> {
	private final Artist artist;
	private int count = 0;
	
	public SeasonValue(Artist artist) {
		this.artist = artist;
	}
	
	public void auction(boolean isDouble) {
		count++;
		if(isDouble) {
			count++;
		}
	}
	
	public int getCount() {
		return count;
	}
	
	public Artist getArtist() {
		return artist;
	}
	
	/**
	 * Resets the count for a new season
	 */
	public void reset() {
		count = 0;
	}
	
	public String toString() {
		return artist + " : " + count;
	}

	@Override
	public int compareTo(SeasonValue o) {
		int diff = count-o.getCount();
		if(diff != 0) {
			return -diff;
		} else {
			for(Artist artist : Artist.values()) {
				if(artist == this.artist) {
					return -1;
				} else if(artist == o.getArtist()){
					return 1;
				}
			}
		}
		return 0;
	}
}
