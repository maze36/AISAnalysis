package analyzer;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import org.geotools.referencing.GeodeticCalculator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

import app.datamodel.AISMessage;
import app.datamodel.Vessel;
import behaviorPredictor.logic.dataModel.ship.OwnShip;
import behaviorPredictor.logic.dataModel.ship.TargetShip;
import behaviorPredictor.logic.util.classicalcpa.CPACalculator;
import behaviorPredictor.logic.util.classicalcpa.CPAResult;
import behaviorPredictor.logic.util.trafficSituation.TrafficEvaluator;
import connectivity.messagesystem.messages.off.content.trafficSituation.GWSO;
import connectivity.messagesystem.messages.off.content.trafficSituation.TrafficEvaluationResult;
import connectivity.messagesystem.messages.ran.content.OwnShipNavigationData;
import connectivity.messagesystem.messages.ran.content.RaTrackDataItem;
import datamodel.Interval;
import datamodel.Track;
import input.CSVReader;
import output.CSVWriter;
import output.Encounter;
import util.MTCASUtil;
import util.Util;

public class Analyzer {

	private GeodeticCalculator calculator;

	public Analyzer() {
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

		boolean bool = false;

		for (AISMessage messageT1 : aisMessagesT1) {
			for (AISMessage messageT2 : aisMessagesT2) {
				Coordinate start = new Coordinate(messageT1.getLat(), messageT1.getLon());
				Coordinate end = new Coordinate(messageT2.getLat(), messageT2.getLon());
				double distance = Util.calculateDistanceNM(start, end);

				if (distance < 0.5) {
					bool = true;
				}
			}
		}
		return bool;
	}

	private boolean isInDistanceWithTimeComponent(Track track1, Track track2, Interval interval, double dist) {
		ArrayList<AISMessage> aisMessagesT1 = findAISMessages(interval, track1);
		ArrayList<AISMessage> aisMessagesT2 = findAISMessages(interval, track2);

		boolean bool = false;

		if (interval == null) {
			return false;
		} else {
			for (AISMessage messageT1 : aisMessagesT1) {
				for (AISMessage messageT2 : aisMessagesT2) {

					Coordinate start = new Coordinate(messageT1.getLat(), messageT1.getLon());
					Coordinate end = new Coordinate(messageT2.getLat(), messageT2.getLon());
					double distance = Util.calculateDistanceNM(start, end);

					if (distance < dist && Math
							.abs(messageT1.getTimestamp().getTime() - messageT2.getTimestamp().getTime()) < 10000) {
						bool = true;
						return bool;
					}
				}
			}
			return bool;
		}
	}

	public static boolean makesManeuver(Track track, Interval interval, double degrees) {
		boolean bool = false;
		ArrayList<AISMessage> aisMessages = findAISMessages(interval, track);
		for (AISMessage aisMessage1 : aisMessages) {
			for (AISMessage aisMessage2 : aisMessages) {
				if (Math.abs(aisMessage1.getCog() - aisMessage2.getCog()) > degrees) {
					bool = true;
				}
			}
		}
		return bool;
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

	// private Encounter findRealCPA(ArrayList<AISMessage> messagesV1,
	// ArrayList<AISMessage> messagesV2,
	// Track trackVessel1, Track trackVessel2) {
	// HashMap<String, ArrayList<AISMessage>> aisTupel =
	// findTimeTupel(messagesV1, messagesV2);
	// Encounter result = calculateDistances(aisTupel, trackVessel1,
	// trackVessel2);
	// Situation res = TrafficEvaluator.rightOfWay(result);
	// return result;
	// }

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

	public ArrayList<ArrayList<Track>> extractExactlyTwoShipsInfluence(ArrayList<Track> tracks, double dist) {
		ArrayList<ArrayList<Track>> result = new ArrayList<ArrayList<Track>>();

		for (Track track1 : tracks) {
			for (Track track2 : tracks) {
				ArrayList<Track> otherTracks = new ArrayList<Track>();

				for (Track track : tracks) {
					if (!track.getAisMessages().get(0).getMmsi().equals(track1.getAisMessages().get(0).getMmsi())
							&& !track.getAisMessages().get(0).getMmsi()
									.equals(track2.getAisMessages().get(0).getMmsi())) {
						otherTracks.add(track);
					}

				}
				if ((!track1.getAisMessages().get(0).getMmsi().equals(track2.getAisMessages().get(0).getMmsi()))
						&& (exactlyTwoShipsInfluence(track1, track2, otherTracks, dist) != null)) {
					ArrayList<Track> partialResult = new ArrayList<Track>();
					partialResult.add(track1);
					partialResult.add(track2);
					result.add(partialResult);
					System.out.println("1 new pair of tracks found.");

				}
			}

		}
		return result;
	}

	public Double computeMinDistance(Track track1, Track track2) {
		ArrayList<Double> distances = new ArrayList<Double>();
		for (AISMessage message1 : track1.getAisMessages()) {
			for (AISMessage message2 : track2.getAisMessages()) {
				if (Math.abs(message1.getTimestamp().getTime() - message2.getTimestamp().getTime()) < 10000) {
					Coordinate start = new Coordinate(message1.getLat(), message1.getLon());
					Coordinate end = new Coordinate(message2.getLat(), message2.getLon());
					double dist = calculateDistanceNM(start, end);
					distances.add(dist);
				}
			}
		}

		if (distances.isEmpty()) {
			return null;
		}

		double minDist = Collections.min(distances);
		return minDist;
	}

	public boolean shipsBothMadeNoManeuverProximity(Track track1, Track track2, double distanceDefault,
			double degreesDefault) {
		boolean dist = false;
		if (computeMinDistance(track1, track2) != null && computeMinDistance(track1, track2) < distanceDefault) {
			dist = true;
		}

		boolean maneuver = false;
		ArrayList<ArrayList<AISMessage>> closePoints = new ArrayList<ArrayList<AISMessage>>();

		for (AISMessage message1 : track1.getAisMessages()) {
			for (AISMessage message2 : track2.getAisMessages()) {
				Coordinate start = new Coordinate(message1.getLat(), message1.getLon());
				Coordinate end = new Coordinate(message2.getLat(), message2.getLon());
				double distance = calculateDistanceNM(start, end);
				if (distance < distanceDefault
						&& (Math.abs(message1.getTimestamp().getTime() - message2.getTimestamp().getTime()) < 10000)) {
					ArrayList<AISMessage> closePairs = new ArrayList<AISMessage>();
					closePairs.add(message1);
					closePairs.add(message2);
					closePoints.add(closePairs);
				}
			}
		}
		// for both ships: does it make a maneuver along the points where it is
		// close to the other ship?
		for (int index = 0; index < 2; index++) {
			for (int i = 0; i < closePoints.size() - 1; i++) {
				for (int j = i + 1; j < closePoints.size(); j++) {
					if ((closePoints.get(i).get(index).getCog() - closePoints.get(j).get(index).getCog() + 360)
							% 360 > degreesDefault) {
						maneuver = true;
					}
				}
			}
		}
		if (dist == true && maneuver == false) {

			return true;
		} else {
			return false;
		}
	}

	public ArrayList<ArrayList<Track>> extractShipPairsWithoutManeuver(ArrayList<Track> tracks, double distanceDefault,
			double degreesDefault) {
		ArrayList<ArrayList<Track>> extractedTrackPairs = new ArrayList<ArrayList<Track>>();
		for (int i = 0; i < tracks.size(); i++) {
			for (int j = i + 1; j < tracks.size(); j++) {
				if (shipsBothMadeNoManeuverProximity(tracks.get(i), tracks.get(j), distanceDefault,
						degreesDefault) == false) {
					ArrayList<Track> trackPair = new ArrayList<Track>();
					trackPair.add(tracks.get(i));
					trackPair.add(tracks.get(j));
					extractedTrackPairs.add(trackPair);
				}
			}
		}
		return extractedTrackPairs;

	}
	/*
	 * public ArrayList<ArrayList<Track>>
	 * extractTwoShipsInfluence(ArrayList<Track> tracks) { tracks =
	 * CSVReader.appendStaticAISData(tracks); ArrayList<ArrayList<Track>> result
	 * = new ArrayList<ArrayList<Track>>(); ArrayList<Track> trackstoremove =
	 * new ArrayList<Track>(); for (Track track : tracks) { double shipLength =
	 * Double.valueOf(track.getLength()); if (shipLength <= 80.0) {
	 * trackstoremove.add(track); } } tracks.removeAll(trackstoremove);
	 * 
	 * for (Track track1 : tracks) { for (Track track2 : tracks) {
	 * 
	 * Integer mmsi1 =
	 * Integer.valueOf(track1.getAisMessages().get(0).getMmsi()); Integer mmsi2
	 * = Integer.valueOf(track2.getAisMessages().get(0).getMmsi()); Interval
	 * interval = createInterval(track1, track2); if (mmsi1 < mmsi2 &&
	 * twoShipsProximity(track1, track2) != null && (makesManeuver(track1,
	 * interval) || makesManeuver(track2, interval))) { ArrayList<Track>
	 * partialResult = new ArrayList<Track>(); partialResult.add(track1);
	 * partialResult.add(track2); result.add(partialResult); System.out.println(
	 * "1 new pair of tracks found."); ArrayList<ArrayList<Track>>
	 * partialResultList = new ArrayList<ArrayList<Track>>();
	 * partialResultList.add(partialResult); try {
	 * CSVWriter.writeTrackCSVArrayList(partialResultList, "EvaluationObject" +
	 * tracks.indexOf(track1) + tracks.indexOf(track2) + ".csv"); } catch
	 * (FileNotFoundException e) { // TODO Auto-generated // catch block
	 * e.printStackTrace(); }
	 * 
	 * } }
	 * 
	 * } return result; }
	 */

	public ArrayList<ArrayList<Track>> extractTwoShipsNoInfluenceLength(ArrayList<Track> tracks, double lengthMin,
			double lengthMax, double degrees, double dist, double minDistDefault, double distForCrossing) {
		tracks = CSVReader.appendStaticAISData(tracks);
		ArrayList<ArrayList<Track>> result = new ArrayList<ArrayList<Track>>();
		ArrayList<Track> trackstoremove = new ArrayList<Track>();

		for (Track track1 : tracks) {
			for (Track track2 : tracks) {

				Integer mmsi1 = Integer.valueOf(track1.getAisMessages().get(0).getMmsi());
				Integer mmsi2 = Integer.valueOf(track2.getAisMessages().get(0).getMmsi());
				Interval interval = createInterval(track1, track2);
				double shipLength1 = Double.valueOf(track1.getLength());
				double shipLength2 = Double.valueOf(track2.getLength());
				if (mmsi1 < mmsi2 && twoShipsProximity(track1, track2, dist) != null
						&& (!makesManeuver(track1, interval, degrees) && !makesManeuver(track2, interval, degrees))
						&& Math.max(shipLength1, shipLength2) > lengthMin
						&& Math.max(shipLength1, shipLength2) < lengthMax && Math.min(shipLength1, shipLength2) >= 80.0
						&& extractCrossing(track1, track2, distForCrossing)
						&& extractIntersectingCourses(track1, track2, minDistDefault)) {
					ArrayList<Track> partialResult = new ArrayList<Track>();
					partialResult.add(track1);
					partialResult.add(track2);
					result.add(partialResult);
					System.out
							.println("1 new pair of tracks found: " + tracks.indexOf(track1) + tracks.indexOf(track2));
					System.out.println("Lengths = " + track1.getLength() + track2.getLength());
					double minDist = computeMinDistance(track1, track2);
					System.out.println(minDist + " of " + tracks.indexOf(track1) + tracks.indexOf(track2));
					ArrayList<ArrayList<Track>> partialResultList = new ArrayList<ArrayList<Track>>();
					partialResultList.add(partialResult);
					try {
						CSVWriter.writeTrackCSVArrayList(partialResultList,
								"EvaluationObject" + tracks.indexOf(track1) + tracks.indexOf(track2) + ".csv");
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}
		return result;
	}

	public ArrayList<ArrayList<Track>> extractTwoShipsWithInfluenceLength(ArrayList<Track> tracks, double lengthMin,
			double lengthMax, double degrees, double dist, double minDistDefault, double distForCrossing) {
		tracks = CSVReader.appendStaticAISData(tracks);
		ArrayList<ArrayList<Track>> result = new ArrayList<ArrayList<Track>>();
		ArrayList<Track> trackstoremove = new ArrayList<Track>();

		for (Track track1 : tracks) {
			for (Track track2 : tracks) {

				Integer mmsi1 = Integer.valueOf(track1.getAisMessages().get(0).getMmsi());
				Integer mmsi2 = Integer.valueOf(track2.getAisMessages().get(0).getMmsi());
				Interval interval = createInterval(track1, track2);
				double shipLength1 = Double.valueOf(track1.getLength());
				double shipLength2 = Double.valueOf(track2.getLength());
				if (mmsi1 < mmsi2 && twoShipsProximity(track1, track2, dist) != null
						&& (makesManeuver(track1, interval, degrees) || makesManeuver(track2, interval, degrees))
						&& Math.max(shipLength1, shipLength2) > lengthMin
						&& Math.max(shipLength1, shipLength2) < lengthMax && Math.min(shipLength1, shipLength2) >= 80.0
						&& extractCrossing(track1, track2, distForCrossing)
						&& extractIntersectingCourses(track1, track2, minDistDefault)) {
					ArrayList<Track> partialResult = new ArrayList<Track>();
					partialResult.add(track1);
					partialResult.add(track2);
					result.add(partialResult);
					System.out
							.println("1 new pair of tracks found: " + tracks.indexOf(track1) + tracks.indexOf(track2));
					System.out.println("Lengths = " + track1.getLength() + track2.getLength());
					double minDist = computeMinDistance(track1, track2);
					System.out.println(minDist + " of " + tracks.indexOf(track1) + tracks.indexOf(track2));
					ArrayList<ArrayList<Track>> partialResultList = new ArrayList<ArrayList<Track>>();
					partialResultList.add(partialResult);
					try {
						CSVWriter.writeTrackCSVArrayList(partialResultList,
								"EvaluationObject" + tracks.indexOf(track1) + tracks.indexOf(track2) + ".csv");
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}
		return result;
	}

	// 330-30° means head-on, since 357-3° is too narrow.
	// dist means that the crossing has to take place when the ships are in a
	// distance smaller than dist from one another
	public boolean extractCrossing(Track track1, Track track2, double distForCrossing) {
		boolean bool = false;
		for (AISMessage message1 : track1.getAisMessages()) {
			for (AISMessage message2 : track2.getAisMessages()) {
				Coordinate start = new Coordinate(message1.getLat(), message1.getLon());
				Coordinate end = new Coordinate(message2.getLat(), message2.getLon());
				double distance = calculateDistanceNM(start, end);
				double cogDiff = (message1.getCog() - message2.getCog() + 360) % 360;

				LineString ownShipLine = MTCASUtil.createLineString(start, message1.getCog(), 3);

				LineString targetShipLine = MTCASUtil.createLineString(end, message2.getCog(), 3);

				if (Math.abs(message1.getTimestamp().getTime() - message2.getTimestamp().getTime()) < 10000
						&& ((30 < cogDiff && cogDiff < 90 + 22.5) || (270 - 22.5 < cogDiff && cogDiff < 330))
						&& distance < distForCrossing && ownShipLine.intersects(targetShipLine)) {
					bool = true;
				}
			}

		}
		return bool;
	}

	public boolean extractIntersectingCourses(Track track1, Track track2, double minDistDefault) {
		boolean bool = false;
		for (AISMessage message1 : track1.getAisMessages()) {
			for (AISMessage message2 : track2.getAisMessages()) {
				Coordinate start = new Coordinate(message1.getLat(), message1.getLon());
				Coordinate end = new Coordinate(message2.getLat(), message2.getLon());
				double dist = calculateDistanceNM(start, end);
				if (dist < minDistDefault) {
					bool = true;
					return bool;
				}
			}
		}
		return bool;
	}

	public ArrayList<ArrayList<Track>> extractIntersectingCrossingTracksNoInfluence(ArrayList<Track> tracks,
			double lengthMin, double lengthMax, double distanceDefault, double degreesDefault, double minDistDefault,
			double distForCrossing) {
		ArrayList<ArrayList<Track>> extractedTracks = new ArrayList<ArrayList<Track>>();
		ArrayList<Double> minDistances = new ArrayList<Double>();
		for (int i = 0; i < tracks.size() - 1; i++) {
			for (int j = i + 1; j < tracks.size(); j++) {
				ArrayList<Track> trackPair = new ArrayList<Track>();
				if (extractCrossing(tracks.get(i), tracks.get(j), distForCrossing)
						&& extractIntersectingCourses(tracks.get(i), tracks.get(j), minDistDefault)
						&& shipsBothMadeNoManeuverProximity(tracks.get(i), tracks.get(j), distanceDefault,
								degreesDefault)) {
					trackPair.add(tracks.get(i));
					trackPair.add(tracks.get(j));
					extractedTracks.add(trackPair);
					System.out.println("1 new pair of tracks found.");
					double minDist = computeMinDistance(tracks.get(i), tracks.get(j));
					minDistances.add(minDist);
					ArrayList<ArrayList<Track>> partialResultList = new ArrayList<ArrayList<Track>>();

					partialResultList.add(trackPair);
					try {
						CSVWriter.writeTrackCSVArrayList(partialResultList, "EvaluationObject"
								+ tracks.indexOf(tracks.get(i)) + tracks.indexOf(tracks.get(j)) + ".csv");
					} catch (FileNotFoundException e) { // TODO Auto-generated
														// catch block
						e.printStackTrace();
					}

				}

			}
		}
		System.out.println(minDistances);
		return extractedTracks;
	}

	public ArrayList<Track> exactlyTwoShipsInfluence(Track track1, Track track2, ArrayList<Track> otherTracks,
			double dist) {
		boolean bool = true;
		Interval interval = createInterval(track1, track2);
		if (!isInDistanceWithTimeComponent(track1, track2, interval, dist)) {
			bool = false;
		}
		for (int i = 0; i < otherTracks.size(); i++) {
			Track track = otherTracks.get(i);
			Interval intersectionInterval1 = createInterval(track1, track);
			Interval intersectionInterval2 = createInterval(track2, track);

			if (isInDistanceWithTimeComponent(track1, track, intersectionInterval1, dist)
					|| isInDistanceWithTimeComponent(track2, track, intersectionInterval2, dist)) {
				bool = false;

			}
		}

		if (bool == true) {
			ArrayList<Track> bothTracks = new ArrayList<Track>();
			track1.setAisMessages(findAISMessages(interval, track1));
			bothTracks.add(track1);
			track2.setAisMessages(findAISMessages(interval, track2));
			bothTracks.add(track2);
			return bothTracks;
		} else {
			return null;
		}

	}

	public ArrayList<Track> twoShipsProximity(Track track1, Track track2, double dist) {
		boolean bool = true;
		Interval interval = createInterval(track1, track2);
		if (!isInDistanceWithTimeComponent(track1, track2, interval, dist)) {
			bool = false;
		}

		if (bool == true) {
			ArrayList<Track> bothTracks = new ArrayList<Track>();
			track1.setAisMessages(findAISMessages(interval, track1));
			bothTracks.add(track1);
			track2.setAisMessages(findAISMessages(interval, track2));
			bothTracks.add(track2);
			return bothTracks;
		} else {
			return null;
		}

	}

	// Predict a maneuver for the give way ship. The distance between both ships
	// at the time of the maneuver is determined by the minimum passing distance
	// at which other ships have not made a maneuver and still did not collide.
	// msg1 belongs to own ship, msg2 to target ship.
	public ArrayList<Double> predictManeuver(AISMessage msg1, AISMessage msg2) {
		double maneuver;
		maneuver = 0;
		// MS=milliseconds
		double timeToManeuverMS = 0;
		ArrayList<Double> maneuverAndTime = new ArrayList<Double>();
		Coordinate start = new Coordinate(msg1.getLat(), msg1.getLon());
		Coordinate end = new Coordinate(msg2.getLat(), msg2.getLon());
		double distStart = calculateDistanceNM(start, end);
		OwnShip ownShip = new OwnShip();
		TargetShip targetShip = new TargetShip();

		// initialising own ship with AIS message
		OwnShipNavigationData ownShipNavigationData = new OwnShipNavigationData();
		ownShipNavigationData.setCog(String.valueOf(msg1.getCog()));
		ownShipNavigationData.setCogAsDouble(msg1.getCog());
		ownShipNavigationData.setLat(String.valueOf(msg1.getLat()));
		ownShipNavigationData.setLon(String.valueOf(msg1.getLon()));
		ownShipNavigationData.setSog(String.valueOf(msg1.getSog()));
		ownShip.setOwnShipNavigationData(ownShipNavigationData);

		// initialising target ship with AIS message

		RaTrackDataItem raTrackDataItem = new RaTrackDataItem();
		raTrackDataItem.setCog(String.valueOf(msg2.getCog()));
		raTrackDataItem.setLat(String.valueOf(msg2.getLat()));
		raTrackDataItem.setLon(String.valueOf(msg2.getLon()));
		raTrackDataItem.setSog(String.valueOf(msg2.getSog()));

		targetShip.setRaTrackDataItem(raTrackDataItem);
		ArrayList<Double> setOfAngles = new ArrayList<Double>();

		for (int k = -17; k <= 17; k++) {
			setOfAngles.add(5.0 * k);
		}

		// Determine the right of way
		TrafficEvaluationResult trafficEvaluationResult = TrafficEvaluator.ownShipRightOfWay(ownShip, targetShip);
		GWSO gwso = trafficEvaluationResult.getOwnShipGWSO();

		// compute all possible DCPA values with COGs in COG+setOfAngles for
		// the case that the own ship is the give way vessel.
		double lenMax = Math.max(Double.parseDouble(ownShip.getStaticShipData().getLength()), 
				Double.parseDouble(targetShip.getAisStaticShipData().getCcrpDistanceFromBow()+
						targetShip.getAisStaticShipData().getCcrpDistanceFromStern()));
		//predDist in NM
		double predDist;
		if(80<=lenMax && lenMax<=179) {
			
			predDist = 0.66;
		} else if(179<lenMax && lenMax<=245) {
			predDist = 0.81;
		} else if (245<lenMax && lenMax<=300) {
			predDist = 0.89;
		} else {
			predDist = 1.27;
		}
		if (distStart < predDist) {
			timeToManeuverMS = 0;
			if (gwso == GWSO.GiveWay) {

				ArrayList<Double> suitableAngles = new ArrayList<Double>();
				for (double angle : setOfAngles) {
					ownShipNavigationData.setCog(String.valueOf((msg1.getCog() + angle) % 360));
					ownShip.setOwnShipNavigationData(ownShipNavigationData);
					CPAResult cpaResult = CPACalculator.calculateCPA(ownShip, targetShip);
					if (cpaResult.getCpaDistance() >= distStart) {
						suitableAngles.add(angle);
					}
				}
				ArrayList<Double> suitableAnglesAbs = new ArrayList<Double>();
				for (double ang : suitableAngles) {
					suitableAnglesAbs.add(Math.abs(ang));
				}
				double minAngle;
				double minAngleAbs = Collections.min(suitableAnglesAbs);
				if (suitableAngles.contains(minAngleAbs) && suitableAngles.contains(-minAngleAbs)) {
					minAngle = minAngleAbs;
				} else if (suitableAngles.contains(minAngleAbs)) {
					minAngle = minAngleAbs;
				} else {
					minAngle = -minAngleAbs;
				}

				// Own ship has right of way. Compute the prediction of the next
				// part of the route of the target ship.
			} else {
				ArrayList<Double> suitableAngles = new ArrayList<Double>();
				for (double angle : setOfAngles) {
					raTrackDataItem.setCog(String.valueOf((msg1.getCog() + angle) % 360));
					targetShip.setRaTrackDataItem(raTrackDataItem);
					CPAResult cpaResult = CPACalculator.calculateCPA(ownShip, targetShip);
					if (cpaResult.getCpaDistance() >= distStart) {
						suitableAngles.add(angle);
					}
				}
				ArrayList<Double> suitableAnglesAbs = new ArrayList<Double>();
				for (double ang : suitableAngles) {
					suitableAnglesAbs.add(Math.abs(ang));
				}
				double minAngle;
				double minAngleAbs = Collections.min(suitableAnglesAbs);
				if (suitableAngles.contains(minAngleAbs) && suitableAngles.contains(-minAngleAbs)) {
					minAngle = minAngleAbs;
				} else if (suitableAngles.contains(minAngleAbs)) {
					minAngle = minAngleAbs;
				} else {
					minAngle = -minAngleAbs;
				}

			}
		}

		maneuverAndTime.add(maneuver);
		maneuverAndTime.add(timeToManeuverMS);
		return maneuverAndTime;
	}

	public static ArrayList<AISMessage> findAISMessages(Interval trackInterval, Track trackV1) {
		if (trackInterval == null) {
			return null;
		} else {
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

					if (distance <= 0.5) {
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

	public ArrayList<Track> calculateTrackDistances(ArrayList<Track> tracksWithoutInfluence) {

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

	private ArrayList<Track> cleanTrackListNew(ArrayList<Track> tracks) {
		ArrayList<Track> tracksToRemove = new ArrayList<Track>();
		for (Track track : tracks) {
			long intervalLenght = track.getEndDate().getTime() - track.getStartDate().getTime();
			double distStartEndTrack;
			AISMessage aisMessageStart = track.getAisMessages().get(0);
			AISMessage aisMessageEnd = track.getAisMessages().get(track.getAisMessages().size() - 1);
			Coordinate start = new Coordinate(aisMessageStart.getLat(), aisMessageStart.getLon());
			Coordinate end = new Coordinate(aisMessageEnd.getLat(), aisMessageEnd.getLon());
			distStartEndTrack = calculateDistanceNM(start, end);
			if (intervalLenght < 120000) {
				tracksToRemove.add(track);
			}
		}
		tracks.removeAll(tracksToRemove);
		return tracks;
	}
}