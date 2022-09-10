package de.jplag.cpp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SourceAnalysis {

    public static final String COMPILE_COMMAND = "gcc -Wall -fsyntax-only %s";
    private Map<String, List<Integer>> linesToDelete = new HashMap<>();

    /**
     * Tells the caller if a token is located in a line containing an unused variable.
     * This usually indicates that the token belongs to a declaration of an unused variable.
     * An edge case is multiple variable declarations in a single line, e.g. 'int a, b;' where a is used an b is unused.
     * @param token The token that will be checked
     * @param file The file the token was scanned in
     * @return True, if the token should not be added to a TokenList, false if it should
     */
    public boolean isTokenIgnored(Token token, String file) {
        if (linesToDelete.containsKey(file)) {
            var ignoredLineNumbers = linesToDelete.get(file);
            return ignoredLineNumbers.contains(token.beginLine);
        }
        return false;
    }

    /**
     *
     * @param directory Root directory of submission files
     * @param files
     */
    public void findUnusedVariableLines(File directory, String[] files) {
        linesToDelete = new HashMap<>();

        var isSingleFile = files.length == 1 && files[0].equals("");

        try {
            Runtime runtime = Runtime.getRuntime();
            Process gcc = runtime.exec(COMPILE_COMMAND.formatted(directory.getAbsolutePath()));
            gcc.waitFor();

            // gcc prints compiler warnings to the error stream, not the standard stream
            BufferedReader stdError = new BufferedReader(new InputStreamReader(gcc.getErrorStream()));

            String line;
            while ((line = stdError.readLine()) != null) {
                processOutputLine(line, isSingleFile);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processOutputLine(String line, boolean isSingleFile) {
        // example output:
        // sourceFile.c:151:8: warning: unused variable 't' [-Wunused-variable]
        if (!line.contains("unused variable")) {
            return;
        }

        // contains [sourceFile, line, column, (warning|error), description]
        var lineSplit = line.split(":");

        var fileName = new File(lineSplit[0]).getName();
        if (isSingleFile) {
            fileName = "";
        }

        var lineNumber = Integer.parseInt(lineSplit[1]);

        if (linesToDelete.containsKey(fileName)) {
            linesToDelete.get(fileName).add(lineNumber);
        } else {
            linesToDelete.put(fileName, new ArrayList<>(lineNumber));
        }
    }
}
