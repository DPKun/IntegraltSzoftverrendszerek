package org.refcounter.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.UnaryOperator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.refcounter.service.ISSNService;
import org.refcounter.web.CheckerType;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * The controller for the application's main view;
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

	@FXML
	private RadioButton inputAsRow;

	@FXML
	private RadioButton inputAsColumn;

	@FXML
	private RadioButton outputAsRow;

	@FXML
	private RadioButton outputAsColumn;

	@FXML
	private ToggleGroup outputNavigation;

	@FXML
	private ToggleGroup inputNavigation;

	@FXML
	private Button newFile;

	@FXML
	private Button newSheet;
	
	@FXML
	private Button Start;

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
				if (sameFile.isSelected()) {
					setToSameFile();
				}
				if (sameWorksheet.isSelected()) {
					outputWorksheet.setDisable(true);
				}

			}
		});
		inputWorksheet.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (sameWorksheet.isSelected()) {
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
	 * @param true
	 *            for the opened file, false for the destination file
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
	 * The method sets the output path to the same value as the input file.
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
			newFile.setDisable(true);

			sameWorksheet.setOpacity(1);
		} else {
			chooseOutputFile.setDisable(false);
			newFile.setDisable(false);
			outputWorksheet.setItems(FXCollections.observableArrayList());
			sameWorksheet.setOpacity(0);
			sameWorksheet.setSelected(false);
		}
	}

	@FXML
	public void setToSameWorksheet() {
		if (sameWorksheet.isSelected()) {
			outputWorksheet.getSelectionModel().clearAndSelect(inputWorksheet.getSelectionModel().getSelectedIndex());
			outputWorksheet.setDisable(true);
			newSheet.setDisable(true);
		} else {
			outputWorksheet.setDisable(false);
			newSheet.setDisable(false);
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

	/**
	 * The method tries to run the showNewFileWindow method of the mainApp and
	 * handles the showing of the results in the window.
	 */
	@FXML
	public void createNewFile() {
		List<String> result = mainApp.showNewFileWindow();
		outputFile.setText(result.get(0));
		outputWorksheet.setItems(FXCollections.observableArrayList(result.get(1)));
		outputWorksheet.getSelectionModel().clearAndSelect(0);
		outputWorksheet.setDisable(false);
	}

	/**
	 * The method tries to run the showNewWorksheetWindow method of the mainApp
	 * and handles the showing of the results in the window.
	 */
	@FXML
	public void createNewWorksheet() {
		ObservableList<String> entries = outputWorksheet.getItems();
		String newSheet = mainApp.showNewWorksheetWindow();
		if (entries.contains(newSheet)) {
			showAlert("Már létezik a munkalap", "Az ön által megadott nevű munkalap már létezik", new Exception());
		} else {
			entries.add(newSheet);
			outputWorksheet.setItems(entries);
			outputWorksheet.getSelectionModel().clearAndSelect(entries.indexOf(newSheet));
		}
	}

	@FXML
	public void startquery() {
		if (!validate()) {
		} else {
			ISSNService service = new ISSNService();
			List<CheckerType> queries = new ArrayList<>();
			if (name.isSelected()) {
				queries.add(CheckerType.NAME);
			}
			if (shortName.isSelected()) {
				queries.add(CheckerType.SHORTNAME);
			}
			if (entry.isSelected()) {
				queries.add(CheckerType.ENTRY);
			}
			List<String> issns = new ArrayList<>();
			// Getting the issn numbers from the chosen file and worksheet.
			try {
				if (inputAsRow.isSelected()) {
					issns = service.loadFromRow(new File(inputFile.getText()),
							inputWorksheet.getSelectionModel().getSelectedItem(), Integer.parseInt(inputRow.getText()),
							Integer.parseInt(inputColumn.getText()));
				} else {
					issns = service.loadFromColumn(new File(inputFile.getText()),
							inputWorksheet.getSelectionModel().getSelectedItem(), Integer.parseInt(inputRow.getText()),
							Integer.parseInt(inputColumn.getText()));
				}
			} catch (NumberFormatException e) {
				showAlert("Nem szám az érték!",
						"Az egyik sorhoz vagy oszlophoz nem számot adott meg, pedig ezt várja a rendszer!", e);
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				showAlert("Nem megfelelő formátum", "Ez a fájl nem a megfelelő formátumú, kérem válasszon másikat!", e);
				e.printStackTrace();
			} catch (IOException e) {
				showAlert("Hiba megnyitáskor",
						"A fájlhoz vagy a munkalaphoz nem lehetett hozzáférni, kérem ellenőrizze, hogy megfelelő paramétereket adott-e meg.",
						e);
				e.printStackTrace();
			}
			// queerying the data from the web.
			HashMap<String, List<String>> webQueries = new HashMap<>();
			if (issns.isEmpty()) {
				showAlert("Nincs ISSN",
						"A keresés nem talált ISSN számot a megadott helyen.\n Kérem ellenőrizze a formátumot és a megadott helyet.",
						new Exception());
			} else {
				try {
					webQueries = service.queryWebpage(queries, issns);
				} catch (InterruptedException e) {
					showAlert("Művelet megszakítva",
							"A művelet menetközben megszakadt.\n" + "Kérem ellenőrizze az internetkapcsolatát", e);
					e.printStackTrace();
				}
			}
			// adding the queried data to the selected file and worksheet
			File outFile = new File(outputFile.getText());
			try {
				if (outputAsRow.isSelected()) {

					service.addResultsAsRow(outFile, outputWorksheet.getSelectionModel().getSelectedItem(),
							Integer.parseInt(outputRow.getText()), Integer.parseInt(outputColumn.getText()),
							webQueries);
				} else {
					service.addResultsAsColumn(outFile, outputWorksheet.getSelectionModel().getSelectedItem(),
							Integer.parseInt(outputRow.getText()), Integer.parseInt(outputColumn.getText()),
							webQueries);
				}
			} catch (NumberFormatException e) {
				showAlert("Nem szám lett megadva",
						"Az oszlop vagy sor mezőnek nem szám lett megadva, kérem számot adjon meg!", e);
				e.printStackTrace();
			} catch (IOException e) {
				showAlert("Nincs hozzáférés",
						"A fájlhoz nem lehet hozzáférni. Kérem zárjon be minden olyan programot, amely használja a fájlt és utána kattintson az OK-ra, vagy az adatok nem kerülnk elmentésre",
						e);
				try {
					if (outputAsRow.isSelected()) {
						service.addResultsAsRow(outFile, outputWorksheet.getSelectionModel().getSelectedItem(),
								Integer.parseInt(outputRow.getText()), Integer.parseInt(outputColumn.getText()),
								webQueries);

					} else {
						service.addResultsAsColumn(outFile, outputWorksheet.getSelectionModel().getSelectedItem(),
								Integer.parseInt(outputRow.getText()), Integer.parseInt(outputColumn.getText()),
								webQueries);
					}
				} catch (NumberFormatException | IOException e1) {
					// if the process denying access to the file is not closed,
					// then the data is lost and the query has to be done again.
				}
				e.printStackTrace();
			}

		}
	}

	/**
	 * A method which checks if all of the needed data is oresent and is
	 * presented in an acceptable manner.
	 * 
	 * @return
	 */
	public boolean validate() {
		String error = "";
		if (!name.isSelected() && !shortName.isSelected() && !entry.isSelected()) {
			error += "Nincs lekérdezés kiválasztva!";
		}
		if (inputWorksheet.getSelectionModel().getSelectedItem() == null) {
			error += "Nincs megadva, hogy melyik munkalapon vannak az ISSN számok!";
		}
		if (outputWorksheet.getSelectionModel().getSelectedItem() == null) {
			error += "Nincs megadva, hogy melyik munkalapra legyenek mentve az adatok!";
		}
		if (inputFile.getText() == null || inputFile.getText() == "") {
			error += "Nincs megadva, hogy mely fájlban találhatóak az ISSN számok!";
		}
		if (outputFile.getText() == null || outputFile.getText() == "") {
			error += "Nincs megadva, hogy mely fájlba legyenek lementve az eredmények!";
		}
		if (inputColumn.getText() == null || inputColumn.getText() == "") {
			error += "Nincs megadva, hogy melyik oszlopban kezdődik az ISSN számok lekérdezése.";
		}
		if (inputRow.getText() == null || inputRow.getText() == "") {
			error += "Nincs megadva, hogy melyik sorban kezdődik az ISSN számok lekérdezése.";
		}
		if (outputColumn.getText() == null || outputColumn.getText() == "") {
			error += "Nincs megadva, hogy melyik oszlopban kezdődik a kinyert adatok mentése.";
		}
		if (outputRow.getText() == null || outputRow.getText() == "") {
			error += "Nincs megadva, hogy melyik sorban kezdődik a kinyert adatok mentése.";
		}
		if (error.equals("")) {
			return true;
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Hibás Mezők");
			alert.setHeaderText("Kérem javítsa ki a hibás mezőket");
			alert.setContentText(error);

			alert.showAndWait();

			return false;
		}
	}
}
