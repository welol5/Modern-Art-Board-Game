import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import core.GameState;
import core.ObservableGameState;
import fxmlgui.GameDriver;
import mlaiplayers.GeneticAIPlayer;
import mlaiplayers.LearningAI;
import mlaiplayers.MemoizerAIPlayer;
import mlaiplayers.MemoizerAIPlayerDB;
import player.BasicPredictiveAIPlayer;
import player.BasicPredictiveAIPlayerV2;
import player.BasicPredictiveAIPlayerV3;
import player.HandStateCardPicker;
import player.HighRoller;
import player.MemoryAIPlayer;
import player.Merchant;
import player.Player;
import player.PlayerType;
import player.RandomPlayer;
import player.ReactiveAIPlayer;

public class GeneticTrainingTournament {

	/**
	 * The amount of games that the MLAI will play.
	 */
	private static int games = 1000;
	
	private static double mutationChance = 0.1;

	/**
	 * This array holds the list of players the MLAI player will be training with.
	 */
	private ArrayList<Player> players = new ArrayList<Player>();

	/**
	 * The list of names the players will be using. This list should be
	 * in the same order as the {@link PlayerType} list.
	 */
	private static ArrayList<String> names;

	/**
	 * The list of {@link PlayerType} that will be playing in this game
	 * in the order that they will be during the game.
	 */
	private static ArrayList<PlayerType> types = null;

	/**
	 * The MLAI being trained.
	 */
	private static LearningAI MLAIPlayer;
	private static PlayerType MLAIType = PlayerType.GENETIC_AI;
	//private static String MLAIFileName = "GeneticAIWeights.txt";

	/**
	 * Ammount of players in a generation
	 */
	private static final int POPULATION_SIZE = 1000;
	private static final int GENERATIONS = 100;
	private static double[][] allPlayersWeights = new double[POPULATION_SIZE][GeneticAIPlayer.EVAL_VALUE_COUNT*2];

	public static void main(String[] args) {
		
		File trackingFile = new File("trackingFile.txt");
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(trackingFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}

		//generate a set of random weights
		for(int i = 0; i < POPULATION_SIZE; i++) {
			for(int k = 0; k < GeneticAIPlayer.EVAL_VALUE_COUNT*2; k++) {
				allPlayersWeights[i][k] = Math.random()*2.0-1.0;
			}
		}

		for(int gen = 0; gen < GENERATIONS; gen++) {
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			int[] MLAIWins = new int[POPULATION_SIZE];
			for(int i = 0; i < MLAIWins.length; i++) {
				MLAIWins[i] = 0;
			}
			
			GameRunnerTrainer[] trainers = new GameRunnerTrainer[POPULATION_SIZE];

			for(int popIndex = 0; popIndex < POPULATION_SIZE; popIndex++) {
				
				
				
				types = new ArrayList<PlayerType>();
				types.add(MLAIType);//add the MLAI
				types.add(PlayerType.MERCHANT);
				types.add(PlayerType.BASIC_PREDICTIVE_AI_V2);
				types = randomizePlayerOrder(types);
				
				trainers[popIndex] = new GameRunnerTrainer(300000,games,types,allPlayersWeights[popIndex]);
			}
			
			//start the training
			for(int i = 0; i < trainers.length; i++) {
				trainers[i].start();
				System.out.println("trainer[" + i + "] started");
			}
			
			//wait for the training to complete
			for(int i = 0; i < trainers.length; i++) {
				try {
					//System.out.println("waiting");
					trainers[i].join();
					System.out.println("trainer[" + i + "] stopped.");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//this generation is compete
			//sort the AIs
			ArrayList<IWEV> sortedPop = new ArrayList<IWEV>();
			for(int i = 0; i < POPULATION_SIZE; i++) {
				sortedPop.add(new IWEV(trainers[i].getGeneticPlayerWeights(),trainers[i].getScore()));
			}
			sortedPop.sort((IWEV a, IWEV b) -> a.compareTo(b));
			System.out.println("Generation : " + gen);
			System.out.println("Highest winrate = " + sortedPop.get(0).evalValue);
			
			//write output to a file
			//writer.println("Gen:" + gen);
			for(int i = 0; i < POPULATION_SIZE; i++) {
				writer.print("Weights:" + i + ":" + sortedPop.get(i).evalValue);
				for(int w = 0; w < GeneticAIPlayer.EVAL_VALUE_COUNT*2; w++) {
					writer.print(":" + sortedPop.get(i).getWeight(w));
				}
				writer.println();
			}
			
			//make the next generation
			double[][] newGenWeights = new double[POPULATION_SIZE][GeneticAIPlayer.EVAL_VALUE_COUNT*2];
			//replace the bottom half of the population
			for(int i = 0; i < POPULATION_SIZE/10; i++) {
				double[] newWeightSet1 = new double[GeneticAIPlayer.EVAL_VALUE_COUNT*2];
				double[] newWeightSet2 = new double[GeneticAIPlayer.EVAL_VALUE_COUNT*2];
				for(int w = 0; w < GeneticAIPlayer.EVAL_VALUE_COUNT*2; w++) {
					if(Math.random() > 0.5) {
						newWeightSet1[w] = sortedPop.get(2*i).getWeight(w);
						newWeightSet2[w] = sortedPop.get(2*i+1).getWeight(w);
					} else {
						newWeightSet1[w] = sortedPop.get(2*i+1).getWeight(w);
						newWeightSet2[w] = sortedPop.get(2*i).getWeight(w);
					}
					
					if(Math.random() < mutationChance) {
						double mutation = Math.pow(2.0, 10*(Math.random()-1));//favors smaller mutations
						if(Math.random() > 0.5) {//chance to be negative
							mutation *=-1;
						}
						if(newWeightSet1[w]+mutation > 1) {
							newWeightSet1[w] = newWeightSet1[w] - mutation;
						} else if(newWeightSet1[w]+mutation < -1) {
							newWeightSet1[w] = newWeightSet1[w] - mutation;
						} else {
							newWeightSet1[w] = newWeightSet1[w] + mutation;
						}
					}
					if(Math.random() < mutationChance) {
						double mutation = Math.pow(2.0, 10*(Math.random()-1));//favors smaller mutations
						if(Math.random() > 0.5) {//chance to be negative
							mutation *=-1;
						}
						if(newWeightSet2[w]+mutation > 1) {
							newWeightSet2[w] = newWeightSet2[w] - mutation;
						} else if(newWeightSet2[w]+mutation < -1) {
							newWeightSet2[w] = newWeightSet2[w] - mutation;
						} else {
							newWeightSet2[w] = newWeightSet2[w] + mutation;
						}
					}
				}
				newGenWeights[i] = newWeightSet1;
				newGenWeights[i+POPULATION_SIZE/4] = newWeightSet2;
			}
			
			//copy over the best of the last gen
			//copies the old pop into the second half of the array
			for(int i = 0; i < (POPULATION_SIZE*9)/10; i++) {
				newGenWeights[i+(POPULATION_SIZE/10)] = sortedPop.get(i).weights;
			}
			
			//replace the old array
			allPlayersWeights = newGenWeights;
		}

		//System.out.println(MLAIWins);

		//save the database
		//		database.saveData(MLAIFileName);
		//System.out.println("MLAI data saved");
		System.out.println("done");
	}
	
	/**
	 * Helper method to take a list of player properties and make
	 * the actual players from them.
	 * @param names of the players that will be playing in the game.
	 * @param types of the players that will be playing in the game.
	 * @param OGS The observable game state. See the class for details.
	 * @return The list of players that will be playing in the game.
	 */
	public static Player[] makePlayers(ArrayList<String> names, ArrayList<PlayerType> types, ObservableGameState OGS, double[] geneticPlayerWeights) {
		Player[] players = new Player[names.size()];

		for(int i = 0; i < players.length; i++) {
			if(types.get(i) == PlayerType.RANDOM) {
				players[i] = new RandomPlayer(names.get(i));
			} else if(types.get(i) == PlayerType.REACTIVE_AI) {
				players[i] = new ReactiveAIPlayer(names.get(i),OGS);
			} else if(types.get(i) == PlayerType.MEMORY_AI) {
				players[i] = new MemoryAIPlayer(names.get(i),OGS, players.length,i);
			} else if(types.get(i) == PlayerType.BASIC_PREDICTIVE_AI) {
				players[i] = new BasicPredictiveAIPlayer(names.get(i),OGS, players.length,i);
			} else if(types.get(i) == PlayerType.BASIC_PREDICTIVE_AI_V2) {
				players[i] = new BasicPredictiveAIPlayerV2(names.get(i),OGS, players.length,i);
			} else if(types.get(i) == PlayerType.HIGH_ROLLER) {
				players[i] = new HighRoller(names.get(i),OGS, players.length,i);
			} else if(types.get(i) == PlayerType.MERCHANT) {
				players[i] = new Merchant(names.get(i),OGS, players.length,i);
			} else if(types.get(i) == PlayerType.HAND_STATE_CARD_PICKER) {
				players[i] = new HandStateCardPicker(names.get(i),OGS, players.length,i);
			} else if(types.get(i) == PlayerType.BASIC_PREDICTIVE_AI_V3) {
				players[i] = new BasicPredictiveAIPlayerV3(names.get(i),OGS, players.length,i);
			} else if(types.get(i) == PlayerType.MEMOIZER_AI) {
				//MLAIPlayer = new MemoizerAIPlayer(names.get(i), OGS, players.length, i, database, 0.01, 0.5);
				players[i] = (Player) MLAIPlayer;
			} else if(types.get(i) == PlayerType.GENETIC_AI) {
				MLAIPlayer = new GeneticAIPlayer(names.get(i), OGS, players.length, i, geneticPlayerWeights);
				players[i] = (Player) MLAIPlayer;
			} else {
				players[i] = new RandomPlayer(names.get(i));
			}
		}
		return players;
	}

	public static ArrayList<PlayerType> randomizePlayerOrder(ArrayList<PlayerType> players){

		for(int i = 0; i < 100; i++) {
			PlayerType player = players.remove((int) Math.random()*players.size());
			players.add(player);
		}

		return players;
	}

	private static class IWEV implements Comparable<IWEV>{

		double[] weights;
		double evalValue;

		public IWEV(double[] weights, double evaluationValue) {
			this.weights = weights;
			evalValue = evaluationValue;
		}

		@Override
		public int compareTo(IWEV o) {
			return -((int)(evalValue*100.0) - (int)(o.evalValue*100.0));
		}
		
		public double getWeight(int index) {
			return weights[index];
		}
		
		public double[] getWeights() {
			return weights;
		}
		
		public double getEvalValue() {
			return evalValue;
		}
	}
}
