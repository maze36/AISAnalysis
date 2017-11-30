package datamodel;

import ais.AISMessage;

public class EvaluationObject {

	private AISMessage aisMessage1;
	private AISMessage aisMessage2;
	private double realDCPA;
	private double predictedDCPA;
	private double deviationV1;
	private double deviationV2;
	private Situation situation;

	public EvaluationObject(AISMessage aisMessage1, AISMessage aisMessage2, double realDCPA, double predictedDCPA,
			double deviationV1, double deviationV2) {
		super();
		this.aisMessage1 = aisMessage1;
		this.aisMessage2 = aisMessage2;
		this.realDCPA = realDCPA;
		this.predictedDCPA = predictedDCPA;
		this.deviationV1 = deviationV1;
		this.deviationV2 = deviationV2;
	}

	public double getDeviationV1() {
		return deviationV1;
	}

	public void setDeviationV1(double deviationV1) {
		this.deviationV1 = deviationV1;
	}

	public double getDeviationV2() {
		return deviationV2;
	}

	public void setDeviationV2(double deviationV2) {
		this.deviationV2 = deviationV2;
	}

	public AISMessage getAisMessage1() {
		return aisMessage1;
	}

	public void setAisMessage1(AISMessage aisMessage1) {
		this.aisMessage1 = aisMessage1;
	}

	public AISMessage getAisMessage2() {
		return aisMessage2;
	}

	public void setAisMessage2(AISMessage aisMessage2) {
		this.aisMessage2 = aisMessage2;
	}

	public double getRealDCPA() {
		return realDCPA;
	}

	public void setRealDCPA(double realDCPA) {
		this.realDCPA = realDCPA;
	}

	public double getPredictedDCPA() {
		return predictedDCPA;
	}

	public void setPredictedDCPA(double predictedDCPA) {
		this.predictedDCPA = predictedDCPA;
	}

	public Situation getSituation() {
		return situation;
	}

	public void setSituation(Situation situation) {
		this.situation = situation;
	}

}
