package output;

import app.datamodel.AISMessage;
import app.datamodel.Vessel;
import datamodel.Situation;
import datamodel.Track;

public class Encounter {

	private Vessel vessel1;
	private Vessel vessel2;

	private AISMessage aisMessage1;
	private AISMessage aisMessage2;

	private Track trackVessel1;
	private Track trackVessel2;

	private Situation situation;

	public Track getTrackVessel1() {
		return trackVessel1;
	}

	public void setTrackVessel1(Track trackVessel1) {
		this.trackVessel1 = trackVessel1;
	}

	public Track getTrackVessel2() {
		return trackVessel2;
	}

	public void setTrackVessel2(Track trackVessel2) {
		this.trackVessel2 = trackVessel2;
	}

	private double dCPA;

	public Encounter(Vessel vessel1, Vessel vessel2, AISMessage aisMessage1, AISMessage aisMessage2, double dCPA,
			Situation situation) {
		this.vessel1 = vessel1;
		this.vessel2 = vessel2;
		this.aisMessage1 = aisMessage1;
		this.aisMessage2 = aisMessage2;
		this.dCPA = dCPA;
		this.situation = situation;
	}

	public Encounter() {
	}

	public void setValues(Vessel vessel1, Vessel vessel2, AISMessage aisMessage1, AISMessage aisMessage2, double dCPA) {
		this.vessel1 = vessel1;
		this.vessel2 = vessel2;
		this.aisMessage1 = aisMessage1;
		this.aisMessage2 = aisMessage2;
		this.dCPA = dCPA;
	}

	public Vessel getVessel1() {
		return vessel1;
	}

	public void setVessel1(Vessel vessel1) {
		this.vessel1 = vessel1;
	}

	public Vessel getVessel2() {
		return vessel2;
	}

	public void setVessel2(Vessel vessel2) {
		this.vessel2 = vessel2;
	}

	public AISMessage getAisMessage1() {
		return aisMessage1;
	}

	public void setAisMessage1(AISMessage aisMessage1) {
		this.aisMessage1 = aisMessage1;
	}

	public AISMessage getAisMessage2() {
		return aisMessage2;
	}

	public void setAisMessage2(AISMessage aisMessage2) {
		this.aisMessage2 = aisMessage2;
	}

	public double getdCPA() {
		return dCPA;
	}

	public void setdCPA(double dCPA) {
		this.dCPA = dCPA;
	}

	public Situation getSituation() {
		return situation;
	}

	public void setSituation(Situation situation) {
		this.situation = situation;
	}

}
