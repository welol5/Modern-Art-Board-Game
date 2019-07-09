package fxmlgui;

import java.net.URL;
import java.util.ResourceBundle;

import core.Artist;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import player.Player;

public class InfoSection implements Initializable{
	@FXML Text title;
	
	@FXML Text LMWin;
	@FXML Text YWin;
	@FXML Text CPWin;
	@FXML Text KGWin;
	@FXML Text KWin;
	
	private Player player;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	public void setPlayer(Player p) {
		player = p;
		bindWinnings();
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				title.setText(p.name + " winnings");
			}
		});
	}
	
	private void bindWinnings() {
		LMWin.textProperty().bind(player.getWinningProperty(Artist.LITE_METAL));
		YWin.textProperty().bind(player.getWinningProperty(Artist.YOKO));
		CPWin.textProperty().bind(player.getWinningProperty(Artist.CHRISTIN_P));
		KGWin.textProperty().bind(player.getWinningProperty(Artist.KARL_GITTER));
		KWin.textProperty().bind(player.getWinningProperty(Artist.KRYPTO));
	}
}
