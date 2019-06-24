package fxmlgui;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import player.Player;

public class PlayerView extends BorderPane implements Observer{
	
	private final Player player;
	
	private final HBox handBox;
	private final HBox biddingPanel;

	public PlayerView(Player player) throws IOException{
		this.player = player;
		player.addObserver(this);
		
		HBox mainPanel = FXMLLoader.load(getClass().getResource("PlayerView.fxml"));
		handBox = ((HBox)((VBox)mainPanel.getChildren().get(0)).getChildren().get(1));
		biddingPanel = ((HBox)((VBox)mainPanel.getChildren().get(0)).getChildren().get(0));
		
	}

	@Override
	public void update(Observable o, Object arg) {
		
	}
}
