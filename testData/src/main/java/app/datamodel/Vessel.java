package app.datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Vessel {

	private String mmsi;

	private ArrayList<Track> tracks;
	private ArrayList<AISMessage> aisMessagesUnsorted;
	private ArrayList<AISMessage> aisMessagesSorted;

	public Vessel() {
		this.aisMessagesUnsorted = new ArrayList<AISMessage>();
		this.setTracks(new ArrayList<Track>());
	}

	public String getMmsi() {
		return mmsi;
	}

	public boolean addTrack(AISMessage message) {
		for (Track track : tracks) {
			long timeLastMsg = track.getAisMessages().get(track.getAisMessages().size() - 1).getTimestamp().getTime();
			long timeDiff = message.getTimestamp().getTime() - timeLastMsg;
			if (timeDiff <= 150000) {
				track.getAisMessages().add(message);
				track.setEndDate(message.getTimestamp());
				return true;
			}
		}

		Track track = new Track(message);
		track.setEndDate(message.getTimestamp());
		track.setId(tracks.size() - 1);
		this.tracks.add(track);
		return false;
	}

	public ArrayList<AISMessage> getAisMessagesUnsorted() {
		return this.aisMessagesUnsorted;
	}

	public void setMmsi(String mmsi) {
		this.mmsi = mmsi;
	}

	public void setAisMessages(ArrayList<AISMessage> aisMessages) {
		this.aisMessagesUnsorted = aisMessages;
	}

	public ArrayList<AISMessage> getAisMessagesSorted() {
		return aisMessagesSorted;
	}

	public void setAisMessagesSorted(ArrayList<AISMessage> aisMessagesSorted) {
		this.aisMessagesSorted = aisMessagesSorted;
	}

	public void sortAISMessages() {
		Collections.sort(aisMessagesUnsorted, new Comparator<AISMessage>() {
			public int compare(AISMessage o1, AISMessage o2) {
				return o1.getTimestamp().compareTo(o2.getTimestamp());
			}
		});
	}

	public ArrayList<Track> getTracks() {
		return tracks;
	}

	public void setTracks(ArrayList<Track> tracks) {
		this.tracks = tracks;
	}

}
