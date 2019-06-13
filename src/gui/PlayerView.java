package gui;

import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import player.Player;

public class PlayerView extends GridPane {
	
	private Player player;
	
	//things that will need to be updated
	private CardPanel cardPane;
	private Text moneyText;
	
	public PlayerView(Player player) {
		this.player = player;
		
		//add the card panel
		cardPane = new CardPanel(player.getHand());
		this.add(cardPane, 0, 1);
		
		//add the money panel
		moneyText = new Text("Money: $" + player.getMoney() + ",000");
		this.add(moneyText, 1, 1);
	}
	
	public CardPanel getCardPanel() {
		return cardPane;
	}
}
