package player;

import core.ObservableGameState;

/**
 * This AI makes a change to how the {@link BasicPredictiveAIV2} bids during standard auctions.
 * It will always bid the highest amount.
 * @author William Elliman
 *
 */
public class HighRoller extends BasicPredictiveAIPlayerV2 {

	public HighRoller(String name, ObservableGameState state, int playerCount, int turnIndex) {
		super(name, state, playerCount, turnIndex);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected int getBid(int highestBid, int highestValue) {
		if(highestValue > highestBid) {
			if(highestValue > money) {
				return highestValue;
			} else {
				return money;
			}
		} else {
			return -1;
		}
	}

	@Override
	public int getFixedPrice() {

		double expectedValue = getValue();
		double playerMultiplier = (((double)players.length)-1)/((double)players.length);		

		int maxValue = Math.abs((int)((playerMultiplier*expectedValue)-1));

		if(maxValue < money) {
			return maxValue;
		} else {
			return money;
		}
	}
}
