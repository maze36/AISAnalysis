package output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import ais.AISMessage;
import app.datamodel.EvaluationObject;

public class CSVWriter {

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
		sb.append(',');
		sb.append("deviationV1");
		sb.append(',');
		sb.append("deviationV2");
		sb.append(',');
		sb.append("situation");
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
			sb.append(obj.getRealDCPA());
			sb.append(',');
			sb.append(obj.getPredictedDCPA());
			sb.append(',');
			sb.append(obj.getDeviationV1());
			sb.append(',');
			sb.append(obj.getDeviationV2());
			sb.append(',');
			sb.append(obj.getSituation());
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
