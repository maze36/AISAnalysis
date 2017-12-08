package datamodel;

import java.util.ArrayList;

public class CompareableTracksContainer {

	private ArrayList<CompareableTracks> compareableTracks;

	public CompareableTracksContainer() {
		this.compareableTracks = new ArrayList<CompareableTracks>();
	}

	public boolean addCompareableTracks(CompareableTracks compareableTracks) {
		return this.compareableTracks.add(compareableTracks);
	}

	public ArrayList<CompareableTracks> getCompareableTracks() {
		return this.compareableTracks;
	}

}
