package analyzer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.geotools.referencing.GeodeticCalculator;

import com.vividsolutions.jts.geom.Coordinate;

import analyzer.trafficSituation.TrafficEvaluator;
import app.datamodel.AISMessage;
import app.datamodel.Vessel;
import datamodel.Interval;
import datamodel.Situation;
import datamodel.Track;
import output.Encounter;
import util.Util;

public class AnalyzerTwoShips {

	private GeodeticCalculator calculator;

	public AnalyzerTwoShips() {
		this.calculator = new GeodeticCalculator();
	}

	public ArrayList<Track> findTrackWithoutVesselInfluence(ArrayList<Track> tracks) {

		System.out.println("Looking for tracks without influence...");

		ArrayList<Track> tracksWithoutInfluence = new ArrayList<Track>();

		boolean isInDistance = false;

		for (int i = 0; i < tracks.size(); i++) {
			Track track1 = tracks.get(i);
			if ((i + 1) < tracks.size()) {
				for (int j = i + 1; j < tracks.size(); j++) {
					Track track2 = tracks.get(j);
					String mmsiT1 = track1.getMmsi();
					String mmsiT2 = track2.getMmsi();

					if (!mmsiT1.equals(mmsiT2)) {
						// Intervall bilden
						Interval interval = createInterval(track1, track2);
						if (interval != null) {
							if (!isInDistance(track1, track2, interval)) {
								isInDistance = false;
							} else {
								isInDistance = true;
								break;
							}
						}
					}
				}
			}
			if (!isInDistance) {
				tracksWithoutInfluence.add(track1);
			}
		}
		return tracksWithoutInfluence;
	}

	private boolean isInDistance(Track track1, Track track2, Interval interval) {
		ArrayList<AISMessage> aisMessagesT1 = findAISMessages(interval, track1);
		ArrayList<AISMessage> aisMessagesT2 = findAISMessages(interval, track2);

		for (AISMessage messageT1 : aisMessagesT1) {
			for (AISMessage messageT2 : aisMessagesT2) {
				Coordinate start = new Coordinate(messageT1.getLat(), messageT1.getLon());
				Coordinate end = new Coordinate(messageT2.getLat(), messageT2.getLon());
				double distance = Util.calculateDistanceNM(start, end);

				if (distance < 1.5) {
					return true;
				}
			}
		}
		return false;
	}

	public ArrayList<Track> exactlyTwoShipsInfluence(Track track1, Track track2, ArrayList<Track> otherTracks) {
		boolean bool = true;
		for (int i = 0; i < otherTracks.size(); i++) {
			Track track = otherTracks.get(i);
			Interval intersectionInterval1 = createInterval(track1, track);
			Interval intersectionInterval2 = createInterval(track2, track);

			if (isInDistance(track1, track, intersectionInterval1)
					|| isInDistance(track2, track, intersectionInterval2)) {
				bool = false;
			}
		}
		if (bool = true) {
			ArrayList<Track> bothTracks = new ArrayList<Track>();
			bothTracks.add(track1);
			bothTracks.add(track2);
			return bothTracks;
		} else {
			return null;
		}
	}

	public ArrayList<ArrayList<Track>> extractTwoShipsInfluence(ArrayList<Track> tracks) {
		ArrayList<ArrayList<Track>> result = new ArrayList<ArrayList<Track>>();
		for (Track track1 : tracks) {
			for (Track track2 : tracks) {
				ArrayList<Track> otherTracks = tracks;
				for (Track track : otherTracks) {
					if (track.getAisMessages().get(0).getMmsi() == track1.getAisMessages().get(0).getMmsi()
							|| track.getAisMessages().get(0).getMmsi() == track2.getAisMessages().get(0).getMmsi()) {
						otherTracks.remove(track);
					}
				}
				if ((track1.getAisMessages().get(0).getMmsi() != track2.getAisMessages().get(0).getMmsi())
						&& (exactlyTwoShipsInfluence(track1, track2, otherTracks)) != null) {
					ArrayList<Track> partialResult = new ArrayList<Track>();
					partialResult.add(track1);
					partialResult.add(track2);
					result.add(partialResult);

				}
			}
		}
		return result;
	}

	/**
	 * Returns a pair of {@link Vessel} if their {@link AISMessage} occurred
	 * almost at the same time and if their CPA falls below a certain threshold.
	 * 
	 * @param vessel1
	 * @param vessel2
	 * @return
	 */
	public ArrayList<AISMessage> findPairsByTracks(Vessel vessel1, Vessel vessel2) {

		if (vessel1.getTracks().isEmpty() || vessel2.getTracks().isEmpty()) {
			return null;
		} else {
			for (Track trackV1 : vessel1.getTracks()) {
				for (Track trackV2 : vessel2.getTracks()) {
					Interval trackInterval = createInterval(trackV1, trackV2);
					if (trackInterval != null) {
						ArrayList<AISMessage> messagesV1 = findAISMessages(trackInterval, trackV1);
						ArrayList<AISMessage> messagesV2 = findAISMessages(trackInterval, trackV2);
						for (AISMessage messageV1 : messagesV1) {
							for (AISMessage messageV2 : messagesV2) {
								double distance = Util.calculateDistanceNM(
										new Coordinate(messageV1.getLat(), messageV1.getLon()),
										new Coordinate(messageV2.getLat(), messageV2.getLon()));
								if (distance < 1) {
									return null;
								}
							}
						}
						return messagesV1;
					}
				}
			}
		}

		return null;
	}

	private Encounter findRealCPA(ArrayList<AISMessage> messagesV1, ArrayList<AISMessage> messagesV2,
			Track trackVessel1, Track trackVessel2) {
		HashMap<String, ArrayList<AISMessage>> aisTupel = findTimeTupel(messagesV1, messagesV2);
		Encounter result = calculateDistances(aisTupel, trackVessel1, trackVessel2);
		Situation res = TrafficEvaluator.rightOfWay(result);
		return result;
	}

	private HashMap<String, ArrayList<AISMessage>> findTimeTupel(ArrayList<AISMessage> messagesV1,
			ArrayList<AISMessage> messagesV2) {

		ArrayList<AISMessage> vessel1List = new ArrayList<AISMessage>();
		ArrayList<AISMessage> vessel2List = new ArrayList<AISMessage>();

		HashMap<String, ArrayList<AISMessage>> resultMap = new HashMap<String, ArrayList<AISMessage>>();

		for (AISMessage message1 : messagesV1) {
			long timeMessage1 = message1.getTimestamp().getTime();
			for (AISMessage message2 : messagesV2) {
				long timeMessage2 = message2.getTimestamp().getTime();
				long result = timeMessage1 - timeMessage2;

				if (result >= (-10000) && result <= (10000)) {
					vessel1List.add(message1);
					vessel2List.add(message2);
				}
			}
		}

		resultMap.put("Vessel1", vessel1List);
		resultMap.put("Vessel2", vessel2List);

		return resultMap;
	}

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

	private Encounter calculateDistances(HashMap<String, ArrayList<AISMessage>> aisTupel, Track trackVessel1,
			Track trackVessel2) {

		ArrayList<AISMessage> aisMessagesV1 = aisTupel.get("Vessel1");
		ArrayList<AISMessage> aisMessagesV2 = aisTupel.get("Vessel2");

		for (int i = 0; i < aisMessagesV1.size(); i++) {
			AISMessage messageV1 = aisMessagesV1.get(i);
			AISMessage messageV2 = aisMessagesV2.get(i);
			System.out.println("Distance for Vessel " + messageV1.getMmsi() + " and Vessel " + messageV2.getMmsi());

			if (messageV1.getSog() > 1 && messageV2.getSog() > 1) {

				Coordinate start = new Coordinate(messageV1.getLat(), messageV1.getLon());
				Coordinate end = new Coordinate(messageV2.getLat(), messageV2.getLon());

				if (start.x > 0 && start.y > 0 && end.x > 0 && end.y > 0) {

					double distance = calculateDistanceNM(start, end);

					if (distance <= 1.5) {
						// cpa
						Encounter encounter = new Encounter();
						int value = messageV1.getTimestamp().compareTo(messageV2.getTimestamp());
						long time = 0;

						if (value > 0) {
							time = messageV2.getTimestamp().getTime() + 600000;
						} else {
							time = messageV1.getTimestamp().getTime() + 600000;
						}

						encounter.setAisMessage1(messageV1);
						encounter.setAisMessage2(messageV2);
						encounter.setdCPA(distance);
						encounter.setTrackVessel1(trackVessel1);
						encounter.setTrackVessel2(trackVessel2);

						for (int j = i + 1; j < aisMessagesV1.size(); j++) {

							long timeNewAISMessage1 = aisMessagesV1.get(j).getTimestamp().getTime();
							long timeNewAISMessage2 = aisMessagesV2.get(j).getTimestamp().getTime();

							if ((time < timeNewAISMessage1) || (time < timeNewAISMessage2)) {
								return encounter;
							} else {
								start = new Coordinate(aisMessagesV1.get(j).getLat(), aisMessagesV1.get(j).getLon());
								end = new Coordinate(aisMessagesV2.get(j).getLat(), aisMessagesV2.get(j).getLon());
								double newDistance = calculateDistanceNM(start, end);
								if (newDistance < distance) {
									encounter.setAisMessage1(aisMessagesV1.get(j));
									encounter.setAisMessage2(aisMessagesV2.get(j));
									encounter.setdCPA(distance);
								}
							}
						}
						return encounter;
					}
				} else {
					return null;
				}
			}
		}
		return null;
	}

	public double calculateDistanceNM(Coordinate start, Coordinate end) {
		calculator.setStartingGeographicPoint(start.y, start.x);
		calculator.setDestinationGeographicPoint(end.y, end.x);
		return (calculator.getOrthodromicDistance()) * 0.000539957;
	}

	public static Interval createInterval(Track track1, Track track2) {

		Date startT1 = track1.getStartDate();
		Date endT1 = track1.getEndDate();

		Date startT2 = track2.getStartDate();
		Date endT2 = track2.getEndDate();

		// null, wenn es keine Schnittmenge gibt
		if (endT1.before(track2.getStartDate())) {
			return null;
		} else if (endT2.before(track1.getStartDate())) {
			return null;
		}

		long startT1Mili = startT1.getTime();
		long startT2Mili = startT2.getTime();
		long endT1Mili = endT1.getTime();
		long endT2Mili = endT2.getTime();

		// Schnittmenge
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

	public static Date endDate(Track track1, Track track2) {

		Date endT1 = track1.getEndDate();
		Date endT2 = track2.getEndDate();

		if (endT2.after(endT1)) {
			return endT1;
		} else {
			return endT2;
		}
	}

	public ArrayList<Track> calcualteTrackDistances(ArrayList<Track> tracksWithoutInfluence) {

		for (Track track : tracksWithoutInfluence) {
			double length = 0;
			ArrayList<AISMessage> messages = track.getAisMessages();
			for (int i = 0; i < messages.size(); i++) {
				if (i + 1 <= messages.size()) {
					for (int j = i + 1; j < messages.size(); j++) {
						Coordinate start = new Coordinate(messages.get(i).getLat(), messages.get(i).getLon());
						Coordinate end = new Coordinate(messages.get(j).getLat(), messages.get(j).getLon());
						length = length + Util.calculateDistanceNM(start, end);
					}
				}

			}
			track.setLength(length);
		}

		return tracksWithoutInfluence;
	}

	public ArrayList<Track> findLongestTrack(ArrayList<Track> tracksWithoutInfluence) {
		double maximum = -1;

		ArrayList<Track> result = new ArrayList<Track>();

		for (Track track : tracksWithoutInfluence) {
			if (maximum == -1) {
				maximum = track.getLength();
			} else {
				if (track.getLength() > maximum) {
					result.clear();
					result.add(track);
					maximum = track.getLength();
				} else if (track.getLength() == maximum) {
					result.add(track);
				}
			}
		}

		return result;
	}

	public ArrayList<Track> findShortestTrack(ArrayList<Track> tracksWithoutInfluence) {
		double minimum = -1;

		ArrayList<Track> result = new ArrayList<Track>();

		for (Track track : tracksWithoutInfluence) {
			if (minimum == -1) {
				minimum = track.getLength();
			} else {
				if (track.getLength() < minimum) {
					result.clear();
					result.add(track);
					minimum = track.getLength();
				} else if (track.getLength() == minimum) {
					result.add(track);
				}
			}
		}

		return result;
	}

}
