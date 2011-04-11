:- module(subst, [subst/4]).


%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Substitution
% subst(E, var(V), T, E') replace V in E with T and return result in E'.

% Substitutions in terms
subst(const(C), _V,  _T, const(C)).
subst([], _V, _T, []).
subst([Te|Ts], V, T, [R|Rs]) :-
	subst(Te, V, T, R), subst(Ts, V, T, Rs).

subst(var(V),	 var(V),   T, T).
subst(var(V),	 var(W),   _T, var(V)) :- not(V=W).
subst(func(Name, Args), var(V), T, func(Name, ArgsN)) :-
	subst(Args, var(V), T, ArgsN).

%Substitutions in logical formulæ
subst(bot,        _V, _T, bot).
subst(not(Phi),    V,  T, not(R)) :- subst(Phi, V, T, R).
subst(id(I),	  _V, _T, id(I)).
subst(bimp(A, B),  V,  T, bimp(Ar, Br)) :-
	subst(A, V, T, Ar),
	subst(B, V, T, Br).
subst(imp(A, B),  V,  T, imp(Ar, Br)) :-
	subst(A, V, T, Ar),
	subst(B, V, T, Br).
subst(and(A, B),  V,  T, and(Ar, Br)) :-
	subst(A, V, T, Ar),
	subst(B, V, T, Br).
subst(or(A, B),  V,  T, or(Ar, Br)) :-
	subst(A, V, T, Ar),
	subst(B, V, T, Br).
subst(eq(T1, T2),  V,  T, eq(T1r, T2r)) :-
	subst(T1, V, T, T1r),
	subst(T2, V, T, T2r).
subst(pred(Name, Args), V, T, pred(Name, Argsr)) :-
	subst(Args, V, T, Argsr).

subst(def(Name, Args, Phi), V, T, def(Name, Argsr, Phir)) :-
	subst(Args, V, T, Argsr),
	subst(Phi,  V, T, Phir).

% Substitution of a list of terms for a list of variables.
subst(Phi, [], [], Phi).

subst(Phi, [V|Vs], [T|Ts], Phires) :-
	subst(Phi,     V, T,  PhiTmp),
	subst(PhiTmp, Vs, Ts, Phires).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

