package datamodel;

import app.datamodel.AISMessage;
import app.datamodel.Vessel;

public class CompareableTracks {

	private Vessel vessel1;
	private Vessel vessel2;

	private Track trackToCompareV1;
	private Track trackToCompareV2;

	private AISMessage aisMessageV1;
	private AISMessage aisMessageV2;

	private double distance;

	public CompareableTracks(Vessel vessel1, Vessel vessel2, Track trackToCompareV1, Track trackToCompareV2,
			AISMessage aisMessageV1, AISMessage aisMessageV2, double distance) {
		this.vessel1 = vessel1;
		this.vessel2 = vessel2;
		this.trackToCompareV1 = trackToCompareV1;
		this.trackToCompareV2 = trackToCompareV2;
		this.aisMessageV1 = aisMessageV1;
		this.aisMessageV2 = aisMessageV2;
		this.distance = distance;
	}

	public Vessel getVessel1() {
		return vessel1;
	}

	public Vessel getVessel2() {
		return vessel2;
	}

	public Track getTrackToCompareV1() {
		return trackToCompareV1;
	}

	public Track getTrackToCompareV2() {
		return trackToCompareV2;
	}

	public void setVessel1(Vessel vessel1) {
		this.vessel1 = vessel1;
	}

	public void setVessel2(Vessel vessel2) {
		this.vessel2 = vessel2;
	}

	public void setTrackToCompareV1(Track trackToCompareV1) {
		this.trackToCompareV1 = trackToCompareV1;
	}

	public void setTrackToCompareV2(Track trackToCompareV2) {
		this.trackToCompareV2 = trackToCompareV2;
	}

	public AISMessage getAisMessageV1() {
		return aisMessageV1;
	}

	public AISMessage getAisMessageV2() {
		return aisMessageV2;
	}

	public void setAisMessageV1(AISMessage aisMessageV1) {
		this.aisMessageV1 = aisMessageV1;
	}

	public void setAisMessageV2(AISMessage aisMessageV2) {
		this.aisMessageV2 = aisMessageV2;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

}
