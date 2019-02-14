package scatterplot;

public class CSVEntry {
	
	double x;
	double y;
	String name;
	
	public CSVEntry(double x, double y, String name) {
		this.x = x;
		this.y = y;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return (x + ", " + y + ", " + name);
	}
}
