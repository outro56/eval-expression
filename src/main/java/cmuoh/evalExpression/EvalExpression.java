package cmuoh.evalExpression;

import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class EvalExpression {
    public static void main(final String[] args) {
        EvalExpression evaluator = new EvalExpression();
        Integer result;

        Scanner scanner = new Scanner(System.in);

        System.out.print("> ");
        while (scanner.hasNext()) {
            try {
                result = evaluator.parse(scanner.nextLine());

                if (result != null) {
                    System.out.println(result);
                }
            } catch (Exception ex) {
                System.err.print("***ERROR: ");
                System.err.println(ex.getMessage());
            }

            System.out.print("> ");
        }
    }

    public Integer parse(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        Expression expr = new NumericExpression();
        for (int i = 0; i < input.length(); ++i) {
            expr = expr.accept(input.charAt(i));
        }

        return expr.evaluate();
    }

    interface Expression {
        /**
         * Parses the next input for the binary expression
         * @param ch
         * @return
         */
        Expression accept(Character ch);

        /**
         * Evalutes the result of the expression
         * @return
         */
        Integer evaluate();
    };

    static class BinaryExpression implements Expression {
        static final String OPERATORS = "+/*-";

        private final Expression lhs;
        private Expression rhs;
        private Character operand;

        /**
         * Construct a binary expression
         * @param lhs
         */
        BinaryExpression(Expression lhs) {
            if (lhs == null) {
                throw new NullPointerException("lhs");
            }
            this.lhs = lhs;
        }

        /***
         * @inheritDoc
         */
        public Expression accept(Character ch) {
            if (operand == null) {
                if (OPERATORS.indexOf(ch) == -1) {
                    throw new IllegalArgumentException(String.format("%s is not a valid operand", ch));
                }
                operand = ch;
            } else {
                if (rhs == null) {
                    rhs = new NumericExpression();
                }
                rhs = rhs.accept(ch);
            }

            return this;
        }

        /***
         * @inheritDoc
         */
        public Integer evaluate() {
            Integer lhsVal = lhs.evaluate();
            Integer rhsVal = (rhs == null) ? null : rhs.evaluate();

            if (operand == null) {
                return lhsVal;
            }

            if (rhsVal == null) {
                throw new IllegalStateException(String.format("Missing second operand for '%s'", operand));
            }

            switch (operand) {
                case '/': return lhsVal / rhsVal;
                case '-': return lhsVal - rhsVal;
                case '*': return lhsVal * rhsVal;
                case '+': return lhsVal + rhsVal;
                default: throw new IllegalStateException(String.format("Unhandled operator '%s'", operand));
            }
        }

        @Override
        public String toString() {
            return String.format("%s%s%s",
                    lhs.toString(),
                    operand == null ? "" : operand.toString(),
                    rhs == null ? "" : rhs.toString());
        }
    }

    static class NumericExpression implements Expression {
        static final String NUMBER_PREFIX = "+-";
        private final StringBuilder sb = new StringBuilder();

        /***
         * @inheritDoc
         */
        public Integer evaluate() {
            Integer num = null;
            Boolean negate = null;
            String input = sb.toString().trim();

            for (int pos = 0; pos < input.length(); ++pos) {
                Character ch = input.charAt(pos);

                if (num == null && negate == null) {
                    if (NUMBER_PREFIX.indexOf(ch) != -1) {
                        negate = ch == '-';
                        continue;
                    }
                }

                if (Character.isDigit(ch)) {
                    int i = Character.getNumericValue(ch);
                    num = (num == null) ? i : (num * 10) + i;
                    continue;
                }

                throw new IllegalArgumentException(String.format("%s is not a valid number", input));
            }

            if (negate != null && num == null) {
                throw new IllegalArgumentException(String.format("%s is not a valid number", input));
            }

            if (num != null) {
                num = Boolean.TRUE.equals(negate) ? -num : num;
            }

            return num;
        }

        /***
         * @inheritDoc
         */
        public Expression accept(Character ch) {
            if (sb.length() == 0 && Character.isWhitespace(ch)) {
                return this;
            }

            if (sb.length() == 0 && NUMBER_PREFIX.indexOf(ch) >= 0) {
                sb.append(ch);
                return this;
            }

            if (BinaryExpression.OPERATORS.indexOf(ch) >= 0) {
                return (new BinaryExpression(this)).accept(ch);
            }

            sb.append(ch);
            return this;
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }
}
