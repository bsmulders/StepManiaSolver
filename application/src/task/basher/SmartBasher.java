/*
This file is part of StepManiaSolver

Copyright (c) 2013, Bobbie Smulders

Contact:  mail@bsmulders.com

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.
 */

package task.basher;

import java.util.ArrayList;

import model.Configuration;
import model.DataPoint;

public class SmartBasher extends AbstractBasher {

	// Settings
	private int maxObjectDistance;

	public SmartBasher() {
		setScanInterval(Configuration.getIntSetting("SmartBasher.ScanInterval"));
		setMaxObjectDistance(Configuration.getIntSetting("SmartBasher.MaxObjectDistance"));
	}

	// Getters and setters
	public int getMaxObjectDistance() {
		return maxObjectDistance;
	}

	public void setMaxObjectDistance(int maxObjectDistance) {
		this.maxObjectDistance = maxObjectDistance;
	}

	// Basher method
	public void scanAndBash(final ArrayList<DataPoint> dataHistory, final ArrayList<DataPoint> dataPoints) {
		// Wait in between button bashes
		if (System.currentTimeMillis() > getNextScan()) {
			Thread scanThread = new Thread() {
				public void run() {
					// The buttonState array stores which keys should be pressed
					boolean buttonState[] = { false, false, false, false };

					// For each data point currently on screen, check the data history for neighbors
					// Enable the button in the buttonState array if a button needs to be pressed
					for (DataPoint dataPoint : dataPoints) {
						int buttons[] = new int[4];

						// Check the entire data history for nearby button matches
						// Store it in buttons every time a match is found
						for (DataPoint counterPoint : dataHistory) {
							if (counterPoint.getButton() > 0
									&& dataPoint.distance(counterPoint) <= getMaxObjectDistance()) {
								buttons[counterPoint.getButton() - 1]++;
							}
						}

						// Find out which button has the most matches (if any)
						int buttonIndex = -1;
						for (int i = 0; i < buttons.length; i++) {
							if (buttons[i] > 0 && (buttonIndex == -1 || buttons[i] > buttons[buttonIndex])) {
								buttonIndex = i;
							}
						}

						// There was a button found, enable it in the buttonState array
						if (buttonIndex != -1) {
							buttonState[buttonIndex] = true;
						}
					}

					// Press all the buttons that are enabled in the buttonState array
					for (int i = 0; i < buttonState.length; i++) {
						if (buttonState[i] == true) {
							sendButtonBashUpdate(i + 1);
						}
					}
				}
			};
			scanThread.start();

			setNextScan(System.currentTimeMillis() + getScanInterval());
		}
	}
}
