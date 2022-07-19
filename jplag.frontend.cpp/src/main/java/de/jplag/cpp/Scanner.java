package de.jplag.cpp;

import java.io.File;

import de.jplag.AbstractParser;
import de.jplag.ErrorConsumer;
import de.jplag.FrontendOptions;
import de.jplag.TokenList;

public class Scanner extends AbstractParser {
    private String currentFile;

    private final FrontendOptions options;

    /**
     * Creates the parser.
     * @param errorConsumer is the consumer for any occurring errors.
     */
    public Scanner(ErrorConsumer errorConsumer) {
        super(errorConsumer);
        this.options = new FrontendOptions();
    }

    /**
     * Creates the parser.
     * @param errorConsumer is the consumer for any occurring errors.
     * @param options optional {@link FrontendOptions} object containing options for additional steps that might improve plagiarism detection
     */
    public Scanner(ErrorConsumer errorConsumer, FrontendOptions options) {
        super(errorConsumer);
        this.options = options;
    }

    public TokenList scan(File directory, String[] files) {
        tokens = new TokenList();
        errors = 0;
        for (String currentFile : files) {
            this.currentFile = currentFile;
            getErrorConsumer().print(null, "Scanning file " + currentFile);
            if (!CPPScanner.scanFile(directory, currentFile, this)) {
                errors++;
            }
            tokens.addToken(new CPPToken(CPPTokenConstants.FILE_END, currentFile));
        }
        return tokens;
    }

    public void add(int type, Token token) {
        int length = token.endColumn - token.beginColumn + 1;
        tokens.addToken(new CPPToken(type, currentFile, token.beginLine, token.beginColumn, length));
    }
}
