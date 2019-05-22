package io;

import java.util.ArrayList;
import java.util.Scanner;

public class CommandLine extends BasicIO{

	@Override
	public String[] getPlayers() {
		String playerName = null;//temporary storage of the current name of a player
		ArrayList<String> players = new ArrayList<String>();//the names of all of the current players
		Scanner input = new Scanner(System.in);//the input method for the names
		System.out.println("Enter the names of the players. When you are done, leave the line blank.");
		while(true) {
			System.out.print("Enter a players name: ");
			playerName = input.nextLine();//get a players name
			if(playerName != "") {//if the line is not blank
				players.add(playerName);//add that player to the list
			} else {
				break;
			}
		}
		input.close();
		
		//return in the String[] format
		String[] p = new String[players.size()];
		return players.toArray(p);
	}

}
