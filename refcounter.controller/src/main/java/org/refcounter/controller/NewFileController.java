package org.refcounter.controller;

import java.io.File;
import java.util.function.UnaryOperator;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextFormatter.Change;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
/**
 * The controller for the newFileWindow
 * @author Daniel Kun
 *
 */
public class NewFileController {

	private String file;
	private String sheet;
	private Stage dialogStage;
	
	@FXML
	private Button directoryChooser;
	
	@FXML
	private Button ok;
	
	@FXML
	private TextField filePath;
	
	@FXML
	private TextField fileName;
	
	@FXML
	private TextField sheetName;
	
	

	public NewFileController() {
	}

	public Stage getDialogStage() {
		return dialogStage;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getSheet() {
		return sheet;
	}

	public void setSheet(String sheet) {
		this.sheet = sheet;
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
		
	fileName.setTextFormatter(new TextFormatter<>(filter));
	sheetName.setTextFormatter(new TextFormatter<>(filter));
    }
	
	/**
	 * Opens a DirectoryChooser and puts the returned value into the filePath variable.
	 */
	@FXML
	public void chooseDirectory(){
		String result="";
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Válasszon Mappát!");
		result=chooser.showDialog(dialogStage).getAbsolutePath();
		filePath.setText(result);
	}
	
	/**
	 * Checks if the current values of the textfields match the requirements of file and worksheet creation.
	 * @return if the values are valid
	 */
	public boolean isInputValid(){
		String error="";
		
		if(filePath.getText() == null || filePath.getText().equals("")){
			error+="Nem választott mappát.";
		}
		
		if(fileName.getText() == null || fileName.getText().equals("")){
			error+="Nem írt be fájlnevet.";
		}
		
		if(new File(concatFile(filePath.getText(), fileName.getText())).exists()){
			error+="Már létezik ilyen nevű fájl a mappában.";
		}
		
		if(sheetName.getText() == null || sheetName.getText().equals("")){
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
	 * A convenience method which combines the filePath and fileName into a working filepath
	 * @param path the filePath
	 * @param name the fileName
	 * @return the combined filepath with extension
	 */
	public String concatFile(String path, String name){
		String result=new String(path);
		result=result.concat(name);
		result=result.concat(".xlsx");
		return result;
	}
	
	/**
	 * checks if the values are ok, then stores the informations in the variables.
	 */
	@FXML
	public void okClicked(){
		boolean checked=false;
		checked = isInputValid();
		if(!checked){}else{
			file=concatFile(filePath.getText(), fileName.getText());
			sheet=sheetName.getText();
			dialogStage.close();
		}
		
	}
}
