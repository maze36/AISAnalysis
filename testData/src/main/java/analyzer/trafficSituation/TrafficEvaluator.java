package analyzer.trafficSituation;

import java.awt.geom.Point2D;

import org.geotools.referencing.GeodeticCalculator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import analyzer.cpa.CPACalculator;
import app.datamodel.AISMessage;
import datamodel.LengthUnit;
import datamodel.Situation;
import output.Encounter;

public class TrafficEvaluator {

	/**
	 * Function: Computes the angle between the routes of 2 ships. Looks at the
	 * difference of the bearing between the intersection of the routes and the
	 * target ship route and the bearing between the intersection and the own
	 * ship route.
	 * 
	 * @param ownShip
	 * @param targetShip
	 * @return complex type Double so null can be returned as well.
	 */
	private static Double angleShipRoutes(AISMessage messageV1, AISMessage messageV2) {
		double cogOwnShip = messageV1.getCog();

		double cogTargetShip = messageV2.getCog();

		Coordinate coordOS = new Coordinate(messageV1.getLat(), messageV1.getLon());

		Coordinate coordTS = new Coordinate(messageV2.getLat(), messageV2.getLon());
		LineString ownShipLine = createLineString(coordOS, cogOwnShip, 20);

		LineString targetShipLine = createLineString(coordTS, cogTargetShip, 20);

		if (ownShipLine.intersects(targetShipLine)) {
			Geometry geomOSTS = ownShipLine.intersection(targetShipLine);
			Coordinate coordOSTS = geomOSTS.getCoordinate();
			double bearingIntersectionOS = calculateAzimuthInDegrees(coordOSTS, coordOS);
			double bearingIntersectionTS = calculateAzimuthInDegrees(coordOSTS, coordTS);
			double angleResult = bearingIntersectionOS - bearingIntersectionTS;
			return angleResult;
		}
		return null;
	}

	public static Situation rightOfWay(Encounter encounter) {

		AISMessage aisMessageShip1 = encounter.getAisMessage1();
		AISMessage aisMessageShip2 = encounter.getAisMessage2();

		double cogOwnShip = aisMessageShip1.getCog();

		double cogTargetShip = aisMessageShip2.getCog();

		double sogOwnShip = aisMessageShip1.getSog();

		double sogTargetShip = aisMessageShip2.getSog();

		Coordinate coordOS = new Coordinate(aisMessageShip1.getLat(), aisMessageShip1.getLon());

		Coordinate coordTS = new Coordinate(aisMessageShip2.getLat(), aisMessageShip2.getLon());

		LineString ownShipLine = createLineString(coordOS, cogOwnShip, 20);

		LineString targetShipLine = createLineString(coordTS, cogTargetShip, 20);
		Geometry geomOSTS = ownShipLine.intersection(targetShipLine);
		Coordinate coordOSTS = geomOSTS.getCoordinate();
		double bearingOSTS = calculateAzimuthInDegrees(coordOS, coordTS);
		/*
		 * Function: If the routes of both ships do not intersect and the DCPA
		 * is sufficiently large, then there is no danger.
		 * 
		 */
		// System.out.println("CPADist: " + CPACalculator.calculateCPA(ownShip,
		// targetShip).getCpaDistance());
		if (!targetShipLine.intersects(ownShipLine)) {
			if ((177 < Math.abs(cogOwnShip - cogTargetShip)) && (Math.abs(cogOwnShip - cogTargetShip) < 183)
					&& (CPACalculator.calculateCPA(aisMessageShip1, aisMessageShip2).getCpaDistance() <= 2)
					&& (calculateDistance(coordOS, coordTS, LengthUnit.NAUTICALMILES) >= 0.3)) {
				// System.out.println("We are in a head-on situation and both
				// ships have to give way. "
				// + "Make a starboard maneuver. The target ship will probably
				// make a starboard maneuver.");
				return Situation.HeadOn;

			} else if ((((Math.abs(cogOwnShip - cogTargetShip) <= 22.5))
					|| ((337.5 <= Math.abs(cogOwnShip - cogTargetShip))))
					&& (CPACalculator.calculateCPA(aisMessageShip1, aisMessageShip2).getCpaDistance() <= 2)
					&& (calculateDistance(coordOS, coordTS, LengthUnit.NAUTICALMILES) >= 0.3)) {
				if (((Math.abs(bearingOSTS - cogOwnShip) <= 45) || (Math.abs(bearingOSTS - cogOwnShip) >= 315))
						&& (sogOwnShip > sogTargetShip)) {
					if (((cogOwnShip - cogTargetShip >= 0) && (cogOwnShip - cogTargetShip <= 90))
							|| (cogOwnShip - cogTargetShip <= -270)) {
						// System.out.println(
						// "We are overtaking the target ship, so we are the
						// give way vessel. Make a port maneuver.");
						encounter.setSituation(Situation.Overtaking);
						return Situation.Overtaking;

					} else {
						// System.out.println(
						// "We are overtaking the target ship, so we are the
						// give way vessel. Make a starboard maneuver.");
						return Situation.Overtaking;
					}
				} else if (((Math.abs(bearingOSTS - cogOwnShip) <= 45) || (Math.abs(bearingOSTS - cogOwnShip) >= 315))
						&& (sogOwnShip < sogTargetShip)) {
					// System.out.println("No danger because we are staying
					// behind the target ship.");
					return Situation.NoDanger;
				} else if (sogTargetShip > sogOwnShip) {
					if (((cogTargetShip - cogOwnShip >= 0) && (cogTargetShip - cogOwnShip <= 90))
							|| (cogTargetShip - cogOwnShip <= -270)) {
						// System.out.println("The target ship is overtaking us,
						// so we are the stand on vessel. "
						// + "The target ship will probably make a port
						// maneuver.");
						return Situation.Overtaking;
					} else {
						// System.out.println("The target ship is overtaking us,
						// so we are the stand on vessel. "
						// + "The target ship will probably make a starboard
						// maneuver.");
						return Situation.Overtaking;
					}
				} else {
					// System.out.println("No danger because the target ship is
					// staying behind us.");
					return Situation.NoDanger;

				}
			} else if ((calculateDistance(coordOS, coordTS, LengthUnit.NAUTICALMILES) <= 0.3)) {
				// System.out.println("Jump from boat/ use life raft!");
				return Situation.JumpFromBoat;
			}

			else {
				// System.out.println("No danger");
				return Situation.NoDanger;

			}
		}

		else if (((177 <=

		angleShipRoutes(aisMessageShip1, aisMessageShip2))
				&& (angleShipRoutes(aisMessageShip1, aisMessageShip2) <= 180))
				|| ((-180 <= angleShipRoutes(aisMessageShip1, aisMessageShip2))
						&& (angleShipRoutes(aisMessageShip1, aisMessageShip2) <= -177))) {
			// System.out.println(
			// "We are in a head-on situation and both ships have to give way.
			// Make a starboard maneuver. "
			// + "The target ship will probably make a starboard maneuver.");
			return Situation.HeadOn;

		} else if (((0 <= angleShipRoutes(aisMessageShip1, aisMessageShip2))
				&& (angleShipRoutes(aisMessageShip1, aisMessageShip2) <= 22.5))
				|| ((-22.5 <= angleShipRoutes(aisMessageShip1, aisMessageShip2))
						&& (angleShipRoutes(aisMessageShip1, aisMessageShip2) <= 0))
						&& (calculateDistance(coordOS, coordOSTS, LengthUnit.NAUTICALMILES) <= calculateDistance(
								coordTS, coordOSTS, LengthUnit.NAUTICALMILES))) {
			if (sogOwnShip < sogTargetShip) {
				if ((-22.5 <= angleShipRoutes(aisMessageShip1, aisMessageShip2))
						&& (angleShipRoutes(aisMessageShip1, aisMessageShip2) <= 0)) {
					// System.out.println("The target ship is overtaking us, so
					// we are the stand on vessel. "
					// + "The target ship will probably make a port maneuver.");
					return Situation.Overtaking;
				} else {
					// System.out.println("The target ship is overtaking us, so
					// we are the stand on vessel. "
					// + "The target ship will probably make a starboard
					// maneuver.");
					return Situation.Overtaking;
				}

			} else {
				// System.out.println("No danger because the target ship is
				// staying behind us.");
				return Situation.NoDanger;

			}
		} else if (((0 <= angleShipRoutes(aisMessageShip1, aisMessageShip2))
				&& (angleShipRoutes(aisMessageShip1, aisMessageShip2) <= 22.5))
				|| ((-22.5 <= angleShipRoutes(aisMessageShip1, aisMessageShip2))
						&& (angleShipRoutes(aisMessageShip1, aisMessageShip2) <= 0))
						&& (calculateDistance(coordOS, coordOSTS, LengthUnit.NAUTICALMILES) >= calculateDistance(
								coordTS, coordOSTS, LengthUnit.NAUTICALMILES))) {
			if (sogOwnShip > sogTargetShip) {
				if ((-22.5 <= angleShipRoutes(aisMessageShip1, aisMessageShip2))
						&& (angleShipRoutes(aisMessageShip1, aisMessageShip2) <= 0)) {
					// System.out.println(
					// "We are overtaking the target ship, so we are the give
					// way vessel. Make a port maneuver.");
					return Situation.Overtaking;

				} else {
					// System.out.println(
					// "We are overtaking the target ship, so we are the give
					// way vessel. Make a starboard maneuver.");
					return Situation.Overtaking;

				}
			} else {
				// System.out.println("No danger because we are staying behind
				// the target ship.");
				return Situation.NoDanger;
			}
		} else if ((angleShipRoutes(aisMessageShip1, aisMessageShip2) + 360) % 360 <= 180) {
			// System.out.println(
			// "We are in a crossing situation and the target ship has right of
			// way. Make a starboard maneuver. "
			// + "The target ship will probably make a starboard maneuver.");
			return Situation.Crossing;
		} else {
			// System.out.println(
			// "We are in a crossing situation and the own ship has right of
			// way. Make a starboard maneuver. "
			// + "The target ship will probably make a starboard maneuver.");
			return Situation.Crossing;
		}

	}

	private static double calculateDistance(Coordinate start, Coordinate end, LengthUnit unit) {
		GeodeticCalculator calculator = new GeodeticCalculator();
		calculator.setStartingGeographicPoint(start.y, start.x);
		calculator.setDestinationGeographicPoint(end.y, end.x);
		double distance = calculator.getOrthodromicDistance();

		switch (unit) {
		case NAUTICALMILES:
			distance = distance * 0.000539957;
			return distance;
		case METER:
			return distance;
		}

		return distance;
	}

	private static double calculateAzimuthInDegrees(Coordinate start, Coordinate end) {
		GeodeticCalculator calculator = new GeodeticCalculator();
		calculator.setStartingGeographicPoint(start.y, start.x);
		calculator.setDestinationGeographicPoint(end.y, end.x);
		double azimuth = calculator.getAzimuth();

		calculator.getCoordinateReferenceSystem();

		double azimuthInDegrees = 0;

		if (azimuth < 0.0) {
			azimuthInDegrees = 360.0 + azimuth;
		} else {
			azimuthInDegrees = azimuth;
		}

		return azimuthInDegrees;

	}

	private static LineString createLineString(Coordinate startPosition, double cog, double distance) {

		// 1. Endpunkt berechnen
		Point2D endPositionPoint = calculateNewPosition(startPosition, distance, cog);
		Coordinate endPosition = new Coordinate(endPositionPoint.getX(), endPositionPoint.getY());

		// 2. Gerade bauen
		GeometryFactory factory = new GeometryFactory();
		Coordinate[] coords = { startPosition, endPosition };

		LineString result = factory.createLineString(coords);

		return result;

	}

	private static Point2D calculateNewPosition(Coordinate start, double distance, double direction) {

		GeodeticCalculator calculator = new GeodeticCalculator();

		calculator.setStartingGeographicPoint(start.y, start.x);

		distance = distance * 1852;

		calculator.setDirection(direction, distance);

		Point2D result = calculator.getDestinationGeographicPoint();
		result.setLocation(result.getY(), result.getX());

		return result;
	}

}
