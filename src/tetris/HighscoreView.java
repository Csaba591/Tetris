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
 * A játékosok ranglistáját jeleníti meg.
 * A menü alsó részén jelenik meg.
 */
public class HighscoreView extends StackPane {
	private TableView<Score> table;
	private TableColumn<Score, String> nameCol;
	private TableColumn<Score, Integer> pointsCol;
	private ObservableList<Score> data;
	private String fileName = "scores.txt";
	
	/**
	 * Létrehozza a táblázatot, feltölti az eddigi eredményekkel és megjeleníti azt.
	 * @param scoresFileName A pontokat tároló txt fájl neve.
	 */
	public HighscoreView(String scoresFileName) {
		table = new TableView<Score>();
		// ha még nincs elmentett pont, ezt írja a táblára
		table.setPlaceholder(new Label("Még nincsenek eredmények."));
		table.getPlaceholder().setStyle("-fx-font-size: 22px");
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		// név oszlop inicializálása
		nameCol = new TableColumn<Score, String>("Név");
		nameCol.setStyle("-fx-font-size: 20px; -fx-alignment: center;");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("playerName"));
		
		// pontszám oszlop inicializálása
		pointsCol = new TableColumn<Score, Integer>("Pontszám");
		pointsCol.setStyle("-fx-font-size: 20px; -fx-alignment: center;");
		pointsCol.setSortType(TableColumn.SortType.DESCENDING);
		// hogy az oszlopok kitöltsék a táblát
		pointsCol.setCellValueFactory(new PropertyValueFactory<>("points"));
		
		table.getColumns().add(nameCol);
		table.getColumns().add(pointsCol);
		readData();
		table.getSortOrder().add(pointsCol);
		this.getChildren().add(table);
	}
	
	/**
	 * Újra beolvassa a játékosok pontjait, így ha új eredmény került be, azt is megjeleníti.
	 */
	public void refresh() {
		readData();
	}
	
	/**
	 * Beolvassa egy txt fileból a játékosok által eddig elért eredményeket, hogy a táblázatban megjelenítse.
	 */
	private void readData() {
		data = FXCollections.observableArrayList();
		try {
			// a Scanner közvetlenül be tud olvasni primitív típusokat és Stringeket
			Scanner sc = new Scanner(new File(fileName));
			while(sc.hasNext())
				data.add(new Score(sc.next(), sc.nextInt()));
			sc.close();
			table.getItems().clear();
			table.setItems(data);
			table.getSortOrder().add(pointsCol);
			table.sort();
		} catch (FileNotFoundException e) {
		// azt jelenti, hogy még nincs mentett pont, ami nem baj, a táblázat ki fogja írni
		}
	}
}
