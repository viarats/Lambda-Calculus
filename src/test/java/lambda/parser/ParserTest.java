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

  @DataProvider(name = "deBruijnData")
  private Object[][] provideDeBruijnData() {
    return new Object[][] {
      {"0", new lambda.deBruijn.term.Variable(0)},
      {"(lambda 1)", new lambda.deBruijn.term.Abstraction(new lambda.deBruijn.term.Variable(1))},
      {
        "(0 1)",
        new lambda.deBruijn.term.Application(
            new lambda.deBruijn.term.Variable(0), new lambda.deBruijn.term.Variable(1))
      },
      {
        "(0 (1 2))",
        new lambda.deBruijn.term.Application(
            new lambda.deBruijn.term.Variable(0),
            new lambda.deBruijn.term.Application(
                new lambda.deBruijn.term.Variable(1), new lambda.deBruijn.term.Variable(2)))
      },
      {
        "((0 1) 2)",
        new lambda.deBruijn.term.Application(
            new lambda.deBruijn.term.Application(
                new lambda.deBruijn.term.Variable(0), new lambda.deBruijn.term.Variable(1)),
            new lambda.deBruijn.term.Variable(2))
      },
      {
        "(lambda (lambda (1 0)))",
        new lambda.deBruijn.term.Abstraction(
            new lambda.deBruijn.term.Abstraction(
                new lambda.deBruijn.term.Application(
                    new lambda.deBruijn.term.Variable(1), new lambda.deBruijn.term.Variable(0))))
      }
    };
  }

  @Test(dataProvider = "deBruijnData")
  void testDeBruijnParse(final String expression, final lambda.deBruijn.term.Term expected) {
    final var actual = parser.parseDeBruijn(expression);
    assertEquals(actual, expected);
  }

  @Test(dataProvider = "deBruijnData")
  void testDeBruijnStringify(final String expected, final lambda.deBruijn.term.Term term) {
    final var actual = parser.stringify(term);
    assertEquals(actual, expected);
  }
}
