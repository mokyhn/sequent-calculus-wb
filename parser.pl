/*
Parser for the propositional calculus
Morten Kuhnrich
2011

Syntax:

 id     ::= an identifier, i.e. a string of small letters
 idlist ::= a list of id's

 t   := c, constant
      | x, variable
      | f(t,...,t), f constructor

 phi ::= 0                    Absurdity
      |  p,q,r,...            Proposition symbols
      |  -phi	              Negation
      |  phi & phi            Conjunction
      |  phi | phi	      Disjunction
      |  phi -> phi           Implication
      |  phi <-> phi          Bi-implication
      |  (phi)	              Parenthesis
      |  t=t		      Equality
      | A(t1, ..., tn)        Predicates
      | A(t1, ..., tn) > phi  Defining equality
	  | ?x.phi                Existential quantifier

 Predicate parse(I, Phi) returns Phi from input I.
*/

:- module(parser, [p/2, pt/2]).


%General definition that helps removing whitespace,
%tabulations, cr etc.
trimpred(X)       :- char_code(X, Code), Code > 32.
trimblancks(X, Y) :- sublist(trimpred, X, Y).

is_small_letter(X) :- memberchk(X,
		      [a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z]).


is_capital_letter(X) :- memberchk(X,
		      ['A','B','C','D','E',
		       'F','G','H','I','J',
		       'K','L','M','N','O',
		       'P','Q','R','S','T',
		       'U','V','W','X','Y','Z']).


% Lower case strings
small_letters([A])    :- is_small_letter(A).
small_letters([A|As]) :- is_small_letter(A), small_letters(As).

% Upper case strings
big_letters([A])    :- is_capital_letter(A).
big_letters([A|As]) :- is_capital_letter(A), big_letters(As).

% Get function name
get_func_name(L, R) :-
	append(R, ['('|_], L),
	small_letters(R).

%Get predicate name
get_pred_name(L, R) :-
	append(R, ['('|_], L),
	big_letters(R).


% L  - input
% Nt - name of function
% L1 - function body symbols
is_function(L, Nt, L1) :-
	last(L, ')'),
	get_func_name(L, Name),
	atom_chars(Nt, Name),
	append(Name, ['('|L1], L2),
	append(L2, [')'], L).

% L  - input
% Nt - name of predicate
% L1 - predicate body symbols
is_predicate(L, Nt, L1) :-
	last(L, ')'),
	get_pred_name(L, Name),
	atom_chars(Nt, Name),
	append(Name, ['('|L1], L2),
	append(L2, [')'], L).

% Parameter list parsing. Ouputs a list of terms
parse_term_list(L, [R]) :- parse_term(L, R).
parse_term_list(L, [T|Ts]) :-
	append(L1, [','| L2], L),
	parse_term(L1, T),
	parse_term_list(L2, Ts).


%Variable
parse_term(L, var(TL)) :-
	big_letters(L),
	atom_chars(TL, L).

%Constant
parse_term(S, const(TL)) :-
	S = [L|_],
	not(L = '.'),
	small_letters(S),
	atom_chars(TL, S).

%Function
parse_term(S, func(Name, Args)) :-
	is_function(S, Name, Largs),
	parse_term_list(Largs, Args).

% Absurdity
parse_phi(['0'], bot).

% Negation Phi := -Phi
parse_phi([-|L], not(Phi)) :- parse_phi(L, Phi).

%Identifiers/proposition symbols Phi := p,q,r,...
parse_phi(L, id(TL))      :-
	small_letters(L),
	atom_chars(TL, L).

%Bi-implication
parse_phi(L, bimp(A, B))  :-
	append(L1, [<,-,>|L2], L),
	parse_phi(L1, A),
	parse_phi(L2, B).

%Implication
parse_phi(L, imp(A, B))  :-
	append(L1, [-,>|L2], L),
	parse_phi(L1, A),
	parse_phi(L2, B).

%Conjunction
parse_phi(L, and(A, B))   :-
	append(L1, [&|L2], L),
	parse_phi(L1, A),
	parse_phi(L2, B).

%Disjunction
parse_phi(L, or(A, B))   :-
	append(L1, ['|'|L2], L),
	parse_phi(L1, A),
	parse_phi(L2, B).

%Parenthesis
parse_phi(L, A) :-
	L = ['('|Ls],
	append(Lp, [')'], Ls),
	parse_phi(Lp, A).

%Equality
parse_phi(L, eq(T1, T2)) :-
        append(L1, ['='|L2], L),
	parse_term(L1, T1),
	parse_term(L2, T2).

%Predicates
parse_phi(L, pred(Name, Args)) :-
	is_predicate(L, Name, Largs),
	parse_term_list(Largs, Args).

% Defining equation for relation symbols
parse_phi(L, def(Name, Args, Phi)) :-
	append(L1, ['>'|L2], L),
	parse_phi(L1, pred(Name, Args)),
	parse_phi(L2, Phi).

% Exists quantifier	
parse_phi(L, exists(var(V), Phi)) :-
    append(['?'|L1], ['.'|L2], L),
	parse_term(L1, var(V)),
	parse_phi(L2,  Phi).
	
% Main predicate for parsing of logical formulæ
p(L, R) :- 	atom_chars(L, Z),
	        trimblancks(Z, W),
		parse_phi(W, R).


% Predicate for for parsing of terms
pt(L, R) :- atom_chars(L, Z),
            trimblancks(Z, W),
	    parse_term(W, R).


