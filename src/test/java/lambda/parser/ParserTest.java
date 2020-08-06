package lambda.parser;

import static org.testng.Assert.assertEquals;

import lambda.term.Abstraction;
import lambda.term.Application;
import lambda.term.DeBruijnAbstraction;
import lambda.term.DeBruijnVariable;
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
        "(x (y (x z)))",
        new Application(
            new Variable("x"),
            new Application(
                new Variable("y"), new Application(new Variable("x"), new Variable("z"))))
      },
      {
        "((x y) (z x))",
        new Application(
            new Application(new Variable("x"), new Variable("y")),
            new Application(new Variable("z"), new Variable("x")))
      },
      {
        "(lambda x (lambda y ((lambda z x) t)))",
        new Abstraction(
            new Variable("x"),
            new Abstraction(
                new Variable("y"),
                new Application(
                    new Abstraction(new Variable("z"), new Variable("x")), new Variable("t"))))
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
    final var actual = parser.toString(term);
    assertEquals(actual, expected);
  }

  @DataProvider(name = "deBruijnData")
  private Object[][] provideDeBruijnData() {
    return new Object[][] {
      {"0", new DeBruijnVariable(0)},
      {"(lambda 3)", new DeBruijnAbstraction(new DeBruijnVariable(3))},
      {"(0 1)", new Application(new DeBruijnVariable(0), new DeBruijnVariable(1))},
      {
        "(0 (1 (2 3)))",
        new Application(
            new DeBruijnVariable(0),
            new Application(
                new DeBruijnVariable(1),
                new Application(new DeBruijnVariable(2), new DeBruijnVariable(3))))
      },
      {
        "((0 1) (2 3))",
        new Application(
            new Application(new DeBruijnVariable(0), new DeBruijnVariable(1)),
            new Application(new DeBruijnVariable(2), new DeBruijnVariable(3)))
      },
      {
        "(lambda (lambda (lambda (0 1))))",
        new DeBruijnAbstraction(
            new DeBruijnAbstraction(
                new DeBruijnAbstraction(
                    new Application(new DeBruijnVariable(0), new DeBruijnVariable(1)))))
      }
    };
  }

  @Test(dataProvider = "deBruijnData")
  void testDeBruijnParse(final String expression, final Term expected) {
    final var actual = parser.parseDeBruijn(expression);
    assertEquals(actual, expected);
  }

  @Test(dataProvider = "deBruijnData")
  void testDeBruijnStringify(final String expected, final Term term) {
    final var actual = parser.toStringDeBruijn(term);
    assertEquals(actual, expected);
  }
}
