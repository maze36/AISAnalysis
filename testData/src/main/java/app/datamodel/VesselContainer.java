package app.datamodel;

import java.util.ArrayList;

public class VesselContainer {

	private ArrayList<Vessel> vesselContainer;

	public VesselContainer() {
		this.vesselContainer = new ArrayList<Vessel>();
	}

	public void add(Vessel vessel) {
		this.vesselContainer.add(vessel);
	}

	public boolean vesselExists(String mmsi) {
		for (Vessel vessel1 : vesselContainer) {
			if (vessel1.getMmsi().equals(mmsi)) {
				return true;
			}
		}
		return false;
	}

	public Vessel get(String mmsi) {
		for (Vessel vessel1 : vesselContainer) {
			if (vessel1.getMmsi().equals(mmsi)) {
				return vessel1;
			}
		}
		return null;
	}

	public ArrayList<Vessel> getVesselContainer() {
		return this.vesselContainer;
	}

}
