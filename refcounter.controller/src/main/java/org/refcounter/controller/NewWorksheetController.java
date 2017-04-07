package org.refcounter.controller;

import java.util.function.UnaryOperator;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextFormatter.Change;
import javafx.stage.Stage;
/**
 * The controller for the newWorksheetWindow
 * @author Daniel Kun
 *
 */
public class NewWorksheetController {
	
	private String sheetName;
	private Stage dialogStage;
	
	@FXML
	private Button createButton;
	
	@FXML
	private TextField sheetNameInput;
	
	
	
	public NewWorksheetController() {
	}
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public Stage getDialogStage() {
		return dialogStage;
	}
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	@FXML
	private void initialize() {
		UnaryOperator<Change> filter = change -> {
			String text = change.getText();

			if (text.matches("[a-zA-Z0-7]*") ) {
				return change;
			}

			return null;
		};
		
	sheetNameInput.setTextFormatter(new TextFormatter<>(filter));
    }
	
	/**
	 * Checks if the sheetNameInput's value passes as a worksheet name.
	 */
	public boolean isOk(){
		String error="";
		if(sheetNameInput.getText() == null || sheetNameInput.getText().equals("")){
			error+="Nem írt be munkalap nevet.";
		}
		
		if(error.equals("")){
			return true;
		}else{
			 // Show the error message.
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Hibás Mezők");
            alert.setHeaderText("Kérem javítsa ki a hibás mezőket");
            alert.setContentText(error);

            alert.showAndWait();

            return false;
		}
	}
	
	/**
	 * if the value of sheetNameInput is ok, it is saved into the sheetName variable.
	 */
	@FXML
	public void createClicked(){
	 if(!isOk()){} else{
		 sheetName=sheetNameInput.getText();
			dialogStage.close();
	 }
	}

}
