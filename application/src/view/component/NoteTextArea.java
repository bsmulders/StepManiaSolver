/*
This file is part of StepManiaSolver

Copyright (c) 2013, Bobbie Smulders

Contact:  mail@bsmulders.com

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.
 */

package view.component;

import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JTextPane;

import model.Model;
import model.Note;

public class NoteTextArea extends JTextPane {

	private static final long serialVersionUID = 4821094802985108638L;

	private Model model;
	private int itemCount = 0;

	public NoteTextArea(Model model) {
		super();
		setContentType("text/html");
		setEditable(false);
		this.setModel(model);
	}

	// Getters and setters
	private Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	private int getItemCount() {
		return itemCount;
	}

	private void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	// Override methods
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		if (getModel() != null) {
			ArrayList<Note> notes = new ArrayList<Note>(getModel().getNotes());

			if (getItemCount() != notes.size()) {
				setItemCount(notes.size());

				StringBuilder statusText = new StringBuilder(
						"<body style=\"font-family: Lucida Grande; font-size: 10px\">");

				for (Note note : notes) {
					statusText.append(note.toHTML());
				}

				statusText.append("</body>");

				setText(statusText.toString());
			}
		}
	}
}
