package mlaiplayers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import core.Artist;
import core.ArtistCount;
import core.Card;
import core.ObservableGameState;
import player.MemoryAIPlayer;
import player.Player;

/**
 * This player is meant to use an evaluation function in order to determine what card
 * to choose based off of the information that is given in the game.
 * 
 * @author William Elliman
 *
 */
public class GeneticAIPlayer extends MemoryAIPlayer implements LearningAI {
	
	public static final int EVAL_VALUE_COUNT = 6;
	
	private double[] weights = new double[EVAL_VALUE_COUNT];

	/**
	 * 
	 * @param name See {@link Player} for details.
	 * @param OGS See {@link Player} for details.
	 * @param playerCount See {@link MemoryAIPlayer} for details.
	 * @param turnIndex See {@link MemoryAIPlayer} for details.
	 * @param dataFile This is the set of weights that will be used to weigh different parts of the evaluation function.
	 */
	public GeneticAIPlayer(String name, ObservableGameState OGS, int playerCount, int turnIndex, File dataFile) {
		super(name, OGS, playerCount, turnIndex);
		
		boolean random = false;
		try {
			Scanner input = new Scanner(dataFile);
			boolean currupt = true;
			for(int i = 0; i < EVAL_VALUE_COUNT && input.hasNextLine(); i++) {
				weights[i] = input.nextDouble();
				if(i == EVAL_VALUE_COUNT-1) {
					currupt = false;//The file was successfully read without errors
				}
			}
			
			if(currupt) {
				System.out.println("File currupt. defaulting to random values");
				random = true;
			}
		} catch (FileNotFoundException e) {
			System.out.println("No data file found. Using new random values");
			random = true;
		}
		
		if(random) {
			for(int i = 0; i < EVAL_VALUE_COUNT; i++) {
				weights[i] = Math.random()*2.0-1.0;
			}
		}
	}
	
	/**
	 * Creates a new GeneticAIPlayer.
	 * @param name See {@link Player} for details.
	 * @param OGS See {@link Player} for details.
	 * @param playerCount See {@link MemoryAIPlayer} for details.
	 * @param turnIndex See {@link MemoryAIPlayer} for details.
	 * @param weights This is the set of weights that will be used to weigh different parts of the evaluation function.
	 */
	public GeneticAIPlayer(String name, ObservableGameState OGS, int playerCount, int turnIndex, double[] weights) {
		super(name, OGS, playerCount, turnIndex);
		
		for(int i = 0; i < EVAL_VALUE_COUNT; i++) {
			this.weights[i] = weights[i];
		}
	}
	
	public GeneticAIPlayer(String name, ObservableGameState OGS, int playerCount, int turnIndex) {
		super(name, OGS, playerCount, turnIndex);
		
		for(int i = 0; i < EVAL_VALUE_COUNT; i++) {
			weights[i] = Math.random()*2.0-1.0; 
		}
	}

	@Override
	public void learn(boolean win) {
		// This AI does not learn because it is genetic, it will have others it will be crossed with
	}

	@Override
	public Card chooseCard() {
		
		double[] artistEvals = new double[Artist.values().length];
		for(int i = 0; i < Artist.values().length; i++) {
			artistEvals[i] = getEvaluationValue(Artist.values()[i]);
		}
		
		//find the best artist in hand
		for(int i = 0; i < artistEvals.length; i++) {
			//get the best Artist
			double bestValue = Double.NEGATIVE_INFINITY;
			int bestIndex = 0;
			for(int k = 0; k < artistEvals.length; k++) {
				if(artistEvals[k] > bestValue) {
					bestValue = artistEvals[k];
					bestIndex = k;
				}
			}
			
			//check if that artist is in hand
			for(Card c : hand) {
				if(c.getArtist() == Artist.values()[bestIndex]) {
					return c;
				}
			}
			
			//the best artist was not there so its value is useless
			artistEvals[bestIndex] = Double.NEGATIVE_INFINITY;
		}
		
		return null;
	}

	@Override
	public Card chooseSecondCard(Artist artist) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBid(int highestBid) {
		double eval = getEvaluationValue(biddingCard.getArtist());
		
		if(money > (int)(((double)money)*eval)) {
			return (int)(((double)money)*eval);
		} else {
			return money;
		}
	}

	@Override
	public int getFixedPrice() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean buy(int price) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void announceCard(Card card, boolean isDouble) {
		// TODO Auto-generated method stub
		//System.out.println("here");
		super.announceCard(card, isDouble);
	}

	@Override
	public void announceSeasonEnd(int season) {
		// TODO Auto-generated method stub

	}

	@Override
	public void announceAuctionWinner(int turn, String name, int price) {
		// TODO Auto-generated method stub

	}
	
	private double getEvaluationValue(Artist artist) {
		
		
		
		//Raw vars are ones without the weight applied yet
		double handScoreRaw = 0;
		double winningsScoreRaw = 0;
		double seasonScoreRaw = 0;
		double normalizedMoneyRaw = 0;
		double normalizedArtistValueCTMoneyRaw = 0;
		double normalizedArtistValueCTOthersRaw = 0;
		
		//TODO incorporate other players winnings into this
		
		double totalScore = 0;
		
		//get hand score
		for(Card c : hand) {
			if(c.getArtist() == biddingCard.getArtist()) {
				handScoreRaw++;
			}
		}
		handScoreRaw /= hand.size();
		
		//get the winnings score
		ArrayList<ArtistCount> sortedWinnings = new ArrayList<ArtistCount>();
		for(int i = 0; i < Artist.values().length; i++) {
			int count = 0;
			for(Card c : winnings) {
				if(c.getArtist() == Artist.values()[i]) {
					count++;
				}
			}
			sortedWinnings.add(new ArtistCount(Artist.values()[i], count));
		}
		sortedWinnings.sort((ArtistCount a, ArtistCount b) -> a.compareTo(b));
		for(int i = 0; i < sortedWinnings.size(); i++) {
			if(sortedWinnings.get(i).getArtist() == artist) {
				winningsScoreRaw = 1.0-((double)i)/Artist.values().length;
			}
		}
		
		//get season values score
		for(int i = 0; i < OGS.getSeasonValues().length; i++) {
			if(OGS.getSeasonValues()[i].getArtist() == artist) {
				winningsScoreRaw = 1.0 - ((double)i)/5.0;
			}
		}
		
		//get normalized money value (compared to other players)
		getBestOtherPlayer();
		if(bestPlayerMoney > money) {
			normalizedMoneyRaw = ((double)money)/((double)bestPlayerMoney);
		} else {
			normalizedMoneyRaw = 1.0;
		}
		
		//get the normalized artist value (compared to money)
		double artistValue = OGS.getArtistValue(artist);
		normalizedArtistValueCTMoneyRaw = artistValue/((double)money);
		
		//get the normalized artist value (compared to best other artist)
		double bestArtistValue = -1;
		for(int i = 0; i < Artist.values().length; i++) {
			if(OGS.getArtistValue(Artist.values()[i]) > bestArtistValue) {
				bestArtistValue = OGS.getArtistValue(Artist.values()[i]);
			}
		}
		normalizedArtistValueCTOthersRaw = artistValue/bestArtistValue;
		
		totalScore += handScoreRaw * weights[0];
		totalScore += winningsScoreRaw * weights[1];
		totalScore += seasonScoreRaw * weights[2];
		totalScore += normalizedMoneyRaw * weights[3];
		totalScore += normalizedArtistValueCTMoneyRaw * weights[4];
		totalScore += normalizedArtistValueCTOthersRaw * weights[5];
		
		return totalScore;
	}

	//not sure if I will use this yet
//	/**
//	 * Sometimes it is useful to only have a positive value returned from the evaluation function.
//	 * @param value
//	 * @return
//	 */
//	private double mapEvalFuncValue(double value) {
//		return (value+(EVAL_VALUE_COUNT))/2;
//	}
}
