:- use_module(parser).


:- use_module(io).
:- use_module(sequent).

%Jeg kunne ikke vise 0->p, ex falso quidlibet
%dog gik (q&-q)->p i stedet.

/*
*/

propositional_tests(
      ['p->p',                 %T1: Identity
       'p|-p',                %T2: Udelukkede tredje
       '-(p&-p)',             %T3: Non kontradiktion
       '(q&-q)->p',           %T4: Ex falso quidlibet
       '(p->q)->(-q->-p)',    %T5: Kontra position
       '(p->q)<->(-q->-p)',   %T6: Transposition
       '(p|p)<->p',	      %T7: Idempotens af disjunktion
       '(p&p)<->p',           %T8: Idempotens af konjunktion
       '(a->b)->((a->c)->(a->(b&c)))',  %T9: Komposition
       'a->(b->(a&b))',                 %T10: Adjunktion
       '(a->b) -> ((b->c)->(a->c))',    %T11: Transitivitet
       '(a->(b->c))->((a&b)->c)',       %T12: Eksportation
       '((a|b)|c)<->(a|(b|c))',         %T13: Associativitet af disjunktion
       '((a&b)&c)<->(a&(b&c))',         %T14: Associativitet af konjunktion
       '(a|b)<->(b|a)',                 %T15: Kommutativitet af diskunktion
       '(a&b)<->(b&a)',                 %T16: Kommutativitet af konkunktion
       '(a|(b&c)) <-> ((a|b) & (a|c))', %T17: Distributativitet af | over &
       '(a&(b|c)) <-> ((a&b) | (a&c))', %T18: Distributativitet af & over |
       '(-(a|b)) <-> ((-a) & (-b))',    %T19: De Morgan
       '(-(a&b)) <-> ((-a) | (-b))',    %T20: De Morgan
       '(--p)<->p',			%T21: Dobbelt negation
       '(a&b)->a',                      %T22
       '(a&b)->b',			%T23
       '(a->b)->((b->a)->(a<->b))',     %T24
       '(a->b)->((c->b)->((a|c)->b))',  %T25
       '(a->b)->((b->(c->d))->((a&c)->d))', %T26
       '(-a)->(a->b)',  %T27
       '(p<->q)<->(q<->p)', %Egen regl
       'b->(a->b)',
       '((a|b)&(-b))->a', % Modus Tollens
       '((a|b)&(-b))->a', % Modus Tollendo Ponens
       '(-(a&b)&a)->(-b)', % Modus Ponendo Tollens
       'x->(y->x)',	                %A1 i Hilbert stil logik
       '(x->(y->z))->((x->y)->(x->z))', %A2, Hilbert stil
       '((x->0)->0)->x'			%A3, Hilbert stil
      ]).

% Seperator line
sep :- nl, print('-------------------------------------------------------------'), nl.


pr(Delta) :- p(Delta, Phi), nl, !, print('Parsing done...'), nl, prove([], [Phi]).
pr(Gamma, Delta) :- p(Gamma, PG), p(Delta, Phi), nl, print('Parsing done...'), nl, prove([PG], [Phi]).



test([]).
test([E|Exps]) :- p(E, Phi), nl, print('Parsing done...'), nl, prove([], [Phi]), sep, test(Exps).



%Simple propositional tests
:- propositional_tests(T), test(T).

% Testing predicates
%
test(['N(X)']).

%Nyt predicat til angivelse af antagelser
%(på venstresiden af sekvent, som parses og goals til højresiden.

%p('V(X)>((X=v)|(X=v(Y) & V(Y)))', R), pp(R).


:- pr('V(X)>((X=v)|(?Y.(X=v(Y) & V(Y))))', 'V(v(v))').

%:- pr('V(X)>((X=v)|(?Y.(X=v(Y) & V(Y))))', '!Z.(V(v(v(Z)))->V(Z))').


% Given ?Y.((v(v)=v(Y) & V(Y))))
% do mgu calculations on the largest fragment containt equalities
% perform the resulting substitution "on the rest of the logical expression"




