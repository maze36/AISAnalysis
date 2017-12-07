package analyzer;

import java.util.ArrayList;
import java.util.Date;

import app.datamodel.AISMessage;
import app.datamodel.Vessel;
import datamodel.Interval;
import datamodel.Track;

public class Algorithm {

	public void findSituation(Vessel vessel1, Vessel vessel2) {
		if (!(vessel1.getTracks().isEmpty()) && !(vessel2.getTracks().isEmpty())) {
			for (Track trackV1 : vessel1.getTracks()) {
				for (Track trackV2 : vessel2.getTracks()) {
					Interval trackInterval = createInterval(trackV1, trackV2);
					if (trackInterval != null) {
						ArrayList<AISMessage> messagesV1 = findAISMessages(trackInterval, trackV1);
						ArrayList<AISMessage> messagesV2 = findAISMessages(trackInterval, trackV2);
						double minDistance = findMinimumDistance(messagesV1, messagesV2);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param messagesV1
	 * @param messagesV2
	 * @return
	 */
	private double findMinimumDistance(ArrayList<AISMessage> messagesV1, ArrayList<AISMessage> messagesV2) {
		return 0;
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
