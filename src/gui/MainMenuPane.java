package gui;

import java.awt.Toolkit;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class MainMenuPane extends VBox{
	
	private GUICore parent;

	public MainMenuPane(GUICore parent, double width, double height) {
		super();
		this.parent = parent;

		double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();

		//make the scene
		this.setStyle("-fx-background-color: #726952;");
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
		this.getChildren().add(title);
		this.getChildren().add(nameListPane);
		this.getChildren().add(goButton);
		this.setSpacing(screenHeight/50);//dynamic to screen size
		this.setFillWidth(true);
		this.setAlignment(Pos.CENTER);

		//set the pane as the scene
		this.setPrefSize(width, height);//set sizes
	}
}
