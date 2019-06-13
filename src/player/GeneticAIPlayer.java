package player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

import core.Artist;
import core.AuctionType;
import core.Card;
import core.ObservableGameState;
import core.ArtistCount;
import io.BasicIO;

/**
 * This player keeps a record of values for every state of the game. It uses a version of Temporal
 * difference where it is on policy. This will probably be updated to SARSA learning later on but
 * this is simpler for now.
 * 
 * This may not work for now and it is not my priority.
 * 
 * @author William Elliman
 *
 */
public class GeneticAIPlayer extends MemoryAIPlayer{

	private Random random = new Random();

	//memory
	private GeneticAIPlayerDB dataBase;
	private final double exploreChance;
	private Stack<GeneticAIPlayerDB.StateData> stateHistory = new Stack<GeneticAIPlayerDB.StateData>();
	private final double alpha;//rate at which new info replaces old info

	//memory during bidding

	public GeneticAIPlayer(String name,ObservableGameState state, int playerCount, int turnIndex, GeneticAIPlayerDB dataBase, double exploreChance, double alpha) {
		super(name,state,playerCount,turnIndex);

		this.exploreChance = exploreChance;
		this.alpha = alpha;
		this.dataBase = dataBase;
	}

	@Override
	public Card chooseCard() {

		//add this state to the state history
		stateHistory.push(dataBase.new StateData(hand, state.getSeasonValues()));

		//explore
		if(random.nextDouble() < exploreChance) {
			if(hand.size() > 0) {
				return hand.remove(random.nextInt(hand.size()));
			} else {
				return null;//hand is empty
			}
		}

		//play the card that leads to the best next state
		int cardToPlay = 0;
		double bestNextStateValue = Double.NEGATIVE_INFINITY;
		for(int i = 0; i < hand.size(); i++) {
			ArrayList<Card> tempHand = (ArrayList<Card>) hand.clone();
			tempHand.remove(i);

			if(i == 0) {
				bestNextStateValue = dataBase.getValue(dataBase.new StateData(tempHand, state.getSeasonValues()));
			} else if(dataBase.getValue(dataBase.new StateData(tempHand, state.getSeasonValues())) > bestNextStateValue) {
				bestNextStateValue = dataBase.getValue(dataBase.new StateData(tempHand, state.getSeasonValues()));
				cardToPlay = i;
			}
		}

		if(hand.size() > 0) {
			return hand.remove(cardToPlay);
		} else {
			return null;//gets here if the hand is empty
		}
	}

	@Override
	public Card chooseSecondCard(Artist artist) {
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
	public int getBid(int highestBid) {
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
		int maxValue = getValue();
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
			maxValue /= 2;
		}

		//if it a once around return the max value this AI will pay
		if((biddingCard.getAuctionType() == AuctionType.ONCE_AROUND || biddingCard.getAuctionType() == AuctionType.SEALED) && maxValue > highestBid) {
			return maxValue;
		} else if (biddingCard.getAuctionType() == AuctionType.ONCE_AROUND || biddingCard.getAuctionType() == AuctionType.SEALED) {
			return -1;
		}

		//try to buy the painting for the lowest possible price
		//System.out.println(maxValue);
		if(maxValue > highestBid) {
			return highestBid + 1;
		} else {
			return -1;
		}
	}

	@Override
	public int getFixedPrice() {
		int maxValue = getValue()/2;
		if(maxValue < money) {
			return maxValue;
		} else {
			return money;
		}
	}

	@Override
	public boolean buy(int price) {

		int value = getValue();

		if(price < value/2 && money > price) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void announceAuctionWinner(int turn, String name, int price) {

	}

	/**
	 * This method uses the stateHistory to update the database so that better moves can be made
	 * @param win did this player win?
	 * @param difference in money between this player and the best other player
	 */
	public void learn(boolean win, int difference) {
		GeneticAIPlayerDB.StateData prevState;//the new state is updated based on the previous

		//give a value to the final state
		prevState = stateHistory.pop();
		if(win) {
			dataBase.putValue(prevState, 1);
		} else {
			dataBase.putValue(prevState, -1);
		}

		//propagate backwards
		while(!stateHistory.isEmpty()) {
			GeneticAIPlayerDB.StateData state = stateHistory.pop();
			double stateValue = dataBase.getValue(state) + alpha*(dataBase.getValue(prevState)-dataBase.getValue(state));
			dataBase.putValue(state, stateValue);
			prevState = state;//prep for next iteration
		}
	}

	public GeneticAIPlayerDB getDB() {
		return dataBase;
	}
}
