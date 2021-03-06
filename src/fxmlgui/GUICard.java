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

/**
 * This class is used as a controller for the Cards seen in the GUI. It does not care about anything else, 
 * but it has methods so that other classes that know about this card can see if any actions have been performed on it.
 * @author William Elliman
 *
 */
public class GUICard{

	/**
	 * Tells if the player has clicked don the card
	 */
	private boolean selected = false;
	
	/**
	 * The card this GUICard is based on.
	 */
	private Card card;
	
	@FXML Text artistText1;
	@FXML Text artistText2;
	
	@FXML Text centerText;
	@FXML VBox wholeCard;

	/**
	 * Sets the GUI part of this card to match the card give. It will set the top and bottom texts to the artist and the center
	 * to the auction type.
	 * @param c the card that the GUI will try to match.
	 * @param isDouble if this card is the one being auctioned, this will determine if an extra "x2" will be added to the
	 * center text.
	 */
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

	/**
	 * @return true if the card has been clicked on.
	 */
	public boolean getIsSelected() {
		return selected;
	}

	/**
	 * Resents the selected var to false so that it can be clicked on again.
	 */
	public synchronized void resetIsSelected() {
		selected = false;
	}

	/**
	 * @return the artist of the card being shown
	 */
	public Artist getArtist() {
		return card.getArtist();
	}
	
	/**
	 * Called by the GUI to let whatever is watching this card know it is selected.
	 */
	private void setIsSelected() {
		//System.out.println("Clicked");
		selected = true;
	}
}
