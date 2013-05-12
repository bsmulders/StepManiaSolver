/*
This file is part of StepManiaSolver

Copyright (c) 2013, Bobbie Smulders

Contact:  mail@bsmulders.com

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.
 */

package task;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import model.Configuration;

public class Communicator extends Thread {

	private ArrayList<CommunicatorListener> listeners = new ArrayList<CommunicatorListener>();

	private boolean clientConnected;
	private ServerSocket serverSocket;
	private BufferedReader clientReader;
	private DataOutputStream clientWriter;

	// Settings
	private int port;

	public Communicator() {
		setPort(Configuration.getIntSetting("Communicator.Port"));
	}

	// Getters and setters
	private boolean isClientConnected() {
		return clientConnected;
	}

	private void setClientConnected(boolean clientConnected) {
		this.clientConnected = clientConnected;

		sendClientConnectionUpdate();
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	private BufferedReader getClientReader() {
		return clientReader;
	}

	private void setClientReader(BufferedReader clientReader) {
		this.clientReader = clientReader;
	}

	private DataOutputStream getClientWriter() {
		return clientWriter;
	}

	private void setClientWriter(DataOutputStream clientWriter) {
		this.clientWriter = clientWriter;
	}

	private int getPort() {
		return port;
	}

	private void setPort(int port) {
		this.port = port;
	}

	// Task methods
	@Override
	public void run() {
		try {
			// Try to start a new server socket in order to receive data
			setServerSocket(new ServerSocket(getPort()));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		while (true) {
			try {
				// Wait for a client to connect
				Socket connection = getServerSocket().accept();
				setClientConnected(true);

				setClientReader(new BufferedReader(new InputStreamReader(connection.getInputStream())));
				setClientWriter(new DataOutputStream(connection.getOutputStream()));

				while (isClientConnected()) {
					// Wait for the client to send data
					String received = getClientReader().readLine();

					if (received == null) {
						// Disconnect when received a null line
						setClientConnected(false);
					} else if (received.substring(0, 2).equals("XY")) {
						// Decode X-Y string and send it to all listeners
						String[] coords = received.split(";");
						double x = Double.parseDouble(coords[1]);
						double y = Double.parseDouble(coords[2]);
						sendIncomingNote(x, y);
					} else if (received.equals("VSYNC")) {
						// Send vertical sync to all listeners
						sendIncomingVsync();
					}
				}
			} catch (IOException ioe) {
				// Something went wrong, disconnect and wait for a new client
				setClientConnected(false);
			}
		}
	}

	public void sendOutgoingButtonPress(int button) {
		// If a client is connected, send a certain button press to the client
		if (isClientConnected()) {
			try {
				getClientWriter().writeBytes(Integer.toString(button) + "\n");
			} catch (IOException e) {
				setClientConnected(false);
			}
		}
	}

	// Listener pattern methods
	public void addListener(CommunicatorListener cl) {
		getListeners().add(cl);
	}

	public void removeListener(CommunicatorListener cl) {
		getListeners().remove(cl);
	}

	private ArrayList<CommunicatorListener> getListeners() {
		return listeners;
	}

	private void sendIncomingNote(double x, double y) {
		for (CommunicatorListener cl : getListeners()) {
			cl.processIncomingNote(x, y);
		}
	}

	private void sendIncomingVsync() {
		for (CommunicatorListener cl : getListeners()) {
			cl.processIncomingVsync();
		}
	}

	private void sendClientConnectionUpdate() {
		for (CommunicatorListener cl : getListeners()) {
			cl.processClientConnection(isClientConnected());
		}
	}

	// Finalize method
	@Override
	public void finalize() {
		try {
			if (getServerSocket() != null) {
				getServerSocket().close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
