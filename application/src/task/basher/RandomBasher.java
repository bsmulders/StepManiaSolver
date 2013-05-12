/*
This file is part of StepManiaSolver

Copyright (c) 2013, Bobbie Smulders

Contact:  mail@bsmulders.com

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.
 */

package task.basher;

import model.Configuration;

public class RandomBasher extends AbstractBasher {

	public RandomBasher() {
		setScanInterval(Configuration.getIntSetting("RandomBasher.ScanInterval"));
	}

	// Basher method
	public void bash() {
		// Wait in between button bashes
		if (System.currentTimeMillis() > getNextScan()) {
			// Pick a random button, press it
			int button = (int) (Math.random() * 4) + 1;
			sendButtonBashUpdate(button);

			setNextScan(System.currentTimeMillis() + getScanInterval());
		}
	}
}
