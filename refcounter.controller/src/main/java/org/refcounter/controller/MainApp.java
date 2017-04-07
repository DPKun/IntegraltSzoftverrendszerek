package org.refcounter.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.refcounter.controller.AppController;
import org.refcounter.controller.NewFileController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The main class of the JavaFX application
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
	 
	 /**
	  * The initializing method of the new file window.
	  * @return the new file with location and its first worksheet name.
	  */
	 public List<String> showNewFileWindow(){
		 List<String> result= new ArrayList<>();
		 try {		 
		// Load the user interface of the application
         FXMLLoader loader = new FXMLLoader();
         loader.setLocation(MainApp.class.getResource("/NewFileWindow.fxml"));
		 AnchorPane panel = (AnchorPane) loader.load();
		
		// Create the dialog Stage.
         Stage dialogStage = new Stage();
         dialogStage.setTitle("Új Fájl");
         dialogStage.initModality(Modality.WINDOW_MODAL);
         dialogStage.initOwner(primaryStage);
         Scene scene = new Scene(panel);
         dialogStage.setScene(scene);
         
         // Give the controller access to the main app.
         NewFileController controller = loader.getController();
         controller.setDialogStage(dialogStage);
         
         dialogStage.showAndWait();
         result.add(controller.getFile());
         result.add(controller.getSheet());
		 } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         return result;
	 }
	 
	 /**
	  * The initializing method of the new worksheet window.
	  * @return the new worksheet name.
	  */
	 public String showNewWorksheetWindow(){
		 String result="";
		 try {
		 // Load the user interface of the application
         FXMLLoader loader = new FXMLLoader();
         loader.setLocation(MainApp.class.getResource("/NewWorksheetWindow.fxml"));
		 AnchorPane panel;
		
			panel = (AnchorPane) loader.load();
		
		
		// Create the dialog Stage.
         Stage dialogStage = new Stage();
         dialogStage.setTitle("Új munkalap");
         dialogStage.initModality(Modality.WINDOW_MODAL);
         dialogStage.initOwner(primaryStage);
         Scene scene = new Scene(panel);
         dialogStage.setScene(scene);
         
         // Give the controller access to the main app.
         NewWorksheetController controller = loader.getController();
         controller.setDialogStage(dialogStage);
         
         dialogStage.showAndWait();
         result=controller.getSheetName();
         
		 } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 return result;
	 }
}
