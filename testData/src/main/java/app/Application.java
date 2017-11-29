package app;

import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.ArrayList;

import ais.Vessel;
import ais.VesselContainer;
import app.cpa.CPAPredictor;
import app.datamodel.EvaluationObject;
import app.datamodel.Track;
import input.CSVReader;
import output.CSVWriter;
import output.Encounter;

public class Application {

	public static void main(String[] args) {

		System.out.println("Start: " + new Timestamp(System.currentTimeMillis()));

		ArrayList<Encounter> result = new ArrayList<Encounter>();

		VesselContainer container = new VesselContainer();

		container = CSVReader.readLargeCSV(container);

		cleanTrackList(container);

		System.out.println("Finished Reading in: " + new Timestamp(System.currentTimeMillis()));

		for (Vessel vessel : container.getVesselContainer()) {
			vessel.sortAISMessages();
		}

		System.out.println("Finished Sorting " + new Timestamp(System.currentTimeMillis()));

		Analyzer analyzer = new Analyzer();

		for (int i = 0; i < container.getVesselContainer().size(); i++) {
			if ((container.getVesselContainer().size() - 1) > i) {
				for (int j = i + 1; j < container.getVesselContainer().size(); j++) {
					Encounter res = analyzer.findPairsByTracks(container.getVesselContainer().get(i),
							container.getVesselContainer().get(j));
					if (res != null) {
						result.add(res);
						if (res.getdCPA() > 1.5) {
							System.out.println();
						}
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

	private static void cleanTrackList(VesselContainer container) {
		ArrayList<Track> tracksToRemove = new ArrayList<Track>();
		for (Vessel vessel : container.getVesselContainer()) {
			for (Track track : vessel.getTracks()) {
				long intervalLenght = track.getEndDate().getTime() - track.getStartDate().getTime();
				if (intervalLenght < 1200000) {
					tracksToRemove.add(track);
				}
			}
			vessel.getTracks().removeAll(tracksToRemove);
		}

	}

}