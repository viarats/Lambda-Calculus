package lambda.parser;

import static org.testng.Assert.assertEquals;

import lambda.term.Abstraction;
import lambda.term.Application;
import lambda.term.Term;
import lambda.term.Variable;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ParserTest {
  private final Parser parser = new LispParser();

  @DataProvider(name = "data")
  private Object[][] provideData() {
    return new Object[][] {
      {"x", new Variable("x")},
      {"(lambda x y)", new Abstraction(new Variable("x"), new Variable("y"))},
      {"(x y)", new Application(new Variable("x"), new Variable("y"))},
      {
        "(x (y z))",
        new Application(new Variable("x"), new Application(new Variable("y"), new Variable("z")))
      },
      {
        "((x y) z)",
        new Application(new Application(new Variable("x"), new Variable("y")), new Variable("z"))
      },
      {
        "(lambda x (lambda y (x y)))",
        new Abstraction(
            new Variable("x"),
            new Abstraction(
                new Variable("y"), new Application(new Variable("x"), new Variable("y"))))
      }
    };
  }

  @Test(dataProvider = "data")
  void testParse(final String expression, final Term expected) {
    final var actual = parser.parse(expression);

    assertEquals(actual, expected);
  }

  @Test(dataProvider = "data")
  void testStringify(final String expected, final Term term) {
    final var actual = parser.stringify(term);

    assertEquals(actual, expected);
  }
}
