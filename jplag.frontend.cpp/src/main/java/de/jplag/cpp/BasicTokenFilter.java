package de.jplag.cpp;

import java.util.List;
import java.util.ListIterator;

/**
 * Contains a basic algorithm for detecting tokens contained in unreachable code.
 */
public class BasicTokenFilter {

    /**
     * Applies the filtering on the provided token list.
     * @param tokenList The list that will be filtered. The contents of this parameter will be modified.
     */
    public static void applyTo(List<CPPToken> tokenList) {
        TokenFilterState stateMachine = TokenFilterState.DEFAULT;

        ListIterator<CPPToken> iterator = tokenList.listIterator();
        while (iterator.hasNext()) {
            var tokenType = iterator.next().getType();

            stateMachine = stateMachine.nextState(tokenType);

            if (stateMachine.shouldTokenBeDeleted()) {
                iterator.remove();
            }
        }
    }

    /**
     * Represents the state of a simple state machine for C++ tokens.
     */
    private enum TokenFilterState implements CPPTokenConstants {
        DEFAULT {
            @Override
            TokenFilterState nextState(int nextType) {
                if (isBlockStartToken(nextType))
                    return BLOCK_BEGINNING;
                if (isJumpToken(nextType))
                    return DEAD_BLOCK_BEGINNING;
                if (nextType == C_CASE)
                    return CASE_BLOCK;
                return DEFAULT;
            }
        },
        BLOCK_BEGINNING {
            @Override
            TokenFilterState nextState(int nextType) {
                if (nextType == C_BLOCK_BEGIN) {
                    return DEFAULT;
                }
                return BLOCK_BEGINNING;
            }
        },
        DEAD_BLOCK {
            @Override
            TokenFilterState nextState(int nextType) {
                return switch (nextType) {
                    case C_BLOCK_END, FILE_END -> DEFAULT;
                    case C_CASE -> CASE_BLOCK;
                    default -> DEAD_BLOCK;
                };
            }

            @Override
            public boolean shouldTokenBeDeleted() {
                return true;
            }
        },
        // the current token starts a dead block, so everything afterwards should be deleted, until the dead block is closed.
        DEAD_BLOCK_BEGINNING {
            @Override
            TokenFilterState nextState(int nextType) {
                return switch (nextType) {
                    case C_BLOCK_END -> DEFAULT;
                    case C_CASE -> CASE_BLOCK;
                    default -> DEAD_BLOCK;
                };
            }
        },
        // case blocks don't use braces, but the end of a case block is easy to recognize
        CASE_BLOCK {
            @Override
            TokenFilterState nextState(int nextType) {
                if (isJumpToken(nextType))
                    return DEAD_BLOCK_BEGINNING;
                if (nextType == C_BLOCK_END)
                    return DEFAULT;
                return CASE_BLOCK;
            }
        };

        private static boolean isBlockStartToken(int token) {
            return token == C_WHILE || token == C_IF || token == C_FOR;
        }

        // jump tokens are tokens that force a code execution jump in EVERY case and therefore indicate unreachable code.
        private static boolean isJumpToken(int token) {
            return token == C_RETURN || token == C_BREAK || token == C_CONTINUE || token == C_THROW || token == C_GOTO;
        }

        /**
         * Determine if the current token should be deleted, because it is located in dead or unreachable code.
         * @return true if the token corresponding to the current state is located in dead or unreachable code, false otherwise.
         */
        public boolean shouldTokenBeDeleted() {
            return false;
        }

        /**
         * Determine the next state depending on the current state and the next token type.
         * @param nextType The type of the next token in the token list.
         * @return the new state corresponding to the next token
         */
        abstract TokenFilterState nextState(int nextType);
    }
}
