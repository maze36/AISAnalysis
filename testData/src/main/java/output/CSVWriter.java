package output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import app.datamodel.AISMessage;
import datamodel.EvaluationObject;
import datamodel.Track;

public class CSVWriter {

	public void writeTrackCSV(ArrayList<Track> tracks) throws FileNotFoundException {

		System.out.println("Writing track list to csv...");

		PrintWriter pw = new PrintWriter(new File("EvaluationObject.csv"));
		StringBuilder sb = new StringBuilder();
		sb.append("MMSI");
		sb.append(',');
		sb.append("trackId");
		sb.append(',');
		sb.append("cog");
		sb.append(',');
		sb.append("sog");
		sb.append(',');
		sb.append("lat");
		sb.append(',');
		sb.append("lon");
		sb.append(',');
		sb.append("start");
		sb.append(',');
		sb.append("end");
		sb.append('\n');

		for (Track track : tracks) {
			for (AISMessage message : track.getAisMessages()) {
				sb.append(message.getMmsi());
				sb.append(',');
				sb.append(track.getId());
				sb.append(',');
				sb.append(message.getCog());
				sb.append(',');
				sb.append(message.getSog());
				sb.append(',');
				sb.append(message.getLat());
				sb.append(',');
				sb.append(message.getLon());
				sb.append(',');
				sb.append(message.getTimestamp());
				sb.append(',');
				sb.append(track.getStartDate());
				sb.append(',');
				sb.append(track.getEndDate());
				sb.append('\n');
			}
		}

		pw.write(sb.toString());
		pw.close();

	}

	public static void writeTrackCSVArrayList(ArrayList<ArrayList<Track>> tracksWithTwoShips, String nameOfFile)
			throws FileNotFoundException {

		System.out.println("Writing track list to csv...");

		PrintWriter pw = new PrintWriter(new File(nameOfFile));
		StringBuilder sb = new StringBuilder();
		sb.append("MMSI");
		sb.append(',');
		sb.append("trackId");
		sb.append(',');
		sb.append("cog");
		sb.append(',');
		sb.append("sog");
		sb.append(',');
		sb.append("lat");
		sb.append(',');
		sb.append("lon");
		sb.append(',');
		sb.append("timestamp");
		sb.append(',');
		sb.append("length");
		sb.append(',');
		sb.append("start");
		sb.append(',');
		sb.append("end");

		sb.append('\n');

		for (ArrayList<Track> trackWithTwoShips : tracksWithTwoShips) {
			for (int i = 0; i <= 1; i++) {
				for (AISMessage message : trackWithTwoShips.get(i).getAisMessages()) {
					sb.append(message.getMmsi());
					sb.append(',');
					sb.append(trackWithTwoShips.get(i).getId());
					sb.append(',');
					sb.append(message.getCog());
					sb.append(',');
					sb.append(message.getSog());
					sb.append(',');
					sb.append(message.getLat());
					sb.append(',');
					sb.append(message.getLon());
					sb.append(',');
					sb.append(message.getTimestamp());
					sb.append(',');
					sb.append(trackWithTwoShips.get(i).getLength());
					sb.append(',');
					sb.append(trackWithTwoShips.get(i).getStartDate());
					sb.append(',');
					sb.append(trackWithTwoShips.get(i).getEndDate());

					sb.append('\n');
				}
			}
		}

		pw.write(sb.toString());
		pw.close();

	}

	public void writeEvaluationCSV(ArrayList<EvaluationObject> evalList) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File("EvaluationObject.csv"));
		StringBuilder sb = new StringBuilder();
		sb.append("latV1");
		sb.append(',');
		sb.append("lonV1");
		sb.append(',');
		sb.append("latV2");
		sb.append(',');
		sb.append("lonV2");
		sb.append(',');
		sb.append("sogV1");
		sb.append(',');
		sb.append("sogV2");
		sb.append(',');
		sb.append("cogV1");
		sb.append(',');
		sb.append("cogV2");
		sb.append(',');
		sb.append("realDCPA");
		sb.append(',');
		sb.append("predictedDCPA");
		sb.append('\n');

		for (EvaluationObject obj : evalList) {

			sb.append(obj.getAisMessage1().getLat());
			sb.append(',');
			sb.append(obj.getAisMessage1().getLon());
			sb.append(',');
			sb.append(obj.getAisMessage2().getLat());
			sb.append(',');
			sb.append(obj.getAisMessage2().getLon());
			sb.append(',');
			sb.append(obj.getAisMessage1().getSog());
			sb.append(',');
			sb.append(obj.getAisMessage2().getSog());
			sb.append(',');
			sb.append(obj.getAisMessage1().getCog());
			sb.append(',');
			sb.append(obj.getAisMessage2().getCog());
			sb.append(',');
			sb.append(obj.getRealDCPA());
			sb.append(',');
			sb.append(obj.getPredictedDCPA());
			sb.append('\n');

		}

		pw.write(sb.toString());
		pw.close();

	}

	public void writeCSV(ArrayList<HashMap<String, AISMessage>> encounters) throws IOException {

		int counter = 1;

		for (HashMap<String, AISMessage> encounter : encounters) {

			PrintWriter pw = new PrintWriter(new File("encounter" + counter + ".csv"));
			StringBuilder sb = new StringBuilder();
			sb.append("mmsi");
			sb.append(',');
			sb.append("heading");
			sb.append(',');
			sb.append("sog");
			sb.append(',');
			sb.append("cog");
			sb.append(',');
			sb.append("rot");
			sb.append(',');
			sb.append("ais");
			sb.append(',');
			sb.append("date");
			sb.append(',');
			sb.append("lat");
			sb.append(',');
			sb.append("lon");
			sb.append('\n');

			AISMessage message1 = encounter.get("Vessel1");
			AISMessage message2 = encounter.get("Vessel2");

			sb.append(message1.getMmsi());
			sb.append(',');
			sb.append(message1.getHeading());
			sb.append(',');
			sb.append(message1.getSog());
			sb.append(',');
			sb.append(message1.getCog());
			sb.append(',');
			sb.append(message1.getRot());
			sb.append(',');
			sb.append("AIS");
			sb.append(',');
			sb.append(message1.getTimestamp());
			sb.append(',');
			sb.append(message1.getLat());
			sb.append(',');
			sb.append(message1.getLon());
			sb.append('\n');

			sb.append(message2.getMmsi());
			sb.append(',');
			sb.append(message2.getHeading());
			sb.append(',');
			sb.append(message2.getSog());
			sb.append(',');
			sb.append(message2.getCog());
			sb.append(',');
			sb.append(message2.getRot());
			sb.append(',');
			sb.append("AIS");
			sb.append(',');
			sb.append(message2.getTimestamp());
			sb.append(',');
			sb.append(message2.getLat());
			sb.append(',');
			sb.append(message2.getLon());
			sb.append('\n');

			pw.write(sb.toString());
			pw.close();

			counter++;

		}
	}

	public void writeObjs(ArrayList<Encounter> result) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File("EvaluationObject1.csv"));
		StringBuilder sb = new StringBuilder();
		sb.append("mmsiV1");
		sb.append(',');
		sb.append("latV1");
		sb.append(',');
		sb.append("lonV1");
		sb.append(',');
		sb.append("mmsiV2");
		sb.append(',');
		sb.append("latV2");
		sb.append(',');
		sb.append("lonV2");
		sb.append(',');
		sb.append("DCPA");
		sb.append('\n');

		for (Encounter obj : result) {
			sb.append(obj.getAisMessage1().getMmsi());
			sb.append(',');
			sb.append(obj.getAisMessage1().getLat());
			sb.append(',');
			sb.append(obj.getAisMessage1().getLon());
			sb.append(',');
			sb.append(obj.getAisMessage2().getMmsi());
			sb.append(',');
			sb.append(obj.getAisMessage2().getLat());
			sb.append(',');
			sb.append(obj.getAisMessage2().getLon());
			sb.append(',');
			sb.append(obj.getdCPA());
			sb.append('\n');

		}

		pw.write(sb.toString());
		pw.close();

	}
}
