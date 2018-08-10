package clientevents;

import java.util.Observable;
import java.util.Observer;

public class ClientEventObserver implements Observer{
	@Override
	public void update(Observable o, Object arg) {
		System.out.println("Broadcast detected");
	}
}
