package lambda.substitution;

import static org.testng.Assert.assertEquals;

import lambda.parser.LispParser;
import lambda.parser.Parser;
import lambda.term.Variable;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SubstitutionTest {
  private final Substitution substitution = new SubstitutionImpl();
  private final Parser parser = new LispParser();

  @DataProvider(name = "data")
  private Object[][] provideData() {
    return new Object[][] {
      {"(lambda x y)", "y", "x", "(lambda x0 x)"},
      {"(lambda x (lambda y z))", "z", "(y x)", "(lambda x0 (lambda x1 (y x)))"},
      {
        "(lambda x ((lambda y z) (x z)))", "z", "(x t)", "(lambda x0 ((lambda y (x t)) (x0 (x t))))"
      },
      {"((lambda x z) (lambda y z))", "z", "(x y)", "((lambda x0 (x y)) (lambda x1 (x y)))"}
    };
  }

  @Test(dataProvider = "data")
  void testSubstitution(
      final String term, final String variable, final String substitution, final String expected) {
    final var actual = parseSubstitution(term, variable, substitution);

    assertEquals(actual, expected);
  }

  private String parseSubstitution(
      final String term, final String variable, final String substitution) {
    final var substituted =
        this.substitution.safeSubstitute(
            parser.parse(term), (Variable) parser.parse(variable), parser.parse(substitution));
    return parser.toString(substituted);
  }
}
