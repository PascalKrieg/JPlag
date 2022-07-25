package de.jplag.experimental;

import de.jplag.TestBase;
import de.jplag.exceptions.ExitException;
import de.jplag.options.LanguageOption;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MossadExampleTest extends TestBase {

    @Test
    void testMossadExample() throws ExitException {
        var result = runJPlag("experimental/mossad_example", jPlagOptions -> {
            jPlagOptions.setLanguageOption(LanguageOption.C_CPP);
            jPlagOptions.setExperimentalOptions(new ExperimentalOptions(12, 3, 1, 1));
            jPlagOptions.setUseGenericTokenFiltering(true);
        });

        var baseline = runJPlag("experimental/mossad_example", jPlagOptions -> {
            jPlagOptions.setLanguageOption(LanguageOption.C_CPP);
        });

        // assert 30% similarity increase
        assertTrue(result.getComparisons().get(0).similarity() > baseline.getComparisons().get(0).similarity() * 1.3);
    }

}
