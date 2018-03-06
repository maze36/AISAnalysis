package app;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import com.vividsolutions.jts.geom.Coordinate;

import analyzer.Analyzer;
import analyzer.cpa.CPACalculator;
import app.datamodel.AISContainer;
import app.datamodel.AISMessage;
import app.datamodel.Vessel;
import app.datamodel.VesselContainer;
import datamodel.CPAResult;
import datamodel.Track;
import input.CSVReader;
import util.Util;

public class TwoShipEncounters {

	public static void main(String[] args) {
		System.out.println("App start: " + new Timestamp(System.currentTimeMillis()));

		ArrayList<Track> tracks = CSVReader.createTracksNew();
		System.out.println("Creating tracks finished.");
		tracks = cleanTrackListNew(tracks, 1.0);
	
		ArrayList<Track> finalTracks = CSVReader.appendStaticAISData(tracks);
		Analyzer analyzer = new Analyzer();
		System.out.println("Cleaning finished.");
		ArrayList<ArrayList<Track>> tracksWithTwoShips164 = analyzer
				.extractTwoShipsWithInfluenceLength(finalTracks, 80, 400, 15, 3, 0.1, 3);
		
		
		System.out.println("App stop: " + new Timestamp(System.currentTimeMillis()));
	}

	/**
	 * 
	 * @param containers
	 */
	private static void findAISInRange(HashMap<String, Object> containers) {
		AISContainer aisContainer = (AISContainer) containers.get("AISContainer");
		VesselContainer vesselContainer = (VesselContainer) containers.get("VesselContainer");
		ArrayList<CPAResult> otherAISMessageInRange = new ArrayList<CPAResult>();
		for (Vessel vessel : vesselContainer.getVesselContainer()) {
			for (Track track : vessel.getTracks()) {
				AISMessage ais = track.getAisMessages().get(0);
				CPAResult cpa = findOtherAISMessageInRange(ais, aisContainer);
				if (cpa != null) {
					otherAISMessageInRange.add(cpa);
				}
			}
		}

	}

	private static CPAResult findOtherAISMessageInRange(AISMessage ais, AISContainer aisContainer) {

		CPAResult minTCPAResult = null;

		for (AISMessage otherMessage : aisContainer.getAISMessages()) {
			if (!otherMessage.getMmsi().equals(ais.getMmsi())) {

				long timestamp1 = otherMessage.getTimestamp().getTime();
				long timestamp2 = otherMessage.getTimestamp().getTime();

				long diff = Math.abs(timestamp2 - timestamp1);

				if (diff <= 10000) {
					Coordinate start = new Coordinate(otherMessage.getLat(), otherMessage.getLon());
					Coordinate end = new Coordinate(ais.getLat(), ais.getLon());
					double distance = Util.calculateDistanceNM(start, end);

					if (distance < 2) {
						CPAResult cpaResult = CPACalculator.calculateCPA(ais, otherMessage);

						if (minTCPAResult == null) {
							minTCPAResult = cpaResult;

							// neues Schiff �berpr�fen
							if (otherShipIsCloser(otherMessage, cpaResult, aisContainer)) {

							}
						} else {
							if (cpaResult.getCpaTime() < minTCPAResult.getCpaTime()) {
								minTCPAResult = cpaResult;
							}
						}
					}

				}

			}
		}
		return minTCPAResult;
	}

	private static boolean otherShipIsCloser(AISMessage ais, CPAResult cpaResult, AISContainer aisContainer) {

		for (AISMessage otherMessage : aisContainer.getAISMessages()) {

		}

		return false;
	}

	private static ArrayList<Track> cleanTrackList(ArrayList<Track> tracks) {
		ArrayList<Track> tracksToRemove = new ArrayList<Track>();
		for (Track track : tracks) {
			long intervalLenght = track.getEndDate().getTime() - track.getStartDate().getTime();
			if (intervalLenght < 120000) {
				tracksToRemove.add(track);
			}
		}
		tracks.removeAll(tracksToRemove);
		return tracks;
	}
	
	private static ArrayList<Track> cleanTrackListNew(ArrayList<Track> tracks, double minLengthTrack) {
		ArrayList<Track> tracksToRemove = new ArrayList<Track>();
		for (Track track : tracks) {
			long intervalLenght = track.getEndDate().getTime() - track.getStartDate().getTime();
			double distStartEndTrack;
			AISMessage aisMessageStart = track.getAisMessages().get(0);
			AISMessage aisMessageEnd = track.getAisMessages().get(track.getAisMessages().size()-1);
			Coordinate start = new Coordinate(aisMessageStart.getLat(),aisMessageStart.getLon());
			Coordinate end = new Coordinate(aisMessageEnd.getLat(), aisMessageEnd.getLon());
			distStartEndTrack = Util.calculateDistanceNM(start, end);
			if (intervalLenght < 120000 && distStartEndTrack < minLengthTrack) {
				tracksToRemove.add(track);
			}
		}
		tracks.removeAll(tracksToRemove);
		return tracks;
	}
	

}
