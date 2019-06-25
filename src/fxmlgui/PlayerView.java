package fxmlgui;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import core.AuctionType;
import core.Card;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import player.Player;

public class PlayerView extends BorderPane implements Observer{
	
	private final Player player;
	
	private final HBox handBox;
	private final HBox biddingPanel;
	private final Text moneyValue;
	
	private final Text LMValue;
	private final Text YValue;
	private final Text CPValue;
	private final Text KGValue;
	private final Text KValue;
	
	private final Text LMCount;
	private final Text YCount;
	private final Text CPCount;
	private final Text KGCount;
	private final Text KCount;

	public PlayerView(Player player) throws IOException{
		this.player = player;
		player.addObserver(this);
		
		//grab all of the useful panels
		HBox mainPanel = FXMLLoader.load(getClass().getResource("PlayerView.fxml"));
		handBox = ((HBox)((VBox)mainPanel.getChildren().get(0)).getChildren().get(1));
		biddingPanel = ((HBox)((VBox)mainPanel.getChildren().get(0)).getChildren().get(0));
		moneyValue = (Text) ((HBox)((VBox)mainPanel.getChildren().get(1)).getChildren().get(4)).getChildren().get(1);
		
		LMValue =  (Text) ((HBox)((VBox)((VBox)mainPanel.getChildren().get(1)).getChildren().get(1)).getChildren().get(1)).getChildren().get(1);
		YValue = (Text) ((HBox)((VBox)((VBox)mainPanel.getChildren().get(1)).getChildren().get(1)).getChildren().get(2)).getChildren().get(1);
		CPValue = (Text) ((HBox)((VBox)((VBox)mainPanel.getChildren().get(1)).getChildren().get(1)).getChildren().get(3)).getChildren().get(1);
		KGValue = (Text) ((HBox)((VBox)((VBox)mainPanel.getChildren().get(1)).getChildren().get(1)).getChildren().get(4)).getChildren().get(1);
		KValue = (Text) ((HBox)((VBox)((VBox)mainPanel.getChildren().get(1)).getChildren().get(1)).getChildren().get(5)).getChildren().get(1);
		
		LMCount =  (Text) ((HBox)((VBox)((VBox)mainPanel.getChildren().get(1)).getChildren().get(2)).getChildren().get(1)).getChildren().get(1);
		YCount = (Text) ((HBox)((VBox)((VBox)mainPanel.getChildren().get(1)).getChildren().get(2)).getChildren().get(2)).getChildren().get(1);
		CPCount = (Text) ((HBox)((VBox)((VBox)mainPanel.getChildren().get(1)).getChildren().get(2)).getChildren().get(3)).getChildren().get(1);
		KGCount = (Text) ((HBox)((VBox)((VBox)mainPanel.getChildren().get(1)).getChildren().get(2)).getChildren().get(4)).getChildren().get(1);
		KCount = (Text) ((HBox)((VBox)((VBox)mainPanel.getChildren().get(1)).getChildren().get(2)).getChildren().get(5)).getChildren().get(1);
		
		this.setCenter(mainPanel);
	}

	@Override
	public void update(Observable o, Object arg) {
		//System.out.println("Here");
		
		//update money value
		moneyValue.setText(player.getMoney() + ",000");
		
		//update hand
		handBox.getChildren().clear();
		for(Card c : player.getHand()) {
			boolean isDouble = false;
			if(c.getAuctionType() == AuctionType.DOUBLE) {
				isDouble = true;
			}
			try {
				GUICard gc = new GUICard(c, isDouble);
				//need infoPanel size
				VBox infoPanel = (VBox) ((HBox)this.getCenter()).getChildren().get(1);
				double totalHandWidth = this.getWidth()-infoPanel.getWidth();
				double fractionOfArea = totalHandWidth/this.getWidth();
				gc.resize(fractionOfArea/player.getHand().size());
				handBox.getChildren().add(gc);
			} catch (IOException e) {
				//this should never happen
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
}
