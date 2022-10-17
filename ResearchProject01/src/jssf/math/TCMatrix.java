package jssf.math;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import jssf.di.ACParam;
import jssf.math.decompositions.TCCholeskyDecomposition;
import jssf.math.decompositions.TCEigenvalueDecomposition;
import jssf.math.decompositions.TCLUDecomposition;
import jssf.math.decompositions.TCMaths;
import jssf.math.decompositions.TCQRDecomposition;
import jssf.math.decompositions.TCSingularValueDecomposition;
import jssf.random.ICRandom;

/**
 * A general m-by-n matrix class.
 * @author uemura, isao, fnob, khonda
 *
 */
public class TCMatrix implements Serializable, Cloneable {
	
	/** For serialization */
	private static final long serialVersionUID = 1L;

	/** An elements. */
	private double[][] fElements;
	
	/** Row dimension (m). */
	private int fM;
	
	/** Column dimension (n). */
	private int fN;
	
	/**
	 * Constructor.
	 * Creates a matrix with size (0,0).
	 */
	public TCMatrix() {
		this(0, 0);
	}
	
	/**
	 * Constructor.
	 * Creates a matrix with specified size.
	 * All elements are initialized with zero.
	 * @param m
	 * @param n
	 */
	public TCMatrix(
			@ACParam(key="RowDimension") int m,
			@ACParam(key="ColumnDimension") int n
	) {
		fM = m;
		fN = n;
		fElements = new double[fM][fN];
	}
	
	/**
	 * �R���X�g���N�^�D
	 * m�����c�x�N�g���im�~1�s��j�Ƃ��ď���������D
	 * @param m �c�x�N�g���̎�����
	 */
	public TCMatrix(
			@ACParam(key="RowDimension") int m
	) {
		this(m, 1);
	}
	
	/**
	 * �R���X�g���N�^�D
	 * m�����c�x�N�g���im�~1�s��j�Ƃ��ď���������D
	 * @param vector m�����c�x�N�g���̗v�f
	 */
	public TCMatrix(double[] vector) {
		this(vector.length, 1);
		for (int i = 0; i < vector.length; ++i) {
			fElements[i][0] = vector[i];
		}
	}
	
	/**
	 * Constructor.
	 * Creates a matrix with specified elements with deep-copying.
	 * @param elements
	 */
	public TCMatrix(double[][] elements) {
		fM = elements.length;
		if(fM != 0) {
			fN = elements[0].length;
		} else {
			fN = 0;
		}
		fElements = new double[fM][fN];
		for(int i=0; i<fM; i++) {
			System.arraycopy(elements[i], 0, fElements[i], 0, fN);
		}
	}
	
	/**
	 * Constructor.
	 * Creates a matrix by deep-copying the specified matrix.
	 * @param src
	 */
	public TCMatrix(TCMatrix src) {
		fM = src.fM;
		fN = src.fN;
		fElements = new double[fM][fN];
		for(int i=0; i<fM; i++) {
			System.arraycopy(src.fElements[i], 0, fElements[i], 0, fN);
		}
	}
	
	/**
	 * Deep-copies from the specified TCMatrix.
	 * @param src TCMatrix
	 * @return this
	 */
	public TCMatrix copyFrom(TCMatrix src) {
		if(fM != src.fM || fN != src.fN) {
			fM = src.fM;
			fN = src.fN;
			fElements = new double[fM][fN];
		}
		for(int i=0; i<fM; i++) {
			System.arraycopy(src.fElements[i], 0, fElements[i], 0, fN);
		}
		return this;
	}

	/**
	 * Deep-copies from the transpose of specified TCMatrix.
	 * @param src TCMatrix
	 * @return this
	 */
	public TCMatrix tcopyFrom(TCMatrix src) {
		if(fM != src.fN || fN != src.fM) {
			fM = src.fN;
			fN = src.fM;
			fElements = new double[fM][fN];
		}
		for(int i=0; i<fM; i++) {
			for(int j=0; j<fN; j++){
				fElements[i][j] = src.fElements[j][i];
			}
		}
		return this;
	}
	
	/**
	 * �����s����R�s�[����D
	 * �s�� src �� src_i0 ���� src_i1 �s�C����� src_j0 ���� src_j1 ��ɑΉ����镔���s����C
	 * ���g�� dst_i0 �s�C dst_j0 ��ȍ~�̕����ɃR�s�[����D
	 * �����R�s�[�Ώۂ̕����s�񂪎��g�̍s��ɓ��肫��Ȃ��ꍇ�C���g�̃T�C�Y���g�債�C
	 * �g�傳��C�������s�񂪃R�s�[����Ȃ��v�f�ɂ�0���i�[�����D
	 * 
	 * @param src TCMatrix
	 * @param src_i0 first row index
	 * @param src_i1 last row index
	 * @param src_j0 first column index
	 * @param src_j1 last column index
	 * @param dst_i0 destination (row index)
	 * @param dst_j0 destination (column index)
	 * @return
	 */
	public TCMatrix copySubmatrixFrom(TCMatrix src, int src_i0, int src_i1, int src_j0, int src_j1, int dst_i0, int dst_j0) {
		if(((fM-dst_i0) < (src_i1-src_i0+1)) || ((fN-dst_j0) < (src_j1-src_j0+1))) {
			int newM = dst_i0 + src_i1 - src_i0 + 1;
			int newN = dst_j0 + src_j1 - src_j0 + 1;
			newM = newM > fM ? newM : fM;
			newN = newN > fN ? newN : fN;
			TCMatrix enlarged = new TCMatrix(newM, newN);
			if(fM!=0 && fN!=0) {
				enlarged.copySubmatrixFrom(this, 0, fM-1, 0, fN-1, 0, 0);
			}
			this.copyFrom(enlarged);
		}
		try {
			for(int i=src_i0; i<=src_i1; i++) {
				for(int j=src_j0; j<=src_j1; j++) {
					fElements[dst_i0+i-src_i0][dst_j0+j-src_j0] = src.fElements[i][j];
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("indices are incorrect.");
		}
		return this;
	}

	/**
	 * Changes sizes of a matrix.
	 * All elements are reinitialized as zero.
	 * @param m new row dimension
	 * @param n new column dimension
	 */
	public void setDimensions(int m, int n) {
		if(fM != m || fN != n) {
			fM = m;
			fN = n;
			fElements = new double[fM][fN];
		}
	}
	
	/**
	 * m�����x�N�g���im�~1�s��j�ɐݒ肷��D
	 * @param m �c�x�N�g���̎�����
	 */
	public void setDimension(int m) {
		if (fM != m || fN != 1) {
			fM = m;
			fN = 1;
			fElements = new double [fM][fN];
		}
	}
	
	/**
	 * Returns a row dimension, i.e. m.
	 * @return
	 */
	public int getRowDimension() {
		return fM;
	}
	
	/**
	 * Returns a column dimension, i.e. n.
	 * @return
	 */
	public int getColumnDimension() {
		return fN;
	}
	
	/**
	 * �s���~�񐔂�Ԃ��D
	 * @return �s���~��
	 */
	public int getDimension() {
		return fM * fN;
	}
	
	/**
	 * Sets a value to the specified element.
	 * @param m
	 * @param n
	 * @param value
	 */
	public TCMatrix setValue(int m, int n, double value) {
		fElements[m][n] = value;
		return this;
	}
	
	/**
	 * Returns a value of the specified element.
	 * @param m
	 * @param n
	 * @return 
	 */
	public double getValue(int m, int n) {
		return fElements[m][n];
	}
	
	/**
	 * Returns a value of the specified element.
	 * @param idx
	 * @return
	 * @author fnob
	 */
	public double getValue(int idx) {
		return fElements[idx/fN][idx%fN];
	}

	/**
	 * (0, 0)�̗v�f�̒l��Ԃ��D
	 * �P�~�P�s��̏ꍇ�̂ݗL���D
	 * @return (0, 0)�̗v�f�̒l
	 */
	public double getValue() {
		assert fN == 1 && fM == 1;
		return fElements[0][0];
	}

	/**
	 * �l���C���f�b�N�X�Ŏw�肳�ꂽ�v�f�֐ݒ肷��B
	 * �����ŁAm�~n�s��X�ɂ����āAX(i, j)�ɒl��ݒ肵�����ꍇ�A�C���f�b�N�X��i*n+j�ƂȂ�B
	 * @param idx �C���f�b�N�X
	 * @author isao
	 */
	public TCMatrix setValue(int idx, double value) {
		fElements[idx/fN][idx%fN] = value;
		return this;
	}
		
	/**
	 * Adds the specified matrix to this matrix.
	 * Updates this object.
	 * A = A + B.
	 * @param b
	 * @return this matrix
	 */
	public TCMatrix add(TCMatrix b) {
		for(int i=0; i<fM; i++) {
			for(int j=0; j<fN; j++) {
				fElements[i][j] += b.fElements[i][j];
			}
		}
		return this;
	}
	
	/**
	 * Adds the specified scalar value to each element of this matrix.
	 * Updates this object.
	 * A<sub>ij</sub> = A<sub>ij</sub> + d
	 * 
	 * @param d
	 * @return this matrix
	 */
	public TCMatrix add(double d) {
		for(int i=0; i<fM; i++) {
			for(int j=0; j<fN; j++) {
				fElements[i][j] += d;
			}
		}
		return this;
	}
	
	/**
	 * Subtructs the specified matrix from this matrix.
	 * Updates this object.
	 * A = A - B.
	 * @param b
	 * @return this matrix
	 */
	public TCMatrix sub(TCMatrix b) {
		for(int i=0; i<fM; i++) {
			for(int j=0; j<fN; j++) {
				fElements[i][j] -= b.fElements[i][j];
			}
		}
		return this;
	}
	
	/**
	 * Subtracts the specified saclar value from each element of this matrix.
	 * Updates this object.
	 * A<sub>ij</sub> = A<sub>ij</sub> - d
	 * 
	 * @param d
	 * @return this matrix
	 */
	public TCMatrix sub(double d) {
		for(int i=0; i<fM; i++) {
			for(int j=0; j<fN; j++) {
				fElements[i][j] -= d;
			}
		}
		return this;
	}
	
	/**
	 * Scalar product.
	 * Updates this object.
	 * A = r * A.
	 * @param r
	 * @return this
	 * @deprecated Use {@link #times(double)} instead.
	 */
	public TCMatrix scalarProduct(double r) {
		for(int i=0; i<fM; i++) {
			for(int j=0; j<fN; j++) {
				fElements[i][j] *= r;
			}
		}
		return this;
	}
	
	/**
	 * Times.
	 * Updates this object.
	 * 	C = A * B.
	 * Notice that C.times(C, B) or C.times(A, C) isn't allowed to use.
	 * In those case, you have to use times(TCMatrix b) or timesLeft(TCMatrix b) respectively.
	 * 
	 * @param src1 �s��A
	 * @param src2 �s��B
	 * @return this �s��C
	 */
	public TCMatrix times(TCMatrix src1, TCMatrix src2) {
		if(fM != src1.fM || src1.fN != src2.fM || fN != src2.fN) {
			throw new IllegalArgumentException("Matrix dimensions are incorrect.");
		}
		for(int i=0; i<src1.fM; i++) {
			for(int j=0; j<src2.fN; j++) {
				fElements[i][j] = 0.0;
				for(int k=0; k<src1.fN; k++){
					fElements[i][j] += src1.fElements[i][k] * src2.fElements[k][j]; 
				}
			}
		}
		return this;
	}
	
	/**
	 * Times.
	 * Updates this object.
	 * A = A * B.
	 * @param b
	 * @return this
	 */
	public TCMatrix times(TCMatrix b) {
		if(fN != b.fM) {
			throw new IllegalArgumentException("Dimensions are incorrect.");
		}
	
		double[][] result = new double[fM][b.fN];
		double val;
		for(int i=0; i<fM; i++) {
			for(int j=0; j<b.fN; j++) {
				val = 0.0;
				for(int k=0; k<fN; k++) {
					val += fElements[i][k] * b.fElements[k][j];
				}
				result[i][j] = val;
			}
		}
		if(fN != b.fN) {
			fElements = new double[fM][b.fN];
			fN = b.fN;
		}
		for(int i=0; i<fM; i++) {
			System.arraycopy(result[i], 0, fElements[i], 0, fN);
		}
		return this;
	}
	
	/**
	 * Returns a matrix represents this matrix times the specified scaler value.
	 * Updates this object.
	 * A = d * A
	 * 
	 * @param d
	 * @return this matrix
	 */
	public TCMatrix times(double d) {
		for(int i=0; i<fM; i++) {
			for(int j=0; j<fN; j++) {
				fElements[i][j] *= d;
			}
		}
		return this;
	}
	
	/**
	 * Times from left.
	 * Updates this object.
	 * A = B * A.
	 * @param b
	 * @return this matrix
	 */
	public TCMatrix timesLeft(TCMatrix b) {
		if(b.fN != fM) {
			throw new IllegalArgumentException("Dimensions are incorrect.");
		}
		double[][] result = new double[b.fM][fN];
		double val;
		for(int i=0; i<b.fM; i++) {
			for(int j=0; j<fN; j++) {
				val = 0.0;
				for(int k=0; k<b.fN; k++) {
					val += b.fElements[i][k] * fElements[k][j];
				}
				result[i][j] = val;
			}
		}
		if(fM != b.fM) {
			fElements = new double[b.fM][fN];
			fM = b.fM;
		}
		for(int i=0; i<fM; i++) {
			System.arraycopy(result[i], 0, fElements[i], 0, fN);
		}
		return this;
	}
	
	/**
	 * Times each element.
	 * The elements <i>a<sub>i,j</sub></i> = <i>a<sub>i,j</sub></i> * <i>b<sub>i,j</sub></i>.
	 * Updates this object.
	 * 
	 * @param b
	 * @return this matrix
	 */
	public TCMatrix timesElement(TCMatrix b) {
		if(fM != b.fM || fN != b.fN) {
			throw new IllegalArgumentException("Dimensions are incorrect.");
		}
		for(int i=0; i<fM; i++) {
			for(int j=0; j<fN; j++) {
				fElements[i][j] *= b.fElements[i][j];
			}
		}
		return this;
	}

	/**
	 * �S�Ă̗v�f��d�Ŋ����Ď��g��Ԃ��D
	 * @param d 
	 * @return ���̍s��
	 */
	public TCMatrix div(double d) {
		for (int m = 0; m < fM; ++m) {
			for (int n = 0; n < fN; ++n) {
				fElements[m][n] /= d;
			}
		}
		return this;
	}
	
	/**
	 * Div each element.
	 * The elements <i>a<sub>i,j</sub></i> = <i>a<sub>i,j</sub></i> * <i>b<sub>i,j</sub></i>.
	 * Updates this object.
	 * 
	 * @param b
	 * @return this matrix
	 */
	public TCMatrix divElement(TCMatrix b) {
		if(fM != b.fM || fN != b.fN) {
			throw new IllegalArgumentException("Dimensions are incorrect.");
		}
		for(int i=0; i<fM; i++) {
			for(int j=0; j<fN; j++) {
				fElements[i][j] /= b.fElements[i][j];
			}
		}
		return this;
	}

	/**
	 * Returns transpose.
	 * Updates this object.
	 * @return this matrix
	 */
	public TCMatrix transpose() {
		if(fM == fN) {
			double temp;
			for(int i=0; i<fM; i++) {
				for(int j=0; j<i; j++) {
					temp = getValue(i, j);
					setValue(i, j, getValue(j, i));
					setValue(j, i, temp);
				}
			}
		} else {
			double[][] result = new double[fN][fM];
			for(int i=0; i<fN; i++) {
				for(int j=0; j<fM; j++) {
					result[i][j] = getValue(j, i);
				}
			}
			fN = fM;
			fM = result.length;
			fElements = new double[fM][fN];
			for(int i=0; i<fM; i++) {
				System.arraycopy(result[i], 0, fElements[i], 0, fN);
			}
		}
		return this;
	}
	
	/**
	 * Calculates and returns trace.
	 * @return the trace of this matrix
	 */
	public double trace() {
		double tr = 0.0;
		int n = Math.min(fM, fN);
		for(int i=0; i<n; i++) {
			tr += getValue(i, i);
		}
		return tr;
	}
	
	/**
	 * Do cholesky decomposition.
	 * @return TCCholeskyDecomposition Class of this matrix
	 */
	public TCCholeskyDecomposition chol() {
		return new TCCholeskyDecomposition(this);
	}
	
	/**
	 * Do LU decomposition.
	 * @return TCLUDecomposition Class of this matrix
	 */
	public TCLUDecomposition lu() {
		return new TCLUDecomposition(this);
	}
	
	/**
	 * Do QR decomposition.
	 * @return TCQRDecomposition Class of this matrix
	 */
	public TCQRDecomposition qr() {
		return new TCQRDecomposition(this);
	}
	
	/**
	 * Do Eigenvalue decomposition.
	 * @return TCEigenvalueDecomposition Class of this matrix
	 */
	public TCEigenvalueDecomposition eig() {
		return new TCEigenvalueDecomposition(this);
	}
	
	/**
	 * Do SingularValue decomposition.
	 * @return TCSingularValuDecomposition Class of this matrix.
	 */
	public TCSingularValueDecomposition svd() {
		return new TCSingularValueDecomposition(this);
	}
	
	/**
	 * Returns determinant of this matrix.
	 * 
	 * @return the determinant of this matrix
	 */
	public double det() {
		return new TCLUDecomposition(this).det();
	}
	
	/**
	 * Returns TCMatrix X such that satisfies A*X = B, where A is this matrix.
	 * @param B
	 * @return The solution of A*X = B.
	 */
	public TCMatrix solve(TCMatrix B) {
		return (fM == fN ? (new TCLUDecomposition(this)).solve(B) :
											(new TCQRDecomposition(this)).solve(B));
	}
	
	/**
	 * Returns an inverse of this matrix.
	 * Updates this object.
	 * @return this matrix
	 */
	public TCMatrix inverse() {
		double[][] ident = new double[fM][fM];
		for(int i=0; i<fM; i++) ident[i][i] = 1.0;
		TCMatrix identityMatrix = new TCMatrix(ident);
		this.copyFrom(solve(identityMatrix));
		return this;
	}
	
	/**
	 * Returns rank of this matrix.
	 * @return the rank of this matrix
	 */
	public int rank() {
		return new TCSingularValueDecomposition(this).rank();
	}
	
	/**
	 * Returns condition number (singular value decomposition) of this matrix.
	 * @return the condition number of this matrix
	 */
	public double cond() {
		return new TCSingularValueDecomposition(this).cond();
	}
	
	/**
	 * Frobenius norm.
	 * @return sqrt of sum of squares of all elements.
	 */
	public double normF() {
		double f = 0.0;
		for(int i=0; i<fM; i++) {
			for(int j=0; j<fN; j++) {
				f = TCMaths.hypot(f, fElements[i][j]);
			}
		}
		return f;
	}
	
	/**
	 * L2�m������Ԃ��D
	 * ���̃��\�b�h�͗�x�N�g���ɑ΂��Ă̂ݓK�p�\�D
	 * @return L2�m����
	 */
	public double normL2() {
		if (getColumnDimension() != 1) {
			throw new IllegalArgumentException("Dimensions are incorrect.");
		}
		double sum = 0.0;
		for (int i = 0; i < getDimension(); ++i) {
			sum += getValue(i) * getValue(i);
		}
		return Math.sqrt(sum);
	}
	
	/**
	 * �P�ʃx�N�g���ɋ�������D
	 * ���̃��\�b�h�͗�x�N�g���ɑ΂��Ă̂ݓK�p�\�D
	 * @return �P�ʃx�N�g��
	 */
	public TCMatrix enforceToUnitVector() {
		if (getColumnDimension() != 1) {
			throw new IllegalArgumentException("Dimensions are incorrect.");
		}
		double length = normL2();
		div(length);
		return this;
	}
	
	/**
	 * ���ς�Ԃ��D
	 * ���̃��\�b�h�͗�x�N�g���ɑ΂��Ă̂ݓK�p�\�D
	 * @param v ��x�N�g��
	 * @return ����
	 */
	public double innerProduct(TCMatrix v) {
		if(getColumnDimension() != 1 || v.getColumnDimension() != 1 || getRowDimension() != v.getRowDimension()) {
			throw new IllegalArgumentException("Dimensions are incorrect.");
		}
		double result = 0.0;
		for (int i = 0; i < getDimension(); ++i) {
			result += getValue(i) * v.getValue(i);
		}
		return result;
	}
	
	/**
	 * Fills the elements of the matrix with specified value.
	 * @param value
	 * @return this matrix
	 */
	public TCMatrix fill(double value) {
		if(fM == fN) {
			for(int i=0; i<fM; i++) {
				fElements[i][i] = value;
				for(int j=0; j<i; j++) {
					fElements[i][j] = value;
					fElements[j][i] = value;
				}
			}
		} else {
			for(int i=0; i<fM; i++) {
				for(int j=0; j<fN; j++) {
					fElements[i][j] = value;
				}
			}
		}
		return this;
	}
	
	/**
	 * Initializes all elements with random double values.
	 * @param random
	 * @return this matrix
	 */
	public TCMatrix rand(ICRandom random) {
		for(int i=0; i<fM; i++) {
			for(int j=0; j<fN; j++) {
				fElements[i][j] = random.nextDouble();
			}
		}
		return this;
	}
	
	/**
	 * Initializes all elements with random double values according to gaussian.
	 * @param random
	 * @return this matrix
	 */
	public TCMatrix randn(ICRandom random) {
		for(int i=0; i<fM; i++) {
			for(int j=0; j<fN; j++) {
				fElements[i][j] = random.nextGaussian();
			}
		}
		return this;
	}
	
	/**
	 * Initializes this matrix as the identity matrix.
	 * The elements <i>x<sub>i,i</sub></i> = 1.0, where <i>i</i> = min {<i>m, n</i>}, and the other elements are 0.
	 * 
	 * @return this matrix
	 */
	public TCMatrix eye() {
		this.fill(0.0);
		int n = fM<fN ? fM : fN;
		for(int i=0; i<n; i++) fElements[i][i] = 1.0;
		
		return this;
	}
	
	/**
	 * matrix exponential by using eigenvalue decomposition 
	 * Note that this way is ``Not'' like matlab that uses Pade approximant.
	 * 
	 * @param sym Is the matrix symmetric?
	 * @author fnob
	 */
	public TCMatrix expm(boolean sym){
		if(fN != fM){
			throw new IllegalArgumentException("Matrix exponential is defined for square matrices");
		}
			
		TCEigenvalueDecomposition eig = eig();
		TCMatrix expD = eig.getD();
		TCMatrix v = eig.getV();
		
	    for(int i=0; i<fN; i++){
	    	expD.setValue(i, i, Math.expm1(expD.getValue(i, i))+1.0);
	    }
	    
	    if(sym){
	    	TCMatrix vT = (new TCMatrix(v)).transpose();
			this.copyFrom(v.times(expD).times(vT));
	    }else{
	    	TCMatrix vI = (new TCMatrix(v)).inverse();
			this.copyFrom(v.times(expD).times(vI));
	    }
	    return this;
	}
	
	/**
	 * element-by-element array exponential.
	 * 
	 * @author fnob
	 */
	public TCMatrix exp(){
		for(int i=0; i<fM; i++){
			for(int j=0; j<fN; j++){
				fElements[i][j] = Math.expm1(fElements[i][j]) + 1.0;
			}
		}
		return this;
	}
	
	/**
	 * element-by-element array sine.
	 * 
	 * @author fnob
	 */
	public TCMatrix sin(){
		for(int i=0; i<fM; i++){
			for(int j=0; j<fN; j++){
				fElements[i][j] = Math.sin(fElements[i][j]);
			}
		}
		return this;
	}

	/**
	 * element-by-element array cosine.
	 * 
	 * @author fnob
	 */
	public TCMatrix cos(){
		for(int i=0; i<fM; i++){
			for(int j=0; j<fN; j++){
				fElements[i][j] = Math.cos(fElements[i][j]);
			}
		}
		return this;
	}
	
	/**
	 * element-by-element array tangent.
	 * 
	 * @author fnob
	 */
	public TCMatrix tan(){
		for(int i=0; i<fM; i++){
			for(int j=0; j<fN; j++){
				fElements[i][j] = Math.tan(fElements[i][j]);
			}
		}
		return this;
	}
	
	/**
	 * ��̍s��̊e�v�f���r���C�傫�����̗v�f�ō\�����ꂽ�s��Ԃ��D
	 * 
	 * @param b matrix
	 * @return this
	 * @author fnob
	 */
	public TCMatrix max(TCMatrix b) {
		if(fN != b.fN | fM != b.fM) {
			throw new IllegalArgumentException("Dimensions are incorrect.");
		}
		
		for(int i=0; i<fM; i++){
			for(int j=0; j<fN; j++){
				if(this.fElements[i][j] < b.fElements[i][j]){
					this.fElements[i][j] = b.fElements[i][j];
				}
			}	
		}
		return this;
	}
	
	/**
	 * �s��̊e�v�fa_(i,j)��max(a_(i,j), d)�Œu��������D
	 * 
	 * @param d ��r�Ώ�
	 * @return this
	 * @author fnob
	 */
	public TCMatrix max(double d) {
		for(int i=0; i<fM; i++){
			for(int j=0; j<fN; j++){
				if(this.fElements[i][j] < d){
					this.fElements[i][j] = d;
				}
			}	
		}
		return this;
	}
	
	/**
	 * m-by-n�s��̍ő�l�Ƃ���ɑΉ�����C���f�b�N�X�𔭌�����D
	 * �ő�l�ɑΉ�����C���f�b�N�X����������ꍇ�C�����̃C���f�b�N�X��S�Ď��o���D
	 * 
	 * @param indexes �ő�l�́w�C���f�b�N�X�x���i�[���郊�X�g.
	 * �������Cm-by-n�s��ɂ�����(i,j)�v�f�́w�C���f�b�N�X�x�́Ci*n + j�ŕ\������邱�Ƃɒ��ӁD
	 * 
	 * @return max �ő�.
	 * @author fnob
	 */
	public double max(ArrayList<Integer> indexes){
		double max = fElements[0][0];
		
		indexes.clear();
		for(int i=0; i<fM; i++){
			for(int j=0; j<fN; j++){
				if(max <= fElements[i][j]){
					if(max == fElements[i][j]){
						indexes.add(i*fN + j);
					}else{
						indexes.clear();
						indexes.add(i*fN + j);
						max = fElements[i][j];
					}
				}
			}
		}
		return max;
	}
	
	/**
	 * m-by-n�s��̍s�����̍ő�l�Ƃ���ɑΉ�����C���f�b�N�X�𔭌�����D
	 * �ő�l�ɑΉ�����C���f�b�N�X����������ꍇ�C�����̃C���f�b�N�X��S�Ď��o���D
	 * 
	 * @param indexes �ő�l�́w�C���f�b�N�X�x���i�[���郊�X�g.
	 * �������Cm-by-n�s��ɂ�����(i,j)�v�f�́w�C���f�b�N�X�x�́Ci*n + j�ŕ\������邱�Ƃɒ��ӁD
	 * 
	 * @param max �ő�l���i�[����x�N�g��(m-by-1�s��)�D
	 * @author fnob
	 */
	public void maxRowDirection(ArrayList<Integer> indexes, TCMatrix max){
		if(max.fM != this.fM | max.fN != 1) {
			throw new IllegalArgumentException("Dimension of max is incorrect.");
		}
		
		indexes.clear();
		ArrayList<Integer> rowIndexes = new ArrayList<Integer>();
		double temp;
		for(int i=0; i<fM; i++){
			rowIndexes.clear();
			rowIndexes.add(0);		
			temp = fElements[i][0];
			for(int j=1; j<fN; j++){
				if(temp <= fElements[i][j]){
					if(temp == fElements[i][j]){
						rowIndexes.add(j);
					}else{
						rowIndexes.clear();
						rowIndexes.add(j);
						temp = fElements[i][j];
					}
				}
			}
			max.fElements[i][0] = temp;
			for(Integer idx : rowIndexes){
				indexes.add(idx + i * fN);
			}
		}
	}
	
	/**
	 * m-by-n�s��̗�����̍ő�l�Ƃ���ɑΉ�����C���f�b�N�X�𔭌�����.
	 * �ő�l�ɑΉ�����C���f�b�N�X����������ꍇ�C�����̃C���f�b�N�X��S�Ď��o��.
	 * 
	 * @param indexes �ő�l�́w�C���f�b�N�X�x���i�[���郊�X�g.
	 * �������Cm-by-n�s��ɂ�����(i,j)�v�f�́w�C���f�b�N�X�x�́Ci*n + j�ŕ\������邱�Ƃɒ���.
	 * 
	 * @param max �ő�l���i�[����x�N�g��(1-by-n�s��).
	 * @author fnob
	 */
	public void maxColumnDirection(ArrayList<Integer> indexes, TCMatrix max){
		if(max.fM != 1 | max.fN != this.fN) {
			throw new IllegalArgumentException("Dimension of max is incorrect.");
		}
		
		indexes.clear();
		ArrayList<Integer> columnIndexes = new ArrayList<Integer>();
		double temp;
		for(int i=0; i<fN; i++){
			columnIndexes.clear();
			columnIndexes.add(0);
			temp =  fElements[0][i];
			for(int j=1; j<fM; j++){
				if(temp <= fElements[j][i]){
					if(temp == fElements[j][i]){
						columnIndexes.add(j);
					}else{
						columnIndexes.clear();
						columnIndexes.add(j);
						temp = fElements[j][i];
					}
				}
			}
			max.fElements[0][i] = temp;
			for(Integer idx : columnIndexes){
				indexes.add(i + idx * fN);
			}
			
		}
	}

	/**
	 * ��̍s��̊e�v�f���r���C�傫�����̗v�f�ō\�����ꂽ�s��Ԃ��D
	 * 
	 * @param b matrix
	 * @return this
	 * @author fnob
	 */
	public TCMatrix min(TCMatrix b) {
		if(fN != b.fN | fM != b.fM) {
			throw new IllegalArgumentException("Dimensions are incorrect.");
		}
		
		for(int i=0; i<fM; i++){
			for(int j=0; j<fN; j++){
				if(this.fElements[i][j] > b.fElements[i][j]){
					this.fElements[i][j] = b.fElements[i][j];
				}
			}	
		}
		return this;
	}
	
	/**
	 * �s��̊e�v�fa_(i,j)��min(a_(i,j), d)�Œu��������D
	 * 
	 * @param d ��r�Ώ�
	 * @return this
	 * @author fnob
	 */
	public TCMatrix min(double d) {
		for(int i=0; i<fM; i++){
			for(int j=0; j<fN; j++){
				if(this.fElements[i][j] > d){
					this.fElements[i][j] = d;
				}
			}	
		}
		return this;
	}
	
	/**
	 * m-by-n�s��̍ŏ��l�Ƃ���ɑΉ�����C���f�b�N�X�𔭌�����D
	 * �ŏ��l�ɑΉ�����C���f�b�N�X����������ꍇ�C�����̃C���f�b�N�X��S�Ď��o���D
	 * 
	 * @param indexes �ŏ��l�́w�C���f�b�N�X�x���i�[���郊�X�g.
	 * �������Cm-by-n�s��ɂ�����(i,j)�v�f�́w�C���f�b�N�X�x�́Ci*n + j�ŕ\������邱�Ƃɒ��ӁD
	 * 
	 * @return min �ŏ��l.
	 * @author fnob
	 */
	public double min(ArrayList<Integer> indexes){
		double min = fElements[0][0];
		
		indexes.clear();
		for(int i=0; i<fM; i++){
			for(int j=0; j<fN; j++){
				if(min >= fElements[i][j]){
					if(min == fElements[i][j]){
						indexes.add(i*fN + j);
					}else{
						indexes.clear();
						indexes.add(i*fN + j);
						min = fElements[i][j];
					}
				}
			}
		}
		return min;
	}
	
	/**
	 * m-by-n�s��̍s�����̍ŏ��l�Ƃ���ɑΉ�����C���f�b�N�X�𔭌�����D
	 * �ŏ��l�ɑΉ�����C���f�b�N�X����������ꍇ�C�����̃C���f�b�N�X��S�Ď��o���D
	 * 
	 * @param indexes �ŏ��l�́w�C���f�b�N�X�x���i�[���郊�X�g.
	 * �������Cm-by-n�s��ɂ�����(i,j)�v�f�́w�C���f�b�N�X�x�́Ci*n + j�ŕ\������邱�Ƃɒ��ӁD
	 * 
	 * @param min �ŏ��l���i�[����x�N�g��(m-by-1�s��)�D
	 * @author fnob
	 */
	public void minRowDirection(ArrayList<Integer> indexes, TCMatrix min){	
		if(min.fM != this.fM | min.fN != 1) {
			throw new IllegalArgumentException("Dimension of min is incorrect.");
		}
		
		indexes.clear();
		ArrayList<Integer> rowIndexes = new ArrayList<Integer>();
		double temp;
		for(int i=0; i<fM; i++){
			rowIndexes.clear();
			rowIndexes.add(0);
			temp = fElements[i][0];
			for(int j=1; j<fN; j++){
				if(temp >= fElements[i][j]){
					if(temp == fElements[i][j]){
						rowIndexes.add(j);
					}else{
						rowIndexes.clear();
						rowIndexes.add(j);
						temp = fElements[i][j];
					}
				}
			}
			min.fElements[i][0] = temp;
			for(Integer idx : rowIndexes){
				indexes.add(idx + i * fN);
			}
		}
	}
	
	/**
	 * m-by-n�s��̗�����̍ŏ��l�Ƃ���ɑΉ�����C���f�b�N�X�𔭌�����D
	 * �ŏ��l�ɑΉ�����C���f�b�N�X����������ꍇ�C�����̃C���f�b�N�X��S�Ď��o���D
	 * 
	 * @param indexes �ŏ��l�́w�C���f�b�N�X�x���i�[���郊�X�g.
	 * �������Cm-by-n�s��ɂ�����(i,j)�v�f�́w�C���f�b�N�X�x�́Ci*n + j�ŕ\������邱�Ƃɒ��ӁD
	 * 
	 * @param min �ŏ��l���i�[����x�N�g��(1-by-n�s��)�D
	 * @author fnob
	 */
	public void minColumnDirection(ArrayList<Integer> indexes, TCMatrix min){	
		if(min.fM != 1 | min.fN != this.fN) {
			throw new IllegalArgumentException("Dimension of min is incorrect.");
		}
		
		indexes.clear();
		ArrayList<Integer> columnIndexes = new ArrayList<Integer>();
		double temp;
		for(int i=0; i<fN; i++){
			columnIndexes.clear();
			columnIndexes.add(0);
			temp =  fElements[0][i];
			for(int j=1; j<fM; j++){
				if(temp >= fElements[j][i]){
					if(temp == fElements[j][i]){
						columnIndexes.add(j);
					}else{
						columnIndexes.clear();
						columnIndexes.add(j);
						temp = fElements[j][i];
					}
				}
			}
			min.fElements[0][i] = temp;
			for(Integer idx : columnIndexes){
				indexes.add(i + idx * fN);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public TCMatrix clone() {
		return new TCMatrix(this);
	}
	
	/**
	 * ���g�� src_i0 ���� src_i1 �s�C����� src_j0 ���� src_j1 ��ɑΉ����镔���s��̃N���[�����쐬���ĕԂ��D
	 * @param src_i0 first row index
	 * @param src_i1 last row index
	 * @param src_j0 first column index
	 * @param src_j1 last column index
	 * @return �����s��̃N���[��
	 */
	public TCMatrix cloneSubmatrix(int src_i0, int src_i1, int src_j0, int src_j1) {
		TCMatrix result = new TCMatrix();
		result.copySubmatrixFrom(this, src_i0, src_i1, src_j0, src_j1, 0, 0);
		return result;
	}
	
	/**
	 * �w�肵���s�̍s�x�N�g���̃N���[����Ԃ��D
	 * @param rowIndex �s
	 * @return �s�x�N�g��
	 */
	public TCMatrix cloneRowVector(int rowIndex) {
		TCMatrix result = new TCMatrix(1, getColumnDimension());
		for (int i = 0; i < getColumnDimension(); ++i) {
			result.setValue(0, i, getValue(rowIndex, i));
		}
		return result;
	}

	/**
	 * �w�肵����̗�x�N�g���̃N���[����Ԃ��D
	 * @param columnIndex ��
	 * @return ��x�N�g��
	 */
	public TCMatrix cloneColumnVector(int columnIndex) {
		TCMatrix result = new TCMatrix(getRowDimension(), 1);
		for (int i = 0; i < getRowDimension(); ++i) {
			result.setValue(i, 0, getValue(i, columnIndex));
		}
		return result;
	}
	
	@Override
	public String toString() {
		if(fM==0 || fN==0) {
			return "[]\n";
		}
		String s = "";
		for(int i=0; i<fM; i++) {
			s += Arrays.toString(fElements[i]);
			if (i < fM - 1) {
				s += "\n";
			}
		}
		return s;
	}
	
	/**
	 * �Ō�̍s�̌�ɍs���ǉ�����D
	 * �����I�ɂ́C����C�������̊m�ۂƗv�f�̃R�s�[���N����̂Ō����͂��܂�ǂ��Ȃ��D
	 * ���炩���ߍő�̍s�����킩���Ă���Ȃ�΁C�ő�̍s���ōs��𐶐����Ă����CcopyAtRow���\�b�h�ŃR�s�[�����Ă������������I�ł���D
	 * @param b �ǉ��������s��D��n�͓����K�v������D
	 * @return (m + b.m)�~n�s��
	 */
	public TCMatrix appendAfterLastRow(TCMatrix b) {
		if (fN != b.fN) {
			throw new IllegalArgumentException("The number of columns must be same.");
		}
		int originalM = fM;
		double[][] originalElements = fElements;
		fElements = new double[fM + b.fM][fN];
		fM += b.fM;
		for (int m = 0; m < originalM; ++m) {
			for (int n = 0; n < fN; ++n) {
				fElements[m][n] = originalElements[m][n];
			}
		}
		for (int m = 0; m < b.fM; ++m) {
			for (int n = 0; n < fN; ++n) {
				fElements[originalM + m][n] = b.fElements[m][n];
			}
		}
		return this;
	}

	/**
	 * �Ō�̗�̌�ɍs���ǉ�����D
	 * �����I�ɂ́C����C�������̊m�ۂƗv�f�̃R�s�[���N����̂Ō����͂��܂�ǂ��Ȃ��D
	 * ���炩���ߍő�̗񐔂��킩���Ă���Ȃ�΁C�ő�̗񐔂ōs��𐶐����Ă����CcopyAtColumn���\�b�h�ŃR�s�[�����Ă������������I�ł���D
	 * @param b �ǉ��������s��D�s��m�͓����ł���K�v������D
	 * @return m�~(n+b.n)�s��
	 */
	public TCMatrix appendAfterLastColumn(TCMatrix b) {
		if (fM != b.fM) {
			throw new IllegalArgumentException("The number of rows must be same.");
		}
		int originalN = fN;
		double[][] originalElements = fElements;
		fElements = new double[fM][fN + b.fN];
		fN += b.fN;
		for (int m = 0; m < fM; ++m) {
			for (int n = 0; n < originalN; ++n) {
				fElements[m][n] = originalElements[m][n];
			}
			for (int n = 0; n < b.fN; ++n) {
				fElements[m][originalN + n] = b.fElements[m][n];
			}
		}
		return this;
	}
	
	/**
	 * �s��̗v�f��NaN���܂�ł��Ȃ������ׂ�D
	 * @return true:�܂�ł���Cfalse:�܂�ł��Ȃ�
	 */
	public boolean isNan() {
		for (int m = 0; m < fM; ++m) {
			for (int n = 0; n < fN; ++n) {
				if (Double.isNaN(fElements[m][n])) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * �w�肳�ꂽ�s�ɍs����R�s�[����D
	 * @param b �R�s�[����s��D�񐔂��R�s�[��̍s��i���̍s��j�Ɠ����ł���K�v������D
	 * @param row �R�s�[����s�D(row+b�̍s��)���R�s�[��̍s��i���̍s��j�̍s�����Ɏ��܂��Ă���K�v������D
	 * @return �R�s�[���ꂽ��̂��̍s��
	 */
	public TCMatrix copyAtRow(TCMatrix b, int row) {
		if (fN != b.fN) {
			throw new IllegalArgumentException("The number of columns must be same.");
		}
		if (row < 0 || row + b.fM > fM) {
			throw new IllegalArgumentException("row+b.fM must be within the range [0, fM].");			
		}
		for (int m = 0; m < b.fM; ++m) {
			for (int n = 0; n < b.fN; ++n) {
				fElements[row + m][n] = b.fElements[m][n];
			}
		}
		return this;
	}
	
	/**
	 * �w�肳�ꂽ��ɍs����R�s�[����D
	 * @param b �R�s�[����s��D�s�����R�s�[��̍s��i���̍s��j�Ɠ����ł���K�v������D
	 * @param row �R�s�[�����D(column+b�̗�)���R�s�[��̍s��i���̍s��j�̗񐔓��Ɏ��܂��Ă���K�v������D
	 * @return �R�s�[���ꂽ��̂��̍s��
	 */
	public TCMatrix copyAtColumn(TCMatrix b, int column) {
		if (fM != b.fM) {
			throw new IllegalArgumentException("The number of rows must be same.");
		}
		if (column < 0 || column + b.fN > fN) {
			throw new IllegalArgumentException("column+b.fN must be within the range [0, fN].");			
		}
		for (int m = 0; m < b.fM; ++m) {
			for (int n = 0; n < b.fN; ++n) {
				fElements[m][column + n] = b.fElements[m][n];
			}
		}		
		return this;
	}
		
	/**
	 * ��Ώ̍s���Ώ̍s��֋�������D
	 * @param ��Ώ̍s��
	 * @return �Ώ̍s��
	 */
	public TCMatrix enforceSymmetry(){
		if (fM != fN) {
			throw new IllegalArgumentException("The numbers of row and column must be same.");
		}
		for(int m=0; m<fM; ++m){
			for(int n=0; n<m; ++n){
				fElements[m][n] = fElements[n][m];
			}
		}
		return this;
	}
	
	public static void main(String[] args) {				
		/*
		//�����s��̎��o��
		TCMatrix mat0 = new TCMatrix(3, 5);
		mat0.eye();
		System.out.println(mat0);
		TCMatrix vec0 = new TCMatrix();
		vec0.copySubmatrixFrom(mat0, 0, mat0.getRowDimension()-1, 2, 2, 0, 0);
		System.out.println(vec0);
		
		//�����I�ȕύX
		TCMatrix mat1 = new TCMatrix(3, 3);
		mat1.fill(0.8);
		TCMatrix vec1 = new TCMatrix(3, 1);
		vec1.fill(3.0);
		System.out.println(mat1);
		mat1.copySubmatrixFrom(vec1, 0, vec1.getRowDimension()-1, 0, vec1.getColumnDimension()-1, 1, 1);
		System.out.println(mat1);
		*/
//		
//		//���g�̊g��
//		TCMatrix mat2 = new TCMatrix(3, 2);
//		mat2.eye();
//		mat2.copySubmatrixFrom(mat2, 0, mat2.getRowDimension()-1, 0, mat2.getColumnDimension()-1, 0, mat2.getColumnDimension());
//		System.out.println(mat2);
//		
//		//�v�f�̒ǉ�
//		TCMatrix dataset = new TCMatrix();
//		TCMatrix data = new TCMatrix(2, 1);
//		for(int t=0; t<10; t++) {
//			data.setValue(0, t);
//			data.setValue(1, t/10.0);
//			dataset.copySubmatrixFrom(data, 0, data.getRowDimension()-1, 0, data.getColumnDimension()-1, 0, dataset.getColumnDimension());
//			System.out.println(dataset);
//		}
//		
//		//�P�ʍs��쐬
//		TCMatrix eye = new TCMatrix();
//		TCMatrix ele = new TCMatrix(1, 1);
//		ele.fill(1.0);
//		System.out.println(eye);
//		for(int i=0; i<4; i++) {
//			eye.copySubmatrixFrom(ele, 0, 0, 0, 0, eye.getRowDimension(), eye.getColumnDimension());
//			System.out.println(eye);
//		}
	}

}
