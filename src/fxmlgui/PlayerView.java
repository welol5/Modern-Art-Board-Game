package fxmlgui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import core.Artist;
import core.AuctionType;
import core.Card;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import player.HumanPlayer;
import player.Player;

/**
 * This class is the main controller of what the user will see. It controls what is shown to the user as the game is being played.
 * It also provides some methods for a human player to play the game.
 * @author William Elliman
 *
 */
public class PlayerView implements Initializable{

	@FXML TextField bidBox;
	@FXML Button bidButton;
	@FXML HBox handBox;
	@FXML HBox OtherPlayersWinningsBox;

	@FXML Text LMPrice;
	@FXML Text YPrice;
	@FXML Text CPPrice;
	@FXML Text KGPrice;
	@FXML Text KPrice;

	@FXML Text LMCount;
	@FXML Text YCount;
	@FXML Text CPCount;
	@FXML Text KGCount;
	@FXML Text KCount;

	@FXML Text LMWin;
	@FXML Text YWin;
	@FXML Text CPWin;
	@FXML Text KGWin;
	@FXML Text KWin;

	@FXML VBox biddingCardBox;
	@FXML Text biddingCardArtist1;
	@FXML Text biddingCardArtist2;
	@FXML Text centerText;

	@FXML Text announcementText;
	@FXML Text moneyText;

	private Player player;
	private boolean isSet = false;

	private ArrayList<GUICard> handCards;

	private volatile boolean bidSet = false;
	private volatile boolean buySet = false;
	private volatile boolean fixedPriceBuy = false;

	/**
	 * Sets the player that this view is going to be watching.
	 * @param player
	 */
	public void setPlayer(Player player) {
		this.player = player;
		moneyText.textProperty().bind(player.getMoneyProperty());
		bindWinnings();
		isSet = true;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		handCards = new ArrayList<GUICard>();
	}

	/**
	 * Used by {@link HumanPlayer} to get input.
	 * @param artist that the {@link HumanPlayer} must pick. null if there is not a requirement.
	 * @return the index of the card int he players hand.
	 */
	public int getCard(Artist artist) {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if(artist == null) {
					announcementText.setText("Please choose a card to play");
				} else {
					announcementText.setText("Please choose a second card to play");
				}
			}
		});

		//the thread needs to wait for the GUI to be updated
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//reset all cards before something is selected
		for(GUICard gc : handCards) {
			//			System.out.println("What");
			gc.resetIsSelected();
		}

		if(artist == null) {

			if(handCards.size() == 0) {
				return -1;
			}

			//loop through the hand to see if a card is chosen
			for(int i = 0; true; i = (i+1)%handCards.size()) {
				GUICard gc = handCards.get(i);
				if(gc.getIsSelected()) {
					return i;
				}

			}
		} else {

			if(handCards.size() == 0) {
				return -1;
			}

			//loop through the hand to see if a card is chosen of the specific artist
			for(int i = 0; true; i = (i+1)%handBox.getChildren().size()) {
				GUICard gc = handCards.get(i);
				if(gc.getIsSelected() && gc.getArtist() == artist) {
					return i;
				}
			}
		}

	}

	/**
	 * A method for {@link HumanPlayer} to get input.
	 * @param highestBid the highest bid so far. This will be used to update the announcement text.
	 * @return the bid the {@link HumanPlayer} would like to make.
	 */
	public int getBid(int highestBid) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				//				System.out.println("Set announcement");
				if(highestBid > 0) {
					announcementText.setText("What would you like to bid? The highestBid so far is " + highestBid + ".");
				} else {
					announcementText.setText("What would you like to bid?");
				}
			}
		});
		bidSet = false;
		while(!bidSet) {}
		//		System.out.println("Bid recived");
		return Integer.parseInt(bidBox.getText());
	}

	/**
	 * Asks the player if they would like to buy a card for a specific price.
	 * @param price
	 * @return the players answer.
	 */
	public boolean askPlayerToBuy(int price) {
		fixedPriceBuy = true;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				announcementText.setText("Whould you like to buy the card(s) for $" + price);
				//System.out.println("set");
			}
		});

		//wait for the player to decide
		while(!buySet) {}
		fixedPriceBuy = false;

		if(bidBox.getText().trim().matches("[yY].*")) {
			return true;
		}
		try {
			if (Integer.parseInt(bidBox.getText()) >= price){
				return true;
			} else {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * @return the price the player is asking for a card.
	 */
	public int getFixedPrice() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				announcementText.setText("How much would you like to offer this painting for?");
			}
		});
		bidSet = false;
		while(!bidSet) {}
		return Integer.parseInt(bidBox.getText());
	}

	@FXML private void setBidSet(ActionEvent event) {

		if(fixedPriceBuy) {
			buySet = true;
			return;
		}

		//check if the bidBox has a number in it
		try {
			Integer.parseInt(bidBox.getText());
			bidSet = true;
		} catch (NumberFormatException e) {
			//do nothing if the bid is bad
		}
	}

	/**
	 * Binds value strings to their {@link Text} counterparts.
	 * @param LMPrice
	 * @param YPrice
	 * @param CPPrice
	 * @param KGPrice
	 * @param KPrice
	 */
	public void bindPrices(
			ReadOnlyStringProperty LMPrice,
			ReadOnlyStringProperty YPrice,
			ReadOnlyStringProperty CPPrice, 
			ReadOnlyStringProperty KGPrice,
			ReadOnlyStringProperty KPrice) {
		this.LMPrice.textProperty().bind(LMPrice);
		this.YPrice.textProperty().bind(YPrice);
		this.CPPrice.textProperty().bind(CPPrice);
		this.KGPrice.textProperty().bind(KGPrice);
		this.KPrice.textProperty().bind(KPrice);
	}

	/**
	 * Binds count strings to their {@link Text} counterparts.
	 * @param LMCount
	 * @param YCount
	 * @param CPCount
	 * @param KGCount
	 * @param KCount
	 */
	public void bindCounts(
			ReadOnlyStringProperty LMCount,
			ReadOnlyStringProperty YCount,
			ReadOnlyStringProperty CPCount, 
			ReadOnlyStringProperty KGCount,
			ReadOnlyStringProperty KCount) {
		this.LMCount.textProperty().bind(LMCount);
		this.YCount.textProperty().bind(YCount);
		this.CPCount.textProperty().bind(CPCount);
		this.KGCount.textProperty().bind(KGCount);
		this.KCount.textProperty().bind(KCount);
	}

	/**
	 * Binds winnings strings to their {@link Text} counterparts.
	 */
	public void bindWinnings() {
		LMWin.textProperty().bind(player.getWinningProperty(Artist.LITE_METAL));
		YWin.textProperty().bind(player.getWinningProperty(Artist.YOKO));
		CPWin.textProperty().bind(player.getWinningProperty(Artist.CHRISTIN_P));
		KGWin.textProperty().bind(player.getWinningProperty(Artist.KARL_GITTER));
		KWin.textProperty().bind(player.getWinningProperty(Artist.KRYPTO));
	}

	/**
	 * Updates GUI to what the player currently has in their hand.
	 */
	public synchronized void updateGUI() {

		//update hand
		handBox.getChildren().clear();
		handCards.clear();
		synchronized (player.getHand()) {
			for(Card c : player.getHand()) {
				try {
					FXMLLoader cardLoader = new FXMLLoader(getClass().getResource("Card.fxml"));
					VBox card = cardLoader.load();
					handCards.add(cardLoader.getController());
					GUICard controller = cardLoader.getController();
					controller.setCard(c, false);
					handBox.getChildren().add(card);

					//System.out.println(handCards.size());

				} catch (IOException e) {
					e.printStackTrace();
					//dont know what happens here
				}
			}
		}
	}

	///////////////////////////////////////////////////////////////////////////////
	//anouncements

	public void announceCard(Card card, boolean isDouble) {

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

		if(card.getAuctionType() == AuctionType.ONCE_AROUND) {
			centerText.setOpacity(1);
			centerText.setText("On");
		} else if(card.getAuctionType() == AuctionType.SEALED) {
			centerText.setOpacity(1);
			centerText.setText("Se");
		} else if(card.getAuctionType() == AuctionType.FIXED_PRICE) {
			centerText.setOpacity(1);
			centerText.setText("FP");
		} else {
			centerText.setText("  ");
			centerText.setOpacity(1);
		}

		if(isDouble) {
			centerText.setText(centerText.getText()+"x2");
		}

		//passing these into Platform.runLater() requires these to be final
		final String artistText = artist;
		final String colorText = color;

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				updateGUI();

				biddingCardArtist1.setText(artistText);
				biddingCardArtist2.setText(artistText);
				biddingCardArtist1.setOpacity(1);
				biddingCardArtist2.setOpacity(1);

				//				if(isDouble) {
				//					centerText.setOpacity(1);
				//				} else {
				//					centerText.setOpacity(0);
				//				}

				biddingCardBox.setStyle("-fx-background-color: " + colorText + "; -fx-background-radius: 25");
			}
		});
	}

	/**
	 * Tells the player who won the auction.
	 * @param name of the winner.
	 * @param price that they paid.
	 */
	public void announceAuctionWinner(String name, int price) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				updateGUI();
				biddingCardArtist1.setOpacity(0);
				biddingCardArtist2.setOpacity(0);
				centerText.setOpacity(0);

				biddingCardBox.setStyle("-fx-background-color: " + "#777777" + "; -fx-background-radius: 25");

				announcementText.setText(name + " has won the auction for " + price + ".");
			}
		});

		//give the player a moment to see who won
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return true if this view has a player to follow.
	 */
	public boolean isSet() {
		return isSet;
	}

	public void addOtherPlayers(Player[] players) {
		//		System.out.println("adding other players");
		for(Player p : players) {
			if(p == player) {
				continue;
			}

			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("InfoSection.fxml"));
				OtherPlayersWinningsBox.getChildren().add(loader.load());

				InfoSection section = loader.getController();
				section.setPlayer(p);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
