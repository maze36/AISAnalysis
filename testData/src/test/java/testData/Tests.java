package testData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import app.datamodel.VesselContainer;
import datamodel.Track;
import input.CSVReader;

public class Tests {

	Track t1;
	Track t2;

	@Before
	public void init() {
		this.t1 = new Track();
		this.t2 = new Track();

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
		String startDate1String = "2017-11-06T06:43:59.0";
		String endDate1String = "2017-11-06T19:43:59.0";

		String startDate2String = "2017-11-06T12:43:59.0";
		String endDate2String = "2017-11-06T20:43:59.0";

		try {
			Date startDate1 = df.parse(startDate1String);
			Date endDate1 = df.parse(endDate1String);

			Date startDate2 = df.parse(startDate2String);
			Date endDate2 = df.parse(endDate2String);

			this.t1.setStartDate(startDate1);
			this.t1.setEndDate(endDate1);
			this.t2.setStartDate(startDate2);
			this.t2.setEndDate(endDate2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testVoyageReadIn() {
		VesselContainer cont = CSVReader.readVoyageData();

		cont = CSVReader.readDynamicData(cont);

	}

}
