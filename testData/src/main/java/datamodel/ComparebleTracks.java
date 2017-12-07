package datamodel;

import app.datamodel.Vessel;

public class ComparebleTracks {

	private Vessel vessel1;
	private Vessel vessel2;

	private Track trackToCompareV1;
	private Track trackToCompareV2;

	public ComparebleTracks(Vessel vessel1, Vessel vessel2, Track trackToCompareV1, Track trackToCompareV2) {
		this.vessel1 = vessel1;
		this.vessel2 = vessel2;
		this.trackToCompareV1 = trackToCompareV1;
		this.trackToCompareV2 = trackToCompareV2;
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

}
