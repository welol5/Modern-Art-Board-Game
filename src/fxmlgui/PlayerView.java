package fxmlgui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import core.Artist;
import core.AuctionType;
import core.Card;
import io.BasicIO;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import player.Player;

public class PlayerView extends BorderPane implements Observer, BasicIO{
	
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
	
	//vars used for io
	private volatile int selection = -1;

	public PlayerView(Player player) throws IOException{
		this.player = player;
		player.addObserver(this);
		player.setGUI(this);
		
		//grab all of the useful panels
		HBox mainPanel = FXMLLoader.load(getClass().getResource("PlayerView.fxml"));
		handBox = ((HBox)((VBox)mainPanel.getChildren().get(0)).getChildren().get(1));
		handBox.setDisable(true);
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

	@Override
	public String[] getPlayers() {
		//Useless here
		return null;
	}

	@Override
	public void startSeason(int s) {
		//Useless here
	}

	@Override
	public void showHand(Player player, ArrayList<Card> hand) {
		//Useless here
		
	}

	@Override
	public void showHand(Player player, ArrayList<Card> hand, Artist artist) {
		//useless here
		
	}

	@Override
	public int getHandIndex(int maxVal) {
		
		if(handBox.getChildren().size() == 0) {
			return -1;
		}
		
		//activate the hand
		handBox.setDisable(false);
		//now wait for one of the cards to be selected
		while(selection == -1) {
			//check if each card is selected
			for(int i = 0; i < handBox.getChildren().size(); i++) {
				if(((GUICard)handBox.getChildren().get(i)).getIsSelected()) {
					selection = i;
					break;
				}
			}
		}
		
		//reset selection
		int temp = selection;
		selection = -1;
		// TODO Auto-generated method stub
		return temp;
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
