package de.jplag;

/**
 * Contains flags that enable experimental features in the C/C++ frontend.
 */
public class FrontendOptions {
    private boolean basicFilteringEnabled = false;
    private boolean sourceAnalysisEnabled = false;

    private boolean dualComparisonEnabled = false;
    private DualComparisonStrategy dualComparisonStrategy;

    public boolean isBasicFilteringEnabled() {
        return basicFilteringEnabled;
    }

    public void setBasicFilteringEnabled(boolean basicFilteringEnabled) {
        this.basicFilteringEnabled = basicFilteringEnabled;
    }


    public boolean isSourceAnalysisEnabled() {
        return sourceAnalysisEnabled;
    }

    public void setSourceAnalysisEnabled(boolean sourceAnalysisEnabled) {
        this.sourceAnalysisEnabled = sourceAnalysisEnabled;
    }


    public boolean isDualComparisonEnabled() {
        return dualComparisonEnabled;
    }

    public void setDualComparisonEnabled(boolean dualComparisonEnabled) {
        this.dualComparisonEnabled = dualComparisonEnabled;
    }

    public DualComparisonStrategy getDualComparisonStrategy() {
        return dualComparisonStrategy;
    }

    public void setDualComparisonStrategy(DualComparisonStrategy dualComparisonStrategy) {
        this.dualComparisonStrategy = dualComparisonStrategy;
    }
}
