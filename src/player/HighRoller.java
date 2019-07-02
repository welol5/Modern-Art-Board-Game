package player;

import core.ObservableGameState;

public class HighRoller extends BasicPredictiveAIPlayerV2 {

	public HighRoller(String name, ObservableGameState state, int playerCount, int turnIndex) {
		super(name, state, playerCount, turnIndex);
		// TODO Auto-generated constructor stub
	}

	protected int getBid(int highestBid, int highestValue) {
		if(highestValue > highestBid) {
			return highestValue;
		} else {
			return -1;
		}
	}
	
	@Override
	public int getFixedPrice() {
		int maxValue = getValue()*((players.length-1)/players.length);
		if(maxValue < money) {
			return maxValue;
		} else {
			return money;
		}
	}
}
