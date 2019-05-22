package core;

public class Card {
	private Artist artist;//artist of the painting
	private AuctionType auctionType;//the type of auction
	
	public Card(Artist a, AuctionType t) {
		artist = a;
		auctionType = t;
	}
	
	public Artist getArtist() {
		return artist;
	}
	
	public AuctionType getAuctionType() {
		return auctionType;
	}
	
	public String toString() {
		String string = "";
		
		//add the artist to the string
		if(artist == Artist.CHRISTIN_P) {
			string += "Christin P. : ";
		} else if(artist == Artist.KARL_GITTER) {
			string += "Karl Gitter : ";
		} else if(artist == Artist.KRYPTO) {
			string += "Krypto      : ";
		} else if(artist == Artist.LITE_METAL) {
			string += "Lite Metal  : ";
		} else if(artist == Artist.YOKO) {
			string += "Yoko        : ";
		}
		
		//add the auction type to the string
		if(auctionType == AuctionType.DOUBLE) {
			string += "Double";
		} else if(auctionType == AuctionType.FIXED_PRICE) {
			string += "Fixed Price";
		} else if(auctionType == AuctionType.ONCE_AROUND) {
			string += "Once Around";
		} else if(auctionType == AuctionType.SEALED) {
			string += "Sealed";
		} else if(auctionType == AuctionType.STANDARD) {
			string += "Standard";
		}
		
		if(string.equals("")) {
			return "Unknown";
		}
		
		return string;
	}
}
