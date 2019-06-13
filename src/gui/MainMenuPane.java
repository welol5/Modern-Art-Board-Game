package gui;

import java.awt.Toolkit;
import java.util.ArrayList;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.stage.Stage;
import player.PlayerType;

public class MainMenuPane extends VBox{

	private GUICore parent;
	private TextField[] nameBoxes;
	private ComboBox<String>[] playerTypes;
	private String[] playerTypesNames = {
			"None",
			"Human",
			"Random",
			"Reactive AI",
			"Memory AI",
			"Basic Predictive AI",
			"Basic Predictive AI V2",
			"Genetic (Do not Use)"};

	public MainMenuPane(GUICore parent, double width, double height) {
		super();
		this.parent = parent;

		double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();

		//make the scene
		this.setStyle("-fx-background-color: #726952;");
		VBox nameListPane = new VBox();
		nameListPane.setSpacing(screenHeight/200);
		nameBoxes = new TextField[5];
		playerTypes = new ComboBox[5];
		for(int i = 0; i < 5; i++) {
			HBox line = new HBox();
			Text playerNameText = new Text("Player name : ");
			playerNameText.setFont(Font.font(null,FontWeight.BOLD,(int)(((double)screenHeight)/80.0)));
			playerNameText.setFill(Color.WHITE);
			line.getChildren().add(playerNameText);
			nameBoxes[i] = new TextField();
			line.getChildren().add(nameBoxes[i]);
			line.setAlignment(Pos.CENTER);

			//add a way to select playerType
			playerTypes[i] = new ComboBox<String>();
			playerTypes[i].getItems().addAll(playerTypesNames);
			playerTypes[i].getSelectionModel().selectFirst();
			line.getChildren().add(playerTypes[i]);

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
		goButton.setOnAction(e -> {
			parent.startGame();
		});

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

	public String[] getNames() {
		ArrayList<String> names = new ArrayList<String>();

		for(int i = 0; i < nameBoxes.length; i++) {
			String name = nameBoxes[i].getText();
			if(name.length() > 0) {
				names.add(name);
				if(playerTypes[i].getSelectionModel().getSelectedIndex() == 0) {
					playerTypes[i].getSelectionModel().select(2);//select random players if the person forgot to select
				}
			} else {
				playerTypes[i].getSelectionModel().selectFirst();
			}
		}

		String[] arrayNames = new String[names.size()];
		arrayNames = names.toArray(arrayNames);
		
		if(arrayNames.length < 3) {
			Stage dialog = new Stage();
			VBox contain = new VBox();
			Text errorText = new Text("At least 3 players are required.");
			contain.getChildren().add(errorText);
			Button okButton = new Button("Ok");
			okButton.setOnAction(e -> {
				dialog.close();
			});
			contain.getChildren().add(okButton);
			contain.setAlignment(Pos.CENTER);
			dialog.setScene(new Scene(contain));
			dialog.show();
			return null;
		}

		return arrayNames;
	}

	public PlayerType[] getPlayerTypes(){
		PlayerType[] types = new PlayerType[getNames().length];

		int i = 0;
		for(ComboBox<String> c : playerTypes) {
			for(int k = 0; k < playerTypesNames.length; k++) {
				if(c.getSelectionModel().getSelectedItem().equals(playerTypesNames[k]) && !c.getSelectionModel().getSelectedItem().equals(playerTypesNames[0])) {
					types[i] = PlayerType.values()[k+1];
					i++;
				}
			}
		}

		return types;
	}
}
