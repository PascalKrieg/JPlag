package de.jplag;

/**
 * Contains flags that enable experimental features in the C/C++ frontend.
 */
public class FrontendOptions {
    private boolean useBasicFiltering = false;
    private boolean useSourceAnalysis = false;

    private boolean useDualComparison = false;
    private DualComparisonStrategy dualComparisonStrategy;

    public boolean useBasicFiltering() {
        return useBasicFiltering;
    }

    public void setUseBasicFiltering(boolean useBasicFiltering) {
        this.useBasicFiltering = useBasicFiltering;
    }


    public boolean useSourceAnalysis() {
        return useSourceAnalysis;
    }

    public void setUseSourceAnalysis(boolean useSourceAnalysis) {
        this.useSourceAnalysis = useSourceAnalysis;
    }


    public boolean useDualComparison() {
        return useDualComparison;
    }

    public void setUseDualComparison(boolean useDualComparison) {
        this.useDualComparison = useDualComparison;
    }

    public DualComparisonStrategy getDualComparisonStrategy() {
        return dualComparisonStrategy;
    }

    public void setDualComparisonStrategy(DualComparisonStrategy dualComparisonStrategy) {
        this.dualComparisonStrategy = dualComparisonStrategy;
    }
}
