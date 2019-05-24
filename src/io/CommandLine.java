package io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import core.Artist;
import core.AuctionType;
import core.Card;
import player.Player;

public class CommandLine extends BasicIO{

	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));//the main input method for this IO type

	@Override
	public String[] getPlayers() {
		String[] players = null;//the names of all of the current players
		System.out.println("How many players? (3 to 5 are allowed).");
		try {
			int playerCount = -1;
			while(playerCount < 3 || playerCount > 5) {
				System.out.print("Please enter a valid number: ");
				playerCount = Integer.parseInt(input.readLine());
			}
			players = new String[playerCount];
			for(int i = 0; i < playerCount; i++) {
				System.out.print("Enter a player's name: ");
				players[i] = input.readLine();//get a players name
			}
		} catch (IOException e) {
			System.out.println("IO exception occured. The program will now shut down.");
			e.printStackTrace();
			System.exit(1);
		}

		return players;
	}

	@Override
	public void startSeason(int s) {
		System.out.println("Season " + s + " is starting...");
	}

	@Override
	public void showHand(Player player, ArrayList<Card> hand) {
		System.out.println(player.name + ", your hand contains");
		for(int i = 0; i < hand.size(); i++) {
			System.out.println(i + " : " + hand.get(i));
		}
	}

	@Override
	public int getHandIndex(int maxVal) {
		System.out.print("Choose and index of the painting you want to bid on : ");
		int index = -1;
		try {
			while(index < 0 || index > maxVal) {
				try {
					index = Integer.parseInt(input.readLine());
				} catch (NumberFormatException ex) {
					System.out.print("Please enter a valid number: ");
				}
			}
		} catch (IOException e) {
			System.out.println("IO exception occured. The program will now shut down.");
			e.printStackTrace();
			System.exit(1);
		}
		return index;
	}

	@Override
	public void end() {
		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getBid(Player player) {
		System.out.print(player.name + ", how much would you like to bid? (-1 to abstain) : ");
		int bid = -1;
		try {
			while(true) {
				try {
					bid = Integer.parseInt(input.readLine());
					break;
				} catch (NumberFormatException e) {
					System.out.print("Please enter a valid number: ");
				}
			}
		} catch (IOException e) {
			System.out.println("IO exception occured. The program will now shut down.");
			e.printStackTrace();
			System.exit(1);
		}
		return bid;
	}

	@Override
	public int getFixedPrice(Card card) {
		System.out.print("How much would you like to sell this card for? : ");
		int price = -1;
		try {
			while(price < 0) {
				try {
					price = Integer.parseInt(input.readLine());
					break;
				} catch (NumberFormatException e) {
					System.out.print("Please enter a valid positive number: ");
				}
			}
		} catch (IOException e) {
			System.out.println("IO exception occured. The program will now shut down.");
			e.printStackTrace();
			System.exit(1);
		}
		return price;
	}

	@Override
	public boolean askPlayertoBuy(Card card, int price) {
		System.out.println("Would you like to buy the painting? (y/n)" + card.getArtist());
		try {
			String answer = input.readLine();
			if(answer.matches("[yY]")) {
				return true;
			}
		} catch(IOException e) {
			System.out.println("IO exception occured. The program will now shut down.");
			e.printStackTrace();
			System.exit(1);
		}
		return false;
	}

	@Override
	public void showHand(Player player, ArrayList<Card> hand, Artist artist) {
		System.out.println(player.name + ", your hand contains the following paintings allowed in the double auction");
		for(int i = 0; i < hand.size(); i++) {
			if(hand.get(i).getArtist() == artist) {
				System.out.println(i + " : " + hand.get(i));
			}
		}
	}

	@Override
	public int getHandIndex(ArrayList<Card> hand, Artist artist) {
		System.out.print("Choose and index of the painting you want to bid on (-1 for nothing): ");
		int index = -1;
		try {
			do{
				try {
					index = Integer.parseInt(input.readLine());
					if(index == -1) {
						return index;
					}
				} catch (NumberFormatException ex) {
					System.out.print("Please enter a valid number: ");
				}
			} while((index < 0 || index > hand.size()) || hand.get(index).getArtist() != artist);
		} catch (IOException e) {
			System.out.println("IO exception occured. The program will now shut down.");
			e.printStackTrace();
			System.exit(1);
		}
		return index;
	}

	@Override
	public void auctionWinner(Player player) {
		System.out.println();
	}

	@Override
	public void announceAuctionType(AuctionType type) {
		System.out.print("Auction Type : ");
		if(type == AuctionType.ONCE_AROUND) {
			System.out.println("Once Around");
		} else if(type == AuctionType.FIXED_PRICE) {
			System.out.println("Fixed Price");
		} else if(type == AuctionType.SEALED) {
			System.out.println("Sealed");
		} else {
			System.out.println("Standard");
		}
	}
}
