package core;

import java.io.Serializable;

public class ArtistCount implements Comparable<ArtistCount>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8235301809371821812L;
	
	
	private final Artist artist;
	private int count = 0;
	
	public ArtistCount(Artist artist) {
		this.artist = artist;
	}
	
	public ArtistCount(Artist artist, int count) {
		this.artist = artist;
		this.count = count;
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
	public int compareTo(ArtistCount o) {
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
	
	public ArtistCount copy() {
		return new ArtistCount(artist,count);
	}
}
