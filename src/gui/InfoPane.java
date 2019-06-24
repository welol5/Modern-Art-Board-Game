package gui;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import core.Artist;
import core.ArtistCount;
import core.ObservableGameState;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class InfoPane extends VBox implements Observer {
	
	private ObservableGameState OGS;
	
	private Text LiteMetalValue = new Text("0");
	private Text YokoValue = new Text("0");
	private Text ChristinPValue = new Text("0");
	private Text KarlGitterValue = new Text("0");
	private Text KryptoValue = new Text("0");
	
	//make the InfoPane
	public InfoPane(ObservableGameState OGS) {
		super();
		
		this.OGS = OGS;
		OGS.addObserver(this);
		
		Text titleText = new Text("Info Panel");
		this.getChildren().add(titleText);
		
		VBox artistValueArea = new VBox();
		Text artistValueText = new Text("Artist Values");
		artistValueArea.getChildren().add(artistValueText);
		HBox valuesArea = new HBox();
		valuesArea.getChildren().addAll(LiteMetalValue,YokoValue,ChristinPValue,KarlGitterValue,KryptoValue);
		artistValueArea.getChildren().add(valuesArea);
		this.getChildren().add(artistValueArea);
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO finish this
		
		updateArtistValues();
	}

	public void updateArtistValues() {
		LiteMetalValue.setText("" + OGS.getArtistValue(Artist.LITE_METAL));
		YokoValue.setText("" + OGS.getArtistValue(Artist.YOKO));
		ChristinPValue.setText("" + OGS.getArtistValue(Artist.CHRISTIN_P));
		KarlGitterValue.setText("" + OGS.getArtistValue(Artist.KARL_GITTER));
		KryptoValue.setText("" + OGS.getArtistValue(Artist.KRYPTO));
	}
}
