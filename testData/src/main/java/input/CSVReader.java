package input;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
	private final static String CSV_LOCATION_VOYAGE = "C:/Users/lsiegel/Documents/AIS-Daten static und dynamic/staticData.csv";
	private final static String CSV_LOCATION_DATA = "C:/Users/lsiegel/Documents/AIS-Daten static und dynamic/Eval Files/dynamicData3First15mioRegion1.csv";
	private static String LINE = "";
	private final static String SPLITTER = ",";

	public static VesselContainer readLargeCSV(VesselContainer container) {

		try {
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(CSV_LOCATION_LARGE));

			while ((LINE = reader.readLine()) != null) {
				String[] aisMessage = LINE.split(SPLITTER);
				AISMessage message = new AISMessage();

				if (Double.valueOf(aisMessage[4]) < 90 && Double.valueOf(aisMessage[4]) > -90
						&& Double.valueOf(aisMessage[5].replaceAll("\"", "")) < 90
						&& Double.valueOf(aisMessage[5].replaceAll("\"", "")) > -90) {
					double lat = Double.valueOf(aisMessage[4]);
					String lon3 = aisMessage[5].replaceAll("\"", "");
					double lon = Double.valueOf(lon3);
					message.setMmsi(aisMessage[0].replaceAll("\"", ""));
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

	public static ArrayList<Track> createTracksNew() {
		System.out.println("Creating tracks...");

		ArrayList<Track> trackList = new ArrayList<Track>();

		int id = 0;

		try {
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(CSV_LOCATION_DATA));
			int lineNumber = 1;
			int lineCount = 0;
			while ((reader.readLine()) != null) {
				lineCount++;
			}
			// reader.close();
			reader = new BufferedReader(new FileReader(CSV_LOCATION_DATA));
			while ((LINE = reader.readLine()) != null) {
				System.out.println("Done: " + (double) lineNumber / lineCount);
				lineNumber++;
				String[] aisString = LINE.split(SPLITTER);
				AISMessage aisMessage = new AISMessage();

				/*
				 * aisMessage.setCog(Double.valueOf(aisString[3]));
				 * aisMessage.setHeading(Double.valueOf(aisString[1]));
				 * aisMessage.setLat(Double.valueOf(aisString[7]));
				 * aisMessage.setLon(Double.valueOf(aisString[8]));
				 * aisMessage.setMmsi(aisString[0].replaceAll("\"", ""));
				 * aisMessage.setRot(Double.valueOf(aisString[4]));
				 * aisMessage.setSog(Double.valueOf(aisString[2]));
				 * aisMessage.setTimestamp(transformDate(aisString[6]));
				 */

				aisMessage.setCog(Double.valueOf(aisString[3]));
				aisMessage.setHeading(Double.valueOf(aisString[1]));
				double lat = Double.valueOf(aisString[7]);
				double lon = Double.valueOf(aisString[8]);

				if (lat < -90 || lat > 90 || lon < -90 || lon > 90) {
					continue;
				}

				aisMessage.setLat(Double.valueOf(aisString[7]));
				aisMessage.setLon(Double.valueOf(aisString[8]));
				aisMessage.setMmsi(aisString[0].replaceAll("\"", ""));
				aisMessage.setRot(Double.valueOf(aisString[4]));
				aisMessage.setSog(Double.valueOf(aisString[2]));
				aisMessage.setTimestamp(transformDate(aisString[6]));

				if (trackList.isEmpty()) {
					Track track = new Track(aisMessage);
					track.setMmsi(aisMessage.getMmsi());
					track.setId(id);
					trackList.add(track);
				} else {
					boolean added = false;
					for (Track track : trackList) {
						if (track.getMmsi().equals(aisMessage.getMmsi())) {
							if (track.addMessage(aisMessage)) {
								added = true;
								break;
							}
						}
					}
					if (!added) {
						Track track = new Track(aisMessage);
						track.setMmsi(aisMessage.getMmsi());
						id += 1;
						track.setId(id);
						trackList.add(track);
					}
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return trackList;
	}

	public static ArrayList<Track> createTracks() {

		System.out.println("Creating tracks...");

		ArrayList<Track> trackList = new ArrayList<Track>();

		int id = 0;

		try {
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(CSV_LOCATION_DATA));
			int lineNumber = 1;
			int lineCount = 0;
			while ((reader.readLine()) != null) {
				lineCount++;
			}
			// reader.close();
			reader = new BufferedReader(new FileReader(CSV_LOCATION_DATA));
			while ((LINE = reader.readLine()) != null) {
				System.out.println("Done: " + (double) lineNumber / lineCount);
				lineNumber++;
				String[] aisString = LINE.split(SPLITTER);
				AISMessage aisMessage = new AISMessage();

				/*
				 * aisMessage.setCog(Double.valueOf(aisString[3]));
				 * aisMessage.setHeading(Double.valueOf(aisString[1]));
				 * aisMessage.setLat(Double.valueOf(aisString[7]));
				 * aisMessage.setLon(Double.valueOf(aisString[8]));
				 * aisMessage.setMmsi(aisString[0].replaceAll("\"", ""));
				 * aisMessage.setRot(Double.valueOf(aisString[4]));
				 * aisMessage.setSog(Double.valueOf(aisString[2]));
				 * aisMessage.setTimestamp(transformDate(aisString[6]));
				 */

				aisMessage.setCog(Double.valueOf(aisString[5]));
				aisMessage.setHeading(Double.valueOf(aisString[3]));
				double lat = Double.valueOf(aisString[9]);
				double lon = Double.valueOf(aisString[10]);

				if (lat < -90 || lat > 90 || lon < -90 || lon > 90) {
					continue;
				}

				aisMessage.setLat(Double.valueOf(aisString[9]));
				aisMessage.setLon(Double.valueOf(aisString[10]));
				aisMessage.setMmsi(aisString[2].replaceAll("\"", ""));
				aisMessage.setRot(Double.valueOf(aisString[6]));
				aisMessage.setSog(Double.valueOf(aisString[4]));
				aisMessage.setTimestamp(transformDate(aisString[8]));

				if (trackList.isEmpty()) {
					Track track = new Track(aisMessage);
					track.setMmsi(aisMessage.getMmsi());
					track.setId(id);
					trackList.add(track);
				} else {
					boolean added = false;
					for (Track track : trackList) {
						if (track.getMmsi().equals(aisMessage.getMmsi())) {
							if (track.addMessage(aisMessage)) {
								added = true;
								break;
							}
						}
					}
					if (!added) {
						Track track = new Track(aisMessage);
						track.setMmsi(aisMessage.getMmsi());
						id += 1;
						track.setId(id);
						trackList.add(track);
					}
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return trackList;

	}

	private static boolean checkIfIsInInterval(Track track, AISMessage aisMessage) {
		long timeLastMsg = track.getAisMessages().get(track.getAisMessages().size() - 1).getTimestamp().getTime();
		long timeDiff = aisMessage.getTimestamp().getTime() - timeLastMsg;
		if (timeDiff <= 150000) {
			track.getAisMessages().add(aisMessage);
			track.setEndDate(aisMessage.getTimestamp());
			return true;
		} else {
			return false;
		}
	}

	private static boolean trackExists(Track track, AISMessage aisMessage) {
		// TODO Auto-generated method stub
		return false;
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

	public static ArrayList<Track> appendStaticAISData(ArrayList<Track> tracks) {

		try {
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(CSV_LOCATION_VOYAGE));

			while ((LINE = reader.readLine()) != null) {
				String[] aisMessage = LINE.split(SPLITTER);

				if (!aisMessage[0].contains("name")) {

					String mmsi = aisMessage[1].replaceAll("\"", "");
					double length = Double.valueOf(aisMessage[2]);

					for (Track track : tracks) {
						if (track.getMmsi().equals(mmsi)) {
							track.setLength(length);
						}
					}
				}

			}
			return tracks;

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

		// String newDateString =
		// timestampString.replace(timestampString.charAt(0), ' ');
		// newDateString = newDateString.replaceAll(" ", "");

		try {
			Date result = df.parse(timestampString);
			return result;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
