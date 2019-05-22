package core;

import java.util.HashMap;

import io.BasicIO;
import io.CommandLine;
import io.IOType;

public class GameDriver implements Runnable{
	
	//IO var
	private BasicIO io;
	
	//GameState var
	GameState state;
	
	/**
	 * Setup the program by giving it a IO type.
	 * IO types are public constant ints in the GameDriver class.
	 * @param gameType
	 */
	public GameDriver(IOType type){
		if(type == IOType.COMMAND_LINE) {
			io = new CommandLine();
		}
	}

	@Override
	public void run() {
		//need to know how many players
		String[] players = io.getPlayers();
		//setup the game state
		state = new GameState(players);
		
		////////////////////////////////////////////////////////////////////
		//everything past this point is incomplete
		
		//the game is now ready for the first season
//		System.out.println("Season 1 Starting");
//		boolean seasonEnd = false;
//		int turn = 0;//keeps track of the turn
//		int biddingTurn;
//		Player currentPlayer;
//		Card biddingCard;
//		while(!seasonEnd) {
//			currentPlayer = state.getPlayer(turn);
//			biddingCard = currentPlayer.chooseCard();
//			
//			//now the bidding begins
//			biddingTurn = (turn+1)%players.length;
//			HashMap<Player, Integer> bids = new HashMap<Player, Integer>();
//			//All of the players make a bid of 0
//			for(int i = 0; i < players.length; i++) {
//				bids.put(state.getPlayer((biddingTurn+i)%players.length), 0);
//			}
//			while(bids.size() > 1) {
//				for(int i = 0; i < bids.size(); i++) {
//					
//				}
//			}
//		}
	}
	
}
