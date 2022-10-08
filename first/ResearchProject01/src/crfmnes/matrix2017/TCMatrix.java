package crfmnes.matrix2017;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import crfmnes.matrix2017.decompositions.TCCholeskyDecomposition;
import crfmnes.matrix2017.decompositions.TCEigenvalueDecomposition;
import crfmnes.matrix2017.decompositions.TCLUDecomposition;
import crfmnes.matrix2017.decompositions.TCQRDecomposition;
import crfmnes.matrix2017.decompositions.TCSingularValueDecomposition;
import jssf.random.ICRandom;

/**
 * �s�񉉎Z���T�|�[�g����N���X.
 * ��������_�����������v��������{���Ă��܂�
 *
 * �Eclass��final�ɂ���̂̓C�����C���W�J��_���Ă��܂����A����ȂɌ��ʂȂ������悤�ł�
 *
 * �E2�����z���1�����z��ɂ��܂����B����ɂ��A�c�x�N�g���ւ̃A�N�Z�X���x�̌��オ���҂���܂�
 *
 *  �E�g�p�������ʂ͏�������܂�(1�����ڂ̔z��̃|�C���^������Ȃ��̂�)�B
 *
 * �E��{�I��final���g���̂ŁA�������Ń��T�C�Y�͂ł��Ȃ��ł��B
 *
 * �EgetValue( i )�̍ۂɔ��ɏd�����Z�������Ă���̂��폜�ł��܂����B
 *
 * �E�c�����ւ�for���[�v���񂷂Ƃ��Ɋ|���Z( i * fNoOfCol + j )�𑫂��Z�ւƕύX����悤�ȃR�[�h�ɂ��Čv�Z���x�������܂���
 *
 * �E�l�C�e�B�u�R�[�h������System.arraycopy�Ȃǂ�ϋɓI�Ɏg�p���܂���
 *
 * @author sayama
 *
 */
public class TCMatrix implements Serializable, Cloneable {

	/** �V���A���C�Y�p��id */
	private static final long serialVersionUID = 1L;

	/** �s��̗v�f�������ɕ��ׂ��z�� **/
	private final double[] fElements;

	/** �s��(�c�̒���) **/
	private final int fNoOfRow;

	/** ��(���̒���) **/
	private final int fNoOfColumn;

	/**
	 * noOfRow x noOfColumn�̍s��𐶐�����B
	 * �s��̊e�v�f��0.0�ŏ����������
	 * @param noOfColumn �s��̍���
	 * @param noOfRow �s��̕�
	 */
	public TCMatrix( final int noOfRow, final int noOfColumn ) {
		fElements = new double[ noOfColumn * noOfRow ];
		fNoOfRow = noOfRow;
		fNoOfColumn = noOfColumn;
	}

	/**
	 * ����height�̏c�x�N�g���𐶐�����B
	 * �s��̊e�v�f��0.0�ŏ����������
	 * @param column
	 */
	public TCMatrix( final int column ) {
		this( column, 1 );
	}

	public TCMatrix () {
		this( 0, 0 );
	}

	/**
	 * �R�s�[�R���X�g���N�^
	 * @param mat �R�s�[���̍s��
	 * @return �������g�̍s��
	 */
	public TCMatrix ( final TCMatrix mat ) {
		fElements = new double[ mat.fElements.length ];
		fNoOfRow = mat.getRowDimension();
		fNoOfColumn = mat.getColumnDimension();
		copyFrom( mat );
	}

	/**
	 * 1�����z��ɂ�鏉����
	 * array�̒��g��deep copy���܂�
	 * @param array
	 */
	public TCMatrix( final double[] array ) {
		this( array.length, 1 );
		System.arraycopy( array, 0, fElements, 0, array.length );
	}

	/**
	 * 2�����z��ɂ�鏉����
	 * array�̒��g��deep copy���܂�
	 * @param array
	 */
	public TCMatrix( final double[][] array ) {
		this( array.length, array[0].length );
		int iStepIndex = 0;
		for ( int i = 0; i < fNoOfRow; i++ ) {
			System.arraycopy( array[i], 0, fElements, iStepIndex, fNoOfColumn );
			iStepIndex += fNoOfColumn;
		}
	}

	/**
	 * src�Ŏw�肵���s������̍s���deep copy���܂��B
	 * @param src �R�s�[���錳�̍s��
	 * @return �������g�̍s��
	 */
	public TCMatrix copyFrom( final TCMatrix src ) {
		assert( fNoOfRow == src.fNoOfRow &&
				fNoOfColumn == src.fNoOfColumn );

		System.arraycopy( src.fElements, 0, fElements, 0, fElements.length );
		return this;
	}

	/**
	 * src�Ŏw�肵���s��̓]�u�����̍s��փf�B�[�v�R�s�[���܂��B
	 * @param src �R�s�[���錳�̍s��
	 * @return �������g�̍s��
	 */
	public TCMatrix tcopyFrom( final TCMatrix src ) {
		assert( fNoOfRow == src.fNoOfColumn &&
				fNoOfColumn == src.fNoOfRow );

		for ( int i = 0; i < fNoOfColumn; i++ ) {
			for ( int j = 0; j < fNoOfRow; j++ ) {
				setValue( j, i, src.getValue( i, j ) );
			}
		}
		return this;
	}

	/**
	 * this��]�u������Ԃ�clone���܂��B
	 * @param src
	 * @return
	 */
	public TCMatrix tclone() {
		return new TCMatrix( fNoOfColumn, fNoOfRow ).tcopyFrom( this );
	}

	/**
	 * src�̕����s�� [ srcFromColumn, stcToColumn, srcFromRow, srcToRow ] ���A
	 * ���̍s��̈ʒu( srcToRow, srcToColumn )������Ƃ��ăR�s�[����B
	 * �����ŁA�����s��̐�[�A�I�[��index�͕����s��Ɋ܂܂��B
	 * @param src
	 * @param srcFromRow src�̍s��̃R�s�[����̈�̏�[
	 * @param srcToRow src�s��̃R�s�[����̈�̉��[
	 * @param srcFromColumn src�̍s��̃R�s�[����̈�̍��[
	 * @param srcToColumn src�̍s��̃R�s�[����̈�̉E�[
	 * @param dstFromRow �������g�̍s��̃R�s�[��̍��[
	 * @param dstFromColumn �������g�̍s��̃R�s�[��̏�[
	 * @return this
	 */
	public TCMatrix copySubmatrixFrom( final TCMatrix src, final int srcFromRow, final int srcToRow,
			final int srcFromColumn, final int srcToColumn, final int dstFromRow, final int dstFromColumn ) {

		final int submatrixColumnDimension = srcToColumn - srcFromColumn + 1;
		final int submatrixRowDimension = srcToRow - srcFromRow + 1;

		assert( 0 <= dstFromRow && dstFromRow + submatrixRowDimension <= fNoOfRow &&             // �����s��̏c���Ăяo�����̍s�񂩂�͂ݏo�Ȃ���
				0 <= dstFromColumn && dstFromColumn + submatrixColumnDimension <= fNoOfColumn && // �����s��̉����Ăяo�����̍s�񂩂�͂ݏo�Ȃ���
				0 <= srcFromRow && srcFromRow <= srcToRow && srcToRow < src.fNoOfRow &&          // �����s��̏c��src�̍s�񂩂�͂ݏo�Ȃ���
				0 <= srcFromColumn && srcFromColumn <= srcToColumn && srcToColumn < src.fNoOfColumn ); // �����s��̉���src�̍s�񂩂�͂ݏo�Ȃ���

		for ( int i = 0; i < submatrixRowDimension; i++ ) {
			for ( int j = 0; j < submatrixColumnDimension; j++ ) {
				setValue( i + dstFromRow, j + dstFromColumn, src.getValue( i + srcFromRow, j + srcFromColumn ) );
			}
		}
		return this;
	}

	/**
	 * this�̗񐔂�Ԃ��܂�( �� = ���̒��� )
	 * @return
	 */
	public int getColumnDimension() {
		return fNoOfColumn;
	}

	/**
	 * this�̍s����Ԃ��܂�( �s�� = �c�̒��� )
	 * @return
	 */
	public int getRowDimension() {
		return fNoOfRow;
	}

	/**
	 * �s�񂻂̂��̂̎������i���v�f���j��Ԃ��܂�
	 * @return
	 */
	public int getDimension() {
		return fElements.length;
	}

	/**
	 * r�sc��ڂ̗v�f��Ԃ��܂�
	 * @param r
	 * @param c
	 * @return
	 */
	public double getValue( final int r, final int c ) {
		assert( 0 <= r && r < fNoOfRow &&
				0 <= c && c < fNoOfColumn );

		return fElements[ r * fNoOfColumn + c ];
	}

	/**
	 * getValue( r / getRowDimension(), r % getRowDimension )�Ɠ����ł�
	 * @param r
	 * @return
	 */
	public double getValue( final int r ) {
		assert( 0 <= r && r < fElements.length );
		return fElements[ r ];
	}

	/**
	 * 1 x 1 �̍s��̏ꍇ�̂݁AgetValue( 0, 0 )�Ɠ����ł�
	 * @return
	 */
	public double getValue() {
		assert( fElements.length == 1 );
		return fElements[ 0 ];
	}

	/**
	 * r�sc��ڂ̗v�f��val�ɂ��܂�
	 * @param r
	 * @param c
	 * @param val
	 * @return
	 */
	public TCMatrix setValue( final int r, final int c, final double val ){
		assert( 0 <= r && r < fNoOfRow &&
				0 <= c && c < fNoOfColumn );

		fElements[ r * fNoOfColumn + c ] = val;
		return this;
	}

	/**
	 * r�s�ڂ̗v�f��val�ɂ��܂�
	 * @param r
	 * @param val
	 * @return
	 */
	public TCMatrix setValue( final int r, final double val ) {
		assert( 0 <= r && r < fElements.length );
		fElements[ r ] = val;
		return this;
	}

	/**
	 * 1 x 1 �̍s��̏ꍇ�̂݁AsetValue( 0, 0 )�Ɠ����ł�
	 * @return
	 */
	public TCMatrix setValue( final double val ) {
		assert( fElements.length == 1 );
		fElements[ 0 ] = val;
		return this;
	}

	/**
	 * this = this + m���s��
	 * this, m�̍s��T�C�Y���������K�v������
	 * @param m
	 * @return this
	 */
	public TCMatrix add( final TCMatrix m ) {
		assert( fNoOfRow == m.fNoOfRow &&
				fNoOfColumn == m.fNoOfColumn );
		for ( int i = 0; i < fElements.length; i++ ) {
			fElements[ i ] += m.fElements[ i ];
		}
		return this;
	}

	/**
	 * this = m1 + m2���s���D
	 * this, m1, m2�̍s��T�C�Y���������K�v������D
	 * @param m1
	 * @param m2
	 * @return
	 */
	public TCMatrix add(final TCMatrix m1, final TCMatrix m2) {
		assert(fNoOfRow == m1.fNoOfRow && fNoOfRow == m2.fNoOfRow
				    && fNoOfColumn == m1.fNoOfColumn && fNoOfColumn == m2.fNoOfColumn);
		for ( int i = 0; i < fElements.length; i++ ) {
			fElements[i] = m1.fElements[i] + m2.fElements[i];
		}
		return this;
	}

	/**
	 * this�̊e�v�f��val�𑫂�
	 * @param val
	 * @return this
	 */
	public TCMatrix add( final double val ) {
		for ( int i = 0; i < fElements.length; i++ ) {
			fElements[ i ] += val;
		}
		return this;
	}

	/**
	 * m�̊e�v�f��val�𑫂������̂�this�ɑ������D
	 * @param m
	 * @param val
	 * @return
	 */
	public TCMatrix add(final TCMatrix m, final double val) {
		assert(val != 0.0);
		assert(fNoOfRow == m.fNoOfRow && fNoOfColumn == m.fNoOfColumn);
		for ( int i = 0; i < fElements.length; i++ ) {
			fElements[i] = m.fElements[i] + val;
		}
		return this;
	}

	/**
	 * this = this - m ���s��
	 * this, m�̍s��T�C�Y�������K�v������
	 * @param m
	 * @return
	 */
	public TCMatrix sub( final TCMatrix m ) {
		assert( fNoOfRow == m.fNoOfRow &&
				fNoOfColumn == m.fNoOfColumn );

		for ( int i = 0; i < fElements.length; i++ ) {
			fElements[ i ] -= m.fElements[ i ];
		}
		return this;
	}

	/**
	 * this = m1 - m2���s���D
	 * this, m1, m2�̍s��T�C�Y���������K�v������D
	 * @param m1
	 * @param m2
	 * @return
	 */
	public TCMatrix sub(final TCMatrix m1, final TCMatrix m2) {
		assert(fNoOfRow == m1.fNoOfRow && fNoOfRow == m2.fNoOfRow
				    && fNoOfColumn == m1.fNoOfColumn && fNoOfColumn == m2.fNoOfColumn);
		for ( int i = 0; i < fElements.length; i++ ) {
			fElements[i] = m1.fElements[i] - m2.fElements[i];
		}
		return this;
	}

	/**
	 * this�̊e�v�f����val������
	 * @param val
	 * @return this
	 */
	public TCMatrix sub( final double val ) {
		for ( int i = 0; i < fElements.length; i++ ) {
			fElements[ i ] -= val;
		}
		return this;
	}

	/**
	 * m�̊e�v�f����val���Ђ������̂�this�ɑ������D
	 * @param m
	 * @param val
	 * @return
	 */
	public TCMatrix sub(final TCMatrix m, final double val) {
		assert(val != 0.0);
		assert(fNoOfRow == m.fNoOfRow && fNoOfColumn == m.fNoOfColumn);
		for ( int i = 0; i < fElements.length; i++ ) {
			fElements[i] = m.fElements[i] - val;
		}
		return this;
	}

	/**
	 * this = m2 * m3 ���s���Athis��Ԃ�
	 * m2 * m3�̌��ʂ�this�̑傫���������̕K�v������
	 * �R�p�^�[���ŕ��ނ��Ă��܂�
	 * @param m
	 * @param m2
	 * @return this
	 */
	public TCMatrix times( final TCMatrix m, final TCMatrix m2 ) {
		assert( m.fNoOfColumn == m2.fNoOfRow &&
				fNoOfRow == m.fNoOfRow &&
				fNoOfColumn == m2.fNoOfColumn );

		if ( m2.fNoOfColumn > 1 ) {
			if( fElements.length > 600 ) { // �s��T�C�Y���傫����΃L���b�V�����d������ikj���[�v������������
				Arrays.fill( fElements, 0.0 );
				int mIStepIndex = 0;
				for ( int i = 0; i < fElements.length; i += fNoOfColumn ) {
					int m2KStepIndex = 0;
					for ( int k = 0; k < m.fNoOfColumn; k++ ) {
						for ( int j = 0; j < fNoOfColumn; j++ ) {
							fElements[ i + j ] += m.fElements[ mIStepIndex + k ] * m2.fElements[ m2KStepIndex + j ];
						}
						m2KStepIndex += fNoOfColumn;
					}
					mIStepIndex += m.fNoOfColumn;
				}
			} else { // �s��T�C�Y�����������val�ŏ������ޒl���L���b�V��������������
				int mIStepIndex = 0;
				for ( int i = 0; i < fElements.length; i += fNoOfColumn ) {
					for ( int j = 0; j < fNoOfColumn; j++ ) {
						int m2KStepIndex = 0;
						double val = 0.0;
						for ( int k = 0; k < m.fNoOfColumn; k++ ) {
							val += m.fElements[ mIStepIndex + k ] * m2.fElements[ m2KStepIndex + j ];
							m2KStepIndex += fNoOfColumn;
						}
						fElements[ i + j ] = val;
					}
					mIStepIndex += m.fNoOfColumn;
				}
			}
		} else { // �s�� x �x�N�g���̏ꍇ�ɂ�for���[�v���Q�d�ɂȂ�̂ő���
			Arrays.fill( fElements, 0.0 );
			int iStepIndex = 0;
			for ( int i = 0; i < fNoOfRow; i++ ) {
				for ( int j = 0; j < m.fNoOfColumn; j++ ) {
					fElements[ i ] += m.fElements[ iStepIndex + j ] * m2.fElements[ j ];
				}
				iStepIndex += m.fNoOfColumn;
			}
		}
		return this;
	}

	/**
	 * this = this * m���s���B���ʂ͎��g�̍s��ɏ㏑�������
	 * this, n�����ɑ傫���̓��������s��̕K�v������
	 * �����łȂ��ꍇ�́Am1.times( m2, m3 )�𐄏�
	 * @param m
	 * @return
	 */
	public TCMatrix times( final TCMatrix m ) {

		assert( fNoOfRow == fNoOfColumn &&
				fNoOfColumn == m.fNoOfRow &&
				m.fNoOfRow == m.fNoOfColumn );

		final int size = fNoOfRow;
		final double[] ithRow = new double[ size ];

		for ( int i = 0; i < fElements.length; i += size ) {
			System.arraycopy( fElements, i, ithRow, 0, size );
			Arrays.fill( fElements, i, i + size, 0.0 );
			int mKIndex = 0;
			for ( int k = 0; k < size; k++ ) {
				for ( int j = 0; j < size; j++ ) {
					fElements[ i + j ] += ithRow[ k ] * m.fElements[ mKIndex + j ];
				}
				mKIndex += size;
			}
		}
		return this;
	}

	/**
	 * this�̑S�Ă̗v�f��val��������
	 * @param val �e�v�f�ɂ�����l
	 * @return this
	 */
	public TCMatrix times(final double val) {
		for ( int i = 0; i < fElements.length; i++ ) {
			fElements[ i ] *= val;
		}
		return this;
	}

	/**
	 * m�̊e�v�f��val�����������̂�this�ɑ������D
	 * @param m
	 * @param val
	 * @return
	 */
	public TCMatrix times(final TCMatrix m, final double val) {
		assert(val != 0.0);
		assert(fNoOfRow == m.fNoOfRow && fNoOfColumn == m.fNoOfColumn);
		for ( int i = 0; i < fElements.length; i++ ) {
			fElements[i] = m.fElements[i] * val;
		}
		return this;
	}

	/**
	 * this = m * this�̌��ʂ�this�ɏ�������
	 * this, m���傫���̓��������s�� n x n �łȂ���Ύg���Ȃ�
	 * �傫��n��double[]��new���N����̂ŁA�����x����������܂���
	 * ����������΁Am1.times( m2, m3 )�𐄏�
	 * @param m �����炩����s��
	 * @return this
	 */
	public TCMatrix timesLeft( final TCMatrix m ) {
		assert( fNoOfRow == fNoOfColumn &&
				fNoOfColumn == m.fNoOfRow &&
				m.fNoOfRow == m.fNoOfColumn );

		final int size = fNoOfRow;
		final double[] jthColumn = new double[ size ];

		for ( int j = 0; j < size; j++ ) {
			int iStepIndex = 0;
			for ( int i = 0; i < size; i++ ) {
				jthColumn[ i ] = fElements[ iStepIndex + j ];
				fElements[ iStepIndex + j ] = 0;
				iStepIndex += size;
			}
			for ( int i = 0; i < fElements.length; i += size ) {
				for ( int k = 0; k < size; k++ ) {
					fElements[ i + j ] += m.fElements[ i + k ] * jthColumn[ k ];
				}
			}
		}
		return this;
	}

	/**
	 * this = this .* m (�v�f���Ƃ̏�Z)
	 * @param m
	 * @return
	 */
	public TCMatrix timesElement( final TCMatrix m ) {
		assert( fNoOfRow == m.fNoOfRow &&
				fNoOfColumn == m.fNoOfColumn );

		for ( int i = 0; i < fElements.length; i++ ) {
			fElements[ i ] *= m.fElements[ i ];
		}

		return this;
	}

	/**
	 * this = m1 .* m2 (�v�f���Ƃ̏�Z)
	 * @param m1
	 * @param m2
	 * @return
	 */
	public TCMatrix timesElement(final TCMatrix m1, final TCMatrix m2) {
		assert(fNoOfRow == m1.fNoOfRow && fNoOfRow == m2.fNoOfRow
				    && fNoOfColumn == m1.fNoOfColumn && fNoOfColumn == m2.fNoOfColumn);
		for ( int i = 0; i < fElements.length; i++ ) {
			fElements[i] = m1.fElements[i] * m2.fElements[i];
		}
		return this;
	}

	/**
	 * this�̊e�v�f��val�Ŋ���
	 * @param val
	 * @return
	 */
	public TCMatrix div(final double val) {
		assert( val != 0.0 );
		for ( int i = 0; i < fElements.length; i++ ) {
			fElements[ i ] /= val;
		}
		return this;
	}

	/**
	 * m�̊e�v�f��val�Ŋ��������̂�this�ɑ������D
	 * @param m
	 * @param val
	 * @return
	 */
	public TCMatrix div(final TCMatrix m, final double val) {
		assert(val != 0.0);
		assert(fNoOfRow == m.fNoOfRow && fNoOfColumn == m.fNoOfColumn);
		for ( int i = 0; i < fElements.length; i++ ) {
			fElements[i] = m.fElements[i] / val;
		}
		return this;
	}

	/**
	 * this = this ./ m (�v�f���Ƃ̏��Z)
	 * @param m
	 * @return
	 */
	public TCMatrix divElement(TCMatrix m) {
		assert( fNoOfRow == m.fNoOfRow &&
				fNoOfColumn == m.fNoOfColumn );

		for ( int i = 0; i < fElements.length; i++ ) {
			fElements[i] /= m.fElements[ i ];
		}
		return this;
	}

	/**
	 * this = m1 ./ m2 (�v�f���Ƃ̏��Z)
	 * @param m1
	 * @param m2
	 * @return
	 */
	public TCMatrix divElement(final TCMatrix m1, final TCMatrix m2) {
		assert(fNoOfRow == m1.fNoOfRow && fNoOfRow == m2.fNoOfRow
				    && fNoOfColumn == m1.fNoOfColumn && fNoOfColumn == m2.fNoOfColumn);
		for ( int i = 0; i < fElements.length; i++ ) {
			fElements[i] = m1.fElements[i] / m2.fElements[i];
		}
		return this;
	}

	public double trace() {
		final int length = Math.min( fNoOfColumn, fNoOfRow );
		double sum = 0.0;
		int iStepIndex = 0;
		for ( int i = 0; i < length; i++ ) {
			sum += fElements[ iStepIndex + i ];
			iStepIndex += fNoOfColumn;
		}
		return sum;
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
		return ( getRowDimension() == getColumnDimension() ? (new TCLUDecomposition(this)).solve(B) :
			(new TCQRDecomposition(this)).solve(B));
	}

	/**
	 * Returns an inverse of this matrix.
	 * Updates this object.
	 * @return this matrix
	 */
	public TCMatrix inverse() {
		final TCMatrix identityMatrix = new TCMatrix( this ).eye();
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


	public double normF() {
		double sum = 0.0;
		for ( final double val : fElements ) {
			sum += val * val;
		}
		return Math.sqrt( sum );
	}

	/**
	 * this��L2�m�������v�Z����
	 * @return L2�m����
	 */
	public double normL2() {
		assert( getColumnDimension() == 1 );

		double sum = 0.0;
		for ( final double val : fElements ) {
			sum += val * val;
		}
		return Math.sqrt( sum );
	}

	/**
	 * this��v�̃��[�N���b�h�������v�Z����
	 * @param vector �����v�Z����x�N�g��
	 * @return ���[�N���b�h����
	 */
	public double distanceL2( final TCMatrix vector ) {
		assert ( fNoOfColumn == 1 && vector.fNoOfColumn == 1 );

		double sum = 0.0;
		for ( int i = 0; i < fElements.length; i++ ) {
			final double diff = fElements[ i ] - vector.fElements[ i ];
			sum += diff * diff;
		}
		return Math.sqrt( sum );
	}

	/**
	 * ���݂̃x�N�g����P�ʍs��ɂ���
	 * this���c�x�N�g���̕K�v������
	 * @return this
	 */
	public TCMatrix enforceToUnitVector() {
		assert( fNoOfColumn == 1 );

		final double norm = normL2();
		div( norm );
		return this;
	}

	/**
	 * vector�Ƃ̓��ς��v�Z����
	 * this, vector�Ƃ��ɓ����������̏c�x�N�g���̕K�v������
	 * @param vector
	 * @return <vector, this>
	 */
	public double innerProduct( final TCMatrix vector ) {
		assert( fNoOfColumn == 1 && vector.fNoOfColumn == 1 &&
				fNoOfRow == vector.fNoOfRow );
		double sum = 0.0;
		for ( int i = 0; i < fElements.length; i++ ) {
			sum += fElements[i] * vector.fElements[i];
		}
		return sum;
	}

	/**
	 * ���݂̍s���S��val�ŏ㏑������
	 * @param val
	 * @return this
	 */
	public TCMatrix fill( final double val ) {
		Arrays.fill( fElements, val );
		return this;
	}

	/**
	 * ���݂̍s����A[0, 1]���̈�l�����ŏ���������
	 * @param random ����������
	 * @return this
	 */
	public TCMatrix rand( final ICRandom random ) {
		for ( int i = 0; i < getDimension(); i++ ) {
			fElements[ i ] = random.nextDouble();
		}
		return this;
	}

	/**
	 * ���݂̍s���W�����K���z�ɏ]�������ŏ㏑������
	 * @param random ����������
	 * @return this
	 */
	public TCMatrix randn( final ICRandom random ) {
		for ( int i = 0; i < getDimension(); i++ ) {
			fElements[ i ] = random.nextGaussian();
		}
		return this;
	}

	/**
	 * ���݂̍s���P�ʍs��Œu��������
	 * @return this
	 */
	public TCMatrix eye() {
		Arrays.fill( fElements, 0.0 );
		final int size = Math.min( fNoOfRow, fNoOfColumn );
		for ( int i = 0; i < size; i++ ) {
			fElements[ i * fNoOfColumn + i ] = 1.0;
		}
		return this;
	}

	/**
	 * ���݂̊e�v�fx���s��w��expm( this )�Œu��������
	 * @param sym ���݂̍s�񂪑Ώ̍s��ł����true
	 * @return this
	 */
	public TCMatrix expm( final boolean sym ){
		assert( fNoOfColumn == fNoOfRow );

		final TCEigenvalueDecomposition eig = eig();
		final TCMatrix expD = eig.getD();
		final TCMatrix v = eig.getV();

	    for( int i = 0; i < fNoOfColumn; i++ ){
	    	expD.setValue( i, i, Math.expm1( expD.getValue( i, i ) ) + 1.0 );
	    }

	    if( sym ){
	    	final TCMatrix vT = v.tclone();
			this.copyFrom( v.times( expD ).times( vT ) );
	    } else {
	    	final TCMatrix vI = v.clone().inverse();
			this.copyFrom( v.times( expD ).times( vI ) );
	    }
	    return this;
	}

	/**
	 * ���݂̊e�v�fx��exp(x)�Œu��������
	 * @return this
	 */
	public TCMatrix exp() {
		for ( int i = 0; i < fElements.length; i++) {
			fElements[ i ] = Math.expm1( fElements[ i ] ) + 1.0;
		}
		return this;
	}

	/**
	 * ���݂̊e�v�fx��sin(x)�Œu��������
	 * @return this
	 */
	public TCMatrix sin() {
		for ( int i = 0; i < fElements.length; i++) {
			fElements[ i ] = Math.sin( fElements[ i ] );
		}
		return this;
	}

	/**
	 * ���݂̊e�v�fx��cos(x)�Œu��������
	 * @return this
	 */
	public TCMatrix cos() {
		for ( int i = 0; i < fElements.length; i++) {
			fElements[ i ] = Math.cos( fElements[ i ] );
		}
		return this;
	}

	/**
	 * ���݂̊e�v�fx��tan(x)�Œu��������
	 * @return this
	 */
	public TCMatrix tan() {
		for ( int i = 0; i < fElements.length; i++) {
			fElements[ i ] = Math.tan( fElements[ i ] );
		}
		return this;
	}

	/**
	 * ��̍s��̊e�v�f���r���C�傫�����̗v�f�ō\�����ꂽ�s��Ԃ��D
	 *
	 * @param b matrix
	 * @return this
	 */
	public TCMatrix max( final TCMatrix m ) {
		assert( fNoOfRow == m.fNoOfRow &&
				fNoOfColumn == m.fNoOfColumn );

		for ( int i = 0; i < fElements.length; i++ ) {
			if ( fElements[ i ] < m.fElements[ i ] ) {
				fElements[ i ] = m.fElements[ i ];
			}
		}
		return this;
	}

	/**
	 * �s��̊e�v�fa_(i,j)��max(a_(i,j), d)�Œu��������D
	 *
	 * @param d ��r�Ώ�
	 * @return this
	 */
	public TCMatrix max( final double val ) {
		for ( int i = 0; i < fElements.length; i++ ) {
			if( fElements[ i ] < val ) {
				fElements[ i ] = val;
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
	 * @return max �ő�l
	 */
	public double max( final ArrayList<Integer> indices ){
		indices.clear();
		double max = Double.NEGATIVE_INFINITY;

		for ( int i = 0; i < fElements.length; i++ ) {
			if ( max < fElements[ i ]  ) {
				indices.clear();
				indices.add( i );
				max = fElements[ i ];
			} else if( max == fElements[ i ] ){
				indices.add( i );
			}
		}
		return max;
	}

	/**
	 * ��̍s��̊e�v�f���r���C���������̗v�f�ō\�����ꂽ�s��Ԃ��D
	 *
	 * @param b matrix
	 * @return this
	 */
	public TCMatrix min( final TCMatrix m ) {
		assert( fNoOfRow == m.fNoOfRow &&
				fNoOfColumn == m.fNoOfColumn );

		for ( int i = 0; i < fElements.length; i++ ) {
			if ( fElements[ i ] > m.fElements[ i ] ) {
				fElements[ i ] = m.fElements[ i ];
			}
		}
		return this;
	}

	/**
	 * �s��̊e�v�fa_(i,j)��min(a_(i,j), d)�Œu��������D
	 *
	 * @param d ��r�Ώ�
	 * @return this
	 */
	public TCMatrix min( final double val ) {
		for ( int i = 0; i < fElements.length; i++ ) {
			if( val < fElements[ i ] ){
				fElements[ i ] = val;
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
	public double min( final ArrayList<Integer> indices ){

		indices.clear();
		double min = Double.MAX_VALUE;
		for ( int i = 0; i < fElements.length; i++ ) {
			if ( fElements[ i ] < min ) {
				indices.clear();
				indices.add( i );
				min = fElements[ i ];
			} else if ( fElements[ i ] == min ) {
				indices.add( i );
			}
		}
		return min;
	}

	@Override
	public TCMatrix clone() {
		return new TCMatrix( this );
	}

	/**
	 * ���g�� src_fromRow ���� src_toRow �s�C����� src_fromColumn ���� src_toColumn ��ɑΉ����镔���s��̃N���[�����쐬���ĕԂ��D
	 * �������A�S�Ă̒[�͋�ԓ��Ɋ܂܂��
	 * @param srcFromRow first row index
	 * @param srcToRow last row index
	 * @param srcFromColumn first column index
	 * @param srcToColumn last column index
	 * @return �����s��̃N���[��
	 */
	public TCMatrix cloneSubmatrix( final int srcFromRow, final int srcToRow,
			final int srcFromColumn, final int srcToColumn ) {

		final int subRowDimension = srcToRow - srcFromRow + 1;
		final int subColumnDimension = srcToColumn - srcFromColumn + 1;
		final TCMatrix result = new TCMatrix( subColumnDimension, subRowDimension );
		result.copySubmatrixFrom( this, srcFromRow, srcToRow, srcFromColumn, srcToColumn, 0, 0 );
		return result;
	}

	/**
	 * �w�肵���s�̍s�x�N�g���̃N���[����Ԃ��D
	 * @param rowIndex �s
	 * @return �s�x�N�g��
	 */
	public TCMatrix cloneRowVector( final int rowIndex ) {
		final TCMatrix result = new TCMatrix( 1, fNoOfColumn );
		final int columnIndex = rowIndex * fNoOfColumn;
		System.arraycopy( fElements, columnIndex, result.fElements, 0, fNoOfColumn );
		return result;
	}

	/**
	 * �w�肵����̗�x�N�g���̃N���[����Ԃ��D
	 * @param columnIndex ��
	 * @return ��x�N�g��
	 */
	public TCMatrix cloneColumnVector( final int columnIndex ) {
		final TCMatrix result = new TCMatrix( fNoOfRow );
		int iStepIndex = 0;
		for ( int i = 0; i < fNoOfRow; ++i ) {
			result.setValue( i, fElements[ iStepIndex + columnIndex ] );
			iStepIndex += fNoOfColumn;
		}
		return result;
	}

	/**
	 * �s��̗v�f��NaN���܂�ł��Ȃ������ׂ�D
	 * @return true:�܂�ł���Cfalse:�܂�ł��Ȃ�
	 */
	public boolean isNan() {
		for ( final double val : fElements ) {
			if ( Double.isNaN( val ) ) {
				return true;
			}
		}
		return false;
	}

	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append( "[" );
		for ( int i = 0; i < fNoOfRow; i++ ) {
			sb.append( "[" );
			sb.append( getValue( i, 0 ) );
			for ( int j = 1; j < fNoOfColumn; j++ ) {
				sb.append( ", " + getValue( i, j ) );
			}
			if ( i == fNoOfRow - 1 ) {
				sb.append( "]" );
			} else {
				sb.append( "], \n" );
			}
		}
		sb.append( "]\n" );

		return sb.toString();
	}

	/**
	 * �w�肳�ꂽ�s�ɍs����R�s�[����D
	 * @param m �R�s�[����s��D�񐔂��R�s�[��̍s��i���̍s��j�Ɠ����ł���K�v������D
	 * @param row �R�s�[����s�D(row+b�̍s��)���R�s�[��̍s��i���̍s��j�̍s�����Ɏ��܂��Ă���K�v������D
	 * @return �R�s�[���ꂽ��̂��̍s��
	 */
	public TCMatrix copyAtRow( final TCMatrix m, final int row ) {
		assert( fNoOfColumn == m.fNoOfColumn &&
				0 <= row && row + m.fNoOfRow <= fNoOfRow );

		final int additionalIndex = row * fNoOfColumn;
		for ( int i = 0; i < m.fElements.length; i += fNoOfColumn ) {
			for ( int j = 0; j < m.fNoOfColumn; ++j ) {
				fElements[ i + additionalIndex + j ] = m.fElements[ i + j ];
			}
		}
		return this;
	}

	/**
	 * �w�肳�ꂽ��ɍs����R�s�[����D
	 * @param m �R�s�[����s��D�s�����R�s�[��̍s��i���̍s��j�Ɠ����ł���K�v������D
	 * @param column �R�s�[�����D(column+b�̗�)���R�s�[��̍s��i���̍s��j�̗񐔓��Ɏ��܂��Ă���K�v������D
	 * @return �R�s�[���ꂽ��̂��̍s��
	 */
	public TCMatrix copyAtColumn( final TCMatrix m, final int column ) {
		assert( fNoOfRow == m.fNoOfRow &&
				0 <= column && column + m.fNoOfColumn <= fNoOfColumn );

		int mIStepIndex = 0;
		for ( int i = 0; i < fElements.length; i += fNoOfColumn ) {
			for ( int j = 0; j < m.fNoOfColumn; j++ ) {
				fElements[ i + j + column ] = m.fElements[ mIStepIndex + j ];
			}
			mIStepIndex += m.fNoOfColumn;
		}
		return this;
	}

	/**
	 * ��Ώ̍s���Ώ̍s��֋�������D
	 * @param ��Ώ̍s��
	 * @return �Ώ̍s��
	 */
	public TCMatrix enforceSymmetry(){
		assert( fNoOfColumn == fNoOfRow );

		int iStepIndex = 0;
		for ( int i = 0; i < fNoOfRow; i++ ) {
			int jStepIndex = 0;
			for ( int j = 0; j < i; j++ ) {
				fElements[ jStepIndex + i ] = fElements[ iStepIndex + j ];
				jStepIndex += fNoOfColumn;
			}
			iStepIndex += fNoOfColumn;
		}
		return this;
	}

	@Override
	public boolean equals( final Object o ) {
		if ( o == this ) {
			return true;
		}
		if ( !( o instanceof TCMatrix ) ) {
			return false;
		}
		final TCMatrix o_ = ( TCMatrix ) o;
		return	getColumnDimension() == o_.getColumnDimension() &&
				getRowDimension() == o_.getRowDimension() &&
				Arrays.equals( fElements, o_.fElements );
	}

	@Override
	public int hashCode() {
		final int multiplier = 37;
		int result = 17;
		for ( int i = 0; i < getRowDimension(); i++ ) {
			for ( int j = 0; j < getColumnDimension(); j++ ) {
				final long l = Double.doubleToLongBits( getValue( i, j ) );
				result = multiplier * result + (int) (l ^ (l >>> 32));
			}
		}
		return result;
	}
}