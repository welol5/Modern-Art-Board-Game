package mlaiplayers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import core.Artist;
import core.ObservableGameState;
import player.MemoryAIPlayer;

public class NNPlayer extends MemoryAIPlayer{

	private final double alpha = 0.5;

	private final int HIDDEN_LAYERS = 1;
	private final int HIDDEN_LAYER_NODES = 20;

	private final int INPUT_NODE_COUNT = 31;

	//These do not include the outputs, however, they do include the inputs
	private ArrayList<ArrayList<Node>> biddingLayers;
	private ArrayList<ArrayList<Node>> pickingLayers;

	private HiddenNode biddingOutputNode;
	private ArrayList<Node> pickingOutputNodes;

	public NNPlayer(String name, ObservableGameState state, int playerCount, int turnIndex, double[][][] biddingHLWeights, double[][] biddingInputWeights, double[] biddingOutputWeights , double[][][] pickingHLWeights, double[][] pickingInputWeights, double[][] pickingOutputWeights) {
		super(name,state,playerCount,turnIndex);
		setWeights(biddingHLWeights,biddingInputWeights,biddingOutputWeights,pickingHLWeights,pickingInputWeights,pickingOutputWeights);
	}

	public void setWeights(double[][][] biddingHLWeights, double[][] biddingInputWeights, double[] biddingOutputWeights , double[][][] pickingHLWeights, double[][] pickingInputWeights, double[][] pickingOutputWeights) {

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

	//TODO
	private void learnBiddingMove(double[] inputs, double correctOutput) {
		setInputs(inputs);
		double output = biddingOutputNode.output();
		double error = output*(1-output)*(correctOutput-output);
		biddingOutputNode.setError(error);

		backpropagate(biddingLayers);
	}
	
	//TODO
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

	private void setInputs(double[] inputs) {
		ArrayList<Node> inputNodes = biddingLayers.get(0);
		for(int i = 0; i < inputs.length; i++) {
			((InputNode)inputNodes.get(i)).setValue(inputs[i]);
		}
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
}
