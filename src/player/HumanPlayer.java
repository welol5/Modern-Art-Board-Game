package player;
import java.util.Scanner;

import core.Card;
import core.Player;
import io.BasicIO;

public class HumanPlayer extends Player{
	
	BasicIO io;

	public HumanPlayer(String name, BasicIO io) {
		super(name);
		this.io = io;
	}

	@Override
	public Card chooseCard() {
		
		System.out.println("Your hand contains");
		for(int i = 0; i < hand.size(); i++) {
			System.out.println(i + " : " + hand.get(i));
		}
		
		System.out.println("Choose and index of the painting you want to bid on : ");
		Scanner input = new Scanner(System.in);
		int index = input.nextInt();
		input.close();
		return hand.remove(index);
	}

	@Override
	public int getBid(Card card) {
		// TODO Auto-generated method stub
		return 0;
	}

}
