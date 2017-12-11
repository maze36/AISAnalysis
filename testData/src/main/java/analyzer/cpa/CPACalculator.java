package analyzer.cpa;

import java.awt.geom.Point2D;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import app.datamodel.AISMessage;
import datamodel.CPAResult;

/**
 * CPACalculator Calculation of the distance of own and target ship at their CPA
 * and the time to the CPA
 * 
 * @author lsiegel, cdenker
 *
 */
public class CPACalculator {

	static GeodeticCalculator gc = new GeodeticCalculator();

	/**
	 * Calculation of the time to CPA of own and target ship in seconds
	 * 
	 * @param pos1
	 * @param pos2
	 * @param vec1
	 * @param vec2
	 * @return Time to CPA
	 */
	public static double CPATime(Vector2D pos1, Vector2D pos2, Vector2D vec1, Vector2D vec2) {
		Vector2D dv = vec1.subtract(vec2);
		double dv2 = dv.dotProduct(dv);
		if (dv2 < 0.00001)
			return 0; // parallel, all times are the same.
		Vector2D w0 = pos1.subtract(pos2);
		double cpatime = -w0.dotProduct(dv) / dv2;
		return cpatime;
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return euclidean distance between a & b
	 */
	public static double distance(Vector2D a, Vector2D b) {
		double dx = a.getX() - b.getX();
		double dy = a.getY() - b.getY();
		return Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * 
	 * @param pos1
	 *            First position as a vector.
	 * @param pos2
	 *            Second position as a vector.
	 * @param vec1
	 *            First vector.
	 * @param vec2
	 *            Second vector.
	 * @return - Distance at CPA in meters.
	 */
	public static double CPADistance(Vector2D pos1, Vector2D pos2, Vector2D vec1, Vector2D vec2) {
		// find CPA time
		double ctime = CPATime(pos1, pos2, vec1, vec2);
		Vector2D P1 = pos1.add(vec1.scalarMultiply(ctime));
		Vector2D P2 = pos2.add(vec2.scalarMultiply(ctime));
		return distance(P1, P2);
	}

	/**
	 * 
	 * @param ownVec
	 *            - vector als lon/lat(!)
	 * @param angle_radian
	 * @return
	 */
	public static Vector2D rotateCW(Vector2D ownVec, double angle_radian) {
		double ca = Math.cos(angle_radian);
		double sa = Math.sin(angle_radian);
		// double xx = ownVec.getX() * cos + ownVec.getX() * sin;
		// double yy = ownVec.getX() *-sin + ownVec.getX() * cos;
		return new Vector2D(sa * ownVec.getX() + ca * ownVec.getY(), ca * ownVec.getX() - sa * ownVec.getY());
	}

	private final static double Meter2NauticalMile = 0.000539957;

	/**
	 * calculates the {@link CPAResult} of an {@link OwnShip} and a given
	 * {@link TargetShip}
	 * 
	 * @param ownShip
	 *            - the ownShip
	 * @param targetShip
	 *            - the targetShip
	 * @return - the {@link CPAResult} of both ships.
	 */
	public static CPAResult calculateCPA(AISMessage message1, AISMessage message2) {

		// knots to m/s
		double ownSOG = message1.getSog() * 0.5144444444444445;
		double otherSOG = message2.getSog() * 0.5144444444444445;

		// create coordinates
		gc.setStartingGeographicPoint(message1.getLon(), message1.getLat());
		gc.setDestinationGeographicPoint(message2.getLon(), message2.getLat());

		double bearing = gc.getAzimuth();
		double distance = gc.getOrthodromicDistance();

		Vector2D pos1 = new Vector2D(0, 0);
		Vector2D pos2 = rotateCW(new Vector2D(distance, 0), Math.toRadians(bearing));

		double ownCog = message1.getCog();

		double otherCog = message2.getCog();

		Vector2D ownVec = new Vector2D(ownSOG, 0);
		ownVec = CPACalculator.rotateCW(ownVec, Math.toRadians(ownCog));
		Vector2D otherVec = new Vector2D(otherSOG, 0);
		otherVec = CPACalculator.rotateCW(otherVec, Math.toRadians(otherCog));

		// calculate CPAd & tCPA
		double cpa = CPACalculator.CPADistance(pos1, pos2, ownVec, otherVec) * Meter2NauticalMile;
		double tcpa = CPACalculator.CPATime(pos1, pos2, ownVec, otherVec);

		// calculate CPA position of ownShip
		GeodeticCalculator geocalc = new GeodeticCalculator(DefaultGeographicCRS.WGS84);
		// mind, this is lon/lat
		geocalc.setStartingGeographicPoint(message1.getLon(), message1.getLat());
		geocalc.setDirection(ownCog /* azimuth */, ownVec.scalarMultiply(tcpa).distance(new Vector2D(0, 0))
		/* distance in meters */);
		Point2D dest = geocalc.getDestinationGeographicPoint();

		Vector2D otherNewPos = otherVec.scalarMultiply(tcpa);
		double distanceOther = otherNewPos.distance(new Vector2D(0, 0));

		geocalc.setStartingGeographicPoint(message2.getLon(), message2.getLat());
		geocalc.setDirection(otherCog /* azimuth */, distanceOther);
		Point2D destOther = geocalc.getDestinationGeographicPoint();

		Point2D retDest = new Point2D.Double();
		retDest.setLocation(dest.getY(), dest.getX());

		Point2D retDest2 = new Point2D.Double();
		retDest2.setLocation(destOther.getY(), destOther.getX());

		return new CPAResult(message1, message2, cpa, tcpa, retDest, retDest2, message1, message2);

	}

	public static double mod(double x, double y) {
		double result = x % y;
		if (result < 0) {
			result += y;
		}
		return result;
	}
}
