package fxmlgui;

import java.io.IOException;

import core.Artist;
import core.AuctionType;
import core.Card;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class GUICard{

	private boolean selected = false;
	private Card card;
	
	@FXML Text artistText1;
	@FXML Text artistText2;
	
	@FXML Text centerText;
	@FXML VBox wholeCard;

	public void setCard(Card c, boolean isDouble) {
		card = c;

		//set the string for the artist and the color
		String artist = null;
		String color = "#FFFFFF";
		if(card.getArtist() == Artist.LITE_METAL) {
			artist = "Lite Metal";
			color = "#c6c35b";
		} else if(card.getArtist() == Artist.YOKO) {
			artist = "Yoko";
			color = "#6da861";
		} else if(card.getArtist() == Artist.CHRISTIN_P) {
			artist = "Christin P.";
			color = "#b26f5c";
		} else if(card.getArtist() == Artist.KARL_GITTER) {
			artist = "Karl Gitter";
			color = "#6196aa";
		} else if(card.getArtist() == Artist.KRYPTO) {
			artist = "Krypto";
			color = "#917145";
		}

		artistText1.setText(artist);
		artistText2.setText(artist);
		
		if(c.getAuctionType() == AuctionType.DOUBLE || isDouble) {
			centerText.setOpacity(1);
			centerText.setText("x2");
		} else if(c.getAuctionType() == AuctionType.ONCE_AROUND) {
			centerText.setOpacity(1);
			centerText.setText("On");
		} else if(c.getAuctionType() == AuctionType.SEALED) {
			centerText.setOpacity(1);
			centerText.setText("Se");
		} else if(c.getAuctionType() == AuctionType.FIXED_PRICE) {
			centerText.setOpacity(1);
			centerText.setText("FP");
		} else if(c.getAuctionType() == AuctionType.STANDARD) {
			centerText.setOpacity(0);
		}
		
		wholeCard.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 25");
		wholeCard.setOnMouseClicked((e) -> setIsSelected());
	}

//	public void resize(double scale) {
//		((Text)((VBox)this.getCenter()).getChildren().get(0)).setFont(new Font(24*scale));
//		((Text)((VBox)this.getCenter()).getChildren().get(1)).setFont(new Font(112*scale));
//		((Text)((VBox)this.getCenter()).getChildren().get(2)).setFont(new Font(24*scale));
//	}

	public boolean getIsSelected() {
		return selected;
	}

	public synchronized void resetIsSelected() {
		selected = false;
	}

	public Artist getArtist() {
		return card.getArtist();
	}
	
	private void setIsSelected() {
		//System.out.println("Clicked");
		selected = true;
	}
}
