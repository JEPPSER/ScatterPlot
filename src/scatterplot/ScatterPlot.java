package scatterplot;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class ScatterPlot extends VBox {

	private ArrayList<String> allNames;
	private final Color[] COLORS = { Color.BLUE, Color.RED, Color.GREEN, Color.PURPLE, Color.YELLOW, Color.PINK,
			Color.BLACK };
	private ArrayList<CSVEntry> entries;
	private Canvas canvas;
	private GraphicsContext g;
	private HBox namePane;
	private Text mousePos;
	private BorderPane borderPane;

	private final int PADDING = 60;

	public ScatterPlot() {
		super();
		canvas = new Canvas();
		canvas.setWidth(500);
		canvas.setHeight(500);
		g = canvas.getGraphicsContext2D();
		namePane = new HBox();
		namePane.setMinHeight(20);
		namePane.setSpacing(5);
		mousePos = new Text();
		borderPane = new BorderPane();
		borderPane.setLeft(namePane);
		borderPane.setRight(mousePos);
		borderPane.setPadding(new Insets(0, 10, 10, 10));
		this.getChildren().addAll(canvas, borderPane);
	}

	public void setEntries(ArrayList<CSVEntry> entries) {
		g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		namePane.getChildren().clear();
		this.entries = entries;
		allNames = new ArrayList<String>();

		// Fetching all unique names
		for (int i = 0; i < entries.size(); i++) {
			if (!allNames.contains(entries.get(i).name)) {
				allNames.add(entries.get(i).name);
			}
		}

		drawPlot();

		// List all unique names and their color
		for (int i = 0; i < allNames.size(); i++) {
			Text text = new Text(allNames.get(i) + ":");
			Circle c = new Circle(5, 5, 5);
			if (i >= COLORS.length - 1) {
				c.setFill(COLORS[COLORS.length - 1]);
				namePane.getChildren().addAll(new Text("other:"), c);
			} else {
				c.setFill(COLORS[i]);
				namePane.getChildren().addAll(text, c);
			}
			c.setTranslateY(namePane.getPadding().getTop() + c.getRadius());
		}

		mousePos.setText("x: 0 y: 0");
	}

	private void onHover(double xScale, double yScale, double xStart, double yStart) {
		canvas.setOnMouseMoved(e -> {

			// Converting mouse position to x and y values in the diagram.
			double x = e.getX() / xScale - PADDING / xScale + xStart;
			double y = canvas.getHeight() / yScale - e.getY() / yScale - PADDING / yScale + yStart;
			mousePos.setText("x: " + (Math.round(x * 1000) / 1000.0) + " y: " + (Math.round(y * 1000) / 1000.0));

			// Looping through all entries to find which one the mouse is
			// hovering over.
			for (int i = 0; i < entries.size(); i++) {

				// Converting x and y position in the diagram to mouse position.
				double xRaw = PADDING + xScale * entries.get(i).x - xScale * xStart;
				double yRaw = canvas.getHeight() - PADDING - entries.get(i).y * yScale + yScale * yStart;

				// Check if distance is within the radius of the dot
				// representing the entry.
				double distance = distance(e.getX(), e.getY(), xRaw, yRaw);
				if (distance <= 5) {
					drawPlot();
					drawPopupWindow(entries.get(i), xRaw, yRaw);
					break;
				}

				// Removes the popup window if the mouse isn't touching an
				// entry.
				if (i == entries.size() - 1) {
					drawPlot();
				}
			}
		});
	}

	private void drawPopupWindow(CSVEntry entry, double xRaw, double yRaw) {
		g.setFill(Color.WHITE);
		g.fillRoundRect(xRaw - 25, yRaw - 60, 50, 50, 10, 10);
		g.setFill(Color.BLACK);
		g.strokeRoundRect(xRaw - 25, yRaw - 60, 50, 50, 10, 10);
		g.fillText(entry.name, xRaw - 20, yRaw - 45);
		g.fillText("x: " + entry.x, xRaw - 20, yRaw - 30);
		g.fillText("y: " + entry.y, xRaw - 20, yRaw - 15);
	}

	private double distance(double x1, double y1, double x2, double y2) {
		return Math.hypot(x1 - x2, y1 - y2);
	}

	private void drawPlot() {
		g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		double xMax = entries.get(0).x;
		double xMin = entries.get(0).x;
		double yMax = entries.get(0).y;
		double yMin = entries.get(0).y;

		// Finding max and min values
		for (int i = 0; i < entries.size(); i++) {
			if (entries.get(i).x > xMax) {
				xMax = entries.get(i).x;
			}
			if (entries.get(i).x < xMin) {
				xMin = entries.get(i).x;
			}
			if (entries.get(i).y > yMax) {
				yMax = entries.get(i).y;
			}
			if (entries.get(i).y < yMin) {
				yMin = entries.get(i).y;
			}
		}

		double xDif = xMax - xMin;
		double yDif = yMax - yMin;
		int xTicks = getSignificantDigit(xDif);
		double xIt = 1.0;
		if (xTicks < 5) {
			xIt = 0.5;
			xTicks *= 2;
		}
		int yTicks = getSignificantDigit(yDif);
		double yIt = 1.0;
		if (yTicks < 5) {
			yIt = 0.5;
			yTicks *= 2;
		}

		yTicks += 2;
		xTicks += 2;

		// Values for the y axis
		double yTickSpacing = (canvas.getHeight() - PADDING * 2) / yTicks;
		double yLog = Math.floor(Math.log10(yDif));
		double yIteration = Math.pow(10, yLog);
		yIteration *= yIt;
		double yStart = Math.floor(yMin / yIteration) * yIteration;

		// Values for the x axis
		double xTickSpacing = (canvas.getWidth() - PADDING * 2) / xTicks;
		double xLog = Math.floor(Math.log10(xDif));
		double xIteration = Math.pow(10, xLog);
		xIteration *= xIt;
		double xStart = Math.floor(xMin / xIteration) * xIteration;

		// Drawing the y axis
		for (int i = 0; i < yTicks + 1; i++) {
			double x1 = PADDING - 5;
			double x2 = PADDING + 5;
			double y = (canvas.getHeight() - PADDING) - yTickSpacing * i;

			// Grid line
			g.setLineWidth(1);
			g.setStroke(Color.GAINSBORO);
			g.strokeLine(PADDING, y, canvas.getWidth() - PADDING, y);

			// Tick
			g.setLineWidth(2);
			g.setStroke(Color.BLACK);
			g.strokeLine(x1, y, x2, y);
			if (yIteration < 1) {
				g.fillText(String.valueOf(yStart + i * yIteration), x1 - 30, y + 5);
			} else {
				g.fillText(String.valueOf((int) (yStart + i * yIteration)), x1 - 30, y + 5);
			}
		}

		// Drawing the x axis
		for (int i = 0; i < xTicks + 1; i++) {
			double y1 = canvas.getHeight() - PADDING - 5;
			double y2 = canvas.getHeight() - PADDING + 5;
			double x = PADDING + xTickSpacing * i;

			// Grid line
			g.setLineWidth(1);
			g.setStroke(Color.GAINSBORO);
			g.strokeLine(x, PADDING, x, canvas.getHeight() - PADDING);

			// Tick
			g.setLineWidth(2);
			g.setStroke(Color.BLACK);
			g.strokeLine(x, y1, x, y2);
			if (xIteration < 1) {
				g.fillText(String.valueOf(xStart + i * xIteration), x - 5, y2 + 20);
			} else {
				g.fillText(String.valueOf((int) (xStart + i * xIteration)), x - 5, y2 + 20);
			}
		}

		g.setStroke(Color.BLACK);
		g.setLineWidth(2);
		g.strokeLine(PADDING, PADDING, PADDING, canvas.getHeight() - PADDING);
		g.strokeLine(PADDING, canvas.getHeight() - PADDING, canvas.getWidth() - PADDING, canvas.getHeight() - PADDING);

		double xScale = xTickSpacing / xIteration;
		double yScale = yTickSpacing / yIteration;

		// Draw all entries
		for (int i = 0; i < entries.size(); i++) {
			Color c = COLORS[COLORS.length - 1];
			// Pick a color for the entry
			for (int j = 0; j < allNames.size(); j++) {
				if (entries.get(i).name.equals(allNames.get(j))) {
					if (j >= COLORS.length) {
						c = COLORS[COLORS.length - 1];
					} else {
						c = COLORS[j];
					}
				}
			}

			// Set opacity to 70%.
			c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 0.7);

			// Converting entry's x and y to canvas x and y.
			double x = PADDING + xScale * entries.get(i).x - xScale * xStart;
			double y = canvas.getHeight() - PADDING - entries.get(i).y * yScale + yScale * yStart;
			g.setFill(c);
			g.fillOval(x - 5, y - 5, 10, 10);
		}
		g.setFill(Color.BLACK);

		g.fillText("y", PADDING / 2, PADDING / 2);
		g.fillText("x", canvas.getWidth() - PADDING / 2, canvas.getHeight() - PADDING / 2);

		onHover(xScale, yScale, xStart, yStart);
	}

	private int getSignificantDigit(double number) {
		String str = String.valueOf(number);
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) != '0' && str.charAt(i) != '.') {
				return Integer.parseInt("" + str.charAt(i));
			}
		}
		return 1;
	}
}
