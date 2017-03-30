package org.refcounter.controller;

import java.io.IOException;

import org.refcounter.controller.AppController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Hello world!
 *
 */
public class MainApp extends Application {
	/**
	 * The main stage of the application ui
	 */
	private Stage primaryStage;
	
	/**
	 * The visible ui of the application
	 */
	private AnchorPane MainView;

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * The starting method of the ui
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			this.primaryStage = primaryStage;
			this.primaryStage.setTitle("MTMT lekérdező");
			showMainView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The initializing method of the main scene
	 */
	 public void showMainView() {
	        try {
	            // Load the user interface of the application
	            FXMLLoader loader = new FXMLLoader();
	            loader.setLocation(MainApp.class.getResource("/MainWindow.fxml"));
	            MainView = (AnchorPane) loader.load();

	            // Give the controller access to the main app.
	            AppController controller = loader.getController();
	            controller.setMainApp(this);
	            
	            Scene scene = new Scene(MainView);
	            primaryStage.setScene(scene);
	            primaryStage.show();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	 
	 public Stage getPrimaryStage() {
	        return primaryStage;
	    }
}
