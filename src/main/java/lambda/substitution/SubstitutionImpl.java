package lambda.substitution;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static lambda.utils.Util.getBoundVariables;
import static lambda.utils.Util.getFreeVariables;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lambda.term.Abstraction;
import lambda.term.Application;
import lambda.term.Term;
import lambda.term.Variable;

// Task 2.5
class SubstitutionImpl implements Substitution {
  private static final int BOUND = 20;
  private static final int INITIAL_INDEX = 0;
  private static final String VARIABLE_NAME = "x";

  @Override
  public Term safeSubstitute(final Term origin, final Variable variable, final Term substitution) {
    final var renamed = renameConflictVariables(origin, substitution);
    return substitute(renamed, variable, substitution);
  }

  private Term substitute(final Term origin, final Variable variable, final Term substitution) {
    final var type = origin.getClass();
    if (type == Variable.class) {
      return origin.equals(variable) ? substitution : origin;
    }

    if (type == Abstraction.class) {
      final var abstraction = (Abstraction) origin;
      return abstraction.getParameter().equals(variable)
          ? origin
          : new Abstraction(
              abstraction.getParameter(),
              substitute(abstraction.getBody(), variable, substitution));
    }

    if (type == Application.class) {
      final var application = (Application) origin;
      return new Application(
          substitute(application.getFunction(), variable, substitution),
          substitute(application.getArgument(), variable, substitution));
    }

    throw new RuntimeException(String.format("Unsupported term => %s", origin));
  }

  private Term renameConflictVariables(final Term origin, final Term substitution) {
    final var substitutionFreeVars = getFreeVariables(substitution);
    final var originBoundVars = getBoundVariables(origin);
    final var conflictVariable =
        originBoundVars.stream()
            .filter(substitutionFreeVars::contains)
            .min(Comparator.comparing(Variable::getName))
            .orElse(null);

    if (conflictVariable == null) {
      return origin;
    }

    final var names =
        Stream.concat(originBoundVars.stream(), substitutionFreeVars.stream())
            .map(Variable::getName)
            .collect(toUnmodifiableSet());
    final var freshVariable = generateFreshVariable(names);

    final var newTerm = renameBoundVariable(origin, conflictVariable, freshVariable);
    return renameConflictVariables(newTerm, substitution);
  }

  private Variable generateFreshVariable(final Set<String> names) {
    return generateFreshVariable(names, INITIAL_INDEX);
  }

  private Variable generateFreshVariable(final Set<String> names, final int index) {
    return IntStream.range(index, index + BOUND)
        .boxed()
        .map(i -> VARIABLE_NAME + i)
        .filter(not(names::contains))
        .findFirst()
        .map(Variable::new)
        .orElseGet(() -> generateFreshVariable(names, index + BOUND));
  }

  private Term renameBoundVariable(final Term term, final Variable oldVar, final Variable newVar) {
    final var type = term.getClass();
    if (type == Variable.class) {
      return term;
    }

    if (type == Abstraction.class) {
      final var abstraction = (Abstraction) term;
      return abstraction.getParameter().equals(oldVar)
          ? new Abstraction(
              newVar,
              renameBoundVariable(
                  substitute(abstraction.getBody(), oldVar, newVar), oldVar, newVar))
          : new Abstraction(
              abstraction.getParameter(),
              renameBoundVariable(abstraction.getBody(), oldVar, newVar));
    }

    if (type == Application.class) {
      final var application = (Application) term;
      return new Application(
          renameBoundVariable(application.getFunction(), oldVar, newVar),
          renameBoundVariable(application.getArgument(), oldVar, newVar));
    }

    throw new RuntimeException(String.format("Unsupported term => %s", term));
  }
}
