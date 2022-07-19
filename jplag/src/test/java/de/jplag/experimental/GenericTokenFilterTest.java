package de.jplag.experimental;

import de.jplag.TestBase;
import de.jplag.Token;
import de.jplag.TokenList;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


public class GenericTokenFilterTest extends TestBase {

    private final ExperimentalOptions options = new ExperimentalOptions(8, 3, 1, 4);

    @Disabled
    @Test
    void testSingleInsertion() {
        var first = new TokenList();
        var second = new TokenList();

        setupSingleInsertionTokenList(first, second);

        var filter = new GenericTokenFilter(first, second, options);
        filter.filter();

        first = filter.getFirst();
        second = filter.getSecond();
        assertIterableEquals(first.allTokens(), second.allTokens());
    }

    private void setupSingleInsertionTokenList(TokenList first, TokenList second){
        int[] firstTypeSequence = {
                1,4,8,2,6,
                5,
                2,1,9,3,5,1,9,6,3,4,4,2,1,6,1,6,4,2,3,1,1,7,9,8,8,5,7,5,4,4,2,1,1,5,3,3,7,6,5,7,7,7
        };
        int[] secondTypeSequence = {
                1,4,8,2,6,
                2,1,9,3,5,1,9,6,3,4,4,2,1,6,1,6,4,2,3,1,1,7,9,8,8,5,7,5,4,4,2,1,1,5,3,3,7,6,5,7,7,7
        };

        for (int type : firstTypeSequence) {
            first.addToken(new TestToken(type));
        }
        for (int type : secondTypeSequence) {
            second.addToken(new TestToken(type));
        }
    }

    @Disabled
    @Test
    void testMultipleInsertions() {
        var first = new TokenList();
        var second = new TokenList();

        setupMultiInsertionTokenList(first, second);

        var filter = new GenericTokenFilter(first, second, options);
        filter.filter();

        first = filter.getFirst();
        second = filter.getSecond();
        assertIterableEquals(first.allTokens(), second.allTokens());
    }

    private void setupMultiInsertionTokenList(TokenList first, TokenList second){
        int[] firstTypeSequence = {
                1,4,8,2,6,2,1,9,3,

                5,1,9,6,3,4,4,2,1,6,1,6,4,2,3,1,1,7,9,8,8,5,7,5,4,4,2,1,1,5,3,3,7,6,5,7,7,7
        };
        int[] secondTypeSequence = {
                1,4,8,2,6,2,1,9,3,
                1,4,
                5,1,9,6,3,4,4,2,1,6,1,6,4,2,3,1,1,7,9,8,8,5,7,5,4,4,2,1,1,5,
                1,
                3,3,7,6,5,7,7,7
        };

        for (int type : firstTypeSequence) {
            first.addToken(new TestToken(type));
        }
        for (int type : secondTypeSequence) {
            second.addToken(new TestToken(type));
        }
    }

    static class TestToken extends Token {
        public TestToken(int type) {
            super(type, "", 0);
        }

        public TestToken(int type, String file, int line) {
            super(type, file, line);
        }

        @Override
        protected String type2string() {
            return Integer.toString(type);
        }


       @Override
        public boolean equals(Object obj) {
            if (obj instanceof TestToken) {
                return this.type == ((TestToken)obj).getType();
            }
           return super.equals(obj);
        }

        @Override
        public String toString() {
            return type2string();
        }
    }
}
