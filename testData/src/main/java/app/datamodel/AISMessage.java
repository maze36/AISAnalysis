package app.datamodel;

import java.util.Date;

public class AISMessage {

	private String mmsi;
	private double heading;
	private double sog;
	private double cog;
	private double rot;
	private Date timestamp;
	private double lat;
	private double lon;

	public String getMmsi() {
		return mmsi;
	}

	public double getHeading() {
		return heading;
	}

	public double getSog() {
		return sog;
	}

	public double getCog() {
		return cog;
	}

	public double getRot() {
		return rot;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public void setMmsi(String mmsi) {
		this.mmsi = mmsi;
	}

	public void setHeading(double heading) {
		this.heading = heading;
	}

	public void setSog(double sog) {
		this.sog = sog;
	}

	public void setCog(double cog) {
		this.cog = cog;
	}

	public void setRot(double rot) {
		this.rot = rot;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

}
