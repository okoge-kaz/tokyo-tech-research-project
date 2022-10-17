package rexjgg;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * ベクトルクラスのテストスイート
 * @author isao
 *
 */
class TVectorTest {

	TVector v1; // 0, 1, 2, 3, 4
	TVector v2; // 1, 2, 3, 4, 5

	@BeforeEach
	public void setUp() {
		v1 = new TVector();
		v2 = new TVector();
		v1.setDimension(5);
		v2.setDimension(5);
		for (int i = 0; i < 5; ++i) {
			v1.setElement(i, i);
			v2.setElement(i, i + 1);
		}
	}

	@Test
	public void testTVector() {
		TVector v = new TVector();
		assertEquals(0, v.getDimension());
	}

	@Test
	public void testTVectorTVector() {
		TVector v = new TVector(v1);
		assertEquals(v1, v);
		assertNotSame(v1, v);
	}

	@Test
	public void testCopyFrom() {
		TVector v = new TVector();
		v.copyFrom(v1);
		assertEquals(v1, v);
		assertNotSame(v1, v);
	}

	@Test
	public void testClone() {
		TVector v = v1.clone();
		assertEquals(v1, v);
		assertNotSame(v1, v);
	}

	@Test
	public void testWriteTo() throws IOException {
		File file = null;
		try {
			Path tmpPath = Files.createTempFile("test", ".txt");
			file = tmpPath.toFile();
			PrintWriter pw = new PrintWriter(file);
			v1.writeTo(pw);
			pw.close();

			BufferedReader br = new BufferedReader(new FileReader(file));
			TVector v = new TVector();
			v.readFrom(br);
			assertEquals(v1, v);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (file != null && file.exists()) {
				file.delete();
			}
		}
	}

	@Test
	public void testReadFrom() throws IOException {
		File file = null;
		try {
			Path tmpPath = Files.createTempFile("test", ".txt");
			file = tmpPath.toFile();
			PrintWriter pw = new PrintWriter(file);
			v1.writeTo(pw);
			pw.close();

			BufferedReader br = new BufferedReader(new FileReader(file));
			TVector v = new TVector();
			v.readFrom(br);
			assertEquals(v1, v);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (file != null && file.exists()) {
				file.delete();
			}
		}
	}

	@Test
	public void testSetDimension() {
		v1.setDimension(10);
		assertEquals(10, v1.getDimension());
	}

	@Test
	public void testGetDimension() {
		assertEquals(5, v1.getDimension());
	}

	@Test
	public void testGetElement() {
		assertEquals(0, v1.getElement(0));
	}

	@Test
	public void testSetElement() {
		v1.setElement(0, 1);
		assertEquals(1, v1.getElement(0));
	}

	@Test
	public void testToString() {
		TVector v = new TVector();
		assertEquals("", v.toString());
	}

	@Test
	public void testEqualsObject() {
		assertEquals(v1, v1);
		assertNotEquals(v1, v2);
	}

	@Test
	public void testAdd() {
		v1.add(v2);
		for (int i = 0; i < v1.getDimension(); ++i) {
			assertEquals(i + (i + 1), v1.getElement(i));
		}
	}

	@Test
	public void testSubtract() {
		v1.subtract(v2);
		for (int i = 0; i < v1.getDimension(); ++i) {
			assertEquals(i - (i + 1), v1.getElement(i));
		}
	}

	@Test
	public void testScalarProduct() {
		v1.scalarProduct(2);
		for (int i = 0; i < v1.getDimension(); ++i) {
			assertEquals(i * 2, v1.getElement(i));
		}
	}

	@Test
	public void testElementwiseDevide() {
		v1.elementwiseDevide(v2);
		for (int i = 0; i < v1.getDimension(); ++i) {
			assertEquals(i / ((double) i + 1), v1.getElement(i));
		}
	}

	@Test
	public void testElementwiseProduct() {
		v1.elementwiseProduct(v2);
		for (int i = 0; i < v1.getDimension(); ++i) {
			assertEquals(i * (i + 1), v1.getElement(i));
		}
	}

	@Test
	public void testInnerProduct() {
		double a = 0;
		for (int i = 0; i < v1.getDimension(); ++i) {
			a += i * (i + 1);
		}
		assertEquals(a, v1.innerProduct(v2));
	}

	@Test
	public void testCalculateL2Norm() {
		assertEquals(Math.sqrt(0 + 1 + 4 + 9 + 16), v1.calculateL2Norm());
	}

	@Test
	public void testNormalize() {
		v1.Normalize();
		assertEquals(1., v1.calculateL2Norm(), TVector.EPS);
		v2.Normalize();
		assertEquals(1., v2.calculateL2Norm(), TVector.EPS);
	}
}
