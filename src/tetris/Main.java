package tetris;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Mészáros Csaba 
 */
public class Main extends Application {

	/**
	 * A program ablakának létrehozása.
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
    	// ha bezárja az ablakot, minden szálat le kell állítani.
    	primaryStage.setOnCloseRequest(e -> { Platform.exit(); System.exit(0); });
    	primaryStage.setScene(scene);
        primaryStage.show();
	}
    
    public static void main(String[] args) {
        launch(args);
    }
}
