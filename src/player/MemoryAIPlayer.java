package player;

import java.util.ArrayList;
import java.util.Random;

import core.Artist;
import core.AuctionType;
import core.Card;
import core.ObservableGameState;
import core.ArtistCount;

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

	//keep track of other players
	protected Player[] players;
	protected final int turnIndex;//keep track of where itself is in the list of turns
	protected int turn = 0;
	protected int bestPlayer = -1;
	protected int bestPlayerMoney = -1;

	//memory during bidding

	/**
	 * 
	 * @param name of the player
	 * @param state the observable game state
	 * @param playerCount the number of players playing the game
	 * @param turnIndex where this player is in terms of order (starts counting at 0)
	 */
	public MemoryAIPlayer(String name, ObservableGameState state, int playerCount, int turnIndex) {
		super(name, state);

		//init player array
		players = new Player[playerCount];
		for(int i = 0; i < players.length; i++) {
			players[i] = new RandomPlayer(null);
		}

		this.turnIndex = turnIndex;
	}

	@Override
	public int getBid(int highestBid) {

		//set the maxValue to the
		int maxValue = getValue();
		//System.out.println("Value : " + maxValue);
		if(isDouble) {
			maxValue*=2;
		}
		//need to know if the player is still bidding
		//if the player is still bidding, do not let them win if it will make a profit
		if((biddingCard.getAuctionType() == AuctionType.ONCE_AROUND || biddingCard.getAuctionType() == AuctionType.SEALED) || state.stillBidding[bestPlayer]) {
			//find the cards value
			//if the card is the bestPlayes, they will profit more from this AI if more than half the value is paid
			if(turn == bestPlayer) {
				maxValue /= 2;
			}
			//if a profit can be made, don't let them win
		} else {
			maxValue = (int)(((double)maxValue)*(((double)players.length)-1)/((double)players.length));
			//System.out.println("ValueAfter : " + maxValue);
		}
		
		return getBid(highestBid,maxValue);
	}
	
	@Override
	public boolean buy(int price) {

		if(price <= (int)(((double)price)*(((double)players.length)-1)/((double)players.length)) && money > price) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void announceCard(Card card, boolean isDouble) {
		//prep for bidding
		biddingCard = card;
		this.isDouble = isDouble;

		getBestOtherPlayer();
	}
	
	/**
	 * This method finds the other player that has the most money or highest value of all the other players.
	 */
	protected void getBestOtherPlayer() {
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
	}

	@Override
	public void announceSeasonEnd(int season) {
		Artist[] top3 = state.getTopSeasonValues();
		for(Player player : players) {
			for(Card c : player.getWinnings()) {
				if(top3[0] == c.getArtist() || top3[1] == c.getArtist() || top3[2] == c.getArtist()) {
					player.recive(state.getArtistValue(c.getArtist()));
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
}
