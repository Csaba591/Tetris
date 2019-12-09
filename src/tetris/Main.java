package tetris;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author M�sz�ros Csaba 
 */
public class Main extends Application {

	/**
	 * A program ablak�nak l�trehoz�sa.
	 */
    @Override
    public void start(Stage primaryStage) throws Exception{    	
    	MainFrame pa = new MainFrame();
    	Scene scene = new Scene(pa);
    	scene.setOnKeyPressed(pa.new InputHandler());
    	primaryStage.setTitle("Tetris");
    	primaryStage.setResizable(false);
    	primaryStage.minWidthProperty().bind(scene.widthProperty());
    	primaryStage.minHeightProperty().bind(scene.heightProperty());
    	// ha bez�rja az ablakot, minden sz�lat le kell �ll�tani.
    	primaryStage.setOnCloseRequest(e -> { Platform.exit(); System.exit(0); });
    	primaryStage.setScene(scene);
        primaryStage.show();
	}
    
    public static void main(String[] args) {
        launch(args);
    }
}
