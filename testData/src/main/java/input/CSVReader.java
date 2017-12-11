package input;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import app.datamodel.AISContainer;
import app.datamodel.AISMessage;
import app.datamodel.Vessel;
import app.datamodel.VesselContainer;
import datamodel.Track;

public class CSVReader {

	private final static String CSV_LOCATION_LARGE = "C:/Users/msteidel/Desktop/largeFile.csv";
	private final static String CSV_LOCATION_SMALL = "C:/Users/msteidel/Desktop/testData.csv";
	private final static String CSV_LOCATION_DYNAMIC = "C:/Users/msteidel/Desktop/dynamicData.csv";
	private final static String CSV_LOCATION_VOYAGE = "C:/Users/msteidel/Desktop/voyageData.csv";
	private final static String CSV_LOCATION_DATA = "C:/Users/msteidel/Desktop/data.csv";
	private static String LINE = "";
	private final static String SPLITTER = ",";

	public static VesselContainer readLargeCSV(VesselContainer container) {

		try {
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(CSV_LOCATION_LARGE));

			while ((LINE = reader.readLine()) != null) {
				String[] aisMessage = LINE.split(SPLITTER);
				AISMessage message = new AISMessage();
				message.setMmsi(aisMessage[0].replaceAll("\"", ""));
				double lat = Double.valueOf(aisMessage[4]);
				String lon3 = aisMessage[5].replaceAll("\"", "");
				double lon = Double.valueOf(lon3);

				if (container.vesselExists(message.getMmsi())) {

					message.setSog(Double.valueOf(aisMessage[1]));
					message.setCog(Double.valueOf(aisMessage[2]));
					message.setTimestamp(transformDate(aisMessage[3]));

					message.setLat(lat);
					message.setLon(lon);
					for (Vessel vessel : container.getVesselContainer()) {
						if (vessel.getMmsi().equals(message.getMmsi())) {
							vessel.addTrack(message);
							break;
						}
					}
					container.get(message.getMmsi()).getAisMessages().add(message);

				} else {
					Vessel vessel = new Vessel();
					vessel.setMmsi(message.getMmsi());
					message.setSog(Double.valueOf(aisMessage[1]));
					message.setCog(Double.valueOf(aisMessage[2]));
					message.setTimestamp(transformDate(aisMessage[3]));
					message.setLat(lat);
					message.setLon(lon);
					Track track = new Track(message);
					track.setId(0);
					track.setEndDate(message.getTimestamp());
					vessel.getTracks().add(track);
					container.add(vessel);
				}
			}

			return container;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static HashMap<String, Object> readCSV() {

		VesselContainer container = new VesselContainer();
		AISContainer aisContainer = new AISContainer();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(CSV_LOCATION_DATA));

			while ((LINE = reader.readLine()) != null) {
				String[] aisString = LINE.split(SPLITTER);
				AISMessage aisObject = new AISMessage();

				aisObject.setCog(Double.valueOf(aisString[3]));
				aisObject.setHeading(Double.valueOf(aisString[1]));
				aisObject.setLat(Double.valueOf(aisString[7]));
				aisObject.setLon(Double.valueOf(aisString[8]));
				aisObject.setMmsi(aisString[0]);
				aisObject.setRot(Double.valueOf(aisString[4]));
				aisObject.setSog(Double.valueOf(aisString[2]));
				aisObject.setTimestamp(transformDate(aisString[6]));
				aisContainer.add(aisObject);
				if (!container.vesselExists(aisObject.getMmsi())) {
					Vessel vessel = new Vessel();
					vessel.setMmsi(aisString[0]);
					vessel.setLength(Double.valueOf(aisString[9]));
					vessel.setShipType(aisString[11]);
					vessel.addTrack(aisObject);
					container.add(vessel);
				} else {
					for (Vessel vessel : container.getVesselContainer()) {
						if (vessel.getMmsi().equals(aisObject.getMmsi())) {
							vessel.addAISMessage(aisObject);
							vessel.addTrack(aisObject);
						}
					}
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("AISContainer", aisContainer);
		result.put("VesselContainer", container);

		return result;
	}

	public static VesselContainer readDynamicData(VesselContainer container) {
		try {
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(CSV_LOCATION_DYNAMIC));

			while ((LINE = reader.readLine()) != null) {
				String[] aisMessage = LINE.split(SPLITTER);
				AISMessage message = new AISMessage();

				if (!aisMessage[0].replaceAll("\"", "").equals("mmsi")) {

					message.setMmsi(aisMessage[0].replaceAll("\"", ""));
					double lat = Double.valueOf(aisMessage[7]);
					String lon3 = aisMessage[8].replaceAll("\"", "");
					double lon = Double.valueOf(lon3);

					if (container.vesselExists(message.getMmsi())) {

						message.setSog(Double.valueOf(aisMessage[2]));
						message.setCog(Double.valueOf(aisMessage[3]));
						message.setTimestamp(transformDate(aisMessage[6]));

						message.setLat(lat);
						message.setLon(lon);
						for (Vessel vessel : container.getVesselContainer()) {
							if (vessel.getMmsi().equals(message.getMmsi())) {
								vessel.addTrack(message);
								break;
							}
						}
						container.get(message.getMmsi()).getAisMessages().add(message);

					} else {
						Vessel vessel = new Vessel();
						vessel.setMmsi(message.getMmsi());
						message.setSog(Double.valueOf(aisMessage[2]));
						message.setCog(Double.valueOf(aisMessage[3]));
						message.setTimestamp(transformDate(aisMessage[6]));
						message.setLat(lat);
						message.setLon(lon);
						Track track = new Track(message);
						track.setId(0);
						track.setEndDate(message.getTimestamp());
						vessel.getTracks().add(track);
						container.add(vessel);
					}
				}
			}

			return container;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static VesselContainer readVoyageData() {

		VesselContainer container = new VesselContainer();

		try {
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(CSV_LOCATION_VOYAGE));

			while ((LINE = reader.readLine()) != null) {
				String[] aisMessage = LINE.split(SPLITTER);
				String mmsi = aisMessage[1].replaceAll("\"", "");

				if (!mmsi.equals("mmsi")) {

					if (!container.vesselExists(mmsi)) {
						Vessel vessel = new Vessel();
						vessel.setMmsi(mmsi);
						vessel.setLength(Double.valueOf(aisMessage[2]));
						vessel.setShipType(aisMessage[5]);
						container.add(vessel);
					}
				}
			}
			return container;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static VesselContainer readSmallCSV(VesselContainer container) {

		try {
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(CSV_LOCATION_SMALL));

			while ((LINE = reader.readLine()) != null) {
				String[] aisMessage = LINE.split(SPLITTER);

				if (aisMessage.length > 0) {

					AISMessage message = new AISMessage();
					message.setMmsi(aisMessage[0]);
					double lat = Double.valueOf(aisMessage[7]);
					double lon = Double.valueOf(aisMessage[8]);

					if (container.vesselExists(message.getMmsi())) {
						message.setSog(Double.valueOf(aisMessage[2]));
						message.setCog(Double.valueOf(aisMessage[1]));
						message.setTimestamp(transformDate(aisMessage[6]));

						message.setLat(lat);
						message.setLon(lon);

						container.get(message.getMmsi()).getAisMessages().add(message);

					} else {
						Vessel vessel = new Vessel();
						vessel.setMmsi(message.getMmsi());
						message.setSog(Double.valueOf(aisMessage[2]));
						message.setCog(Double.valueOf(aisMessage[1]));
						message.setTimestamp(transformDate(aisMessage[6]));
						message.setLat(lat);
						message.setLon(lon);
						container.add(vessel);
					}
				}

				return container;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static Date transformDate(String timestampString) {

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);

		String newDateString = timestampString.replace(timestampString.charAt(0), ' ');
		newDateString = newDateString.replaceAll(" ", "");

		try {
			Date result = df.parse(newDateString);
			return result;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
