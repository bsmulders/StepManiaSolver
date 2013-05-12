/*
This file is part of StepManiaSolver

Copyright (c) 2013, Bobbie Smulders

Contact:  mail@bsmulders.com

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.
 */

package model;

public class DataPoint {

	private double x, y;
	private int button;

	public DataPoint(double x, double y) {
		setX(x);
		setY(y);
	}

	public DataPoint(double x, double y, int button) {
		this(x, y);
		setButton(button);
	}

	// Getters and setters
	public double getX() {
		return x;
	}

	private void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	private void setY(double y) {
		this.y = y;
	}

	public int getButton() {
		return button;
	}

	public void setButton(int button) {
		this.button = button;
	}

	// Helper methods
	public double distance(DataPoint comparePoint) {
		double xDistance = Math.abs(this.getX() - comparePoint.getX());
		double yDistance = Math.abs(this.getY() - comparePoint.getY());
		double distance = xDistance + yDistance;

		return distance;
	}

	public String toString() {
		return String.format("X: %3.3f, Y: %3.3f, Button: %d", getX(), getY(), getButton());
	}
}
