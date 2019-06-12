package gui;

import java.util.ArrayList;

import core.Artist;
import core.AuctionType;
import core.Card;
import io.BasicIO;
import javafx.application.Application;
import javafx.stage.Stage;
import player.Player;

public class GUICore extends Application implements BasicIO{

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startSeason(int s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showHand(Player player, ArrayList<Card> hand) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showHand(Player player, ArrayList<Card> hand, Artist artist) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getHandIndex(int maxVal) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHandIndex(ArrayList<Card> hand, Artist artist) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getBid(String player, int money, int highestSoFar) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFixedPrice(Card card) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean askPlayertoBuy(Card card, int price) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void auctionWinner(String name, int price) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void announceAuctionType(AuctionType type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void announceCard(Card card) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void announceWinner(Player player) {
		// TODO Auto-generated method stub
		
	}

}
