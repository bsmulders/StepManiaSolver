/*
This file is part of StepManiaSolver

Copyright (c) 2013, Bobbie Smulders

Contact:  mail@bsmulders.com

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.
 */

package view.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import model.DataPoint;
import model.GameBorder;
import model.Model;
import model.ViewMode;

public class NeuralNetworkEvaluationPanel extends JPanel {

	private static final long serialVersionUID = 8892223711360570674L;

	private Model model;

	public NeuralNetworkEvaluationPanel(Model model, int width, int height) {
		setModel(model);

		setDoubleBuffered(true);
		setPreferredSize(new Dimension(width, height));
		setBackground(Color.BLACK);
	}

	// Getters and setters
	private Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	// Override methods
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		ArrayList<DataPoint> copy = new ArrayList<DataPoint>(getModel().getNeuralNetworkEvaluation());

		Graphics2D graphics2D = (Graphics2D) graphics;

		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		graphics2D.setColor(getBackground());
		graphics2D.fillRect(0, 0, getWidth(), getHeight());

		int width = getWidth();
		int height = getHeight();
		int radius = (int) (width * 0.1);
		int margin = (int) (width * 0.05);
		GameBorder gameBorder = getModel().getGameBorder();

		if (getModel().getViewMode() == ViewMode.SIMPLE) {
			for (DataPoint dataPoint : copy) {
				if (dataPoint.getButton() > 0
						&& (dataPoint.getX() == -96 || dataPoint.getX() == -32 || dataPoint.getX() == 32 || dataPoint
								.getX() == 96)) {
					switch (dataPoint.getButton()) {
					case 1:
						graphics2D.setColor(Color.RED);
						break;
					case 2:
						graphics2D.setColor(Color.GREEN);
						break;
					case 3:
						graphics2D.setColor(Color.BLUE);
						break;
					case 4:
						graphics2D.setColor(Color.YELLOW);
						break;
					}

					int x = (int) map(dataPoint.getX(), gameBorder.getLeft(), gameBorder.getRight(), margin,
							width - (margin * 2));
					int y = (int) map(dataPoint.getY(), gameBorder.getTop(), gameBorder.getBottom(), margin,
							height - (margin * 2));

					graphics2D.fillOval(x - (radius / 2), y - (radius / 2), radius, radius);
				}
			}
		} else {
			for (DataPoint dataPoint : copy) {
				switch (dataPoint.getButton()) {
				case 0:
					graphics2D.setColor(Color.WHITE);
					break;
				case 1:
					graphics2D.setColor(Color.RED);
					break;
				case 2:
					graphics2D.setColor(Color.GREEN);
					break;
				case 3:
					graphics2D.setColor(Color.BLUE);
					break;
				case 4:
					graphics2D.setColor(Color.YELLOW);
					break;
				}

				int x = (int) map(dataPoint.getX(), gameBorder.getLeft(), gameBorder.getRight(), margin,
						width - (margin * 2));
				int y = (int) map(dataPoint.getY(), gameBorder.getTop(), gameBorder.getBottom(), margin,
						height - (margin * 2));

				graphics2D.drawLine(x, y, x, y);
			}
		}

		graphics2D.setColor(Color.GRAY);
		graphics2D.drawString("Neural network", 10, 20);
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	// Helper method
	private double map(double value, double istart, double istop, double ostart, double ostop) {
		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	}
}
