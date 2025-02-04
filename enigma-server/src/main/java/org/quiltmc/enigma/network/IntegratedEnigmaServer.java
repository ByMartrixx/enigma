package org.quiltmc.enigma.network;

import org.quiltmc.enigma.api.translation.mapping.EntryRemapper;

import javax.swing.SwingUtilities;

public class IntegratedEnigmaServer extends EnigmaServer {
	public IntegratedEnigmaServer(byte[] jarChecksum, char[] password, EntryRemapper mappings, int port) {
		super(jarChecksum, password, mappings, port);
	}

	@Override
	protected void runOnThread(Runnable task) {
		SwingUtilities.invokeLater(task);
	}
}
