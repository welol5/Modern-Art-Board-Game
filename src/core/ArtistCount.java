package core;

import java.io.Serializable;

/**
 * This class is here because there are many instances where having an {@link Artist} and a number linked is useful.
 * @author William Elliman
 *
 */
public class ArtistCount implements Comparable<ArtistCount> {
	
	/**
	 * The artist.
	 */
	private final Artist artist;
	
	/**
	 * The value.
	 */
	private int count = 0;
	
	/**
	 * Initializes a new ArtistCount with a count of 0.
	 * 
	 * @param artist
	 */
	public ArtistCount(Artist artist) {
		this.artist = artist;
	}
	
	/**
	 * Initializes a new ArtistCount with count that is specified
	 * 
	 * @param artist
	 * @param count
	 */
	public ArtistCount(Artist artist, int count) {
		this.artist = artist;
		this.count = count;
	}
	
	/**
	 * Use this to increment the count.
	 * @param isDouble will increment by 2 if true.
	 */
	public void auction(boolean isDouble) {
		count++;
		if(isDouble) {
			count++;
		}
	}
	
	/**
	 * @return the count value.
	 */
	public int getCount() {
		return count;
	}
	
	/**
	 * @return the {@link Artist}.
	 */
	public Artist getArtist() {
		return artist;
	}
	
	/**
	 * Decrements count.
	 */
	public void removeCard() {
		count--;
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
	
	/**
	 * @return a copy of this ArtistCount.
	 */
	public ArtistCount copy() {
		return new ArtistCount(artist,count);
	}
}
