/*
This file is part of StepManiaSolver

Copyright (c) 2013, Bobbie Smulders

Contact:  mail@bsmulders.com

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.
 */

package task;

import java.util.ArrayList;

import model.Configuration;
import model.DataPoint;

public class CollectionComparator {

	private ArrayList<CollectionComparatorListener> listeners = new ArrayList<CollectionComparatorListener>();

	private Thread compareThread;

	private int lastPressedButton;
	private long expirationDate;
	private double topBorder;

	// Settings
	private int buttonExpiration;
	private double borderMargin;
	private int maxObjectDistance;

	public CollectionComparator() {
		setButtonExpiration(Configuration.getIntSetting("CollectionComparator.ButtonExpiration"));
		setMaxObjectDistance(Configuration.getIntSetting("CollectionComparator.MaxObjectDistance"));
		setBorderMargin(Configuration.getDoubleSetting("CollectionComparator.BorderMargin"));
	}

	// Getters and setters
	public Thread getCompareThread() {
		return compareThread;
	}

	public void setCompareThread(Thread compareThread) {
		this.compareThread = compareThread;
	}

	private int getLastPressedButton() {
		return lastPressedButton;
	}

	public void setLastPressedButton(int lastPressedButton) {
		this.lastPressedButton = lastPressedButton;
		setExpirationDate(System.currentTimeMillis() + getButtonExpiration());
	}

	private long getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(long expirationDate) {
		this.expirationDate = expirationDate;
	}

	public int getButtonExpiration() {
		return buttonExpiration;
	}

	public void setButtonExpiration(int buttonExpiration) {
		this.buttonExpiration = buttonExpiration;
	}

	private double getTopBorder() {
		return topBorder;
	}

	public void setTopBorder(double topBorder) {
		this.topBorder = topBorder;
	}

	private double getBorderMargin() {
		return borderMargin;
	}

	private void setBorderMargin(double borderMargin) {
		this.borderMargin = borderMargin;
	}

	public int getMaxObjectDistance() {
		return maxObjectDistance;
	}

	public void setMaxObjectDistance(int maxObjectDistance) {
		this.maxObjectDistance = maxObjectDistance;
	}

	// Task method
	public void compare(final ArrayList<DataPoint> oldList, final ArrayList<DataPoint> newList) {
		// Do not check for results X millisecond after the button has pressed (typically 100ms)
		if (System.currentTimeMillis() < getExpirationDate()) {
			setCompareThread(new Thread() {
				public void run() {
					ArrayList<DataPoint> results = new ArrayList<DataPoint>();
					int destroyed = 0;

					// Go to all the data points on the old list
					for (DataPoint dataPoint : oldList) {
						boolean isReincarnated = false;

						// Go to all the data points on the new list
						for (DataPoint comparePoint : newList) {
							// If a data point is found on both the new and old list around the same location,
							// we can assure that it has moved and has not disappeared
							if (dataPoint.distance(comparePoint) <= getMaxObjectDistance()) {
								isReincarnated = true;
							}
						}

						// A data point has disappeared and it was not because it reached a border
						// Associated the last pressed button with this data point
						if (!isReincarnated && dataPoint.getY() > (getTopBorder() * (1 - getBorderMargin()))) {
							dataPoint.setButton(getLastPressedButton());
							destroyed++;
						}

						results.add(dataPoint);
					}

					// If more than half of the data points have been destroyed, something went wrong,
					// so ignore the results. Otherwise, send the data points to the listeners
					if (results.size() / 2 >= destroyed) {
						for (DataPoint result : results) {
							sendCollectionComperatorResult(result);
						}
					} else {
						System.err.println("Lag prevention just kicked in!");
					}
				}
			});
			getCompareThread().start();
		}
	}

	// Listener pattern methods
	public void addListener(CollectionComparatorListener ccl) {
		getListeners().add(ccl);
	}

	public void removeListener(CollectionComparatorListener ccl) {
		getListeners().remove(ccl);
	}

	private ArrayList<CollectionComparatorListener> getListeners() {
		return listeners;
	}

	private void sendCollectionComperatorResult(DataPoint dataPoint) {
		for (CollectionComparatorListener ccl : getListeners()) {
			ccl.processCollectionComperatorResult(dataPoint);
		}
	}

	// Finalize method
	@Override
	public void finalize() {
		if (getCompareThread() != null) {
			getCompareThread().interrupt();
		}
	}
}
