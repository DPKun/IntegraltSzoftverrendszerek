package org.refcounter.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
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
		inputFile.textProperty().addListener(new ChangeListener<String>() {

			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				System.out.println(inputFile.textProperty());
				enableSameFile();

			}
		});
		sameFile.selectedProperty().addListener(new ChangeListener<Boolean>() {

			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				setToSameFile();

			}

		});
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
	 * The method sets the input path with a filechooser
	 */
	@FXML
	public void setInputPath() {
		inputFile.setText(chooseFile());
	}

	/**
	 * The method sets the output path with a filechooser
	 */
	@FXML
	public void setOutputPath() {
		outputFile.setText(chooseFile());
	}

	/**
	 * The method sets the output path to the same value as the filechooser
	 */
	@FXML
	public void setToSameFile() {
		if (sameFile.isSelected()) {
			outputFile.setText(inputFile.getText());
			chooseOutputFile.setDisable(true);
		} else {
			chooseOutputFile.setDisable(false);
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
