/*
This file is part of StepManiaSolver

Copyright (c) 2013, Bobbie Smulders

Contact:  mail@bsmulders.com

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.
 */

package task;

import java.util.ArrayList;
import java.util.HashSet;

import model.Configuration;
import model.DataPoint;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.simple.EncogUtility;

public class NeuralNetworkTrainer {

	private ArrayList<NeuralNetworkTrainerListener> listeners = new ArrayList<NeuralNetworkTrainerListener>();

	private Thread trainerThread;

	// Settings
	private int hiddenLayerNeurons[] = new int[2];
	private double maxTrainingError;
	private int maxTrainingTime;
	private int maxObjectDistance;

	public NeuralNetworkTrainer() {
		setHiddenLayerNeurons(0, Configuration.getIntSetting("NeuralNetworkTrainer.HiddenLayerNeurons.0"));
		setHiddenLayerNeurons(1, Configuration.getIntSetting("NeuralNetworkTrainer.HiddenLayerNeurons.1"));
		setMaxTrainingError(Configuration.getDoubleSetting("NeuralNetworkTrainer.MaxTrainingError"));
		setMaxTrainingTime(Configuration.getIntSetting("NeuralNetworkTrainer.MaxTrainingTime"));
		setMaxObjectDistance(Configuration.getIntSetting("NeuralNetworkTrainer.MaxObjectDistance"));
	}

	// Getters and setters
	public Thread getTrainerThread() {
		return trainerThread;
	}

	public void setTrainerThread(Thread trainerThread) {
		this.trainerThread = trainerThread;
	}

	private int getHiddenLayerNeurons(int index) {
		return hiddenLayerNeurons[index];
	}

	private void setHiddenLayerNeurons(int index, int hiddenLayerNeurons) {
		this.hiddenLayerNeurons[index] = hiddenLayerNeurons;
	}

	private double getMaxTrainingError() {
		return maxTrainingError;
	}

	private void setMaxTrainingError(double maxTrainingError) {
		this.maxTrainingError = maxTrainingError;
	}

	private int getMaxTrainingTime() {
		return maxTrainingTime;
	}

	private void setMaxTrainingTime(int maxTrainingTime) {
		this.maxTrainingTime = maxTrainingTime;
	}

	private int getMaxObjectDistance() {
		return maxObjectDistance;
	}

	private void setMaxObjectDistance(int maxObjectDistance) {
		this.maxObjectDistance = maxObjectDistance;
	}

	// Task methods
	public void train(final ArrayList<DataPoint> dataHistory) {
		if (isTraining()) {
			throw new IllegalStateException();
		}

		setTrainerThread(new Thread() {
			public void run() {
				// Clean and normalize the data history
				ArrayList<DataPoint> cleanedDataHistory = cleanDataHistory(dataHistory);
				ArrayList<DataPoint> normalizedDataHistory = normalizeDataHistory(cleanedDataHistory);

				// Create a new neural network and data set
				BasicNetwork neuralNetwork = EncogUtility.simpleFeedForward(2, getHiddenLayerNeurons(0),
						getHiddenLayerNeurons(1), 5, true);
				MLDataSet dataSet = new BasicMLDataSet();

				// Add all points of the data history to the data set
				for (DataPoint dataPoint : normalizedDataHistory) {
					MLData input = new BasicMLData(2);
					input.setData(0, dataPoint.getX());
					input.setData(1, dataPoint.getY());

					// If getButton() is 0, the output will be 0, 0, 0, 0
					// If getButton() is 2, the output will be 0, 1, 0, 0
					// If getButton() is 4, the output will be 0, 0, 0, 1
					MLData ideal = new BasicMLData(5);
					for (int i = 0; i <= 4; i++) {
						ideal.setData(i, (dataPoint.getButton() == i) ? 1 : 0);
					}

					MLDataPair pair = new BasicMLDataPair(input, ideal);
					dataSet.add(pair);
				}

				// Create a training method
				MLTrain trainingMethod = new ResilientPropagation((ContainsFlat) neuralNetwork, dataSet);
				long startTime = System.currentTimeMillis();
				int timeLeft = getMaxTrainingTime();
				int iteration = 0;

				// Train the network using multiple iterations on the training method
				do {
					trainingMethod.iteration();
					timeLeft = (int) ((startTime + getMaxTrainingTime()) - System.currentTimeMillis());
					iteration++;

					sendNeuralNetworkIteration(iteration, trainingMethod.getError(), timeLeft);
				} while (trainingMethod.getError() > getMaxTrainingError() && timeLeft > 0
						&& !trainingMethod.isTrainingDone());
				trainingMethod.finishTraining();

				// Return the neural network to all listeners
				sendNeuralNetworkTrainerResult(neuralNetwork);
			}
		});
		getTrainerThread().start();
	}

	public boolean isTraining() {
		if (trainerThread != null) {
			return trainerThread.isAlive();
		}
		return false;
	}

	public void stopTraining() {
		// Immediately stop training the network
		if (trainerThread != null) {
			trainerThread.interrupt();
		}
	}

	// Helper methods
	private ArrayList<DataPoint> normalizeDataHistory(ArrayList<DataPoint> dataHistory) {
		// Copy the data and create a new list
		ArrayList<DataPoint> dataHistoryCopy = new ArrayList<DataPoint>(dataHistory);
		ArrayList<DataPoint> normalizedDataHistory = new ArrayList<DataPoint>();

		// Gather the occurrences of all buttons
		int[] buttons = new int[5];

		for (DataPoint dataPoint : dataHistoryCopy) {
			buttons[dataPoint.getButton()]++;
		}

		// Check to see which button has the most occurrences
		int buttonIndex = -1;
		for (int i = 0; i < buttons.length; i++) {
			if (buttons[i] > 0 && (buttonIndex == -1 || buttons[i] > buttons[buttonIndex])) {
				buttonIndex = i;
			}
		}

		// Rebuild the data history, adding certain items multiple times based on total occurrence
		for (DataPoint dataPoint : dataHistoryCopy) {
			int times = buttons[buttonIndex] / buttons[dataPoint.getButton()];

			for (int i = 0; i < times; i++) {
				normalizedDataHistory.add(dataPoint);
			}
		}

		return normalizedDataHistory;
	}

	private ArrayList<DataPoint> cleanDataHistory(ArrayList<DataPoint> dataHistory) {
		// Copy the data and create a new list
		ArrayList<DataPoint> cleanDataHistory = new ArrayList<DataPoint>(dataHistory);
		HashSet<DataPoint> toRemove = new HashSet<DataPoint>();

		// Compare each data point to each other data point
		for (DataPoint dataPoint : cleanDataHistory) {
			if (toRemove.contains(dataPoint)) {
				continue;
			}

			// If the datapoint is closeby and has the value 0 or the same value,
			// que it to be removed
			for (DataPoint comparePoint : cleanDataHistory) {
				if (dataPoint != comparePoint
						&& dataPoint.distance(comparePoint) <= getMaxObjectDistance()
						&& (comparePoint.getButton() == 0 || dataPoint.getButton() == comparePoint
								.getButton())) {
					toRemove.add(comparePoint);
				}
			}
		}

		// Remove the queued data points from the list
		cleanDataHistory.removeAll(toRemove);

		return cleanDataHistory;
	}

	// Listener pattern methods
	public void addListener(NeuralNetworkTrainerListener nntl) {
		getListeners().add(nntl);
	}

	public void removeListener(NeuralNetworkTrainerListener nntl) {
		getListeners().remove(nntl);
	}

	private ArrayList<NeuralNetworkTrainerListener> getListeners() {
		return listeners;
	}

	private void sendNeuralNetworkTrainerResult(BasicNetwork neuralNetwork) {
		for (NeuralNetworkTrainerListener nntl : getListeners()) {
			nntl.processNeuralNetworkTrainerResult(neuralNetwork);
		}
	}

	private void sendNeuralNetworkIteration(int iteration, double error, int timeleft) {
		for (NeuralNetworkTrainerListener nntl : getListeners()) {
			nntl.processNeuralNetworkIteration(iteration, error, timeleft);
		}
	}

	// Finalize method
	@Override
	public void finalize() {
		if (getTrainerThread() != null) {
			getTrainerThread().interrupt();
		}
	}
}
