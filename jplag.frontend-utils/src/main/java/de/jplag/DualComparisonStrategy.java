package de.jplag;

import java.util.List;

/**
 * Defines a strategy for running a second algorithm for finding plagiarism.
 */
public interface DualComparisonStrategy {

    /**
     * Runs the strategy and enables querying the result, if successful
     * @param submissionPaths The paths that contain the submission files
     * @return True, if the strategy ran successful and the results can be queried. False, if there was an error.
     * In this case, the results have to be ignored.
     */
    boolean runComparison(List<String> submissionPaths);

    /**
     * Query, if a pair of submissions is marked as suspicious by the strategy. The strategy has to be run first.
     * @param firstSubmission The name of the first submission.
     * @param secondSubmission The name of the second submission.
     * @return True, if the submission pair is marked as suspicious, false otherwise.
     */
    boolean isPairSuspicious(String firstSubmission, String secondSubmission);

}
