package datamodel;

import com.vividsolutions.jts.geom.Coordinate;

public class RTMNode {

	private Coordinate coordinate;

	public RTMNode(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

}
