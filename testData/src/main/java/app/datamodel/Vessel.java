package app.datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import datamodel.Track;

public class Vessel {

	private String mmsi;

	private ArrayList<Track> tracks;
	private ArrayList<AISMessage> aisMessages;
	private double length;
	private String shipType;

	public Vessel() {
		this.aisMessages = new ArrayList<AISMessage>();
		this.setTracks(new ArrayList<Track>());
	}

	public String getMmsi() {
		return mmsi;
	}

	/**
	 * Adds a the {@link AISMessage} to the existing tracks. If there are not
	 * existing any tracks yet, a new will be created.
	 * 
	 * @param message
	 *            The {@link AISMessage} to be added.
	 * @return
	 */
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

	public ArrayList<AISMessage> getAisMessages() {
		return this.aisMessages;
	}

	public void setMmsi(String mmsi) {
		this.mmsi = mmsi;
	}

	public void setAisMessages(ArrayList<AISMessage> aisMessages) {
		this.aisMessages = aisMessages;
	}

	public void sortAISMessages() {
		Collections.sort(aisMessages, new Comparator<AISMessage>() {
			public int compare(AISMessage o1, AISMessage o2) {
				return o1.getTimestamp().compareTo(o2.getTimestamp());
			}
		});
	}

	public boolean addAISMessage(AISMessage aisMessage) {
		return this.aisMessages.add(aisMessage);
	}

	public ArrayList<Track> getTracks() {
		return tracks;
	}

	public void setTracks(ArrayList<Track> tracks) {
		this.tracks = tracks;
	}

	public double getLength() {
		return length;
	}

	public String getShipType() {
		return shipType;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public void setShipType(String shipType) {
		this.shipType = shipType;
	}

}
