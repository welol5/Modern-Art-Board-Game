package fxmlgui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This is just here so that the program can start, and so that the name tells users where to start the program.
 * @author William Elliman
 *
 */
public class Main extends Application{

	@Override
	public void start(Stage primaryStage) throws Exception {
		VBox mainMenu = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
		
		Scene scene = new Scene(mainMenu);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Modern Art");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
