package lambda.debruijn;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TranslatorTest {

  private final Translator translator = new TranslatorImpl();

  @DataProvider(name = "removeNamesData")
  private Object[][] provideRemoveNamesData() {
    return new Object[][] {
      {"x", "0"},
      {"(x y)", "(0 1)"},
      {"((x y) z)", "((0 1) 2)"},
      {"(x (y z))", "(0 (1 2))"},
      {"(lambda x (lambda y x))", "(lambda (lambda 1))"},
      {"(lambda x (lambda y z))", "(lambda (lambda 2))"},
      {
        "(lambda x (lambda y (lambda z ((z z) (y x)))))", "(lambda (lambda (lambda ((0 0) (1 2)))))"
      },
      {
        "(lambda x ((lambda y ((lambda z y) y)) (lambda x x)))",
        "(lambda ((lambda ((lambda 1) 0)) (lambda 0)))"
      }
    };
  }

  @Test(dataProvider = "removeNamesData")
  void testRemoveNames(final String term, final String expected) {
    final var actual = translator.addIndexes(term);
    assertEquals(actual, expected);
  }

  @DataProvider(name = "addNamesData")
  private Object[][] provideAddNamesData() {
    return new Object[][] {
      {"0", "y0"},
      {"(0 1)", "(y0 y1)"},
      {"((0 1) 2)", "((y0 y1) y2)"},
      {"(0 (1 2))", "(y0 (y1 y2))"},
      {"(lambda (lambda 1))", "(lambda x0 (lambda x1 x0))"},
      {"(lambda (lambda 2))", "(lambda x0 (lambda x1 y0))"},
      {
        "(lambda (lambda (lambda ((0 0) (1 2)))))",
        "(lambda x0 (lambda x1 (lambda x2 ((x2 x2) (x1 x0)))))"
      },
      {
        "(lambda ((lambda ((lambda 1) 0)) (lambda 0)))",
        "(lambda x0 ((lambda x1 ((lambda x2 x1) x1)) (lambda x1 x1)))"
      }
    };
  }

  @Test(dataProvider = "addNamesData")
  void testAddNames(final String term, final String expected) {
    final var actual = translator.addNames(term);
    assertEquals(actual, expected);
  }
}
