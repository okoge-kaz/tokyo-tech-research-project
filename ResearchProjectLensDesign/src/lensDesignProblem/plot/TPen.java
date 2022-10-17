package lensDesignProblem.plot;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;

public class TPen {

	private double fSrcX;
	private double fSrcY;
	private int fDstX;
	private int fDstY;
	private double fFactorX;
	private double fFactorY;
	private Graphics2D fGraphics;

	public TPen() {
	}

	public void setScaling(double srcX1, double srcY1, double srcX2, double srcY2,
		                      int dstX1, int dstY1, int dstX2, int dstY2) {
		fFactorX = (double) (dstX2 - dstX1) / (srcX2 - srcX1);
		fFactorY = (double) (dstY2 - dstY1) / (srcY2 - srcY1);
		fSrcX = srcX1;
		fSrcY = srcY1;
		fDstX = dstX1;
		fDstY = dstY1;
	}

	public void setDefaultScaling() {
		fSrcX = fSrcY = 0.0;
		fDstX = fDstY = 0;
		fFactorX = fFactorY = 1.0;
	}

	public void setGraphics(Graphics g) {
		fGraphics = (Graphics2D) g;
	}

	public void setColor(Color color) {
		fGraphics.setColor(color);
	}

	private int transX(double x) {
		return fDstX + (int) ((x - fSrcX) * fFactorX);
	}

	private int transY(double y) {
		return fDstY + (int) ((y - fSrcY) * fFactorY);
	}

	public void drawLine(double x1, double y1, double x2, double y2) {
		fGraphics.drawLine(transX(x1), transY(y1), transX(x2), transY(y2));
	}

	public void drawArc(double x1, double y1, double x2, double y2,
		                    double startAngle, double deltaAngle) {
		int x = Math.min(transX(x1), transX(x2));
		int y = Math.min(transY(y1), transY(y2));
		int width = Math.abs(transX(x1) - transX(x2));
		int height = Math.abs(transY(y1) - transY(y2));
		Arc2D.Double arc = new Arc2D.Double();
		arc.setArc(x, y, width, height, startAngle, deltaAngle, Arc2D.OPEN);
		fGraphics.draw(arc);
	}

	public void fillArc(double x1, double y1, double x2, double y2,
		                    double startAngle, double deltaAngle) {
		int x = Math.min(transX(x1), transX(x2));
		int y = Math.min(transY(y1), transY(y2));
		int width = Math.abs(transX(x1) - transX(x2));
		int height = Math.abs(transY(y1) - transY(y2));
		Arc2D.Double arc = new Arc2D.Double();
		arc.setArc(x, y, width, height, startAngle, deltaAngle, Arc2D.OPEN);
		fGraphics.fill(arc);
	}

	public void drawDot(double x, double y, double r, boolean transformingR) {
		if (transformingR) {
			fillArc(x - r, y - r, x + r, y + r, 0.0, 360.0);
		} else {
			int intX = transX(x) - (int)r;
			int intY = transY(y) - (int)r;
			int width = (int)(2.0 * r);
			int height = (int)(2.0 * r);
			fGraphics.fillArc(intX, intY, width, height, 0, 360);
		}
	}

	public void drawString(String str, double x, double y) {
		fGraphics.drawString(str, transX(x), transY(y));
	}
	
}
