package org.refcounter.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.function.UnaryOperator;

import org.refcounter.service.ISSNService;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.StringConverter;

/**
 * The controller for the application's view;
 *
 */
public class AppController {

	private static ISSNService service = new ISSNService();
	private MainApp mainApp;

	@FXML
	private CheckBox sameFile;

	@FXML
	private CheckBox sameWorksheet;

	@FXML
	private Button chooseInputFile;

	@FXML
	private Button chooseOutputFile;

	@FXML
	private CheckBox name;

	@FXML
	private CheckBox shortName;

	@FXML
	private CheckBox entry;

	@FXML
	private ChoiceBox<String> inputWorksheet;

	@FXML
	private ChoiceBox<String> outputWorksheet;

	@FXML
	private TextField inputFile;

	@FXML
	private TextField outputFile;

	@FXML
	private TextField inputColumn;

	@FXML
	private TextField outputColumn;

	@FXML
	private TextField inputRow;

	@FXML
	private TextField outputRow;

	public AppController() {

	}

	/**
	 * The initialization of the controller
	 */
	@FXML
	private void initialize() {
		// listeners
		inputFile.textProperty().addListener(new ChangeListener<String>() {

			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				enableSameFile();
				if(sameFile.isSelected()){
					setToSameFile();
				}
				if(sameWorksheet.isSelected()){
					outputWorksheet.setDisable(true);
				}

			}
		});
		inputWorksheet.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if(sameWorksheet.isSelected()){
					outputWorksheet.getSelectionModel().clearAndSelect(newValue.intValue());
				}
				
			}

	
		});
		sameFile.selectedProperty().addListener(new ChangeListener<Boolean>() {

			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				setToSameFile();

			}

		});
		sameWorksheet.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				setToSameWorksheet();
			}
		});
		// number only filter
		UnaryOperator<Change> filter = change -> {
			String text = change.getText();

			if (text.matches("[0-9]*")) {
				return change;
			}

			return null;
		};
		inputColumn.setTextFormatter(new TextFormatter<>(filter));
		inputRow.setTextFormatter(new TextFormatter<>(filter));
		outputColumn.setTextFormatter(new TextFormatter<>(filter));
		outputRow.setTextFormatter(new TextFormatter<>(filter));
	}

	/**
	 * Sets the main app of the application
	 * 
	 * @param mainApp
	 *            the main app;
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	/**
	 * Opens a FileChooser interface for the user on which an excel file can be
	 * chosen.
	 * 
	 * @return the absolute path of the chosen file;
	 */
	public String chooseFile() {
		String result = "";
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Válasszon egy fájlt");
		List<String> extensions = new ArrayList<String>();
		extensions.add("*.xls");
		extensions.add("*.xlsx");
		chooser.getExtensionFilters().add(new ExtensionFilter("Excel", extensions));
		File selected = chooser.showOpenDialog(mainApp.getPrimaryStage());
		if (selected != null) {
			result = selected.getAbsolutePath();
		}
		return result;
	}

	/**
	 * A method for showing an alert message when the application throws an
	 * exception
	 * 
	 * @param title
	 *            The title of the message window
	 * @param text
	 *            The actual message
	 * @param e
	 *            The thrown exception
	 */
	public void showAlert(String title, String text, Exception e) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(title);
		if (e.getMessage() != null) {
			alert.setHeaderText(null);
			alert.setContentText(text);
		} else {
			alert.setHeaderText(text);
			alert.setContentText(e.getMessage());
		}
		alert.showAndWait();

	}

	/**
	 * The method sets the input path with a filechooser
	 */
	@FXML
	public void setInputPath() {
		File file = new File(chooseFile());
		inputFile.setText(file.getAbsolutePath());
		ObservableList<String> sheets;
		try {
			sheets = FXCollections.observableArrayList(service.getWorkSheets(file));
		} catch (IOException e) {
			sheets = FXCollections.observableArrayList();
			showAlert("Fájl nem hozzáférhető",
					"A fájlhoz nem lehet hozzáférni, kérjük ellenőrizze, hogy máshol meg van-e nyitva, vagy írásvédett-e",
					e);
		}
		inputWorksheet.setItems(sheets);
		if (!sheets.isEmpty()) {
			inputWorksheet.setDisable(false);
			inputWorksheet.getSelectionModel().selectFirst();
			inputWorksheet.setValue(inputWorksheet.getSelectionModel().getSelectedItem());
		}
	}

	/**
	 * The method sets the output path with a filechooser
	 */
	@FXML
	public void setOutputPath() {
		File file = new File(chooseFile());
		outputFile.setText(file.getAbsolutePath());
		ObservableList<String> sheets;
		try {
			sheets = FXCollections.observableArrayList(service.getWorkSheets(file));
		} catch (IOException e) {
			sheets = FXCollections.observableArrayList();
			showAlert("Fájl nem hozzáférhető",
					"A fájlhoz nem lehet hozzáférni, kérjük ellenőrizze, hogy máshol meg van-e nyitva, vagy írásvédett-e",
					e);
		}
		outputWorksheet.setItems(sheets);
		if (!sheets.isEmpty()) {
			outputWorksheet.setDisable(false);
			outputWorksheet.getSelectionModel().selectFirst();
			outputWorksheet.setValue(outputWorksheet.getSelectionModel().getSelectedItem());
			
		}
	}

	/**
	 * The method sets the output path to the same value as the filechooser
	 */
	@FXML
	public void setToSameFile() {
		if (sameFile.isSelected()) {
			outputWorksheet.setDisable(false);
			outputFile.setText(inputFile.getText());
			ObservableList<String> sheets;
			try {
				sheets = FXCollections.observableArrayList(service.getWorkSheets(new File(inputFile.getText())));
			} catch (IOException e) {
				sheets = FXCollections.observableArrayList();
				showAlert("Fájl nem hozzáférhető",
						"A fájlhoz nem lehet hozzáférni, kérjük ellenőrizze, hogy máshol meg van-e nyitva, vagy írásvédett-e",
						e);
			}
			outputWorksheet.setItems(sheets);
			if (!sheets.isEmpty()) {
				outputWorksheet.getSelectionModel().selectFirst();
				outputWorksheet.setValue(outputWorksheet.getSelectionModel().getSelectedItem());
			}
			chooseOutputFile.setDisable(true);
			
			sameWorksheet.setOpacity(1);
		} else {
			chooseOutputFile.setDisable(false);
			outputWorksheet.setItems(FXCollections.observableArrayList());
			sameWorksheet.setOpacity(0);
			sameWorksheet.setSelected(false);
		}
	}
	
	@FXML
	public void setToSameWorksheet(){
		if(sameWorksheet.isSelected()){
			outputWorksheet.getSelectionModel().clearAndSelect(inputWorksheet.getSelectionModel().getSelectedIndex());
			outputWorksheet.setDisable(true);
		}else{
			outputWorksheet.setDisable(false);
		}
	}

	/**
	 * Enables the sameFile checkbox
	 */
	@FXML
	public void enableSameFile() {
		if (!inputFile.getText().equals("")) {
			sameFile.setOpacity(1);
		} else {
			sameFile.setOpacity(0);
		}
	}
}
