package shapefile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.feature.Feature;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import datamodel.RTM;
import datamodel.RTMEdge;
import datamodel.RTMNode;

public class ShapefileReader {
	/**
	 * Reads in a shape and returns an {@link ArrayList} that contains the
	 * {@link Coordinate}.
	 * 
	 * @param path
	 *            The path of the shapefile
	 * @return
	 */

	public static RTM getRTM(String path) {
		ArrayList<Geometry> rtmList = readShapefile(path);
		return buildRTM(rtmList);
	}

	private static RTM buildRTM(ArrayList<Geometry> rtmList) {
		RTM rtm = new RTM();
		for (Geometry geometry : rtmList) {
			RTMNode startNode = new RTMNode(geometry.getCoordinates()[0]);
			RTMNode endNode = new RTMNode(geometry.getCoordinates()[1]);
			RTMEdge edge = new RTMEdge(startNode, endNode);
			rtm.addEdge(edge);
		}
		return rtm;
	}

	private static File getFile(String path) {
		File file = new File(path);

		return file;
	}

	@SuppressWarnings("unused")
	private static ArrayList<Coordinate> convertLineStringIntoCoordinate(ArrayList<Geometry> lineStrings) {

		ArrayList<Coordinate> result = new ArrayList<Coordinate>();

		for (Geometry geo : lineStrings) {
			for (int i = 0; i < geo.getCoordinates().length; i++) {
				Coordinate coord = geo.getCoordinates()[i];
				result.add(coord);
			}
		}

		return result;
	}

	private static ArrayList<Geometry> readShapefile(String path) {
		File shapefile = getFile("C:/Users/msteidel/Desktop/RTM_MWotS_jun14/RTM_MWotS_jun14_clean.shp");

		HashMap<String, URL> connect = new HashMap<String, URL>();
		ArrayList<Geometry> result = new ArrayList<Geometry>();
		try {
			connect.put("url", shapefile.toURI().toURL());
			DataStore dataStore = DataStoreFinder.getDataStore(connect);
			String[] typeNames = dataStore.getTypeNames();
			String typeName = typeNames[0];

			FeatureSource<?, ?> featureSource = dataStore.getFeatureSource(typeName);
			FeatureCollection<?, ?> collection = featureSource.getFeatures();
			SimpleFeatureType schema = (SimpleFeatureType) featureSource.getSchema();
			CoordinateReferenceSystem dataCRS = schema.getCoordinateReferenceSystem();
			CoordinateReferenceSystem worldCRS = CRS.decode("EPSG:4326");
			MathTransform transform = CRS.findMathTransform(dataCRS, worldCRS);
			FeatureIterator<?> iterator = collection.features();

			while (iterator.hasNext()) {
				Feature feature = iterator.next();
				GeometryAttribute geometryAttr = feature.getDefaultGeometryProperty();
				Geometry geometry = (Geometry) geometryAttr.getValue();
				if (geometry != null) {
					Geometry resultGeometry = JTS.transform(geometry, transform);
					result.add(resultGeometry);
				}
			}
			iterator.close();
			return result;

		} catch (IOException | FactoryException | MismatchedDimensionException | TransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
}
