package gui;

import java.awt.Toolkit;
import java.util.ArrayList;

import core.Artist;
import core.AuctionType;
import core.Card;
import io.BasicIO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import player.Player;

public class GUICore extends Application implements BasicIO{
	
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		//setup basic overall stuff
		primaryStage.setTitle("Modern Art");
		primaryStage.setHeight(Toolkit.getDefaultToolkit().getScreenSize().getHeight()*(3.0/4.0));
		primaryStage.setWidth(Toolkit.getDefaultToolkit().getScreenSize().getWidth()*(3.0/4.0));
		
		showStartup(primaryStage);
		primaryStage.show();
	}

	@Override
	public String[] getPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startSeason(int s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showHand(Player player, ArrayList<Card> hand) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showHand(Player player, ArrayList<Card> hand, Artist artist) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getHandIndex(int maxVal) {
		// TODO Auto-generated method stub
		return 0;
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
	
	/////////////////////////////////////////////////
	//below here are helper methods to show specific things
	
	private void showStartup(Stage stage) {
		double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		
		//make the scene
		VBox pane = new VBox();
		pane.setStyle("-fx-background-color: #726952;");
		VBox nameListPane = new VBox();
		nameListPane.setSpacing(screenHeight/200);
		for(int i = 0; i < 5; i++) {
			HBox line = new HBox();
			Text playerNameText = new Text("Player name : ");
			playerNameText.setFont(Font.font(null,FontWeight.BOLD,(int)(((double)screenHeight)/80.0)));
			playerNameText.setFill(Color.WHITE);
			line.getChildren().add(playerNameText);
			line.getChildren().add(new TextField());
			line.setAlignment(Pos.CENTER);
			
			nameListPane.getChildren().add(line);
		}
		
		//make an interesting looking title
		Text title = new Text("Modern Art");
		title.setTextAlignment(TextAlignment.CENTER);
		title.setFont(Font.font("Impact", FontWeight.BOLD, (int)(((double)screenHeight)/10.0)));
		DropShadow ds = new DropShadow();
		ds.setRadius(screenHeight/200);
		ds.setOffsetX(screenHeight/200);
		ds.setOffsetY(screenHeight/200);
		//ds.setColor(Color.DARKGRAY);
		title.setEffect(ds);
		title.setFill(Color.WHITE);
		title.setStroke(Color.web("#5b4c37"));
		title.setStrokeWidth(screenHeight/300.0);
		
		//need a go button
		Button goButton = new Button("Start Game");
		
		//add everything to the main box
		pane.getChildren().add(title);
		pane.getChildren().add(nameListPane);
		pane.getChildren().add(goButton);
		pane.setSpacing(screenHeight/50);//dynamic to screen size
		pane.setFillWidth(true);
		pane.setAlignment(Pos.CENTER);
		
		//set the pane as the scene
		pane.setPrefSize(stage.getWidth(), stage.getHeight());//set sizes
		Scene scene = new Scene(pane);
		stage.setScene(scene);
	}

}
