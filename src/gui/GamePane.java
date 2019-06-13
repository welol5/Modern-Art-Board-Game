package gui;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import player.Player;

public class GamePane extends GridPane{
	
	private Player player;

	public GamePane(Player player) {
		super();
		this.player = player;
	}
	
}
