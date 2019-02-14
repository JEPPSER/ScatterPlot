package scatterplot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FileChooser fileChooser = new FileChooser();
		Button openFileButton = new Button("Open File");
		ScatterPlot scatterPlot = new ScatterPlot();
		VBox vbox = new VBox();
		vbox.getChildren().addAll(openFileButton, scatterPlot);

		openFileButton.setOnAction(e -> {
			File file = fileChooser.showOpenDialog(primaryStage);
			if (file.getPath().toLowerCase().endsWith(".csv")) {
				ArrayList<CSVEntry> entries = readCSVFile(file);
				scatterPlot.setEntries(entries);
			}
		});

		Scene scene = new Scene(vbox);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private ArrayList<CSVEntry> readCSVFile(File file) {
		ArrayList<CSVEntry> entries = new ArrayList<CSVEntry>();
		try {
			String data = new String(Files.readAllBytes(Paths.get(file.getPath())));
			String[] lines = data.split("\n");
			for (int i = 0; i < lines.length; i++) {
				String[] values = lines[i].split(",");
				if (isEntry(values)) {
					double x = Double.parseDouble(values[0]);
					double y = Double.parseDouble(values[1]);
					String name = values[2];
					CSVEntry entry = new CSVEntry(x, y, name);
					entries.add(entry);
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return entries;
	}
	
	private boolean isEntry(String[] values) {
		if (values.length != 3) {
			return false;
		}
		if (!(isDouble(values[0]) || isInteger(values[0]))) {
			return false;
		}
		if (!(isDouble(values[1]) || isInteger(values[1]))) {
			return false;
		}
		return true;
	}
	
	private boolean isInteger(String value) {
		for (int i = 0; i < value.length(); i++) {
			if (!(i == 0 && value.charAt(i) == '-')) {
				if (!Character.isDigit(value.charAt(i))) {
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean isDouble(String value) {
		int pointCounter = 0;
		for (int i = 0; i < value.length(); i++) {
			if (!(i == 0 && value.charAt(i) == '-')) {
				if (value.charAt(i) == '.') {
					pointCounter++;
				} else if (!Character.isDigit(value.charAt(i))) {
					return false;
				}
			}	
		}
		if (pointCounter != 1) {
			return false;
		}	
		return true;
	}
}
