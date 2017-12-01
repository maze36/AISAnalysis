package testData;

import org.junit.Test;

import app.cpa.CPACalculator;
import app.datamodel.AISMessage;
import app.datamodel.CPAResult;

public class TestCPA {

	@Test
	public void testCPA() {

		AISMessage message1 = new AISMessage();
		message1.setCog(180);
		message1.setHeading(180);
		message1.setMmsi("12345");
		message1.setRot(1);
		message1.setSog(12);
		message1.setLat(53.844717);
		message1.setLon(8.051106);

		AISMessage message2 = new AISMessage();
		message2.setCog(270);
		message2.setHeading(270);
		message2.setMmsi("12345");
		message2.setRot(1);
		message2.setSog(12);
		message2.setLat(53.79172);
		message2.setLon(8.19992);

		CPAResult res = CPACalculator.calculateCPA(message1, message2);

		System.out.println(res);

	}

}
