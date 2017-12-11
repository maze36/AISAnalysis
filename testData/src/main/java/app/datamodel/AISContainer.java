package app.datamodel;

import java.util.ArrayList;

public class AISContainer {

	private ArrayList<AISMessage> aisContainer;

	public AISContainer() {
		this.aisContainer = new ArrayList<AISMessage>();
	}

	public void get(int index) {
		this.aisContainer.get(index);
	}

	public void add(AISMessage message) {
		this.aisContainer.add(message);
	}

	public ArrayList<AISMessage> getAISMessages() {
		return this.aisContainer;
	}

}
