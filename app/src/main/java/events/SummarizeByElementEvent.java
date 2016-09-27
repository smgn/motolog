package events;

public class SummarizeByElementEvent {
	private double amountInEntries;
	private int numInEntries;


	public SummarizeByElementEvent(double amountInEntries, int numInEntries) {
		this.amountInEntries = amountInEntries;
		this.numInEntries = numInEntries;

	}

	public double getAmountInEntries() {
		return amountInEntries;
	}

	public int getNumInEntries() {
		return numInEntries;
	}
}
