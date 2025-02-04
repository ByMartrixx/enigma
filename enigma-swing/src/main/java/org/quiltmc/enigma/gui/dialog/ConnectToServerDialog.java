package org.quiltmc.enigma.gui.dialog;

import org.quiltmc.enigma.gui.Gui;
import org.quiltmc.enigma.gui.config.Config;
import org.quiltmc.enigma.gui.util.ScaleUtil;
import org.quiltmc.enigma.network.EnigmaServer;
import org.quiltmc.enigma.network.ServerAddress;
import org.quiltmc.enigma.util.Pair;
import org.quiltmc.enigma.util.validation.Message;
import org.quiltmc.enigma.util.validation.StandardValidation;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ConnectToServerDialog extends AbstractDialog {
	private JTextField usernameField;
	private JTextField ipField;
	private JPasswordField passwordField;

	public ConnectToServerDialog(Frame owner, Gui gui) {
		super(owner, gui, "prompt.connect.title", "prompt.connect.confirm", "prompt.cancel");

		Dimension preferredSize = this.getPreferredSize();
		preferredSize.width = ScaleUtil.scale(400);
		this.setPreferredSize(preferredSize);
		this.pack();
		this.setLocationRelativeTo(owner);
	}

	@Override
	protected List<Pair<String, Component>> createComponents() {
		this.usernameField = new JTextField(Config.net().username.value());
		this.ipField = new JTextField(Config.net().remoteAddress.value());
		this.passwordField = new JPasswordField(Config.net().password.value());

		this.usernameField.addActionListener(event -> this.confirm());
		this.ipField.addActionListener(event -> this.confirm());
		this.passwordField.addActionListener(event -> this.confirm());

		return Arrays.asList(
				new Pair<>("prompt.connect.username", this.usernameField),
				new Pair<>("prompt.connect.address", this.ipField),
				new Pair<>("prompt.password", this.passwordField)
		);
	}

	@Override
	public void validateInputs() {
		if (StandardValidation.notBlank(this.vc, this.ipField.getText()) && ServerAddress.from(this.ipField.getText(), EnigmaServer.DEFAULT_PORT) == null) {
			this.vc.raise(Message.INVALID_IP);
		}
	}

	public Result getResult() {
		if (!this.isActionConfirm()) return null;
		this.vc.reset();
		this.validateInputs();
		if (!this.vc.canProceed()) return null;
		return new Result(
				this.usernameField.getText(),
				this.ipField.getText(),
				Objects.requireNonNull(ServerAddress.from(this.ipField.getText(), EnigmaServer.DEFAULT_PORT)),
				this.passwordField.getPassword()
		);
	}

	public static Result show(Gui gui) {
		ConnectToServerDialog d = new ConnectToServerDialog(gui.getFrame(), gui);

		d.setVisible(true);
		Result r = d.getResult();

		d.dispose();
		return r;
	}

	public record Result(String username, String addressStr, ServerAddress address, char[] password) {
	}
}
