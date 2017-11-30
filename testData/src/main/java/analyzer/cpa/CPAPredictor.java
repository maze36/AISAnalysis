package analyzer.cpa;

import org.geotools.referencing.GeodeticCalculator;

import ais.AISMessage;
import analyzer.trafficSituation.TrafficEvaluator;
import datamodel.CPAResult;
import datamodel.EvaluationObject;
import output.Encounter;

public class CPAPredictor {

	GeodeticCalculator calculator = new GeodeticCalculator();

	public EvaluationObject predictAndCompareCPA(Encounter encounter) {

		long timestampCPAV1 = encounter.getAisMessage1().getTimestamp().getTime();
		long timestampCPAV2 = encounter.getAisMessage2().getTimestamp().getTime();

		AISMessage aisMessagePredV1 = new AISMessage();
		AISMessage aisMessagePredV2 = new AISMessage();
		// suche t-x minuten

		for (AISMessage aisMessage : encounter.getTrackVessel1().getAisMessages()) {
			long timeAIS = aisMessage.getTimestamp().getTime();

			long diff = timestampCPAV1 - timeAIS;

			if (diff <= 1820000 && diff >= 1780000) {
				aisMessagePredV1 = aisMessage;
				break;
			}
		}

		for (AISMessage aisMessage : encounter.getTrackVessel2().getAisMessages()) {
			long timeAIS = aisMessage.getTimestamp().getTime();

			long diff = timestampCPAV2 - timeAIS;

			if (diff <= 1220000 && diff >= 1180000) {
				aisMessagePredV2 = aisMessage;
				break;
			}
		}

		if (aisMessagesValid(aisMessagePredV1, aisMessagePredV2)) {
			return predictCPA(aisMessagePredV1, aisMessagePredV2, encounter);
		} else {
			return null;
		}

	}

	private boolean aisMessagesValid(AISMessage aisMessagePredV1, AISMessage aisMessagePredV2) {
		if (aisMessagePredV1.getLat() == 0.0 || aisMessagePredV1.getLon() == 0.0) {
			return false;
		} else if (aisMessagePredV2.getLat() == 0.0 || aisMessagePredV2.getLon() == 0.0) {
			return false;
		} else {
			return true;
		}
	}

	private EvaluationObject predictCPA(AISMessage aisMessagePredV1, AISMessage aisMessagePredV2, Encounter encounter) {

		CPAResult cpaResultV1 = CPACalculator.calculateCPA(aisMessagePredV1, aisMessagePredV2);

		calculator.setStartingGeographicPoint(cpaResultV1.getOwnShipCPA().getY(), cpaResultV1.getOwnShipCPA().getX());
		calculator.setDestinationGeographicPoint(cpaResultV1.getOtherShipCPA().getY(),
				cpaResultV1.getOtherShipCPA().getX());

		double predictedDistance = calculator.getOrthodromicDistance() * 0.000539957;

		calculator.setStartingGeographicPoint(encounter.getAisMessage1().getLon(), encounter.getAisMessage1().getLat());
		calculator.setDestinationGeographicPoint(cpaResultV1.getOwnShipCPA().getY(),
				cpaResultV1.getOwnShipCPA().getX());

		double distanceV1 = calculator.getOrthodromicDistance() * 0.000539957;

		calculator.setStartingGeographicPoint(encounter.getAisMessage2().getLon(), encounter.getAisMessage2().getLat());
		calculator.setDestinationGeographicPoint(cpaResultV1.getOtherShipCPA().getY(),
				cpaResultV1.getOtherShipCPA().getX());

		double distanceV2 = calculator.getOrthodromicDistance() * 0.000539957;

		double realDistance = calculator.getOrthodromicDistance() * 0.000539957;

		EvaluationObject evaluationObject = new EvaluationObject(aisMessagePredV1, aisMessagePredV2, realDistance,
				predictedDistance, distanceV1, distanceV2);

		evaluationObject.setSituation(TrafficEvaluator.rightOfWay(encounter));
		return evaluationObject;

	}

}
