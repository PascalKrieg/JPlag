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
            runWindows(firstTokenList, secondTokenList);
            runWindows(secondTokenList, firstTokenList);
        }

        first = new TokenList();
        firstTokenList.forEach(first::addToken);

        second = new TokenList();
        secondTokenList.forEach(second::addToken);
    }

    private void runWindows(ArrayList<Token> firstTokenList, ArrayList<Token> secondTokenList) {
        for (int i = 0;
             i < firstTokenList.size() - options.getGenericWindowLength() - options.getGenericMaxInsertionLength();
             i += options.getGenericWindowIncrement()) {
            for (int j = 0; j < secondTokenList.size() - options.getGenericWindowLength(); j++) {
                filterWindow(firstTokenList, i, secondTokenList, j);
            }
        }
    }


    private void filterWindow(List<Token> firstList, int firstWindowStart, List<Token> secondList, int secondWindowStart) {
        int deletionStart = 0;
        int deletionLength = 0;
        boolean deletionWasAlreadyFound = false;

        if (firstList.get(firstWindowStart).getType() != secondList.get(secondWindowStart).getType()) {
            return;
        }

        // delete tokens in the first list
        for (int i = 0, j = 0;
             j < options.getGenericWindowLength() + deletionLength
                     && i < options.getGenericWindowLength()
                     && isInBounds(firstList, firstWindowStart, i)
                     && isInBounds(secondList, secondWindowStart, j);
             i++, j++) {

            if (firstList.get(firstWindowStart + i).getType() == secondList.get(secondWindowStart + j).getType()) {
                continue;
            }

            // don't delete tokens at the start or end of a window
            // if two windows are slightly offset for the same type sequence, the start would always be deleted, e.g.
            // A B C D E
            //   B C D E or
            // A b a C D E
            //     A C D E
            // where lower case letters represent deleted tokens
            if (i < options.getGenericWindowPadding() || i > options.getGenericWindowLength() - options.getGenericWindowPadding()) {
                return;
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

                if (i > options.getGenericWindowLength() - options.getGenericWindowPadding()) {
                    return;
                }

                if (firstList.get(firstWindowStart + i).getType() == secondList.get(secondWindowStart + j).getType()) {
                    deletionWasAlreadyFound = true;
                    deletionLength = deletionLengthCounter;
                    break;
                }
            }
        }

        if (deletionWasAlreadyFound) {
            // enable when debugging failing edge cases to see the windows and which tokens are deleted
            //printDeletionInfo(firstList, firstWindowStart, secondList, secondWindowStart, deletionStart, deletionLength);
            for (int i = 0; i < deletionLength; i++) {
                // Don't iterate. Always remove at deletion start because the rest of the list moves forward on every deletion
                firstList.remove(deletionStart);
            }
        }
    }

    private boolean isInBounds(List<?> list, int windowStart, int offset) {
        return list.size() > windowStart + offset;
    }

    // Debugging Utility
    private void printDeletionInfo(List<Token> firstList, int firstWindowStart, List<Token> secondList, int secondWindowStart, int deletionStart, int deletionLength) {
        System.out.println("Deletion info:");
        System.out.println("First window start: " + firstWindowStart + ", first list size: " + firstList.size() + ", deletion start: " + deletionStart + ", deletion length: " + deletionLength);
        System.out.println("second window start: " + secondWindowStart + ", second list size: " + secondList.size());

        var deletionOffset = deletionStart - firstWindowStart;

        var sb = new StringBuilder();
        for (int i = firstWindowStart; i < firstWindowStart + options.getGenericWindowLength() + deletionLength && i < firstList.size(); i++) {
            sb.append(firstList.get(i)).append(" ");
        }
        System.out.println(sb);

        sb = new StringBuilder();
        for (int i = secondWindowStart; i < secondWindowStart + options.getGenericWindowLength() && i < secondList.size(); i++) {
            if (i == secondWindowStart + deletionOffset) {
                sb.append("_ ".repeat(deletionLength));
            }
            sb.append(secondList.get(i)).append(" ");
        }
        System.out.println(sb);
        System.out.println("");
    }
}
