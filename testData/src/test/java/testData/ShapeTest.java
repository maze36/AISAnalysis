package testData;

import org.geotools.graph.structure.Graph;
import org.junit.Test;

import app.datamodel.AISMessage;
import shapefile.ShapefileReader;
import util.Util;

public class ShapeTest {

	@Test
	public void testShape() {
		String t = "C:/Users/msteidel/Desktop/RTM_MWotS_jun14/RTM_MWotS_jun14_clean.shp";
		Graph rtm = ShapefileReader.getRTM(t);

		AISMessage aisMessage = new AISMessage();
		aisMessage.setLat(53.4);
		aisMessage.setLon(8.56);

		Util.findeNearestNode(aisMessage, rtm);

		System.out.println(rtm);

	}

}
