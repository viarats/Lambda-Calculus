package lambda.substitution;

import static org.testng.Assert.assertEquals;

import lambda.parser.LispParser;
import lambda.parser.Parser;
import lambda.term.Variable;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SubstitutionTest {
  private final Substitution substitution = new Substitution();
  private final Parser parser = new LispParser();

  @DataProvider(name = "data")
  private Object[][] provideData() {
    return new Object[][] {
      {"(lambda y x)", "x", "y", "(lambda x0 y)"},
      {"(lambda y (lambda z x))", "x", "(y z)", "(lambda x1 (lambda x0 (y z)))"}
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
    return parser.stringify(substituted);
  }
}
