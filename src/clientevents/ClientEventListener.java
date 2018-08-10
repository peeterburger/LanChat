package clientevents;

public interface ClientEventListener {
	void onBroadcastRecived();
	void onTimeout();
}
