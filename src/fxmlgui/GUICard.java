package fxmlgui;

import java.io.IOException;

import core.Artist;
import core.Card;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class GUICard extends BorderPane{
	
	public GUICard(Card card, boolean isDouble) throws IOException{
		VBox gui = FXMLLoader.load(getClass().getResource("Card.fxml"));
		
		//set the string for the artist
		String artist = null;
		Color color = Color.GRAY;
		if(card.getArtist() == Artist.LITE_METAL) {
			artist = "Lite Metal";
			color = Color.web("#c6c35b");
		} else if(card.getArtist() == Artist.YOKO) {
			artist = "Yoko";
			color = Color.web("#6da861");
		} else if(card.getArtist() == Artist.CHRISTIN_P) {
			artist = "Christin P.";
			color = Color.web("#b26f5c");
		} else if(card.getArtist() == Artist.KARL_GITTER) {
			artist = "Karl Gitter";
			color = Color.web("#6196aa");
		} else if(card.getArtist() == Artist.KRYPTO) {
			artist = "Krypto";
			color = Color.web("#917145");
		}
		
		//set the string
		((Text)gui.getChildren().get(0)).setText(artist);
		((Text)gui.getChildren().get(2)).setText(artist);
		
		//set the color
		gui.setStyle("-fx-background-color: #" + color.toString().substring(2) + ";" + "-fx-background-radius: 20.0;");
		
		//let the player know if its a double auction by setting the opacity of the x2
		if(isDouble) {
			((Text)gui.getChildren().get(1)).setOpacity(1);
		} else {
			((Text)gui.getChildren().get(1)).setOpacity(0);
		}
		
		this.setCenter(gui);
	}
}
