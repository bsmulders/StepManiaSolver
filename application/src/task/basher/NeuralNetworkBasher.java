/*
This file is part of StepManiaSolver

Copyright (c) 2013, Bobbie Smulders

Contact:  mail@bsmulders.com

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.
 */

package task.basher;

import java.util.ArrayList;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;

import model.Configuration;
import model.DataPoint;

public class NeuralNetworkBasher extends AbstractBasher {

	public NeuralNetworkBasher() {
		setScanInterval(Configuration.getIntSetting("NeuralNetworkBasher.ScanInterval"));
	}

	// Basher methods
	public void scanAndBash(final BasicNetwork neuralNetwork, final ArrayList<DataPoint> dataPoints) {
		// Wait in between button bashes
		if (System.currentTimeMillis() > getNextScan()) {
			Thread scanThread = new Thread() {
				public void run() {
					// The buttonState array stores which keys should be pressed
					boolean buttonState[] = { false, false, false, false, false };

					// For each data point currently on screen, ask the network for it's button
					// Enable the button in the buttonState array if a button needs to be pressed
					for (DataPoint dataPoint : dataPoints) {
						int button = enquireNeuralNetwork(neuralNetwork, dataPoint);
						buttonState[button] = true;
					}

					// Press all the buttons that are enabled in the buttonState array
					for (int i = 1; i < buttonState.length; i++) {
						if (buttonState[i] == true) {
							sendButtonBashUpdate(i);
						}
					}
				}
			};
			scanThread.start();

			setNextScan(System.currentTimeMillis() + getScanInterval());
		}
	}

	public int enquireNeuralNetwork(final BasicNetwork neuralNetwork, DataPoint dataPoint) {
		MLData input = new BasicMLData(2);
		input.setData(0, dataPoint.getX());
		input.setData(1, dataPoint.getY());
		MLData output = neuralNetwork.compute(input);

		// Check to see which button has the highest output
		double buttons[] = output.getData();
		int buttonIndex = -1;
		for (int i = 0; i < buttons.length; i++) {
			if (buttons[i] > 0 && (buttonIndex == -1 || buttons[i] > buttons[buttonIndex])) {
				buttonIndex = i;
			}
		}

		return buttonIndex;
	}
}
