package org.quiltmc.enigma.command;

import org.quiltmc.enigma.api.ProgressListener;
import org.quiltmc.enigma.util.MappingOperations;
import org.quiltmc.enigma.api.translation.mapping.serde.MappingFormat;
import org.quiltmc.enigma.api.translation.mapping.serde.MappingParseException;
import org.quiltmc.enigma.api.translation.mapping.EntryMapping;
import org.quiltmc.enigma.api.translation.mapping.serde.MappingFileNameFormat;
import org.quiltmc.enigma.api.translation.mapping.serde.MappingSaveParameters;
import org.quiltmc.enigma.api.translation.mapping.serde.MappingsWriter;
import org.quiltmc.enigma.api.translation.mapping.tree.EntryTree;
import org.quiltmc.enigma.util.Utils;

import java.io.IOException;
import java.nio.file.Path;

public class ComposeMappingsCommand extends Command {
	public ComposeMappingsCommand() {
		super(Argument.LEFT_MAPPINGS.required(),
				Argument.RIGHT_MAPPINGS.required(),
				Argument.OUTPUT_MAPPING_FORMAT.required(),
				Argument.MAPPING_OUTPUT.required(),
				Argument.KEEP_MODE.required());
	}

	@Override
	public void run(String... args) throws IOException, MappingParseException {
		Path left = getReadablePath(this.getArg(args, 0));
		Path right = getReadablePath(this.getArg(args, 1));
		String resultFormat = this.getArg(args, 2);
		Path result = getWritablePath(this.getArg(args, 3));
		String keepMode = this.getArg(args, 4);

		run(left, right, resultFormat, result, keepMode);
	}

	@Override
	public String getName() {
		return "compose-mappings";
	}

	@Override
	public String getDescription() {
		return "Merges the two mapping trees (left and right) into a common (middle) name set, handling conflicts according to the given \"keep mode\".";
	}

	public static void run(Path leftFile, Path rightFile, String resultFormat, Path resultFile, String keepMode) throws IOException, MappingParseException {
		MappingSaveParameters saveParameters = new MappingSaveParameters(MappingFileNameFormat.BY_DEOBF, false);

		MappingFormat leftFormat = MappingFormat.parseFromFile(leftFile);
		EntryTree<EntryMapping> left = leftFormat.read(leftFile);
		MappingFormat rightFormat = MappingFormat.parseFromFile(rightFile);
		EntryTree<EntryMapping> right = rightFormat.read(rightFile);
		EntryTree<EntryMapping> result = MappingOperations.compose(left, right, keepMode.equals("left") || keepMode.equals("both"), keepMode.equals("right") || keepMode.equals("both"));

		Utils.delete(resultFile);
		MappingsWriter writer = MappingCommandsUtil.getWriter(resultFormat);
		writer.write(result, resultFile, ProgressListener.none(), saveParameters);
	}
}
