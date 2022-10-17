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

class TIndividualTest {

	TIndividual x1; // eval: 1, v: 0, 1, 2, 3, 4
	TIndividual x2; // eval: 2, v: 1, 2, 3, 4, 5

	@BeforeEach
	public void setUp() {
		x1 = new TIndividual();
		x2 = new TIndividual();
		x1.setEvaluationValue(1);
		x2.setEvaluationValue(2);
		x1.getVector().setDimension(5);
		x2.getVector().setDimension(5);
		for (int i = 0; i < 5; ++i) {
			x1.getVector().setElement(i, i);
			x2.getVector().setElement(i, i + 1);
		}
	}

	@Test
	void testTIndividual() {
		TIndividual x = new TIndividual();
		assertEquals(Double.NaN, x.getEvaluationValue());
	}

	@Test
	void testTIndividualTIndividual() {
		TIndividual x = new TIndividual(x1);
		assertEquals(x1.getEvaluationValue(), x.getEvaluationValue());
		assertEquals(x1.getVector(), x.getVector());
	}

	@Test
	void testClone() {
		TIndividual x = x1.clone();
		assertEquals(x1.getEvaluationValue(), x.getEvaluationValue());
		assertEquals(x1.getVector(), x.getVector());
	}

	@Test
	void testWriteTo() {
		File file = null;
		try {
			Path tmpPath = Files.createTempFile("test", ".txt");
			file = tmpPath.toFile();
			PrintWriter pw = new PrintWriter(file);
			x1.writeTo(pw);
			pw.close();

			BufferedReader br = new BufferedReader(new FileReader(file));
			TIndividual x = new TIndividual();
			x.readFrom(br);
			assertEquals(x1.getEvaluationValue(), x.getEvaluationValue());
			assertEquals(x1.getVector(), x.getVector());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (file != null && file.exists()) {
				file.delete();
			}
		}
	}

	@Test
	void testReadFrom() {
		File file = null;
		try {
			Path tmpPath = Files.createTempFile("test", ".txt");
			file = tmpPath.toFile();
			PrintWriter pw = new PrintWriter(file);
			x1.writeTo(pw);
			pw.close();

			BufferedReader br = new BufferedReader(new FileReader(file));
			TIndividual x = new TIndividual();
			x.readFrom(br);
			assertEquals(x1.getEvaluationValue(), x.getEvaluationValue());
			assertEquals(x1.getVector(), x.getVector());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (file != null && file.exists()) {
				file.delete();
			}
		}
	}

	@Test
	void testToString() {
		TIndividual x = new TIndividual();
		assertEquals("NaN\n", x.toString());
	}

	@Test
	void testGetEvaluationValue() {
		TIndividual x = new TIndividual();
		assertEquals(Double.NaN, x.getEvaluationValue());
	}

	@Test
	void testSetEvaluationValue() {
		x1.setEvaluationValue(3.);
		assertEquals(3., x1.getEvaluationValue());
	}

	@Test
	void testGetVector() {
		TIndividual x = new TIndividual();
		assertEquals(Double.NaN, x.getEvaluationValue());
	}

}
