package crfmnes;

import crfmnes.matrix2017.TCMatrix;

/**
 * Created by masahiro on 2016/12/27.
 */
public class TIndividual {

  private double fEvaluationValue;

  private TCMatrix fZ;

  private TCMatrix fY;

  private TCMatrix fX;

  private double fNorm;

  /**
   * constructor
   */
  public TIndividual() {
    this(0);
  }

  /**
   * constructor
   * @param dim dimension
   */
  public TIndividual(int dim) {
    fEvaluationValue = Double.NaN;
    fX = new TCMatrix(dim);
    fY = new TCMatrix(dim);
    fZ = new TCMatrix(dim);
  }

  public double getEvaluationValue() {
    return fEvaluationValue;
  }

  public void setEvaluationValue(double value) {
    fEvaluationValue = value;
  }

  public void setNorm() {
  	fNorm = fX.normL2();
  }

  public double getNorm() {
  	return fNorm;
  }

  public TCMatrix getZ() {
    return fZ;
  }

  public TCMatrix getY() {
  	return fY;
  }

  public TCMatrix getX() {
    return fX;
  }

  @Override
  public TIndividual clone() {
    return new TIndividual(); // not implemented
  }

}
