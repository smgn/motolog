package events;

import java.util.List;

public class SummarizeEvent {
	private double totalCost;
	private double costPerDay;
	private int totalDistance;
	private double costPerDistance;
	private int numEntries;
	private List<String> listElements;

	public SummarizeEvent(double totalCost, double costPerDay, int totalDistance,
	                      double costPerDistance, int numEntries, List<String> listElements) {
		this.totalCost = totalCost;
		this.costPerDay = costPerDay;
		this.totalDistance = totalDistance;
		this.costPerDistance = costPerDistance;
		this.numEntries = numEntries;
		this.listElements = listElements;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public double getCostPerDay() {
		return costPerDay;
	}

	public int getTotalDistance() {
		return totalDistance;
	}

	public double getCostPerDistance() {
		return costPerDistance;
	}

	public int getNumEntries() {
		return numEntries;
	}

	public List<String> getListElements() {
		return listElements;
	}
}
