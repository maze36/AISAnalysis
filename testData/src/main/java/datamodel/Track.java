package datamodel;

import java.util.ArrayList;
import java.util.Date;

import app.datamodel.AISMessage;

public class Track {

	private int id;
	private String mmsi;
	private ArrayList<AISMessage> aisMessages;

	private Date startDate;

	private Date endDate;

	public Track(int id, ArrayList<AISMessage> aisMessages, Date startDate, Date endDate) {
		super();
		this.id = id;
		this.aisMessages = aisMessages;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Track() {
		// TODO Auto-generated constructor stub
	}

	public Track(AISMessage message) {
		this.startDate = message.getTimestamp();
		this.endDate = this.startDate;
		this.aisMessages = new ArrayList<AISMessage>();
		this.aisMessages.add(message);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<AISMessage> getAisMessages() {
		return aisMessages;
	}

	public boolean addMessage(AISMessage aisMessage) {
		long timeLastMsg = this.aisMessages.get(this.aisMessages.size() - 1).getTimestamp().getTime();
		long timeDiff = aisMessage.getTimestamp().getTime() - timeLastMsg;
		if (timeDiff <= 150000) {
			aisMessages.add(aisMessage);
			setEndDate(aisMessage.getTimestamp());
			return true;
		} else {
			return false;
		}

	}

	public void setAisMessages(ArrayList<AISMessage> aisMessages) {
		this.aisMessages = aisMessages;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getMmsi() {
		return mmsi;
	}

	public void setMmsi(String mmsi) {
		this.mmsi = mmsi;
	}

}
