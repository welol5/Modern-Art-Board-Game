package core;

import java.io.IOException;

import io.BasicIO;
import io.CommandLine;

public class GameDriver implements Runnable{
	
	//IO types
	public static final int COMMAND_LINE = 0;
	
	//IO var
	private BasicIO io;
	
	/**
	 * Setup the program by giving it a IO type.
	 * IO types are public constant ints in the GameDriver class.
	 * @param gameType
	 */
	public GameDriver(int gameType) throws IOException{
		if(gameType == COMMAND_LINE) {
			io = new CommandLine();
		} else {
			throw new IOException(gameType + " is not a valid gameType");
		}
	}

	@Override
	public void run() {
		//need to know how many players
		
	}
	
}
