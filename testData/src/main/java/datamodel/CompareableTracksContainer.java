package datamodel;

import java.util.ArrayList;

public class CompareableTracksContainer {

	private ArrayList<ComparebleTracks> compareableTracks;

	public CompareableTracksContainer() {
		this.compareableTracks = new ArrayList<ComparebleTracks>();
	}

	public boolean addCompareableTracks(ComparebleTracks compareableTracks) {
		return this.compareableTracks.add(compareableTracks);
	}

	public ArrayList<ComparebleTracks> getCompareableTracks() {
		return this.compareableTracks;
	}

}
