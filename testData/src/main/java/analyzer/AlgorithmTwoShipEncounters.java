package analyzer;

import java.util.ArrayList;
import java.util.Date;

import org.geotools.referencing.GeodeticCalculator;

import com.vividsolutions.jts.geom.Coordinate;

import app.datamodel.AISMessage;
import app.datamodel.Vessel;
import datamodel.CompareableTracks;
import datamodel.Interval;
import datamodel.Track;
import util.Util;

public class AlgorithmTwoShipEncounters {

	public AlgorithmTwoShipEncounters() {
		new GeodeticCalculator();
	}

	/**
	 * Iterates over all {@link Track} from each {@link Vessel} and tries to
	 * find the two {@link Track} at which they are the closest to each other.
	 * 
	 * @param vessel1
	 * @param vessel2
	 * @return The {@link CompareableTracks} at which the two {@link Vessel} are
	 *         closest to each other.
	 */
	public CompareableTracks findCompareableTracks(Vessel vessel1, Vessel vessel2) {
		CompareableTracks compareableTracks = null;
		if (!(vessel1.getTracks().isEmpty()) && !(vessel2.getTracks().isEmpty())) {
			for (Track trackV1 : vessel1.getTracks()) {
				for (Track trackV2 : vessel2.getTracks()) {
					Interval trackInterval = createInterval(trackV1, trackV2);
					if (trackInterval != null) {
						ArrayList<AISMessage> messagesV1 = findAISMessages(trackInterval, trackV1);
						ArrayList<AISMessage> messagesV2 = findAISMessages(trackInterval, trackV2);
						compareableTracks = findTimeTupelWithDistance(messagesV1, messagesV2, vessel1, vessel2, trackV1,
								trackV2);

					}
				}
			}
		}
		return compareableTracks;
	}

	private CompareableTracks findTimeTupelWithDistance(ArrayList<AISMessage> messagesV1,
			ArrayList<AISMessage> messagesV2, Vessel vessel1, Vessel vessel2, Track trackToCompareV1,
			Track trackToCompareV2) {
		CompareableTracks result = null;
		for (AISMessage messageV1 : messagesV1) {
			long timeMessage1 = messageV1.getTimestamp().getTime();
			for (AISMessage messageV2 : messagesV2) {
				long timeMessage2 = messageV2.getTimestamp().getTime();
				long timeResult = timeMessage1 - timeMessage2;
				if (timeResult >= (-10000) && timeResult <= (10000)) {
					Coordinate start = new Coordinate();
					Coordinate end = new Coordinate(messageV2.getLat(), messageV2.getLon());
					double distance = Util.calculateDistanceNM(start, end);
					result = new CompareableTracks(vessel1, vessel2, trackToCompareV1, trackToCompareV2, messageV1,
							messageV2, distance);
					return result;
				}
			}
		}
		return result;
	}

	/**
	 * Find the minimum distance in a series of two {@link AISMessage}.
	 * 
	 * @param messagesV1
	 *            The {@link AISMessage} of the first {@link Vessel}.
	 * @param messagesV2
	 *            The {@link AISMessage} of the second {@link Vessel}.
	 * @return The {@link CompareableTracks}-Object which describes the CPA.
	 */
	private CompareableTracks findMinimumDistance(Vessel vessel1, Vessel vessel2, Track trackToCompareV1,
			Track trackToCompareV2, ArrayList<AISMessage> messagesV1, ArrayList<AISMessage> messagesV2) {

		double minDistance = -1;

		CompareableTracks result = null;

		for (int i = 0; i < messagesV1.size(); i++) {
			AISMessage messageV1 = messagesV1.get(i);
			AISMessage messageV2 = messagesV2.get(i);
			System.out.println("Distance for Vessel " + messageV1.getMmsi() + " and Vessel " + messageV2.getMmsi());

			if (messageV1.getSog() > 1 && messageV2.getSog() > 1) {

				Coordinate start = new Coordinate(messageV1.getLat(), messageV1.getLon());
				Coordinate end = new Coordinate(messageV2.getLat(), messageV2.getLon());

				double distance = Util.calculateDistanceNM(start, end);

				if (minDistance != -1) {
					if (distance < minDistance) {
						minDistance = distance;
						result = new CompareableTracks(vessel1, vessel2, trackToCompareV1, trackToCompareV2, messageV1,
								messageV2, distance);
					}
				}
			}
		}

		return result;

	}

	/**
	 * Checks whether the two tracks occurred at the same time or not. If they
	 * did, an {@link Interval} representing the start and end time is returned.
	 * 
	 * @param track1
	 *            The {@link Track} of the first {@link Vessel}.
	 * @param track2
	 *            The {@link Track} of the second {@link Vessel}.
	 * @return <code>null</code> if they do not intersect. Otherwise the
	 *         {@link Interval}.
	 */
	private static Interval createInterval(Track track1, Track track2) {

		Date startT1 = track1.getStartDate();
		Date endT1 = track1.getEndDate();

		Date startT2 = track2.getStartDate();
		Date endT2 = track2.getEndDate();

		// No intersection
		if (endT1.before(track2.getStartDate())) {
			return null;
		} else if (endT2.before(track1.getStartDate())) {
			return null;
		}

		long startT1Mili = startT1.getTime();
		long startT2Mili = startT2.getTime();
		long endT1Mili = endT1.getTime();
		long endT2Mili = endT2.getTime();

		// Intersection
		if (startT1Mili <= startT2Mili) {
			if (endT1Mili <= endT2Mili) {
				return new Interval(startT2, endT1);
			} else {
				return new Interval(startT2, endT2);
			}
		} else {
			if (endT1Mili <= endT2Mili) {
				return new Interval(startT1, endT1);
			} else {
				return new Interval(startT1, endT2);
			}
		}

	}

	/**
	 * Returns an {@link ArrayList} with {@link AISMessage}
	 * 
	 * @param trackInterval
	 * @param trackV1
	 * @return
	 */
	private ArrayList<AISMessage> findAISMessages(Interval trackInterval, Track trackV1) {

		Date start = trackInterval.getStart();
		Date end = trackInterval.getEnd();

		ArrayList<AISMessage> result = new ArrayList<AISMessage>();

		if (!trackV1.getAisMessages().isEmpty()) {
			for (AISMessage message : trackV1.getAisMessages()) {
				long timestamp = message.getTimestamp().getTime();
				if (timestamp >= start.getTime() && timestamp <= end.getTime()) {
					result.add(message);
				}
			}

		}
		return result;
	}

}
