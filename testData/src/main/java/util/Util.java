package util;

import java.util.Iterator;

import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.Graph;
import org.geotools.referencing.GeodeticCalculator;

import com.vividsolutions.jts.geom.Coordinate;

import app.datamodel.AISMessage;
import datamodel.RTMNode;

public class Util {

	private static GeodeticCalculator calculator = new GeodeticCalculator();

	public static RTMNode findeNearestNode(AISMessage aisMessage, Graph rtm) {

		Coordinate vesselPos = new Coordinate(aisMessage.getLat(), aisMessage.getLon());
		double minDistance = -1;
		double bearing = 0;

		for (@SuppressWarnings("rawtypes")
		Iterator iterator = rtm.getEdges().iterator(); iterator.hasNext();) {
			Edge edge = (Edge) iterator.next();
			Coordinate coordNodeA = (Coordinate) edge.getNodeA().getObject();
			Coordinate coordNodeB = (Coordinate) edge.getNodeB().getObject();
			double newDistanceNodeA = calculateDistanceNM(coordNodeA, vesselPos);
			double newDistanceNodeB = calculateDistanceNM(coordNodeB, vesselPos);
			if (minDistance == -1) {
				if (newDistanceNodeA < newDistanceNodeB) {
					minDistance = newDistanceNodeA;
					bearing = calculateBearing(vesselPos, coordNodeA);
				} else {
					minDistance = newDistanceNodeB;
					bearing = calculateBearing(vesselPos, coordNodeB);
				}
			} else {
				if (newDistanceNodeA < newDistanceNodeB) {
					if (newDistanceNodeA < minDistance) {
						minDistance = newDistanceNodeA;
						bearing = calculateBearing(vesselPos, coordNodeA);
					}
				} else {
					if (newDistanceNodeB < minDistance) {
						minDistance = newDistanceNodeB;
						bearing = calculateBearing(vesselPos, coordNodeB);
					}
				}
			}
		}
		System.out.println();
		return null;
	}

	public static double calculateDistanceNM(Coordinate start, Coordinate end) {
		calculator.setStartingGeographicPoint(start.y, start.x);
		calculator.setDestinationGeographicPoint(end.y, end.x);
		return (calculator.getOrthodromicDistance()) * 0.000539957;
	}

	private static double calculateBearing(Coordinate start, Coordinate end) {
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

}
