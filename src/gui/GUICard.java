package gui;

import core.Artist;
import core.Card;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GUICard extends VBox{
	
	public GUICard(Parent parent, Card card) {
		super(parent);
		Text artistName = new Text();
		
		//find the text and color to use
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
		//give the panel some text
		artistName.setText(artist);
		this.getChildren().add(artistName);
		
		//set the color
		this.setStyle("-fx-background-color: " + color.toString() + ";");
	}
}
