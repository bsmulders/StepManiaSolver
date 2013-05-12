/*
This file is part of StepManiaSolver

Copyright (c) 2013, Bobbie Smulders

Contact:  mail@bsmulders.com

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.
 */

package model;

public class GameBorder {

	private int left, right, top, bottom;

	protected GameBorder() {
	}

	public GameBorder(int left, int right, int top, int bottom) {
		this();
		setLeft(left);
		setRight(right);
		setTop(top);
		setBottom(bottom);
	}

	// Getters and setters
	public int getLeft() {
		return left;
	}

	protected void setLeft(int left) {
		this.left = left;
	}

	public int getRight() {
		return right;
	}

	protected void setRight(int right) {
		this.right = right;
	}

	public int getTop() {
		return top;
	}

	protected void setTop(int top) {
		this.top = top;
	}

	public int getBottom() {
		return bottom;
	}

	protected void setBottom(int bottom) {
		this.bottom = bottom;
	}
}
