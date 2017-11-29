package input;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ais.AISMessage;
import ais.Vessel;
import ais.VesselContainer;
import app.datamodel.Track;

public class CSVReader {

	private final static String CSV_LOCATION_LARGE = "C:/Users/msteidel/Desktop/largeFile.csv";
	private final static String CSV_LOCATION_SMALL = "C:/Users/msteidel/Desktop/testData.csv";
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
					container.get(message.getMmsi()).getAisMessagesUnsorted().add(message);

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

						container.get(message.getMmsi()).getAisMessagesUnsorted().add(message);

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
