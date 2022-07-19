package de.jplag.experimental;

import de.jplag.Token;
import de.jplag.TokenList;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides an algorithm that compares two TokenList and deletes token sequences that prevent a token match.
 * The goal is to prevent code insertion obfuscation attacks.
 */
public class GenericTokenFilter {
    private TokenList first;
    private TokenList second;

    private final ExperimentalOptions options;

    /**
     * Constructs an object holding the tokenlists, options and result after running.
     * @param first The first TokenList. Will not be modified.
     * @param second The second TokenList. Will not be modified.
     * @param options The parameters for running the algorithm.
     */
    public GenericTokenFilter(TokenList first, TokenList second, ExperimentalOptions options) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("TokenLists cannot be null");
        }
        this.first = first;
        this.second = second;
        this.options = options;
    }

    public TokenList getFirst() {
        return first;
    }

    public TokenList getSecond() {
        return second;
    }

    /**
     * Perform the algorithm on copies of the TokenLists given in the constructor.
     * The modified copies can then be retrieved using the getFirst() and getSecond() methods.
     */
    public void filter() {
        var firstTokenList = new ArrayList<Token>();
        first.allTokens().forEach(firstTokenList::add);

        var secondTokenList = new ArrayList<Token>();
        second.allTokens().forEach(secondTokenList::add);


        for (int iteration = 0; iteration < options.getGenericMaxIterations(); iteration++) {
            for (int i = 0; i < firstTokenList.size() - options.getGenericWindowLength(); i += options.getGenericWindowIncrement()) {
                for (int j = 0; j < secondTokenList.size() - options.getGenericWindowLength(); j += options.getGenericWindowIncrement()) {
                    filterWindow(secondTokenList, j, firstTokenList, i);
                }
            }

            for (int i = 0; i < secondTokenList.size() - options.getGenericWindowLength(); i += options.getGenericWindowIncrement()) {
                for (int j = 0; j < firstTokenList.size() - options.getGenericWindowLength(); j += options.getGenericWindowIncrement()) {
                    filterWindow(secondTokenList, i, firstTokenList, j);
                }
            }
        }

        first = new TokenList();
        firstTokenList.forEach(first::addToken);

        second = new TokenList();
        secondTokenList.forEach(second::addToken);
    }

    /**
     *
     * @param firstList
     * @param firstWindowStart
     * @param secondList
     * @param secondWindowStart
     * @return Returns the increment for the next windows start.
     */
    private void filterWindow(List<Token> firstList, int firstWindowStart, List<Token> secondList, int secondWindowStart) {
        int deletionStart = 0;
        int deletionLength = 0;
        boolean deletionWasAlreadyFound = false;

        // delete tokens in the first list
        for (int i = 1, j = 1;
             j < options.getGenericWindowLength()
                     && isInBounds(firstList, firstWindowStart, i)
                     && isInBounds(secondList, secondWindowStart, j);
             i++, j++) {

            if (firstList.get(firstWindowStart + i).getType() == secondList.get(secondWindowStart + j).getType()) {
                continue;
            }

            // if there already was a mismatching section in this window, start next window at the end of the first mismatch
            if (deletionWasAlreadyFound) {
                return;
            }

            // set current position as deletion start and check for length of mismatch
            deletionStart = firstWindowStart + i;
            for (int deletionLengthCounter = 1; isInBounds(firstList, deletionStart, deletionLengthCounter); deletionLengthCounter++) {
                if (deletionLengthCounter > options.getGenericMaxInsertionLength()) {
                    return;
                }

                i++;

                if (firstList.get(firstWindowStart + i).getType() == secondList.get(secondWindowStart + j).getType()) {
                    deletionWasAlreadyFound = true;
                    deletionLength = deletionLengthCounter;
                    break;
                }
            }
        }

        if (deletionWasAlreadyFound) {
            for (int i = 0; i < deletionLength; i++) {
                // Don't iterate. Always remove at deletion start because the rest of the list moves forward on every deletion
                firstList.remove(deletionStart);
            }
        }
    }

    private boolean isInBounds(List<?> list, int windowStart, int offset) {
        return list.size() > windowStart + offset;
    }
}
