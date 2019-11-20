package mlaiplayers;

import java.util.ArrayList;

import core.Artist;
import core.ObservableGameState;
import player.MemoryAIPlayer;

public class NNPlayer extends MemoryAIPlayer implements LearningAI {

	private final int HIDDEN_LAYERS = 1;
	private final int HIDDEN_LAYER_NODES = 20;

	private final int INPUT_NODE_COUNT = 31;

	private ArrayList<ArrayList> biddingLayers;
	private ArrayList<ArrayList> pickingLayers;

	private HiddenNode biddingOutputNode;
	private ArrayList<Node> pickingOutputNodes;
	
	public NNPlayer(String name, ObservableGameState state, int playerCount, int turnIndex, double[][][] biddingHLWeights, double[][] biddingInputWeights, double[] biddingOutputWeights , double[][][] pickingHLWeights, double[][] pickingInputWeights, double[][] pickingOutputWeights) {
		super(name,state,playerCount,turnIndex);
		setWeights(biddingHLWeights,biddingInputWeights,biddingOutputWeights,pickingHLWeights,pickingInputWeights,pickingOutputWeights);
	}
	
	public void setWeights(double[][][] biddingHLWeights, double[][] biddingInputWeights, double[] biddingOutputWeights , double[][][] pickingHLWeights, double[][] pickingInputWeights, double[][] pickingOutputWeights) {

		ArrayList<Node> inputNodes = new ArrayList<Node>();
		makeInputNodes(inputNodes);

		biddingLayers = new ArrayList<ArrayList>();
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

	@Override
	public void learn(boolean win) {
		// TODO Auto-generated method stub

	}

	private abstract class Node{
		public abstract double output();
		public abstract void addInput(Node input, Double weight);
	}

	private class InputNode extends Node{
		private double value;

		public void setValue(double value) {
			this.value = value;
		}

		public double output() {
			return value;
		}

		public void addInput(Node input, Double weight) {
			//Do nothing
			//input nodes take no inputs
		}
	}

	private class HiddenNode extends Node{
		private ArrayList<Node> inputs;
		private ArrayList<Double> weights;

		public void addInput(Node input, Double weight) {
			inputs.add(input);
			weights.add(weight);
		}

		public double output() {
			double output = 0;
			for(int i = 0; i < inputs.size(); i++) {
				output += inputs.get(i).output()*weights.get(i).doubleValue();
			}
			return output;
		}
	}
}
