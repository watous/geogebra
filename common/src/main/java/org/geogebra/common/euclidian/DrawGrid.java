package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.MyMath;

/**
 * Helper class for drawing the grid
 * 
 * @author zbynek
 *
 */
public class DrawGrid {
	private EuclidianView view;

	/**
	 * 
	 * @param euclidianView
	 *            view
	 */
	public DrawGrid(EuclidianView euclidianView) {
		view = euclidianView;
	}

	/**
	 * 
	 * @param g2
	 *            graphics
	 * @param xCrossPix
	 *            x crossing pixel
	 * @param yCrossPix
	 *            y crossing pixel
	 * @param subGrid
	 *            whether to draw subgrid
	 */
	protected void drawCartesianGrid(GGraphics2D g2, double xCrossPix,
			double yCrossPix, boolean subGrid) {
		if (view.getXaxisLog()) {
			drawVerticalGridLog(g2, xCrossPix, yCrossPix);
		} else {
			drawVerticalGridLinear(g2, xCrossPix, yCrossPix, subGrid);
		}

		// horizontal grid lines
		if (view.getYaxisLog()) {
			drawHorizontalGridLog(g2, xCrossPix, yCrossPix);
		} else {
			drawHorizontalGridLinear(g2, xCrossPix, yCrossPix, subGrid);
		}

	}



	// private static int brighterComponent(int comp) {
	// double factor = 0.3;
	// return (int) (255 - ((255 - comp) * factor));
	//
	// }

	/**
	 * @return brighter color
	 */
	private static GColor getBrighterColor(GColor orig) {
		// return GColor.newColor(brighterComponent(orig.getRed()),
		// brighterComponent(orig.getGreen()),
		// brighterComponent(orig.getBlue()));
		return GColor.newColor(orig.getRed(), orig.getGreen(), orig.getBlue(),
				60);
	}

	private void drawHorizontalGridLinear(GGraphics2D g2, double xCrossPix1,
			double yCrossPix1, boolean subGrid) {

		double xCrossPix = xCrossPix1;
		double yCrossPix = yCrossPix1;
		double tickStepY = view.getYscale() * view.gridDistances[1];
		double start = view.getYZero() % tickStepY;
 		double smallStep;
		int topSubGrids = 0;

		// number of parts splitted by subgrids
		int n = 1;
		if (view.getApplication().has(Feature.MINOR_GRIDLINES) && subGrid) {
			n = 5;
			smallStep = tickStepY / n;
			//start of subgrids
			start = view.getYZero() % smallStep;
			//start of grids
			double start2 = view.getYZero() % tickStepY;		
			// number of subgrids at the top, above the highest main grid
			topSubGrids = Math.round((float) ((start2 - start) / smallStep));
		}

		final double left = view.positiveAxes[0] ? xCrossPix : 0;

		final double yAxisEnd = (view.positiveAxes[1]
				&& yCrossPix < view.getHeight()) ? yCrossPix : view.getHeight();

		double pix;

		if (view.getApplication().has(Feature.MINOR_GRIDLINES)
				&& view.getApplication().has(Feature.SPEED_UP_GRID_DRAWING)) {
			// draw main grid
			g2.setColor(view.getGridColor());
			g2.startGeneralPath();
			double startGrid = start + topSubGrids * tickStepY / n;
			pix = startGrid;
			for (int j = 0; pix <= yAxisEnd; j++) {
				drawHorizontalGridLine(g2, pix, left, yCrossPix);
				pix = startGrid + (j * tickStepY);
			}
			g2.endAndDrawGeneralPath();

			// draw sub grid
			g2.setColor(getBrighterColor(view.getGridColor()));
			g2.startGeneralPath();
			pix = start;
			for (int j = 0; pix <= yAxisEnd; j++) {
				if ((j - topSubGrids - 1) % n != 0) {
					// don't draw over main grid
					drawHorizontalGridLine(g2, pix, left, yCrossPix);
				}
				pix = start + (j * tickStepY / n);
			}
			g2.endAndDrawGeneralPath();
		} else {
			pix = start;
			if (view.getApplication().has(Feature.MINOR_GRIDLINES)) {
				pix = start - tickStepY / n;
			} else if (pix > (view.getHeight() - EuclidianView.SCREEN_BORDER)) {
				pix -= tickStepY;
			}

			for (int j = 0; pix <= yAxisEnd; j++) {
				// don't draw the grid line x=0 if the y-axis is showing
				// or if it's too close (eg sticky axes)
				if (!view.showAxes[0] || Math.abs(pix - yCrossPix) > 2d) {

					if (view.getApplication().has(Feature.MINOR_GRIDLINES)) {
						if ((j - topSubGrids - 1) % n == 0) {
							// g2.setStrokeLineWidth(1);
							g2.setColor(view.getGridColor());

						} else {
							// g2.setStrokeLineWidth(0.4);
							g2.setColor(getBrighterColor(view.getGridColor()));
						}
					}

					addStraightLineToGeneralPath(g2, left, pix,
								view.getWidth(), pix);
				}

				if (view.getApplication().has(Feature.MINOR_GRIDLINES)) {
					pix = start + (j * tickStepY / n);
				} else {
					pix = start + (j * tickStepY);
				}

			}
		}
	}

	private void drawHorizontalGridLine(GGraphics2D g2, double pix, double left,
			double yCrossPix) {
		// don't draw the grid line x=0 if the y-axis is showing
		// or if it's too close (eg sticky axes)
		if (!view.showAxes[0] || Math.abs(pix - yCrossPix) > 2d) {
			addStraightLineToGeneralPath(g2, left, pix, view.getWidth(), pix);
		}
	}

	private void drawHorizontalGridLog(GGraphics2D g2, double xCrossPix,
			double yCrossPix) {
		double tickStepY = view.getYscale() * view.gridDistances[1];
		double start = view.getYZero() % tickStepY;
		double pix = 0;
		final double left = view.positiveAxes[0] ? xCrossPix : 0;
		final double yAxisEnd = (view.positiveAxes[1]
				&& yCrossPix < view.getHeight()) ? yCrossPix : view.getHeight();
		double pow = MyMath.nextPrettyNumber(view.getYmin(), 1);
		g2.startGeneralPath();
		for (int j = 0; pix <= yAxisEnd; j++) {
			// don't draw the grid line x=0 if the y-axis is showing
			// or if it's too close (eg sticky axes)

			pix = view.toScreenCoordYd(pow);
			if (!view.showAxes[0] || Math.abs(pix - yCrossPix) > 2d) {

				if (view.axesLabelsPositionsY.contains(
						Integer.valueOf((int) (pix + Kernel.MIN_PRECISION)))) {

					// hits axis label, draw in 2 sections
					drawLineAvoidingLabelsH(g2, left, pix, view.getWidth(),
							pix);
				} else {

					// not hitting axis label, just draw it
					addStraightLineToGeneralPath(g2, left, pix, view.getWidth(), pix);
				}
			}

			pix = start + (j * tickStepY);
			pow = pow * 10;
		}
		g2.endAndDrawGeneralPath();

	}

	private void drawVerticalGridLinear(GGraphics2D g2, double xCrossPix,
			double yCrossPix1, boolean subGrid) {

		double yCrossPix = yCrossPix1;

		if (view.getApplication().has(Feature.TICK_NUMBERS_AT_EDGE)) {
			if (yCrossPix1 >= view.getHeight() - view.xLabelHeights - 5) {
				// If the xAxis is offscreen on the bottom, or almost offscreen,
				// numbers
				// will be fixed at the bottom edge of view, and because of this
				// grid won't be drawn there, there will be some space for the
				// numbers. The position of this space depends on value of
				// yCrossPix.
				yCrossPix = view.getHeight() - view.xLabelHeights - 5;
			} else if (yCrossPix1 <= 0) {
				yCrossPix = 0 + Kernel.MIN_PRECISION;
			}
		}

		// vertical grid lines
		double tickStepX = view.getXscale() * view.gridDistances[0];
		double xAxisStart = (view.positiveAxes[0] && xCrossPix > 0)
				? xCrossPix + (((view.getXZero() - xCrossPix) % tickStepX)
						+ tickStepX) % tickStepX
				: (view.getXZero() % tickStepX);

		double smallStep;
		int leftSubGrids = 0;

		// number of parts splitted by subgrids
		int n = 1;
		if (view.getApplication().has(Feature.MINOR_GRIDLINES) && subGrid) {
			n = 5;
			smallStep = tickStepX / n;
			// start of subgrids
			xAxisStart = (view.positiveAxes[0] && xCrossPix > 0)
					? xCrossPix + (((view.getXZero() - xCrossPix) % smallStep)
							+ smallStep) % smallStep
					: (view.getXZero() % smallStep);
			// start of grids
			double start2 = view.getXZero() % tickStepX;
			// number of subgrids on the left
			leftSubGrids = Math
					.round((float) ((start2 - xAxisStart) / smallStep));
		}

		final double yAxisEnd = (view.positiveAxes[1]
				&& yCrossPix < view.getHeight()) ? yCrossPix : view.getHeight();
		final double bottom = view.positiveAxes[1] ? yAxisEnd
				: view.getHeight();
		double pix;

		if (view.getApplication().has(Feature.MINOR_GRIDLINES)
				&& view.getApplication().has(Feature.SPEED_UP_GRID_DRAWING)) {

			// draw main grid
			g2.startGeneralPath();
			g2.setColor(view.getGridColor());
			double startGrid = xAxisStart + leftSubGrids * tickStepX / n;
			pix = startGrid;
			for (int i = 0; pix <= view.getWidth(); i++) {
				drawVerticalGridLine(g2, pix, bottom, xCrossPix, yCrossPix);
				pix = startGrid + (i * tickStepX);
			}
			g2.endAndDrawGeneralPath();

			// draw sub grid
			g2.startGeneralPath();
			g2.setColor(getBrighterColor(view.getGridColor()));
			pix = xAxisStart;
			for (int i = 1; pix <= view.getWidth(); i++) {
				if ((i - leftSubGrids - 1) % n != 0) {
					// don't draw over main grid
					drawVerticalGridLine(g2, pix, bottom, xCrossPix, yCrossPix);
				}
				pix = xAxisStart + (i * tickStepX / n);
			}
			g2.endAndDrawGeneralPath();

		} else {
			pix = xAxisStart;

			if (view.getApplication().has(Feature.MINOR_GRIDLINES)) {
				pix = xAxisStart;
			} else if (pix < EuclidianView.SCREEN_BORDER) {
				pix += tickStepX;
			}

			for (int i = 1; pix <= view.getWidth(); i++) {
				// don't draw the grid line x=0 if the y-axis is showing
				// or if it's too close (eg sticky axes)

				if (!view.showAxes[1] || Math.abs(pix - xCrossPix) > 2d) {
					if (view.getApplication().has(Feature.MINOR_GRIDLINES)) {
						if ((i - leftSubGrids - 1) % n == 0) {
							// g2.setStrokeLineWidth(1);
							g2.setColor(view.getGridColor());

						} else {
							// g2.setStrokeLineWidth(0.4);
							g2.setColor(getBrighterColor(view.getGridColor()));
						}
					}

					if (view.axesLabelsPositionsX.contains(Integer.valueOf((int) (pix + Kernel.MIN_PRECISION)))) {

						// hits axis label, draw in 2 sections
						drawLineAvoidingLabelsV(g2, pix, 0, pix, bottom, yCrossPix);
					} else {
						// not hitting axis label, just draw it
						addStraightLineToGeneralPath(g2, pix, 0, pix, bottom);

					}

				}

				if (view.getApplication().has(Feature.MINOR_GRIDLINES)) {
					pix = xAxisStart + (i * tickStepX / n);
				} else {
					pix = xAxisStart + (i * tickStepX);
				}

			}
		}
	}

	private void drawVerticalGridLine(GGraphics2D g2, double pix, double bottom, double xCrossPix, double yCrossPix) {
		// don't draw the grid line x=0 if the y-axis is showing
		// or if it's too close (eg sticky axes)
		if (!view.showAxes[1] || Math.abs(pix - xCrossPix) > 2d) {
			if (view.axesLabelsPositionsX.contains(Integer.valueOf((int) (pix + Kernel.MIN_PRECISION)))) {

				// hits axis label, draw in 2 sections
				drawLineAvoidingLabelsV(g2, pix, 0, pix, bottom, yCrossPix);
			} else {
				// not hitting axis label, just draw it
				addStraightLineToGeneralPath(g2, pix, 0, pix, bottom);
			}
		}
	}

	private void drawVerticalGridLog(GGraphics2D g2, double xCrossPix,
			double yCrossPix) {
		// vertical grid lines
		double tickStepX = view.getXscale() * view.gridDistances[0];
		final double xAxisStart = (view.positiveAxes[0] && xCrossPix > 0)
				? xCrossPix + (((view.getXZero() - xCrossPix) % tickStepX)
						+ tickStepX) % tickStepX
				: (view.getXZero() % tickStepX);

		final double yAxisEnd = (view.positiveAxes[1]
				&& yCrossPix < view.getHeight()) ? yCrossPix : view.getHeight();
		final double bottom = view.positiveAxes[1] ? yAxisEnd
				: view.getHeight();
		double pix = 0;
		double pow = MyMath.nextPrettyNumber(view.getYmin(), 1);
		g2.startGeneralPath();
		for (int i = 0; pix <= view.getWidth(); i++) {
			// don't draw the grid line x=0 if the y-axis is showing
			// or if it's too close (eg sticky axes)

			pix = view.toScreenCoordXd(pow);

			if (!view.showAxes[1] || Math.abs(pix - xCrossPix) > 2d) {
				if (view.axesLabelsPositionsX.contains(
						Integer.valueOf((int) (pix + Kernel.MIN_PRECISION)))) {

					// hits axis label, draw in 2 sections
					drawLineAvoidingLabelsV(g2, pix, 0, pix, bottom, yCrossPix);
				} else {
					// not hitting axis label, just draw it
					addStraightLineToGeneralPath(g2, pix, 0, pix, bottom);

				}

			}
			pow = pow * 10;
			pix = xAxisStart + (i * tickStepX);
		}
		g2.endAndDrawGeneralPath();

	}

	private void drawLineAvoidingLabelsH(GGraphics2D g2, double x1, double y1,
			double x2, double y2) {

		addStraightLineToGeneralPath(g2, x1, y1, x2, y2);

	}

	private void drawLineAvoidingLabelsV(GGraphics2D g2, double x1, double y1,
			double x2, double y2, double yCrossPix) {

		if (yCrossPix > y1 && yCrossPix < y2) {
			// split in 2
			addStraightLineToGeneralPath(g2, x1, y1, x2, yCrossPix);

			addStraightLineToGeneralPath(g2, x1, yCrossPix + view.xLabelHeights + 5, x2, y2);

		} else {
			addStraightLineToGeneralPath(g2, x1, y1, x2, y2);
		}
	}

	private void addStraightLineToGeneralPath(GGraphics2D g2, double x1, double y1, double x2, double y2) {
		if (view.getApplication().has(Feature.SPEED_UP_GRID_DRAWING)) {
			g2.addStraightLineToGeneralPath(x1, y1, x2, y2);
		} else {
			g2.drawStraightLine(x1, y1, x2, y2);
		}
	}

}
