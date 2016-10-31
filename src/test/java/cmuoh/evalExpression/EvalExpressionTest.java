package cmuoh.evalExpression;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple EvalExpression.
 */
public class EvalExpressionTest {
    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    @Test
    public void parseNumbersSuccess() throws Exception {
        Map<String, Integer> expectedValues = new HashMap<>();
        expectedValues.put("0", 0);
        expectedValues.put("0123", 123);
        expectedValues.put("-123", -123);
        expectedValues.put("+123", 123);
        expectedValues.put("123", 123);
        expectedValues.put("1000000", 1000000);
        expectedValues.put("-1000000", -1000000);

        EvalExpression evalExpression = new EvalExpression();
        for (Map.Entry<String, Integer> expected : expectedValues.entrySet()){
            Integer actualValue = evalExpression.parse(expected.getKey());
            errorCollector.checkThat(expected.getValue(), equalTo(actualValue));
        }
    }

    @Test
    public void parseNumbersFailure() throws Exception {
        List<String> invalidNumbers = new ArrayList<>();
        invalidNumbers.add("-");
        invalidNumbers.add("+");
        invalidNumbers.add("123+");
        invalidNumbers.add("123-");
        invalidNumbers.add("1 23");
        invalidNumbers.add("1.23");
        invalidNumbers.add("*123");
        invalidNumbers.add("1s");
        invalidNumbers.add("7.2s");

        EvalExpression evalExpression = new EvalExpression();
        for (String invalidNumber : invalidNumbers){
            try {
                Integer val = evalExpression.parse(invalidNumber);
                errorCollector.addError(new Exception(
                        String.format("Unexpected value (%d) for invalid input: %s", val, invalidNumber)));
            } catch (Exception ex) {
            }
        }
    }

    @Test
    public void parseNumbersWithWhiteSpaceSuccess() throws Exception {
        Map<String, Integer> expectedValues = new HashMap<>();
        expectedValues.put(" -123", -123);
        expectedValues.put("123\t", 123);
        expectedValues.put("\n+123", 123);

        EvalExpression evalExpression = new EvalExpression();
        for (Map.Entry<String, Integer> expected : expectedValues.entrySet()){
            Integer actualValue = evalExpression.parse(expected.getKey());
            errorCollector.checkThat(expected.getValue(), equalTo(actualValue));
        }
    }

    @Test
    public void parseWhitespace() throws Exception {
        Map<String, Integer> expectedValues = new HashMap<>();
        expectedValues.put("", null);
        expectedValues.put(" ", null);
        expectedValues.put("\t\n", null);
        expectedValues.put("     \t   ", null);

        EvalExpression evalExpression = new EvalExpression();
        for (Map.Entry<String, Integer> expected : expectedValues.entrySet()){
            Integer actualValue = evalExpression.parse(expected.getKey());
            errorCollector.checkThat(expected.getValue(), equalTo(actualValue));
        }
    }

    @Test
    public void parseExpressions() throws Exception {
        Map<String, Integer> expressions = new HashMap<>();
        expressions.put("2+1", 3);
        expressions.put("45 - -5", 50);
        expressions.put("45--5", 50);
        expressions.put("45/-5", -9);
        expressions.put("2*3", 6);
        expressions.put("1 + 3-5", -1);

        EvalExpression evalExpression = new EvalExpression();
        for (Map.Entry<String, Integer> expected : expressions.entrySet()){
            Integer actualValue = evalExpression.parse(expected.getKey());
            errorCollector.checkThat(
                    String.format("Evaluating expression: %s", expected.getKey()),
                    expected.getValue(),
                    equalTo(actualValue));
        }
    }
}
