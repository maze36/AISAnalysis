package util;

import java.util.ArrayList;
import java.util.Iterator;

import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.Graph;
import org.geotools.referencing.GeodeticCalculator;

import com.vividsolutions.jts.geom.Coordinate;

import app.datamodel.AISMessage;

public class Util {

	private static GeodeticCalculator calculator = new GeodeticCalculator();

	public static Edge findeNearestEdge(AISMessage aisMessage, Graph rtm) {

		Coordinate vesselPos = new Coordinate(aisMessage.getLat(), aisMessage.getLon());
		double minDistance = -1;
		Edge nearestEdge = null;
		ArrayList<Edge> nearestEdges = new ArrayList<Edge>();

		for (@SuppressWarnings("rawtypes")
		Iterator iterator = rtm.getEdges().iterator(); iterator.hasNext();) {
			Edge edge = (Edge) iterator.next();
			Coordinate coordNodeA = (Coordinate) edge.getNodeA().getObject();
			Coordinate coordNodeB = (Coordinate) edge.getNodeB().getObject();
			double newDistanceNodeA = calculateDistanceNM(coordNodeA, vesselPos);
			double newDistanceNodeB = calculateDistanceNM(coordNodeB, vesselPos);

			if (newDistanceNodeA <= 5 || newDistanceNodeB <= 5) {
				nearestEdges.add(edge);
			}
		}

		return nearestEdge;
	}

	public static void determineNode(Edge nearestEdge, AISMessage aisMessage) {
		Coordinate vesselPosition = new Coordinate(aisMessage.getLat(), aisMessage.getLon());

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
