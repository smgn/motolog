package events;

import android.os.Bundle;

public class ReloadMainLogEvent {
    private Bundle bundle;
    public ReloadMainLogEvent(Bundle bundle) {
        this.bundle = bundle;
    }

	public Bundle getBundle() {
		return bundle;
	}
}
