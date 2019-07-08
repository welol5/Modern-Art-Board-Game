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
		
		double expectedValue = getValue();
		double playerMultiplier = (((double)players.length)-1)/((double)players.length);
		
		System.out.println("price : " + ((playerMultiplier*expectedValue)-1));
		
		int maxValue = (int)((playerMultiplier*expectedValue)-1);
		
		if(maxValue < money) {
			return maxValue;
		} else {
			return money;
		}
	}
}
