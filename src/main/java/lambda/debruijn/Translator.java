package lambda.debruijn;

import lambda.term.Term;

public interface Translator {
  Term addNames(Term term);

  Term addIndices(Term term);
}
