package lambda.substitution;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toUnmodifiableSet;

import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lambda.term.Abstraction;
import lambda.term.Application;
import lambda.term.Term;
import lambda.term.Variable;

class Substitution {
  private static final int BOUND = 20;
  private static final int INITIAL_INDEX = 0;
  private static final String PARAMETER = "x";

  Term safeSubstitute(final Term origin, final Variable variable, final Term substitution) {
    return substitute(renameConflictingVariables(origin, substitution), variable, substitution);
  }

  private Term substitute(final Term origin, final Variable variable, final Term substitution) {
    if (origin instanceof Variable) {
      return origin.equals(variable) ? substitution : origin;
    }

    if (origin instanceof Abstraction) {
      final var abstraction = ((Abstraction) origin);
      return abstraction.getParameter().equals(variable)
          ? origin
          : new Abstraction(
              abstraction.getParameter(),
              substitute(abstraction.getBody(), variable, substitution));
    }

    if (origin instanceof Application) {
      final var application = (Application) origin;
      return new Application(
          substitute(application.getArgument(), variable, substitution),
          substitute(application.getFunction(), variable, substitution));
    }

    throw new RuntimeException("Invalid term");
  }

  private Term renameConflictingVariables(final Term origin, final Term substitution) {
    final var substitutionFreeVars = substitution.getFreeVariables();
    final var conflictVariable =
        origin.getBoundVariables().stream()
            .filter(substitutionFreeVars::contains)
            .findFirst()
            .orElse(null);

    if (conflictVariable == null) {
      return origin;
    }

    final var names =
        Stream.concat(origin.getBoundVariables().stream(), substitutionFreeVars.stream())
            .map(Variable::getName)
            .collect(toUnmodifiableSet());
    final var freshVariable = generateFreshVariable(names);

    final var newTerm = renameBoundVariable(origin, conflictVariable, freshVariable);
    return renameConflictingVariables(newTerm, substitution);
  }

  private Variable generateFreshVariable(final Set<String> names) {
    return generateFreshVariable(names, INITIAL_INDEX);
  }

  private Variable generateFreshVariable(final Set<String> names, final int index) {
    return IntStream.range(index, index + BOUND)
        .boxed()
        .map(num -> PARAMETER + num)
        .filter(not(names::contains))
        .findFirst()
        .map(Variable::new)
        .orElseGet(() -> generateFreshVariable(names, index + BOUND));
  }

  private Term renameBoundVariable(final Term term, final Variable oldVar, final Variable newVar) {
    if (term instanceof Variable) {
      return term;
    }

    if (term instanceof Abstraction) {
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

    if (term instanceof Application) {
      final var application = (Application) term;
      return new Application(
          renameBoundVariable(application.getFunction(), oldVar, newVar),
          renameBoundVariable(application.getArgument(), oldVar, newVar));
    }

    throw new RuntimeException("Invalid term");
  }
}
