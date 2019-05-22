package core;

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
		
		//the game is now ready for the first season
	}
	
}
