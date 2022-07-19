package de.jplag;

/**
 * Contains flags that enable experimental features in the C/C++ frontend.
 */
public class FrontendOptions {
    private boolean useBasicFiltering = false;

    public boolean useBasicFiltering() {
        return useBasicFiltering;
    }

    public void setUseBasicFiltering(boolean useBasicFiltering) {
        this.useBasicFiltering = useBasicFiltering;
    }
}
