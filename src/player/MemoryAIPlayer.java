package player;

import java.util.ArrayList;
import java.util.Random;

import core.Artist;
import core.AuctionType;
import core.Card;
import core.ObservableGameState;
import core.ArtistCount;
import io.BasicIO;

/**
 * This player takes advantage of keeping track of the major actions made in the game. It keeps track of who
 * won what card and how much they bid on it. It then uses that infomation to calculate how much it can bid
 * so that it will make a profit and the best other player will not.
 * @author William Elliman
 *
 */
public class MemoryAIPlayer extends ReactiveAIPlayer{

	private Random random = new Random();

	//memory
	//hand keeps track of the cards in the players hand
	private ArrayList<ArtistCount> playedCards = new ArrayList<ArtistCount>();//this could probably be an array
	private ArtistPlayChance[] chances = new ArtistPlayChance[Artist.values().length];
	
	//keep track of other players
	private Player[] players;
	private final int turnIndex;//keep track of where itself is in the list of turns
	private int turn = 0;
	
	//memory during bidding
	private Card biddingCard;
	
	public MemoryAIPlayer(String name, BasicIO io, int playerCount, int turnIndex) {
		super(name,io);

		//init playedCards to 0s
		for(Artist artist : Artist.values()) {
			playedCards.add(new ArtistCount(artist));
		}

		//init ArtistPlayChances
		for(int i = 0; i < Artist.values().length; i++) {
			chances[i] = new ArtistPlayChance(Artist.values()[i]);
		}
		
		//init player array
		players = new Player[playerCount];
		for(int i = 0; i < players.length; i++) {
			players[i] = new RandomPlayer(null,null);
		}
		
		this.turnIndex = turnIndex;
	}

	@Override
	public Card chooseCard(ObservableGameState state) {
		//go through the artists in terms of most to least favored
		for(int f = 0; f < Artist.values().length; f++) {
			Artist favored = chooseFavordArtist(state, f);
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
		}

		//this will be left here until the full method is implemented
		if(hand.size() == 0) {
			return null;
		}
		return hand.remove(random.nextInt(hand.size()));
	}

	@Override
	public Card chooseSecondCard(Artist artist, ObservableGameState state) {
		//check if the hand contains the artist
		boolean contains = false;
		for(Card c : hand) {
			if(c.getArtist() == artist && c.getAuctionType() != AuctionType.DOUBLE) {
				contains = true;
				break;
			}
		}
		if(!contains) {
			return null;
		}

		//the player has a card that will work, choose one randomly
		Card card = null;
		int index = 0;
		while(card == null || (card.getArtist() != artist && card.getAuctionType() != AuctionType.DOUBLE)) {
			index = random.nextInt(hand.size());
			card = hand.get(index);
		}
		hand.remove(index);
		return card;
	}

	@Override
	public int getBid(ObservableGameState state) {
		int bestPlayer = -1;
		int bestPlayerMoney = -1;
		
		//first it to find the player who is doing the best that is not this AI
		for(int i = 0; i < players.length; i++) {
			//skip this player
			if(i == turnIndex) {
				continue;
			}
			
			//the best player will be found by finding the est values for each player
			//first calculate the players est value
			Artist[] top3 = state.getTopSeasonValues();
			int value = players[i].getMoney();
			for(Card c : players[i].getWinnings()) {
				if(c.getArtist() == top3[0]) {
					value += state.getArtistValue(top3[0]) + 30;
				} else if(c.getArtist() == top3[1]) {
					value += state.getArtistValue(top3[0]) + 20;
				} else if(c.getArtist() == top3[2]) {
					value += state.getArtistValue(top3[0]) + 10;
				}
			}
			
			//value now holds the players value
			if(value > bestPlayerMoney) {
				bestPlayerMoney = value;
				bestPlayer = i;
			}
		}
		
		//now the best other player has been found
		
		//find that players value
		Artist[] top3 = state.getTopSeasonValues();
		for(Card card : players[bestPlayer].getWinnings()) {
			if(card.getArtist() == top3[0]) {
				bestPlayerMoney += state.getArtistValue(card.getArtist()) + 30;
			} else if(card.getArtist() == top3[1]) {
				bestPlayerMoney += state.getArtistValue(card.getArtist()) + 20;
			} else if(card.getArtist() == top3[2]) {
				bestPlayerMoney += state.getArtistValue(card.getArtist()) + 10;
			}
		}
		//bestPlayerMoney holds the highest value another player has
		
		//calculate this players value
		int AIvalue = money;
		for(Card card : getWinnings()) {
			if(card.getArtist() == top3[0]) {
				AIvalue += state.getArtistValue(card.getArtist()) + 30;
			} else if(card.getArtist() == top3[1]) {
				AIvalue += state.getArtistValue(card.getArtist()) + 20;
			} else if(card.getArtist() == top3[2]) {
				AIvalue += state.getArtistValue(card.getArtist()) + 10;
			}
		}
		
		//AIvalue and BestPlayerMoney hold money values
		
		//set the maxValue to the
		int maxValue = getValue(state);
		if(state.isDouble) {
			maxValue*=2;
		}
		//need to know if the player is still bidding
		//if the player is still bidding, do not let them win if it will make a profit
		if((state.card.getAuctionType() == AuctionType.ONCE_AROUND || state.card.getAuctionType() == AuctionType.SEALED) || state.stillBidding[bestPlayer]) {
			//find the cards value
			//if the card is the bestPlayes, they will profit more from this AI if more than half the value is paid
			if(turn == bestPlayer) {
				maxValue /= 2;
			}
			//if a profit can be made, don't let them win
		} else {
			maxValue /= 2;
		}
		
		//if it a once around return the max value this AI will pay
		if((state.card.getAuctionType() == AuctionType.ONCE_AROUND || state.card.getAuctionType() == AuctionType.SEALED) && maxValue > state.highestBid) {
			return maxValue;
		} else if (state.card.getAuctionType() == AuctionType.ONCE_AROUND || state.card.getAuctionType() == AuctionType.SEALED) {
			return -1;
		}
		
		//try to buy the painting for the lowest possible price
		//System.out.println(maxValue);
		if(maxValue > state.highestBid) {
			return state.highestBid + 1;
		} else {
			return -1;
		}
	}

	@Override
	public int getFixedPrice(ObservableGameState state) {
		int maxValue = getValue(state)/2;
		if(maxValue < money) {
			return maxValue;
		} else {
			return money;
		}
	}

	@Override
	public boolean buy(ObservableGameState state) {

		int value = getValue(state);

		if(state.highestBid < value/2 && money > state.highestBid) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void announceCard(Card card, boolean isDouble) {
		for(int i = 0; i < playedCards.size(); i++) {
			if(playedCards.get(i).getArtist() == card.getArtist()) {
				playedCards.get(i).auction(isDouble);
			}
		}
		
		//prep for bidding
		biddingCard = card;
	}
	
	@Override
	public void announceSeasonEnd(int season, ObservableGameState state) {
		Artist[] top3 = state.getTopSeasonValues();
		for(int i = 0; i < players.length; i++) {
			for(Player player : players) {
				for(Card c : player.getWinnings()) {
					if(top3[0] == c.getArtist() || top3[1] == c.getArtist() || top3[2] == c.getArtist()) {
						player.recive(state.getArtistValue(c.getArtist()));
					}
				}
			}
		}
	}
	
	@Override
	public void announceAuctionWinner(int turn, String name, int price) {
		players[turn].pay(price);
		players[turn].givePainting(biddingCard);
		biddingCard = null;
		turn++;
	}

	private class ArtistPlayChance{
		private double chance = 0;
		public final Artist artist;
		public final double cardCount;

		public ArtistPlayChance(Artist artist) {
			this.artist = artist;

			int cardCount = 16;
			for(int i = 0; i < 5; i++) {
				if(artist == Artist.values()[i]) {
					cardCount = 12+i;
				}
			}
			this.cardCount = cardCount;
		}

		public void updateChance(int playedCards, int totalPlayedCards) {
			//this assumes an even distribution
			chance = (cardCount-playedCards)/(70-totalPlayedCards);
		}

		public double getChance() {
			return chance;
		}
	}
}
