package datamodel;

import java.util.ArrayList;

import app.datamodel.Vessel;

public class CompareableTracksContainer {

	private ArrayList<CompareableTracks> compareableTracks;
	private Vessel vessel;

	public CompareableTracksContainer() {
		this.compareableTracks = new ArrayList<CompareableTracks>();
	}

	public boolean addCompareableTracks(CompareableTracks compareableTracks) {
		return this.compareableTracks.add(compareableTracks);
	}

	public ArrayList<CompareableTracks> getCompareableTracks() {
		return this.compareableTracks;
	}

	public Vessel getVessel() {
		return this.vessel;
	}

}
