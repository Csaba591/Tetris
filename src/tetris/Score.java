package tetris;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * A j�t�kos pontj�t t�rolja �s kezeli.
 * Szerializ�lhat�, �gy amikor a j�t�kos elmenti a j�t�kot a pontsz�mot is menti.
 */
public class Score implements Serializable {
	private static final long serialVersionUID = -7280582741038120280L;
	private SimpleStringProperty playerName;
	private SimpleIntegerProperty points;
	
	/**
	 * L�trehoz egy pontsz�m p�ld�nyt pName j�t�kosnak 0 ponttal.
	 * @param pName A j�t�kos neve.
	 */
	public Score(String pName) {
		playerName = new SimpleStringProperty(pName);
		points = new SimpleIntegerProperty(0);
	}
	
	/**
	 * L�trehoz egy pontsz�m p�ld�nyt pName j�t�kosnak, points darab ponttal.
	 * @param pName A j�t�kos neve.
	 * @param points A pontsz�ma.
	 */
	public Score(String pName, int points) {
		playerName = new SimpleStringProperty(pName);
		this.points = new SimpleIntegerProperty(points);
	}
	
	/**
	 * Egyel n�veli a pontsz�mot.
	 * Egy betelt sor t�rl�s��rt j�r pont.
	 */
	public void incrementPoints() {
		points.set(points.get()+1);
	}
	
	/**
	 * points mennyis�g� pontot ad a j�t�kos pontsz�m�hoz.
	 * @param points A pontsz�mhoz adand� mennyis�g.
	 */
	public void addPoints(int points) {
		this.points.set(this.points.get()+points);
	}
	
	/**
	 * A j�t�kos neve. Observable String objektum.
	 * @return Observable String, ami a j�t�kos neve.
	 */
	public SimpleStringProperty playerNameProperty() {
		return playerName;
	}
	
	/**
	 * A j�t�kos pontsz�ma. Observable Integer objektum.
	 * @return Observable Integer, ami a j�t�kos pontsz�ma.
	 */
	public SimpleIntegerProperty pointsProperty() {
		return points;
	}
	
	/**
	 * A j�t�kos neve sima Stringk�nt.
	 * @return A j�t�kos neve.
	 */
	public String getPlayerName() {
		return playerName.get();
	}
	
	/**
	 * A j�t�kos pontsz�ma primit�v int-k�nt.
	 * @return A j�t�kos pontsz�ma.
	 */
	public int getPoints() {
		return points.get();
	}
	
	/**
	 * playerName-re �ll�tja a j�t�kos nev�t.
	 * @param playerName A j�t�kos �j neve.
	 */
	public void setPlayerName(String playerName) {
		this.playerName.set(playerName);
	}
	
	/**
	 * ponts-ra �ll�tja a j�t�kos pontsz�m�t.
	 * @param points A j�t�kos �j pontsz�ma.
	 */
	public void setPoints(Integer points) {
		this.points.set(points);
	}
	
	/**
	 * Elmenti a j�t�kos pontsz�m�t.
	 * Hozz��rja a nevet �s pontsz�mot egy k�z�s adatt�rol� txt-be.
	 * Form�tum:
	 * [n�v]
	 * [pontsz�m]
	 */
	public void save() {
		try {
			FileWriter fw = new FileWriter("scores.txt", true);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
			pw.println(playerName.get());
			pw.println(points.get());
			pw.close();
		} catch (IOException e) {
			new ErrorAlert(e.toString(), "Error");
		}
	}
}
