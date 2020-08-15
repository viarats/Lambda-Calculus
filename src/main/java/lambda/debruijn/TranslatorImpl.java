package lambda.debruijn;

import static java.util.stream.Collectors.toUnmodifiableList;
import static lambda.utils.Util.getFreeVariables;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import lambda.term.Abstraction;
import lambda.term.Application;
import lambda.term.DeBruijnAbstraction;
import lambda.term.DeBruijnVariable;
import lambda.term.Term;
import lambda.term.Variable;

// Task 2.8
class TranslatorImpl implements Translator {
  private static final int INITIAL_DEPTH = 0;
  private static final int INITIAL_INDEX = 0;
  private static final int BOUND = 20;

  @Override
  public Term addNames(final Term term) {
    return addNames(term, createNameContext(), INITIAL_DEPTH);
  }

  @Override
  public Term addIndices(final Term term) {
    return addIndices(term, getFreeVariablesSorted(term), INITIAL_DEPTH);
  }

  private Term addNames(final Term term, final List<String> context, int depth) {
    final var type = term.getClass();
    if (type == Variable.class) {
      return term;
    }

    if (type == DeBruijnVariable.class) {
      final var name = createFreeVariableName(((DeBruijnVariable) term).getIndex(), depth);
      return new Variable(name);
    }

    if (type == DeBruijnAbstraction.class) {
      final var parameter = context.get(0);
      return new Abstraction(
          new Variable(parameter),
          addNames(
              substituteIndexWithName(
                  INITIAL_INDEX, parameter, ((DeBruijnAbstraction) term).getBody()),
              getTail(context),
              ++depth));
    }

    if (type == Application.class) {
      final var application = (Application) term;
      return new Application(
          addNames(application.getFunction(), context, depth),
          addNames(application.getArgument(), context, depth));
    }

    throw new RuntimeException(String.format("Invalid term => %s", term));
  }

  private String createFreeVariableName(final int index, final int depth) {
    return "y" + (index - depth);
  }

  private Term substituteIndexWithName(int index, final String name, final Term term) {
    final var type = term.getClass();
    if (type == Variable.class) {
      return term;
    }

    if (type == DeBruijnVariable.class) {
      return index == ((DeBruijnVariable) term).getIndex() ? new Variable(name) : term;
    }

    if (type == DeBruijnAbstraction.class) {
      return new DeBruijnAbstraction(
          substituteIndexWithName(++index, name, ((DeBruijnAbstraction) term).getBody()));
    }

    if (type == Application.class) {
      final var application = (Application) term;
      return new Application(
          substituteIndexWithName(index, name, application.getFunction()),
          substituteIndexWithName(index, name, application.getArgument()));
    }

    throw new RuntimeException(String.format("Invalid term => %s", term));
  }

  private List<String> createNameContext() {
    return IntStream.range(0, BOUND)
        .boxed()
        .map(index -> "x" + index)
        .collect(toUnmodifiableList());
  }

  private List<String> getTail(final List<String> list) {
    return IntStream.range(1, list.size()).boxed().map(list::get).collect(toUnmodifiableList());
  }

  private Term addIndices(final Term term, final List<Variable> freeVariables, int depth) {
    final var type = term.getClass();
    if (type == Variable.class) {
      return new DeBruijnVariable(freeVariables.indexOf(term) + depth);
    }

    if (type == DeBruijnVariable.class) {
      return term;
    }

    if (type == Abstraction.class) {
      final var abstraction = (Abstraction) term;
      return new DeBruijnAbstraction(
          addIndices(
              substituteNameWithIndex(abstraction.getParameter(), 0, abstraction.getBody()),
              freeVariables,
              ++depth));
    }

    if (type == Application.class) {
      final var application = (Application) term;
      return new Application(
          addIndices(application.getFunction(), freeVariables, depth),
          addIndices(application.getArgument(), freeVariables, depth));
    }

    throw new RuntimeException(String.format("Invalid term => %s", term));
  }

  private Term substituteNameWithIndex(final Variable variable, int index, final Term term) {
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
      final var abstraction = (Abstraction) term;
      return abstraction.getParameter().equals(variable)
          ? term
          : new Abstraction(
              abstraction.getParameter(),
              substituteNameWithIndex(variable, ++index, abstraction.getBody()));
    }

    if (type == Application.class) {
      final var application = (Application) term;
      return new Application(
          substituteNameWithIndex(variable, index, application.getFunction()),
          substituteNameWithIndex(variable, index, application.getArgument()));
    }

    throw new RuntimeException(String.format("Invalid term => %s", term));
  }

  private List<Variable> getFreeVariablesSorted(final Term term) {
    return getFreeVariables(term).stream()
        .sorted(Comparator.comparing(Variable::getName))
        .collect(toUnmodifiableList());
  }
}
