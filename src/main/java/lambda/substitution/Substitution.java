package lambda.substitution;

import lambda.term.Term;
import lambda.term.Variable;

public interface Substitution {
  Term safeSubstitute(Term origin, Variable variable, Term substitution);
}
