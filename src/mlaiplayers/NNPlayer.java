package mlaiplayers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import core.Artist;
import core.ArtistCount;
import core.AuctionType;
import core.Card;
import core.ObservableGameState;
import player.MemoryAIPlayer;

public class NNPlayer extends MemoryAIPlayer{

	private final double alpha = 0.5;

	private final int HIDDEN_LAYERS = 1;
	private final int HIDDEN_LAYER_NODES = 20;

	private final int INPUT_NODE_COUNT = 34;

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

		if(HIDDEN_LAYERS > 0) {
			ArrayList<Node> biddingFirstLayer = new ArrayList<Node>();
			//make the first layer
			for(int i = 0; i < HIDDEN_LAYER_NODES; i++) {
				biddingFirstLayer.add(new HiddenNode());
			}
			biddingLayers.add(biddingFirstLayer);

			setInputNodes(inputNodes, biddingFirstLayer, biddingInputWeights);

			for(int i = 0; i < HIDDEN_LAYERS-1; i++) {
				ArrayList<Node> layer = new ArrayList<Node>();
				for(int k = 0; k < HIDDEN_LAYER_NODES; k++) {
					layer.add(new HiddenNode());
				}
				biddingLayers.add(layer);

				setInputNodes(layer, biddingLayers.get(i+1), biddingHLWeights[i]);
			}

			biddingOutputNode = new HiddenNode();
			ArrayList<Node> biddingOutputLayer = new ArrayList<Node>();
			biddingOutputLayer.add(biddingOutputNode);
			biddingLayers.add(biddingOutputLayer);
			for(int i = 0; i < biddingLayers.get(biddingLayers.size()-1).size(); i++) {
				biddingOutputNode.addInput((HiddenNode)biddingLayers.get(biddingLayers.size()-1).get(i), biddingOutputWeights[i]);
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

			//make the hidden layers
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
				for(int i = 0; i < pickingLayers.get(pickingLayers.size()-1).size(); i++) {
					pickingOutputNodes.get(k).addInput((HiddenNode)pickingLayers.get(pickingLayers.size()-1).get(i), pickingOutputWeights[k][i]);
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
	}
	
	public void clearMoves() {
		moves = new ArrayList<Move>();
	}
	
	public ArrayList<Move> getMoves(){
		return moves;
	}

	private void setInputNodes(ArrayList<Node> inputs, ArrayList<Node> outputs, double[][] weights) {
		for(int i = 0; i < outputs.size(); i++) {
			for(int k = 0; k < inputs.size(); k++) {
				outputs.get(i).addInput(inputs.get(k), weights[i][k]);
			}
		}
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

		double error = node.output()*(1-node.output())*sumErrors;
		((HiddenNode)node).setError(error);
	}

	private void updateWeight(Node node, Node input) {
		double newWeight = ((HiddenNode)node).getWeight(input)+alpha*(((HiddenNode)node).getError()*input.output());
		((HiddenNode)node).updateWeight(input,newWeight);
	}

	public void learn(ArrayList<Move> correctMoves) {
		for(Move m : correctMoves) {
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
		for(int i = network.size()-1; i > 0; i--) {
			//get the current layer
			ArrayList<Node> outputLayer = network.get(i);
			ArrayList<Node> layer = network.get(i-1);
			for(Node n: layer) {
				calcError(n, outputLayer);
			}
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
	}

	private class InputNode extends Node{
		private double value;

		public void setValue(double value) {
			this.value = value;
		}

		public double output() {
			return value;
		}

		public void addInput(Node input, double weight) {
			//Do nothing
			//input nodes take no inputs
		}
	}

	private class HiddenNode extends Node{

		private HashMap<Node,Double> inputs;
		private double error = 0;

		public HiddenNode() {
			inputs = new HashMap<Node,Double>();
		}

		public void addInput(Node input, double weight) {
			//			inputs.add(input);
			//			weights.add(weight);
			inputs.put(input, weight);
		}

		public double output() {
			//			double output = 0;
			//			for(int i = 0; i < inputs.size(); i++) {
			//				output += inputs.get(i).output()*weights.get(i).doubleValue();
			//			}
			//			return output;

			double output = 0;
			for(Map.Entry<Node, Double> entry : inputs.entrySet()) {
				output += entry.getKey().output()*entry.getValue();
			}
			return output;
		}

		public double getWeight(Node input) {
			return inputs.get(input);
		}

		public void updateWeight(Node input, double weight) {
			inputs.put(input, weight);
		}

		public void setError(double error) {
			this.error = error;
		}

		public double getError() {
			return error;
		}
	}
	
	public class Move {
		private double[] inputs;
		private double[] outputs;
		private boolean isBidding = false;
		
		public Move(double[] i, double[] o, boolean isBidding) {
			inputs = i;
			outputs = o;
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
			if(biddingCard.getArtist() == Artist.values()[i-12]) {
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
		getInputs();
		double[] outputs = new double[Artist.values().length];
		for(int i = 0; i < Artist.values().length; i++) {
			outputs[i] = pickingOutputNodes.get(i).output();
		}
		
		Move m = new Move(getCurrentInputValues(), outputs, false);
		
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
					hand.remove(c);
					return c;
				}
			}
		}
		
		return hand.remove(0);
	}
	
	//TODO need fixed price stuff
}
