package tetris;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{    	
    	PlayArea pa = new PlayArea();
    	Scene scene = new Scene(pa);
    	scene.setOnKeyPressed(pa.new MovementHandler());
    	primaryStage.setTitle("Tetris");
    	primaryStage.setOnCloseRequest(e -> { Platform.exit(); System.exit(0); });
    	primaryStage.setScene(scene);
        primaryStage.show();
	}
    
    public static void main(String[] args) {
        launch(args);
    }
}
