package io;

import java.util.ArrayList;
import java.util.Scanner;

public class CommandLine extends BasicIO{

	@Override
	public String[] getPlayers() {
		String playerName = null;//temporary storage of the current name of a player
		ArrayList<String> players = new ArrayList<String>();//the names of all of the current players
		Scanner input = new Scanner(System.in);//the input method for the names
		System.out.print("How many players? (3 to 5 are allowed) : ");
		int playerCount = input.nextInt();
		input.nextLine();
		for(int i = 0; i < playerCount; i++) {
			System.out.print("Enter a player's name: ");
			playerName = input.nextLine();//get a players name
			//System.out.println(playerName);//debug
		}
		input.close();
		
		//return in the String[] format
		String[] p = new String[players.size()];
		return players.toArray(p);
	}

}
