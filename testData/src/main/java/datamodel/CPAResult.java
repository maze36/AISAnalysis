package datamodel;

import java.awt.geom.Point2D;

import ais.AISMessage;

public class CPAResult implements Comparable<CPAResult> {

	double cpaDistance;
	double cpaTime;

	Point2D ownShipCPA;
	Point2D otherShipCPA;

	/**
	 * 
	 * @param ownShip
	 *            - our own ship
	 * @param targetShip
	 *            - the target ship
	 * @param cpaDistance
	 *            - the distance between ownShip and targetShip
	 * @param cpaTime
	 *            - the time of ownShip till reached ownShipCPA
	 * @param ownShipCPA
	 *            - the ownShip's CPA position - order: lat/lon
	 * @param otherShipCPA
	 *            - the otherShip's CPA position - order: lat/lon
	 */
	public CPAResult(AISMessage message1, AISMessage message2, double cpaDistance, double cpaTime, Point2D ownShipCPA,
			Point2D otherShipCPA) {
		super();
		this.cpaDistance = cpaDistance;
		this.cpaTime = cpaTime;
		this.ownShipCPA = ownShipCPA;
		this.otherShipCPA = otherShipCPA;
	}

	/**
	 * Ctor
	 */
	public CPAResult() {
	}

	public double getCpaDistance() {
		return cpaDistance;
	}

	public void setCpaDistance(double cpaDistance) {
		this.cpaDistance = cpaDistance;
	}

	public double getCpaTime() {
		return cpaTime;
	}

	public void setCpaTime(double cpaTime) {
		this.cpaTime = cpaTime;
	}

	public Point2D getOwnShipCPA() {
		return ownShipCPA;
	}

	public void setOwnShipCPA(Point2D ownShipCPA) {
		this.ownShipCPA = ownShipCPA;
	}

	public Point2D getOtherShipCPA() {
		return otherShipCPA;
	}

	public void setOtherShipCPA(Point2D otherShipCPA) {
		this.otherShipCPA = otherShipCPA;
	}

	public int compareTo(CPAResult other) {
		// compares CPA distances
		if (this.getCpaDistance() < other.getCpaDistance()) {
			return -1;
		}
		if (this.getCpaDistance() == other.getCpaDistance()) {
			return 0;
		}
		if (this.getCpaDistance() > other.getCpaDistance()) {
			return 1;
		}
		return 0;
	}

}
