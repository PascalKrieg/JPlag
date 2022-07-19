package de.jplag.cpp;

import de.jplag.DualComparisonStrategy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implements a DualComparisonStrategy that compiles single file submissions to assembly using high optimization
 * and then compares the resulting assembly code.
 * A pair of submissions is marked as suspicious if the corresponding assembly code is identical or almost identical.
 */
public class CLangDualComparison implements DualComparisonStrategy {
    private static final String CLANG_LOCATION = "clang";
    private static final String CLANG_COMMAND = "-w -c -Werror=unused-local-typedef -O3 -S";
    private static final String OBJECT_SUFFIX = ".o";

    private static final boolean COMPILE_IN_PARALLEL = true;
    public static final double IDENTICAL_LINES_THRESHOLD = 0.7;

    private final List<String> suspiciousTuples = new ArrayList<>();
    private final List<String> errorSubmissions = new ArrayList<>();

    @Override
    public boolean runComparison(List<String> submissionPaths) {
        try {
            Path temporaryDirectory = createTemporaryDirectory();
            compileSubmissionsToAssembly(temporaryDirectory, submissionPaths);

            var assemblyFiles = new File(temporaryDirectory.toString()).listFiles();
            if (assemblyFiles == null) {
                // temporary directory does not exist or IO Error
                return false;
            }

            return compareAllAssemblyFiles(assemblyFiles);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Iterate over all Asse
     * @param assemblyFiles
     * @return
     */
    private boolean compareAllAssemblyFiles(File[] assemblyFiles) {
        for (int i = 0; i < assemblyFiles.length - 1; i++) {
            for (int j = i + 1; j < assemblyFiles.length; j++) {
                var suspicious = false;

                try {
                    var firstContent = Files.readString(assemblyFiles[i].toPath());
                    var secondContent = Files.readString(assemblyFiles[j].toPath());
                    suspicious = areStringsSimilar(firstContent, secondContent);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

                if (suspicious) {
                    var firstSubmissionName = objectNameToSubmissionName(assemblyFiles[i].getName());
                    var secondSubmissionName = objectNameToSubmissionName(assemblyFiles[j].getName());

                    var identifier = buildSubmissionIdentifier(firstSubmissionName, secondSubmissionName);
                    suspiciousTuples.add(identifier);
                }
            }
        }
        return true;
    }

    @Override
    public boolean isPairSuspicious(String firstSubmission, String secondSubmission) {
        if (errorSubmissions.contains(firstSubmission) || errorSubmissions.contains(secondSubmission)) {
            return true;
        }

        if (firstSubmission.compareTo(secondSubmission) < 0) {
            return suspiciousTuples.contains(firstSubmission + "-" + secondSubmission);
        } else if (firstSubmission.compareTo(secondSubmission) > 0) {
            return suspiciousTuples.contains(secondSubmission + "-" + firstSubmission);
        } else {
            throw new IllegalArgumentException("Submissions can't be identical");
        }
    }

    private Path createTemporaryDirectory() throws IOException {
        Path temporaryDirectory = Files.createTempDirectory("jplag-tmp-");
        temporaryDirectory.toFile().deleteOnExit();
        return temporaryDirectory;
    }

    private void compileSubmissionsToAssembly(Path temporaryDirectory, List<String> submissionPaths) {
        var rootDirectory = new File(submissionPaths.get(0));
        if (!rootDirectory.isDirectory()) {
            return;
        }
        var submissionFiles = rootDirectory.listFiles();

        if (submissionFiles == null) {
            throw new IllegalArgumentException();
        }

        if (COMPILE_IN_PARALLEL) {
            Arrays.stream(submissionFiles).parallel().forEach(file -> {
                compileFile(file, temporaryDirectory);
            });
        } else {
            Arrays.stream(submissionFiles).forEach(file -> {
                compileFile(file, temporaryDirectory);
            });
        }
    }

    private void compileFile(File file, Path temporaryDirectory) {
        var command = "%s %s %s -o %s%s".formatted(CLANG_LOCATION, CLANG_COMMAND, file,
                Path.of(temporaryDirectory.toString(), file.getName()).toString(), OBJECT_SUFFIX);
        try {
            var compilingProcess = Runtime.getRuntime().exec(command);
            var status = compilingProcess.waitFor();

            if (status != 0) {
                errorSubmissions.add(file.getName());
            }
        } catch (IOException | InterruptedException e) {
            errorSubmissions.add(file.getName());
            e.printStackTrace();
        }
    }

    private String objectNameToSubmissionName(String objectName) {
        return objectName.substring(0, objectName.length() - OBJECT_SUFFIX.length());
    }

    private String buildSubmissionIdentifier(String first, String second) {
        if (first.compareTo(second) < 0) {
            return first + "-" + second;
        } else if (first.compareTo(second) > 0) {
            return second + "-" + first;
        } else {
            throw new IllegalArgumentException("Submissions can't be identical");
        }
    }

    /**
     * Compares two strings line by line and determines, if the ratio of identical lines is above a constant threshold.
     * @param first The content of the first submission
     * @param second The content of the second submission
     * @return True, if the ratio of identical lines is above {@link CLangDualComparison#IDENTICAL_LINES_THRESHOLD}
     */
    private boolean areStringsSimilar(String first, String second) {
        var firstLines = first.split("\n");
        var secondLines = second.split("\n");
        int identicalLines = 0;
        for (int i = 0; i < firstLines.length && i < secondLines.length; i++) {
            if (firstLines[i].equals(secondLines[i])) {
                identicalLines++;
            }
        }

        return (float) identicalLines / Math.max(firstLines.length, secondLines.length) > IDENTICAL_LINES_THRESHOLD;
    }
}
