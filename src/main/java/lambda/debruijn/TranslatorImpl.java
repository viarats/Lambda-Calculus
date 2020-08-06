package lambda.debruijn;

import static java.util.stream.Collectors.toUnmodifiableList;
import static lambda.utils.Util.getFreeVariables;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import lambda.parser.LispParser;
import lambda.parser.Parser;
import lambda.term.Abstraction;
import lambda.term.Application;
import lambda.term.DeBruijnAbstraction;
import lambda.term.DeBruijnVariable;
import lambda.term.Term;
import lambda.term.Variable;

// Task 2.8
class TranslatorImpl implements Translator {
  private static final Parser PARSER = new LispParser();

  private static final int INITIAL_DEPTH = 0;
  private static final int START = 0;
  private static final int END = 20;

  @Override
  public String addNames(final String term) {
    final var parsed = PARSER.parseDeBruijn(term);
    final var named = addNames(parsed, createAddNamesContext(), INITIAL_DEPTH);

    return PARSER.toString(named);
  }

  @Override
  public String addIndexes(final String term) {
    final var parsed = PARSER.parse(term);
    final var indexed = addIndexes(parsed, createAddIndexesContext(parsed), INITIAL_DEPTH);

    return PARSER.toStringDeBruijn(indexed);
  }

  private List<String> createAddNamesContext() {
    return IntStream.range(START, END)
        .boxed()
        .map(index -> "x" + index)
        .collect(toUnmodifiableList());
  }

  private Term addNames(final Term term, final List<String> context, final int depth) {
    final var type = term.getClass();
    if (type == Variable.class) {
      return term;
    }

    if (type == DeBruijnVariable.class) {
      return new Variable(createVariableName(((DeBruijnVariable) term).getIndex(), depth));
    }

    if (type == DeBruijnAbstraction.class) {
      return new Abstraction(
          new Variable(context.get(0)),
          addNames(
              substituteIndexWithName(0, context.get(0), ((DeBruijnAbstraction) term).getBody()),
              getTail(context),
              depth + 1));
    }

    if (type == Application.class) {
      return new Application(
          addNames(((Application) term).getFunction(), context, depth),
          addNames(((Application) term).getArgument(), context, depth));
    }

    throw new RuntimeException(String.format("Invalid term => %s", term));
  }

  private String createVariableName(final int index, final int depth) {
    return "y" + (index - depth);
  }

  private Term substituteIndexWithName(final int index, final String name, final Term term) {
    final var type = term.getClass();
    if (type == Variable.class) {
      return term;
    }

    if (type == DeBruijnVariable.class) {
      return index == ((DeBruijnVariable) term).getIndex() ? new Variable(name) : term;
    }

    if (type == Abstraction.class) {
      return new DeBruijnAbstraction(
          substituteIndexWithName(index + 1, name, ((Abstraction) term).getBody()));
    }

    if (type == DeBruijnAbstraction.class) {
      return new DeBruijnAbstraction(
          substituteIndexWithName(index + 1, name, ((DeBruijnAbstraction) term).getBody()));
    }

    if (type == Application.class) {
      return new Application(
          substituteIndexWithName(index, name, ((Application) term).getFunction()),
          substituteIndexWithName(index, name, ((Application) term).getArgument()));
    }

    throw new RuntimeException(String.format("Invalid term => %s", term));
  }

  private List<String> getTail(final List<String> list) {
    return IntStream.range(1, list.size()).boxed().map(list::get).collect(toUnmodifiableList());
  }

  private Term addIndexes(final Term term, final List<Variable> context, final int depth) {
    final var type = term.getClass();
    if (type == Variable.class) {
      return new DeBruijnVariable(getIndexOfVariable((Variable) term, context) + depth);
    }

    if (type == DeBruijnVariable.class) {
      return term;
    }

    if (type == Abstraction.class) {
      return new DeBruijnAbstraction(
          addIndexes(
              substituteNameWithIndex(
                  ((Abstraction) term).getParameter(), 0, ((Abstraction) term).getBody()),
              context,
              depth + 1));
    }

    if (type == Application.class) {
      return new Application(
          addIndexes(((Application) term).getFunction(), context, depth),
          addIndexes(((Application) term).getArgument(), context, depth));
    }

    throw new RuntimeException(String.format("Invalid term => %s", term));
  }

  private Term substituteNameWithIndex(final Variable variable, final int index, final Term term) {
    final var type = term.getClass();
    if (type == Variable.class) {
      return (((Variable) term).getName().equals(variable.getName()))
          ? new DeBruijnVariable(index)
          : term;
    }

    if (type == DeBruijnVariable.class) {
      return term;
    }

    if (type == Abstraction.class) {
      return ((Abstraction) term).getParameter().equals(variable)
          ? term
          : new Abstraction(
              ((Abstraction) term).getParameter(),
              substituteNameWithIndex(variable, index + 1, ((Abstraction) term).getBody()));
    }

    if (type == Application.class) {
      return new Application(
          substituteNameWithIndex(variable, index, ((Application) term).getFunction()),
          substituteNameWithIndex(variable, index, ((Application) term).getArgument()));
    }

    throw new RuntimeException(String.format("Invalid term => %s", term));
  }

  private List<Variable> createAddIndexesContext(final Term term) {
    return getFreeVariables(term).stream()
        .sorted(Comparator.comparing(Variable::getName))
        .collect(toUnmodifiableList());
  }

  private int getIndexOfVariable(final Variable variable, final List<Variable> variables) {
    return variables.indexOf(variable);
  }
}
