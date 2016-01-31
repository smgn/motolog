package events;

public class CopyDatabaseEvent {

	public String fromDbPath;
	public String toDbPath;

	public CopyDatabaseEvent(String fromDbPath, String toDbPath) {
		this.fromDbPath = fromDbPath;
		this.toDbPath = toDbPath;
	}
}
