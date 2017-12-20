package app;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import com.vividsolutions.jts.geom.Coordinate;

import analyzer.Algorithm;
import analyzer.Analyzer;
import analyzer.cpa.CPACalculator;
import app.datamodel.AISContainer;
import app.datamodel.AISMessage;
import app.datamodel.Vessel;
import app.datamodel.VesselContainer;
import datamodel.CPAResult;
import datamodel.CompareableTracksContainer;
import datamodel.Track;
import gui.GUI;
import input.CSVReader;
import output.Encounter;
import util.Util;

public class Application {

	public static void main(String[] args) {

		GUI gui = new GUI();

		gui.startGUI();

		System.out.println("Start: " + new Timestamp(System.currentTimeMillis()));

		ArrayList<Encounter> result = new ArrayList<Encounter>();

		HashMap<String, Object> containers = CSVReader.readCSV();

		VesselContainer vesselContainer = (VesselContainer) containers.get("VesselContainer");
		AISContainer aisContainer = (AISContainer) containers.get("AISContainer");

		cleanTrackList(vesselContainer);

		Analyzer analyzer = new Analyzer();

		runAlgorithm(containers);
	}

	private static void runAlgorithm(HashMap<String, Object> containers) {
		Algorithm algorithm = new Algorithm();
		CompareableTracksContainer compTracksContainer = new CompareableTracksContainer();

		findAISInRange(containers);

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

	private static void cleanTrackList(VesselContainer container) {
		ArrayList<Track> tracksToRemove = new ArrayList<Track>();
		ArrayList<Vessel> vesselsToRemove = new ArrayList<Vessel>();
		for (Vessel vessel : container.getVesselContainer()) {
			for (Track track : vessel.getTracks()) {
				long intervalLenght = track.getEndDate().getTime() - track.getStartDate().getTime();
				if (intervalLenght < 1200000) {
					tracksToRemove.add(track);
				}
			}
			vessel.getTracks().removeAll(tracksToRemove);
			if (vessel.getTracks().isEmpty()) {
				vesselsToRemove.add(vessel);
			}
		}

		container.getVesselContainer().removeAll(vesselsToRemove);

	}

}
