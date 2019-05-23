package io;

import java.util.ArrayList;
import java.util.Scanner;

import core.Card;

public class CommandLine extends BasicIO{
	
	Scanner input = new Scanner(System.in);//the main input method for this IO type

	@Override
	public String[] getPlayers() {
		String[] players;;//the names of all of the current players
		System.out.print("How many players? (3 to 5 are allowed) : ");
		int playerCount = input.nextInt();
		input.nextLine();
		players = new String[playerCount];
		for(int i = 0; i < playerCount; i++) {
			System.out.print("Enter a player's name: ");
			players[i] = input.nextLine();//get a players name
		}
		
		return players;
	}

	@Override
	public void startSeason(int s) {
		System.out.println("Season " + s + " is starting...");
	}

	@Override
	public void showHand(ArrayList<Card> hand) {
		System.out.println("Your hand contains");
		for(int i = 0; i < hand.size(); i++) {
			System.out.println(i + " : " + hand.get(i));
		}
	}

	@Override
	public int getHandIndex() {
		System.out.println("Choose and index of the painting you want to bid on : ");
		int index = input.nextInt();
		return index;
	}

	@Override
	public void end() {
		input.close();
	}
}
