package testData;

import org.junit.Test;

import datamodel.RTM;
import datamodel.RTMEdge;
import shapefile.ShapefileReader;

public class ShapeTest {

	@Test
	public void testShape() {
		String t = "C:/Users/msteidel/Desktop/RTM_MWotS_jun14/RTM_MWotS_jun14_clean.shp";
		RTM rtm = ShapefileReader.getRTM(t);

		for (RTMEdge edge : rtm.getEdgesWithNodes()) {

		}

		System.out.println(rtm);

	}

}
