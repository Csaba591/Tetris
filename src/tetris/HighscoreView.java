package tetris;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

/**
 * A j�t�kosok ranglist�j�t jelen�ti meg.
 * A men� als� r�sz�n jelenik meg.
 */
public class HighscoreView extends StackPane {
	private TableView<Score> table;
	private TableColumn<Score, String> nameCol;
	private TableColumn<Score, Integer> pointsCol;
	private ObservableList<Score> data;
	private String fileName = "scores.txt";
	
	/**
	 * L�trehozza a t�bl�zatot, felt�lti az eddigi eredm�nyekkel �s megjelen�ti azt.
	 * @param scoresFileName A pontokat t�rol� txt f�jl neve.
	 */
	public HighscoreView(String scoresFileName) {
		table = new TableView<Score>();
		// ha m�g nincs elmentett pont, ezt �rja a t�bl�ra
		table.setPlaceholder(new Label("M�g nincsenek eredm�nyek."));
		table.getPlaceholder().setStyle("-fx-font-size: 22px");
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		// n�v oszlop inicializ�l�sa
		nameCol = new TableColumn<Score, String>("N�v");
		nameCol.setStyle("-fx-font-size: 20px; -fx-alignment: center;");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("playerName"));
		
		// pontsz�m oszlop inicializ�l�sa
		pointsCol = new TableColumn<Score, Integer>("Pontsz�m");
		pointsCol.setStyle("-fx-font-size: 20px; -fx-alignment: center;");
		pointsCol.setSortType(TableColumn.SortType.DESCENDING);
		// hogy az oszlopok kit�lts�k a t�bl�t
		pointsCol.setCellValueFactory(new PropertyValueFactory<>("points"));
		
		table.getColumns().add(nameCol);
		table.getColumns().add(pointsCol);
		readData();
		table.getSortOrder().add(pointsCol);
		this.getChildren().add(table);
	}
	
	/**
	 * �jra beolvassa a j�t�kosok pontjait, �gy ha �j eredm�ny ker�lt be, azt is megjelen�ti.
	 */
	public void refresh() {
		readData();
	}
	
	/**
	 * Beolvassa egy txt fileb�l a j�t�kosok �ltal eddig el�rt eredm�nyeket, hogy a t�bl�zatban megjelen�tse.
	 */
	private void readData() {
		data = FXCollections.observableArrayList();
		try {
			// a Scanner k�zvetlen�l be tud olvasni primit�v t�pusokat �s Stringeket
			Scanner sc = new Scanner(new File(fileName));
			while(sc.hasNext())
				data.add(new Score(sc.next(), sc.nextInt()));
			sc.close();
			table.getItems().clear();
			table.setItems(data);
			table.getSortOrder().add(pointsCol);
			table.sort();
		} catch (FileNotFoundException e) {
		// azt jelenti, hogy m�g nincs mentett pont, ami nem baj, a t�bl�zat ki fogja �rni
		}
	}
}
