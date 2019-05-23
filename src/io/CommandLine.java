package io;

import java.util.ArrayList;
import java.util.Scanner;

public class CommandLine extends BasicIO{

	@Override
	public String[] getPlayers() {
		String[] players;;//the names of all of the current players
		Scanner input = new Scanner(System.in);//the input method for the names
		System.out.print("How many players? (3 to 5 are allowed) : ");
		int playerCount = input.nextInt();
		input.nextLine();
		players = new String[playerCount];
		for(int i = 0; i < playerCount; i++) {
			System.out.print("Enter a player's name: ");
			players[i] = input.nextLine();//get a players name
			//System.out.println(playerName);//debug
		}
		input.close();
		
		return players;
	}

	@Override
	public void startSeason(int s) {
		System.out.println("Season " + s + " is starting...");
	}
}
