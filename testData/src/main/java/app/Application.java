package app;

import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.ArrayList;

import analyzer.Algorithm;
import analyzer.Analyzer;
import analyzer.cpa.CPAPredictor;
import app.datamodel.AISMessage;
import app.datamodel.Vessel;
import app.datamodel.VesselContainer;
import datamodel.CompareableTracksContainer;
import datamodel.EvaluationObject;
import datamodel.Track;
import input.CSVReader;
import output.CSVWriter;
import output.Encounter;

public class Application {

	public static void main(String[] args) {

		System.out.println("Start: " + new Timestamp(System.currentTimeMillis()));

		ArrayList<Encounter> result = new ArrayList<Encounter>();

		VesselContainer container = fillVesselContainer();
		countTracksAndAISMessages(container);

		System.out.println(container.getVesselContainer().size());

		Analyzer analyzer = new Analyzer();

		runAlgorithm(container);

		for (int i = 0; i < container.getVesselContainer().size(); i++) {
			if ((container.getVesselContainer().size() - 1) > i) {
				for (int j = i + 1; j < container.getVesselContainer().size(); j++) {
					Encounter res = analyzer.findPairsByTracks(container.getVesselContainer().get(i),
							container.getVesselContainer().get(j));
					if (res != null) {
						result.add(res);
					}
				}
			}
		}

		ArrayList<EvaluationObject> evalList = new ArrayList<EvaluationObject>();
		CPAPredictor cpaPred = new CPAPredictor();

		for (Encounter encounter : result) {
			EvaluationObject evalObj = cpaPred.predictAndCompareCPA(encounter);
			if (evalObj != null) {
				evalList.add(evalObj);
			}
		}

		System.out.println("Finished predicting CPA" + new Timestamp(System.currentTimeMillis()));

		CSVWriter writer = new CSVWriter();
		try {
			// writer.writeEvaluationCSV(evalList);
			writer.writeEvaluationCSV(evalList);
			System.out.println("Finished" + new Timestamp(System.currentTimeMillis()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void runAlgorithm(VesselContainer vesselContainer) {
		Algorithm algorithm = new Algorithm();
		CompareableTracksContainer compTracksContainer = new CompareableTracksContainer();
		for (int i = 0; i < vesselContainer.getVesselContainer().size(); i++) {
			if ((vesselContainer.getVesselContainer().size() - 1) > i) {
				for (int j = i + 1; j < vesselContainer.getVesselContainer().size(); j++) {
					compTracksContainer.addCompareableTracks(algorithm.findCompareableTracks(
							vesselContainer.getVesselContainer().get(i), vesselContainer.getVesselContainer().get(j)));
				}
			}
		}

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

	/**
	 * Reads the AIS message in and returns the {@link VesselContainer} with
	 * sorted {@link AISMessage}.
	 * 
	 * @return {@link VesselContainer}
	 */
	private static VesselContainer fillVesselContainer() {

		VesselContainer container = CSVReader.readVoyageData();
		container = CSVReader.readDynamicData(container);
		cleanTrackList(container);

		for (Vessel vessel : container.getVesselContainer()) {
			vessel.sortAISMessages();
		}

		return container;

	}

	private static void countTracksAndAISMessages(VesselContainer container) {

		int tracks = 0;
		int aisMessages = 0;

		for (Vessel vessel : container.getVesselContainer()) {
			tracks += vessel.getTracks().size();
			aisMessages += vessel.getAisMessagesUnsorted().size();
		}

		System.out.println("Tracks " + tracks + " AISMessages " + aisMessages);
	}

}
