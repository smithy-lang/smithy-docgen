/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core;

import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import software.amazon.smithy.codegen.core.CodegenException;
import software.amazon.smithy.utils.SmithyUnstableApi;

/**
 * Provides various utility methods.
 */
@SmithyUnstableApi
public final class DocgenUtils {

    private static final Logger LOGGER = Logger.getLogger(DocgenUtils.class.getName());

    private DocgenUtils() {}

    /**
     * Executes a given shell command in a given directory.
     *
     * @param command The string command to execute, e.g. "sphinx-build".
     * @param directory The directory to run the command in.
     * @return Returns the console output of the command.
     */
    public static String runCommand(String command, Path directory) {
        String[] finalizedCommand;
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            finalizedCommand = new String[]{"cmd.exe", "/c", command};
        } else {
            finalizedCommand = new String[]{"sh", "-c", command};
        }

        ProcessBuilder processBuilder = new ProcessBuilder(finalizedCommand)
                .redirectErrorStream(true)
                .directory(directory.toFile());

        try {
            Process process = processBuilder.start();
            List<String> output = new ArrayList<>();

            // Capture output for reporting.
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                process.getInputStream(), Charset.defaultCharset()))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    LOGGER.finest(line);
                    output.add(line);
                }
            }

            process.waitFor();
            process.destroy();

            String joinedOutput = String.join(System.lineSeparator(), output);
            if (process.exitValue() != 0) {
                throw new CodegenException(format(
                    "Command `%s` failed with output:%n%n%s", command, joinedOutput));
            }
            return joinedOutput;
        } catch (InterruptedException | IOException e) {
            throw new CodegenException(e);
        }
    }

    /**
     * Replaces all newline characters in a string with the system line separator.
     * @param input The string to normalize
     * @return A string with system-appropriate newlines.
     */
    public static String normalizeNewlines(String input) {
        return input.replaceAll("\r?\n", System.lineSeparator());
    }
}