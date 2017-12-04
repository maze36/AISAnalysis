package util;

import org.geotools.referencing.GeodeticCalculator;

import com.vividsolutions.jts.geom.Coordinate;

import app.datamodel.AISMessage;
import datamodel.RTM;
import datamodel.RTMEdge;
import datamodel.RTMNode;

public class Util {

	private GeodeticCalculator calculator = new GeodeticCalculator();

	public RTMNode findeNearestNode(AISMessage aisMessage, RTM rtm) {

		Coordinate vesselPos = new Coordinate(aisMessage.getLat(), aisMessage.getLon());

		for (RTMEdge edge : rtm.getEdgesWithNodes()) {
			double distance = calculateDistanceNM(edge.getStartNode().getCoordinate(), vesselPos);
			if (minDistance == -1) {
				minDistance = distance;
			} else {
				if (distance < minDistance) {
					minDistance = distance;
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

}
