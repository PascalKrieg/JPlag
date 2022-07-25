package de.jplag.experimental;

import de.jplag.TestBase;
import de.jplag.Token;
import de.jplag.TokenList;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


public class GenericTokenFilterTest extends TestBase {

    private final ExperimentalOptions options = new ExperimentalOptions(14, 3, 1, 2);

    @Test
    void testSingleInsertionFirstWindow() {
        var first = buildTokenList(new int[]{
                1,4,8,2,6,
                5,
                2,1,9,3,5,1,9,6,3,4,4,2,1,6,1,6,4,2,3,1,1,7,9,8,8,5,7,5,4,4,2,1,1,5,3,3,7,6,5,7,7,7});
        var second = buildTokenList(new int[]{
                1,4,8,2,6,
                2,1,9,3,5,1,9,6,3,4,4,2,1,6,1,6,4,2,3,1,1,7,9,8,8,5,7,5,4,4,2,1,1,5,3,3,7,6,5,7,7,7
        });

        assertEqualFilterResult(new GenericTokenFilter(first, second, options));
    }

    @Test
    void testSingleInsertionMiddle() {
        var first = buildTokenList(new int[]{
                1,4,8,2,6,2,1,9,3,5,1,9,6,3,4,4,2,1,
                7,
                6,1,6,4,2,3,1,1,7,9,8,8,5,7,5,4,4,2,1,1,5,3,3,7,6,5,7,7,7});
        var second = buildTokenList(new int[]{
                1,4,8,2,6,2,1,9,3,5,1,9,6,3,4,4,2,1,
                6,1,6,4,2,3,1,1,7,9,8,8,5,7,5,4,4,2,1,1,5,3,3,7,6,5,7,7,7
        });

        assertEqualFilterResult(new GenericTokenFilter(first, second, options));
    }

    @Test
    void testMultipleInsertions() {
        var first = buildTokenList(new int[]{
                1,4,8,2,6,2,1,9,3,
                5,1,9,6,3,4,4,2,1,6,1,6,4,2,3,1,1,7,9,8,8,5,7,5,4,4,2,1,1,5,
                3,3,7,6,5,7,7,7
        });
        var second = buildTokenList(new int[] {
                1,4,8,2,6,2,1,9,3,
                1,4,
                5,1,9,6,3,4,4,2,1,6,1,6,4,2,3,1,1,7,9,8,8,5,7,5,4,4,2,1,1,5,
                8,
                3,3,7,6,5,7,7,7
        });

        assertEqualFilterResult(new GenericTokenFilter(first, second, options));
    }

    @Test
    void testTooCloseInsertions() {
        var first = buildTokenList(new int[] {
                1,2,2,3,6,7,3,9,9,3,8,5,5,4,6,1,2,3,9,6,2,5,1,4,
                8,1,9,2,
                5,5,1,7,5,2,7,1,7,9,6,2,6,9,2,4,7,8,1,8,5,8,9,3,5,6,2,5,3,7,7,3,7,1,6,3,8,9,2,8}
        );

        var second = buildTokenList(new int[] {
                1,2,2,3,6,7,3,9,9,3,8,5,5,4,6,1,2,3,9,6,2,5,1,4,
                1,6,
                8,1,9,2,
                8,
                5,5,1,7,5,2,7,1,7,9,6,2,6,9,2,4,7,8,1,8,5,8,9,3,5,6,2,5,3,7,7,3,7,1,6,3,8,9,2,8}
        );

        var localOptions = new ExperimentalOptions(14, 3, 1, 1);
        localOptions.setGenericWindowPadding(5);
        var filter = new GenericTokenFilter(first, second, localOptions);
        filter.filter();
        assertNotEquals(filter.getFirst().size(), filter.getSecond().size());
    }


    private TokenList buildTokenList(int[] types) {
        var tokenList = new TokenList();
        for (int type : types) {
            tokenList.addToken(new TestToken(type));
        }
        return tokenList;
    }

    private void assertEqualFilterResult(GenericTokenFilter filter) {
        filter.filter();
        assertIterableEquals(filter.getFirst().allTokens(), filter.getSecond().allTokens());
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
