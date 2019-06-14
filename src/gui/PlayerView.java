package gui;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import core.Artist;
import core.AuctionType;
import core.Card;
import io.BasicIO;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import player.Player;

public class PlayerView extends GridPane implements BasicIO, Observer{

	private Player player = null;

	//things that will need to be updated
	private CardPanel cardPane;
	private Text moneyText;
	
	public PlayerView(Player player) {
		this.setStyle("-fx-background-color: #726952;");
		
		this.player = player;
		player.addObserver(this);

		//add the card panel
		cardPane = new CardPanel(player.getHand());
		this.add(cardPane, 0, 1);
		cardPane.update();
		
		//make the infoPane

		//add the money panel
		moneyText = new Text("Money: $" + player.getMoney() + ",000");
		this.add(moneyText, 1, 1);
	}

	public CardPanel getCardPanel() {
		return cardPane;
	}

	////////////////////////////////////
	//below here is IO stuff

	@Override
	public String[] getPlayers() {return null;}//useless here

	@Override
	public void startSeason(int s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showHand(Player player, ArrayList<Card> hand) {}//useless here

	@Override
	public void showHand(Player player, ArrayList<Card> hand, Artist artist) {}//useless here for now

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
		// close and end everything
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

	//This is where the panel will update everything
	@Override
	public void update(Observable o, Object arg) {
		cardPane.update();
		moneyText.setText("Money: $" + player.getMoney() + ",000");
	}
}
