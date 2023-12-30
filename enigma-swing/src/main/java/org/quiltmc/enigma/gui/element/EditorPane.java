package org.quiltmc.enigma.gui.element;

import javax.swing.JEditorPane;
import javax.swing.JToolTip;

public class EditorPane extends JEditorPane {
	@Override
	public JToolTip createToolTip() {
		var toolTip = new EntryToolTip();
		toolTip.setComponent(this);
		return toolTip;
	}
}
