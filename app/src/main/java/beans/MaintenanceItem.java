package beans;

public class MaintenanceItem {

    int key;
	String Vehicle;
	String MaintElem;
	String MaintType;
	double FuelAmount;
	double Consumption;
	String Date;
	int Odometer;
	String Details;
	int MileageType;
	double Cash;

	public MaintenanceItem(String vehicle,
                           String maintElem,
                           String maintType,
                           double fuelAmount,
                           double consumption,
                           String date,
                           int odometer,
                           String details,
                           int mileageType,
                           double cash) {
		Vehicle = vehicle;
		MaintElem = maintElem;
		MaintType = maintType;
		FuelAmount = fuelAmount;
		Consumption = consumption;
		Date = date;
		Odometer = odometer;
		Details = details;
		MileageType = mileageType;
		Cash = cash;
	}

	public MaintenanceItem(int key,
                           String vehicle,
                           String maintElem,
                           String maintType,
                           double fuelAmount,
                           double consumption,
                           String date,
                           int odometer,
                           String details,
                           int mileageType,
                           double cash) {
		this.key = key;
		Vehicle = vehicle;
		MaintElem = maintElem;
		MaintType = maintType;
		FuelAmount = fuelAmount;
		Consumption = consumption;
		Date = date;
		Odometer = odometer;
		Details = details;
		MileageType = mileageType;
		Cash = cash;
	}

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getVehicle() {
        return Vehicle;
    }

    public void setVehicle(String vehicle) {
        Vehicle = vehicle;
    }

    public String getMaintElem() {
        return MaintElem;
    }

    public void setMaintElem(String maintElem) {
        MaintElem = maintElem;
    }

    public String getMaintType() {
        return MaintType;
    }

    public void setMaintType(String maintType) {
        MaintType = maintType;
    }

    public double getFuelAmount() {
        return FuelAmount;
    }

    public void setFuelAmount(double fuelAmount) {
        FuelAmount = fuelAmount;
    }

    public double getConsumption() {
        return Consumption;
    }

    public void setConsumption(double consumption) {
        Consumption = consumption;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getOdometer() {
        return Odometer;
    }

    public void setOdometer(int odometer) {
        Odometer = odometer;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }

    public int getMileageType() {
        return MileageType;
    }

    public void setMileageType(int mileageType) {
        MileageType = mileageType;
    }

    public double getCash() {
        return Cash;
    }

    public void setCash(double cash) {
        Cash = cash;
    }
}
