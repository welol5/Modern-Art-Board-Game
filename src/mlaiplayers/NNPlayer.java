package mlaiplayers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import core.Artist;
import core.ArtistCount;
import core.AuctionType;
import core.Card;
import core.ObservableGameState;
import player.MemoryAIPlayer;

public class NNPlayer extends MemoryAIPlayer{

	private final double alpha = 0.05;

	public static final int HIDDEN_LAYERS = 1;
	public static final int HIDDEN_LAYER_NODES = 20;

	public static final int INPUT_NODE_COUNT = 34;

	//These do not include the outputs, however, they do include the inputs
	private ArrayList<ArrayList<Node>> biddingLayers;
	private ArrayList<ArrayList<Node>> pickingLayers;

	private HiddenNode biddingOutputNode;
	private ArrayList<Node> pickingOutputNodes;

	//var for tracking moves made
	private ArrayList<Move> moves;

	//things the AI needs to keep track of
	private double totalCardsPlayed = 0;

	public NNPlayer(String name, ObservableGameState state, int playerCount, int turnIndex, double[][][] biddingHLWeights, double[][] biddingInputWeights, double[] biddingOutputWeights , double[][][] pickingHLWeights, double[][] pickingInputWeights, double[][] pickingOutputWeights) {
		super(name,state,playerCount,turnIndex);
		setWeights(biddingHLWeights,biddingInputWeights,biddingOutputWeights,pickingHLWeights,pickingInputWeights,pickingOutputWeights);
	}

	public void setWeights(double[][][] biddingHLWeights, double[][] biddingInputWeights, double[] biddingOutputWeights , double[][][] pickingHLWeights, double[][] pickingInputWeights, double[][] pickingOutputWeights) {

		moves = new ArrayList<Move>();

		ArrayList<Node> inputNodes = new ArrayList<Node>();
		makeInputNodes(inputNodes);

		biddingLayers = new ArrayList<ArrayList<Node>>();
		biddingLayers.add(inputNodes);

		pickingLayers = new ArrayList<ArrayList<Node>>();
		pickingLayers.add(inputNodes);

		if(HIDDEN_LAYERS > 0) {
			ArrayList<Node> biddingFirstLayer = new ArrayList<Node>();
			//make the first layer
			for(int i = 0; i < HIDDEN_LAYER_NODES; i++) {
				biddingFirstLayer.add(new HiddenNode());
			}
			biddingLayers.add(biddingFirstLayer);

			setInputNodes(inputNodes, biddingFirstLayer, biddingInputWeights);

			for(int i = 1; i < HIDDEN_LAYERS-1; i++) {
				ArrayList<Node> layer = new ArrayList<Node>();
				for(int k = 0; k < HIDDEN_LAYER_NODES; k++) {
					layer.add(new HiddenNode());
				}
				biddingLayers.add(layer);

				setInputNodes(biddingLayers.get(i), layer, biddingHLWeights[i-1]);
			}

			biddingOutputNode = new HiddenNode();
			ArrayList<Node> biddingOutputLayer = new ArrayList<Node>();
			biddingOutputLayer.add(biddingOutputNode);
			biddingLayers.add(biddingOutputLayer);
			for(int i = 0; i < biddingLayers.get(biddingLayers.size()-2).size(); i++) {
				biddingOutputNode.addInput((HiddenNode)biddingLayers.get(biddingLayers.size()-2).get(i), biddingOutputWeights[i]);
			}

			//picking network
			ArrayList<Node> pickingFirstLayer = new ArrayList<Node>();
			//make the first layer
			for(int i = 0; i < HIDDEN_LAYER_NODES; i++) {
				pickingFirstLayer.add(new HiddenNode());
			}
			pickingLayers.add(pickingFirstLayer);

			//input nodes are the same for both networks
			setInputNodes(inputNodes, pickingFirstLayer, pickingInputWeights);

			//make the hidden layers//TODO fix this
			for(int i = 0; i < HIDDEN_LAYERS-1; i++) {
				ArrayList<Node> layer = new ArrayList<Node>();
				for(int k = 0; k < HIDDEN_LAYER_NODES; k++) {
					layer.add(new HiddenNode());
				}
				pickingLayers.add(layer);

				setInputNodes(layer, pickingLayers.get(i+1), pickingHLWeights[i]);
			}

			pickingOutputNodes = new ArrayList<Node>();
			pickingLayers.add(pickingOutputNodes);
			//make output nodes
			for(int i = 0; i < Artist.values().length; i++) {
				pickingOutputNodes.add(new HiddenNode());
			}

			//set inputs for output nodes
			for(int k = 0; k < pickingOutputNodes.size(); k++) {
				for(int i = 0; i < pickingLayers.get(pickingLayers.size()-2).size(); i++) {
					pickingOutputNodes.get(k).addInput((HiddenNode)pickingLayers.get(pickingLayers.size()-2).get(i), pickingOutputWeights[k][i]);
				}
			}
		} else {
			//TODO deal with no hidden layers
		}

	}

	public NNPlayer(String name, ObservableGameState state, int playerCount, int turnIndex) {
		super(name,state,playerCount,turnIndex);
		//make random weights

		//bidding inputs
		double[][] biddingInputs = new double[HIDDEN_LAYER_NODES][INPUT_NODE_COUNT];
		for(int i = 0; i < HIDDEN_LAYER_NODES; i++) {
			for(int k = 0; k < INPUT_NODE_COUNT; k++) {
				biddingInputs[i][k] = Math.random();
			}
		}

		//picking inputs
		double[][] pickingInputs = new double[HIDDEN_LAYER_NODES][INPUT_NODE_COUNT];
		for(int i = 0; i < HIDDEN_LAYER_NODES; i++) {
			for(int k = 0; k < INPUT_NODE_COUNT; k++) {
				pickingInputs[i][k] = Math.random();
			}
		}

		//bidding outputs
		double[] biddingOutputs = new double[HIDDEN_LAYER_NODES];
		for(int i = 0; i < HIDDEN_LAYER_NODES; i++) {
			biddingOutputs[i] = Math.random();
		}

		//picking outputs
		double[][] pickingOutputs = new double[Artist.values().length][HIDDEN_LAYER_NODES];
		for(int i = 0; i < pickingOutputs.length; i++) {
			for(int k = 0; k < HIDDEN_LAYER_NODES; k++) {
				pickingOutputs[i][k] = Math.random();
			}
		}

		//bidding hiddens
		double[][][] biddingHLWeights = new double[HIDDEN_LAYERS][HIDDEN_LAYER_NODES][HIDDEN_LAYER_NODES];
		for(int i = 0; i < HIDDEN_LAYERS; i++) {
			for(int k = 0; k < HIDDEN_LAYER_NODES; k++) {
				for(int j = 0; j < HIDDEN_LAYER_NODES; j++) {
					biddingHLWeights[i][k][j] = Math.random();
				}
			}
		}

		//picking hiddens
		double[][][] pickingHLWeights = new double[HIDDEN_LAYERS][HIDDEN_LAYER_NODES][HIDDEN_LAYER_NODES];
		for(int i = 0; i < HIDDEN_LAYERS; i++) {
			for(int k = 0; k < HIDDEN_LAYER_NODES; k++) {
				for(int j = 0; j < HIDDEN_LAYER_NODES; j++) {
					pickingHLWeights[i][k][j] = Math.random();
				}
			}
		}

		setWeights(biddingHLWeights,biddingInputs,biddingOutputs,pickingHLWeights,pickingInputs,pickingOutputs);
	}

	public void updateOGS(ObservableGameState state) {
		this.state = state;
		OGS = state;

		money = 100;
	}

	public void clearMoves() {
		moves = new ArrayList<Move>();
	}

	public ArrayList<Move> getMoves(){
		return moves;
	}

	private void setInputNodes(ArrayList<Node> inputs, ArrayList<Node> outputs, double[][] weights) {
		//		System.out.println("layer");
		//		System.out.println("weights : " + weights.length);
		for(int i = 0; i < outputs.size(); i++) {
			//			System.out.println("weights[" + i + "] : " + weights[i].length);
			for(int k = 0; k < inputs.size(); k++) {
				//				System.out.println(outputs.get(i) + " contains " + inputs.get(k));
				outputs.get(i).addInput(inputs.get(k), weights[i][k]);
			}
		}
		//		System.out.println("end layer");
	}

	private void makeInputNodes(ArrayList<Node> inputs) {
		for(int i = 0; i < INPUT_NODE_COUNT; i++) {
			inputs.add(new InputNode());
			((InputNode)inputs.get(i)).setValue(0.5);
		}
	}

	private void calcError(Node node, ArrayList<Node> outputs) {
		double sumErrors = 0;
		for(Node n : outputs) {
			sumErrors += ((HiddenNode)n).getWeight(node)*((HiddenNode)n).getError();
		}
		//		System.out.println("sumErrors " + sumErrors);

		double error = node.output()*(1-node.output())*sumErrors;
		try {
			((HiddenNode)node).setError(error);//TODO tries to cast input nodes to hidden nodes
		} catch (ClassCastException e) {
			//if this exception occurs the node is an input and the error does not matter
		}
	}

	private void updateWeight(Node node, Node input) {
		//		System.out.println("update " + node + " to " + input);
		//		System.out.println(input);
		//		System.out.println(node);
		//		System.out.println(input.output());
		//		System.out.println(((HiddenNode)node).getError());
		double newWeight = ((HiddenNode)node).getWeight(input)+alpha*(((HiddenNode)node).getError()*input.output());
		//		System.out.println( "old : " + ((HiddenNode)node).getWeight(input) + " : new : " + newWeight);
		((HiddenNode)node).updateWeight(input,newWeight);
		//		if(newWeight == Double.NaN) {
		//			System.exit(0);
		//		}
	}

	public void learn(ArrayList<Move> correctMoves) {
		for(Move m : correctMoves) {
			//			System.out.println(m.isBidding + " : " + m.getOutputs().length);
			if(m.isBidding) {
				learnBiddingMove(m.getInputs(), m.getOutputs()[0]);
			} else {
				learnPickingMove(m.getInputs(), m.getOutputs());
			}
		}
	}

	private void learnBiddingMove(double[] inputs, double correctOutput) {
		setInputs(inputs);
		double output = biddingOutputNode.output();
		double error = output*(1-output)*(correctOutput-output);
		biddingOutputNode.setError(error);

		backpropagate(biddingLayers);
	}

	private void learnPickingMove(double[] inputs, double[] correctOutputs) {
		setInputs(inputs);
		//		System.out.println(correctOutputs.length);
		double[] outputs = new double[pickingOutputNodes.size()];

		//get initial outputs
		for(int i = 0; i < outputs.length; i++) {
			outputs[i] = pickingOutputNodes.get(i).output();
		}

		//calculate error for the output nodes
		for(int i = 0; i < outputs.length; i++) {
			((HiddenNode)pickingOutputNodes.get(i)).setError(outputs[i]*(1-outputs[i])*(correctOutputs[i]-outputs[i]));
		}

		backpropagate(pickingLayers);
	}

	private void backpropagate(ArrayList<ArrayList<Node>> network) {
		//calculate errors
		//		System.out.println(network.size());
		for(int i = network.size()-1; i > 0; i--) {
			//			System.out.println("layer");
			//get the current layer
			ArrayList<Node> outputLayer = network.get(i);
			ArrayList<Node> layer = network.get(i-1);
			//			System.out.println(layer.size());
			for(Node n: layer) {
				calcError(n, outputLayer);
				try {
					//				System.out.println(((HiddenNode)n).getError());
				} catch (Exception e) {}
			}
			//			System.out.println("end layer");
		}

		//update weights
		for(int i = network.size()-1; i > 0; i--) {
			ArrayList<Node> layer = network.get(i);
			ArrayList<Node> inputLayer = network.get(i-1);
			for(int layerNodeIndex = 0; layerNodeIndex < layer.size(); layerNodeIndex++) {
				for(int inputNodeIndex = 0; inputNodeIndex < inputLayer.size(); inputNodeIndex++) {
					updateWeight(layer.get(layerNodeIndex), inputLayer.get(inputNodeIndex));
				}
			}
		}
		//		System.out.println("done updateing");
	}

	/**
	 * Sets the values for all of the input nodes in the current neural netowork.
	 * @param inputs
	 */
	private void setInputs(double[] inputs) {
		ArrayList<Node> inputNodes = biddingLayers.get(0);
		for(int i = 0; i < inputs.length; i++) {
			((InputNode)inputNodes.get(i)).setValue(inputs[i]);
		}
	}

	private double[] getCurrentInputValues() {
		double[] inputs = new double[biddingLayers.get(0).size()];
		for(int i = 0; i < inputs.length; i++) {
			inputs[i] = biddingLayers.get(0).get(i).output();
		}
		return inputs;
	}

	private abstract class Node{
		public abstract double output();
		public abstract void addInput(Node input, double weight);
		public final String ID;
		public Node() {
			String tempID = "";
			for(int i = 0; i < 32; i++) {
				tempID += (char)((int)(Math.random()*128));
			}
			ID = tempID;
		}
	}

	private class InputNode extends Node{
		private double value;

		public InputNode() {
			super();
		}

		public void setValue(double value) {
			this.value = value;
		}

		public double output() {
			if(value <= 1) {
				return value;
			} else {
				return 1/(1+Math.exp(-1*value));
			}
		}

		public void addInput(Node input, double weight) {
			//Do nothing
			//input nodes take no inputs
		}
	}

	private class HiddenNode extends Node{

		private HashMap<String,Double> inputs;
		private ArrayList<Node> inputNodes;
		private double error = 0;

		public HiddenNode() {
			super();
			inputs = new HashMap<String,Double>();
			inputNodes = new ArrayList<Node>();
		}

		public void addInput(Node input, double weight) {
			//			inputs.add(input);
			//			weights.add(weight);
			inputNodes.add(input);
			inputs.put(input.ID, weight);
		}

		public double output() {
			double output = 0;
			for(Node n : inputNodes) {
				output += n.output()*inputs.get(n.ID);
				//				System.out.println(inputs.get(n.ID));
			}
			return 1/(1+Math.exp(-1*output));
		}

		public double getWeight(Node input) {
			return inputs.get(input.ID);
		}

		public void updateWeight(Node input, double weight) {
			inputs.put(input.ID, weight);
		}

		public void setError(double error) {
			this.error = error;
		}

		public double getError() {
			return error;
		}

		public int getInputNodeCount(){
			return inputNodes.size();
		}

		public double getWeight(int index) {
			return inputs.get(inputNodes.get(index).ID);
		}
	}

	public class Move {
		private double[] inputs;
		private double[] outputs;
		private boolean isBidding = false;

		public Move(double[] i, double[] o, boolean isBidding) {
			inputs = i;
			outputs = o;
			this.isBidding = isBidding;
		}

		public double[] getInputs() {
			return inputs;
		}

		public double[] getOutputs() {
			return outputs;
		}

		public boolean getIsBidding() {
			return isBidding;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////
	//override

	private void getInputs() {
		double[] inputs = new double[INPUT_NODE_COUNT];

		inputs[0] = turn;
		inputs[1] = totalCardsPlayed;

		inputs[2] = state.getSeasonValue(Artist.values()[0]);
		inputs[3] = state.getSeasonValue(Artist.values()[1]);
		inputs[4] = state.getSeasonValue(Artist.values()[2]);
		inputs[5] = state.getSeasonValue(Artist.values()[3]);
		inputs[6] = state.getSeasonValue(Artist.values()[4]);

		inputs[7] = state.getArtistValue(Artist.values()[0]);
		inputs[8] = state.getArtistValue(Artist.values()[1]);
		inputs[9] = state.getArtistValue(Artist.values()[2]);
		inputs[10] = state.getArtistValue(Artist.values()[3]);
		inputs[11] = state.getArtistValue(Artist.values()[4]);

		for(int i = 12; i < 17; i++) {
			if(biddingCard == null) {
				inputs[i] = 0;
			} else if(biddingCard.getArtist() == Artist.values()[i-12]) {
				inputs[i] = 1;
			} else {
				inputs[i] = 0;
			}
		}

		//coppied and pasted, does not need to be sorted
		ArrayList<ArtistCount> sortedWinnings = new ArrayList<ArtistCount>();
		for(Artist a : Artist.values()) {
			int count = 0;
			for(Card c : winnings) {
				if(c.getArtist() == a) {
					count++;
				}
			}
			sortedWinnings.add(new ArtistCount(a,count));
		}
		//sortedWinnings.sort((ArtistCount a, ArtistCount b) -> a.compareTo(b));//love this

		for(int i = 18; i < 23; i++) {
			inputs[i] = sortedWinnings.get(i-18).getCount();
		}

		getBestOtherPlayer();
		inputs[24] = ((double)money)/((double)bestPlayerMoney);

		inputs[25] = players.length;

		double[] artistHandCount = new double[Artist.values().length];
		for(int i = 0; i < Artist.values().length; i++) {
			artistHandCount[i] = 0;
			for(Card c : hand) {
				if(c.getArtist() == Artist.values()[i]) {
					artistHandCount[i]++;
				}
			}
		}
		for(int i = 26; i < 31; i++) {
			inputs[i] = artistHandCount[i-26];
		}

		if(isDouble) {
			inputs[32] = 1;
		} else {
			inputs[32] = 0;
		}

		ArrayList<Node> inputLayer = biddingLayers.get(0);

		for(int i = 0; i < inputs.length; i++) {
			((InputNode)inputLayer.get(i)).setValue(inputs[i]);
		}
	}

	@Override
	public void announceCard(Card card, boolean isDouble) {
		this.biddingCard = card;
		this.isDouble = isDouble;
		totalCardsPlayed++;
	}

	@Override
	public int getBid(int highestBid) {
		getInputs();
		double bid = biddingOutputNode.output()*((double)money);

		if(bid > money) {
			double[] outputNode = new double[1];
			outputNode[0] = 1;
			Move m = new Move(getCurrentInputValues(), outputNode, true); 
			moves.add(m);
			return money;
		} else {
			double[] outputNode = new double[1];
			outputNode[0] = biddingOutputNode.output();
			Move m = new Move(getCurrentInputValues(),outputNode,true);
			moves.add(m);
			return (int)bid;
		}
	}

	@Override
	public boolean buy(int price) {
		getInputs();
		double bid = biddingOutputNode.output()*((double)money);

		if(bid > price) {
			double[] outputNode = new double[1];
			outputNode[0] = 1;
			Move m = new Move(getCurrentInputValues(),outputNode,true);
			moves.add(m);
			return true;
		} else {
			double[] outputNode = new double[1];
			outputNode[0] = 0;
			Move m = new Move(getCurrentInputValues(),outputNode,true);
			moves.add(m);
			return false;
		}
	}

	@Override
	public Card chooseCard() {
		
		if(hand.size() < 1) {
			return null;
		}
		
		//		System.out.println("picking");
		getInputs();
		double[] outputs = new double[Artist.values().length];
		for(int i = 0; i < Artist.values().length; i++) {
			outputs[i] = pickingOutputNodes.get(i).output();
		}
		//		System.out.println(outputs.length);

		Move m = new Move(getCurrentInputValues(), outputs, false);
		moves.add(m);

		//		System.out.print("Move");
		//		for(int i = 0; i < outputs.length; i++) {
		//			System.out.print(" : " + outputs[i]);
		//		}
		//		System.out.println();

		for(int i = 0; i < outputs.length; i++) {
			int highestIndex = 0;
			double highestValue = Double.NEGATIVE_INFINITY;
			for(int k = 0; k < outputs.length; k++) {
				if(outputs[k] > highestValue) {
					highestIndex = k;
					highestValue = outputs[k];
				}
			}

			Artist artist = Artist.values()[highestIndex];

			for(Card c : hand) {
				if(c.getArtist() == artist && c.getAuctionType() != AuctionType.DOUBLE) {
					//					System.out.println("card chosen");
					hand.remove(c);
					return c;
				}
			}
			outputs[highestIndex] = 0;
		}
		//		System.out.println("defaulted");
		return hand.remove(0);
	}

	//TODO need fixed price stuff


	///////////////////////////////////////////////////////////////
	//saving

	public void printNetworkToFile(PrintWriter writer) throws IOException {

		writer.println("--network start--");
		writer.println("" + INPUT_NODE_COUNT + ":input node count");
		writer.println("" + HIDDEN_LAYER_NODES + ":hidden layer nodes");
		writer.println("" + HIDDEN_LAYERS + ":hidden layers");
		writer.println("" + alpha + ":alpha");

		//print the bidding network
		writer.println("-=bidding network start=-");
		//starts at 1 to ignore the input layer
		for(int i = 1; i < biddingLayers.size(); i++) {
			writer.println("$$layer start");
			for(int k = 0; k < biddingLayers.get(i).size(); k++) {
				writer.print("[");
				for(int w = 0; w < ((HiddenNode)biddingLayers.get(i).get(k)).getInputNodeCount(); w++) {
					writer.print(((HiddenNode)biddingLayers.get(i).get(k)).getWeight(w));
					if(w+1 < ((HiddenNode)biddingLayers.get(i).get(k)).getInputNodeCount()) {
						writer.print(",");
					}
				}
				writer.println("]");
			}
		}

		//print the picking network
		writer.println("-=picking network start=-");
		//starts at 1 to ignore the input layer
		for(int i = 1; i < pickingLayers.size(); i++) {
			writer.println("$$layer start");
			for(int k = 0; k < pickingLayers.get(i).size(); k++) {
				//				System.out.println(pickingLayers.get(i).size());
				writer.print("[");
				for(int w = 0; w < ((HiddenNode)pickingLayers.get(i).get(k)).getInputNodeCount(); w++) {
					writer.print(((HiddenNode)pickingLayers.get(i).get(k)).getWeight(w));
					if(w+1 < ((HiddenNode)pickingLayers.get(i).get(k)).getInputNodeCount()) {
						writer.print(",");
					}
				}
				writer.println("]");
			}
		}
		writer.println("--network printed--");
		writer.flush();
		System.out.println("network saved");
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//loading

	public static double[][][] loadWeights(Scanner reader) {
		System.out.println("loading network");
		double[][][] weights = new double[HIDDEN_LAYERS+1][][];
		reader.nextLine();//remove the first layer start line

		//for every layer including the output layer
		for(int i = 0; i < NNPlayer.HIDDEN_LAYERS+1; i++) {
			ArrayList<double[]> layerValues = new ArrayList<double[]>();
			for(int k = 0; true; k++) {
				String line;
				try {
					line = reader.nextLine();
				} catch(NoSuchElementException e) {
					break;
				}
				if(line.equals("$$layer start") || line.equals("--network printed--") || line.equals("-=picking network start=-")) {
					break;
				} else {
					line = line.substring(1, line.length()-2);
					String[] values = line.split(",");
					double[] nodeWeights = new double[values.length];
					for(int j = 0; j < values.length; j++) {
						nodeWeights[j] = Double.parseDouble(values[j]);
					}
					layerValues.add(nodeWeights);
				}
			}

			double[][] layerWeights = layerValues.toArray(new double[1][]);
			//			if(layerWeights.length < 20) {
			//				System.out.println("layerWeights : " + layerWeights.length);
			//				System.out.println(layerWeights[0][0]);
			//			}

			//			System.out.println(layerValues);

			//			for(int j = 0; j < layerWeights.length; j++) {
			//				System.out.println(layerWeights[j] + " : " + i);
			//			}

			//only include valid layers
			if(layerWeights != null) {
				//				System.out.println("layer weight " + layerWeights);
				weights[i] = layerWeights;
			}

		}

		return weights;
	}

	public NNPlayer(String name, ObservableGameState state, int playerCount, int turnIndex, double[][][] biddingWeights, double[][][] pickingWeights) {
		super(name,state,playerCount,turnIndex);

		//Separate out the parts of the input for the constructor
		double[][] biddingInputWeights = biddingWeights[0];
		double[][][] biddingHiddenLayerWeights = new double[biddingWeights.length-2][][];
		for(int i = 0; i < biddingHiddenLayerWeights.length; i++) {
			biddingHiddenLayerWeights[i] = biddingWeights[i+1];
		}
		double[][] biddingOutputWeights = biddingWeights[biddingWeights.length-1];
		//			System.out.println(biddingOutputWeights);

		double[][] pickingInputWeights = pickingWeights[0];
		double[][][] pickingHiddenLayerWeights = new double[pickingWeights.length-2][][];
		for(int i = 0; i < pickingHiddenLayerWeights.length; i++) {
			pickingHiddenLayerWeights[i] = pickingWeights[i+1];
		}
		double[][] pickingOutputWeights = pickingWeights[biddingWeights.length-1];

		setWeights(biddingHiddenLayerWeights, biddingInputWeights, biddingOutputWeights[0], pickingHiddenLayerWeights, pickingInputWeights, pickingOutputWeights);
	}

	public NNPlayer(String name, ObservableGameState state, int playerCount, int turnIndex, Scanner fileInput) {
		super(name,state,playerCount,turnIndex);

		System.out.println("loading player");
		String checkNetworkFile = fileInput.nextLine();
		//System.out.println(checkNetworkFile);
		if(!checkNetworkFile.equalsIgnoreCase("--network start--")) {
			throw new IllegalArgumentException("File scanner did not start at the begining of a network");
		} else {
			//assumes network settings match current settings
			String inputNodeCount = fileInput.nextLine().split(":")[0];
			String hiddenLayerNodes = fileInput.nextLine().split(":")[0];
			String hiddenLayers = fileInput.nextLine().split(":")[0];
			String alpha = fileInput.nextLine().split(":")[0];
			fileInput.nextLine();//read in the bidding network start line
			double[][][] biddingWeights = loadWeights(fileInput);
			//			fileInput.nextLine();//read in the bidding network end line
			//			fileInput.nextLine();//read in the picking network start line
			double[][][] pickingWeights = loadWeights(fileInput);
			//			fileInput.nextLine();//read in the picking network end line

			//debug
			//			for(int i = 0; i < biddingWeights.length; i++) {
			//				System.out.println("BW: " + biddingWeights[i]);
			//			}

			//Separate out the parts of the input for the constructor
			double[][] biddingInputWeights = biddingWeights[0];
			double[][][] biddingHiddenLayerWeights = new double[biddingWeights.length-2][][];
			for(int i = 0; i < biddingHiddenLayerWeights.length; i++) {
				biddingHiddenLayerWeights[i] = biddingWeights[i+1];
			}
			double[][] biddingOutputWeights = biddingWeights[biddingWeights.length-1];
			//			System.out.println(biddingOutputWeights);

			double[][] pickingInputWeights = pickingWeights[0];
			double[][][] pickingHiddenLayerWeights = new double[pickingWeights.length-2][][];
			for(int i = 0; i < pickingHiddenLayerWeights.length; i++) {
				pickingHiddenLayerWeights[i] = pickingWeights[i+1];
			}
			double[][] pickingOutputWeights = pickingWeights[biddingWeights.length-1];

			setWeights(biddingHiddenLayerWeights, biddingInputWeights, biddingOutputWeights[0], pickingHiddenLayerWeights, pickingInputWeights, pickingOutputWeights);
		}
	}
}
