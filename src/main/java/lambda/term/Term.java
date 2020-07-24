package lambda.term;

import java.util.Set;

public interface Term {

  Set<Variable> getFreeVariables();

  Set<Variable> getBoundVariables();
}
