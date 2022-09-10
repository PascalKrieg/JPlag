package de.jplag.cpp;

import java.io.File;
import java.util.LinkedList;

import de.jplag.AbstractParser;
import de.jplag.ErrorConsumer;
import de.jplag.FrontendOptions;
import de.jplag.TokenList;

public class Scanner extends AbstractParser {
    private String currentFile;

    private LinkedList<CPPToken> tokenList;
    private final SourceAnalysis sourceAnalysis;

    private final FrontendOptions options;

    /**
     * Creates the parser.
     * @param errorConsumer is the consumer for any occurring errors.
     */
    public Scanner(ErrorConsumer errorConsumer) {
        this(errorConsumer, new FrontendOptions());
    }

    /**
     * Creates the parser.
     * @param errorConsumer is the consumer for any occurring errors.
     * @param options optional {@link FrontendOptions} object containing options for additional steps that might improve plagiarism detection
     */
    public Scanner(ErrorConsumer errorConsumer, FrontendOptions options) {
        super(errorConsumer);
        sourceAnalysis = new SourceAnalysis();
        this.options = options;
    }

    public TokenList scan(File directory, String[] files) {
        tokenList = new LinkedList<>();

        if (options.isSourceAnalysisEnabled()) {
            sourceAnalysis.findUnusedVariableLines(directory, files);
        }

        errors = 0;
        for (String currentFile : files) {
            this.currentFile = currentFile;
            getErrorConsumer().print(null, "Scanning file " + currentFile);
            if (!CPPScanner.scanFile(directory, currentFile, this)) {
                errors++;
            }
            tokenList.add(new CPPToken(CPPTokenConstants.FILE_END, currentFile));
        }

        if (options.isBasicFilteringEnabled()) {
            BasicTokenFilter.applyTo(tokenList);
        }

        var tokens = new TokenList();
        for (CPPToken cppToken : tokenList) {
            tokens.addToken(cppToken);
        }

        return tokens;
    }

    public void add(int type, Token token) {
        int length = token.endColumn - token.beginColumn + 1;

        if (options.isSourceAnalysisEnabled() && sourceAnalysis.isTokenIgnored(token, currentFile)) {
            return;
        }

        tokenList.add(new CPPToken(type, currentFile, token.beginLine, token.beginColumn, length));
    }
}
