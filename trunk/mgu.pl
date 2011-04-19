
%%
%% Martelli and Montanari's algorithm for
%% the extraction of the most general unifier in
%% a set of term equations
%%


:- module(mgu, [mgu/2, mgu/3]).

:- use_module(subst).


% Generate a set of equalities from two input lists [X|Xs] and [Y|Ys]
% Return result in R
gen_eqs([], [], []).
gen_eqs([X|Xs], [Y|Ys], [eq(X,Y)|R]) :-
	gen_eqs(Xs, Ys, R).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Function symbols
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Mismatch of constant names
mmgu(E1, E2) :-
	member(E, E1),
	E = eq(const(Name1), const(Name2)),
	Name1 \== Name2,
	E2 = [error],
	!.

% Mismatch of function symbol names
mmgu(E1, E2) :-
	member(E, E1),
	E = eq(func(Name1, _), func(Name2, _)),
	Name1 \== Name2,
	E2 = [error],
	!.

% Mismatch of arity
mmgu(E1, E2) :-
	member(E, E1),
	E = eq(func(Name, Args1), func(Name, Args2)),
    length(Args1, Len1),
    length(Args2, Len2),
    Len1 \== Len2,
	E2 = [error],
	!.

% Equating a function with a constant, left
mmgu(E1, E2) :-
	member(E, E1),
	E = eq(func(_, _), const(_)),
	E2 = [error],
	!.

% Equating a function with a constant, right
mmgu(E1, E2) :-
	member(E, E1),
	E = eq(const(_), func(_, _)),
	E2 = [error],
	!.



%If E is f(s1, ..., sn) = f(t1, ..., tn) then remove E from E1 and
%add s1 = t1 ... sn = tn. Return result as E2.
mmgu(E1, E2) :-
	member(E, E1),
	E = eq(func(Name, Args1), func(Name, Args2)),  % Condition for the rule
	length(Args1, Len),
	length(Args2, Len),
        subtract(E1, [E], Ep),
	gen_eqs(Args1, Args2, Eqs),
	union(Ep, Eqs, E2),
	!.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Identity t = t
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% If t = t occurs in E1 remove this and return
% the rest of equations in E2
mmgu(E1, E2) :-
	 member(E, E1),
	 E = eq(T, T), % Condition
	 subtract(E1, [E], E2), !.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Term t = x
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% If t=x is in E1 and t is not a variable then replace with x=t and
% return result in E2
mmgu(E1, E2) :-
	 member(E, E1),
	 E = eq(T, var(X)),
	 not(T=var(_)),
         subtract(E1, [E], Ep),
	 union(Ep, [eq(var(X), T)], E2), !.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Term x = t
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

mmgu(E1, E2) :-
	member(E, E1),
	E = eq(var(X), T),
	vars(T, VarsT),
	member(var(X), VarsT),
	E2 = [error],
	!.

mmgu(E1, E2) :-
	member(E, E1),
	E = eq(var(X), T),
	not(T=var(X)),
        subtract(E1, [E], Ep),
	vars(Ep, VEp),
	member(var(X), VEp),
	subst(Ep, var(X), T, Epp),
	E2 = [E|Epp], !.


mmgu(E1, E2)	:- E2 = E1.  % ... otherwise behave as the identity function


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Fixed point computation
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

mgu(R1, R2) :-
    mmgu(R1, Rp),
	R1 = Rp,
	R2 = Rp.

mgu(R1, R2) :-
    mmgu(R1, Rtmp),
    mgu(Rtmp, R2).



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% MGU substitution
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%Utility relation, that creates a substitution from a set of
%equations
create_subst(Eqs, Dom, Rng) :-
	Eqs = [],
	Dom = [],
	Rng = [].

create_subst([eq(D, R)|Es], [D|Ds], [R|Rs]) :-
	create_subst(Es, Ds, Rs).


mgu(Eqs, Dom, Rng) :-
	mgu(Eqs, Es),
	create_subst(Es, Dom, Rng).







