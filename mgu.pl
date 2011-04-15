
%%
%% Martelli and Montanari's algorithm for
%% the extraction of the most general unifier in
%% a set of term equations
%%


:- module(mgu, [mmgu/2, mmgu_com/2, mmgu_ii/2]).

:- use_module(subst).


% Generate a set of equalities from two input lists [X|Xs] and [Y|Ys]
% Return result in R
gen_eqs([], [], []).
gen_eqs([X|Xs], [Y|Ys], [eq(X,Y)|R]) :-
	gen_eqs(Xs, Ys, R).

% Return variables of an expression
vars([], []).
vars(const(_C), []).
vars(var(X), [var(X)]).
vars(func(_Name, Args), V) :- vars(Args, V).
vars([T|Ts], R) :-
	vars(T,  R1),
	vars(Ts, R2),
	union(R1, R2, R).


%If E is f(s1, ..., sn) = f(t1, ..., tn) then remove E from E1 and
%add s1 = t1 ... sn = tn. Return result as E2.
mmgu_i(E1, E2) :-
	member(E, E1),
	(E = eq(func(Name1, Args1), func(Name2, Args2))  % Condition for the rule
	  ->                                            % Body of the rule
	(Name1 = Name2 ->
		(
		   length(Args1, Len1),
		   length(Args2, Len2),
		   Len1 = Len2 ->
		   (
			 subtract(E1, [E], Ep),
			 gen_eqs(Args1, Args2, Eqs),
			 union(Ep, Eqs, E2)
		   ) ; E2 = [error_ia] % Mismatch of number of term arguments
		) ; E2 = [error_ib] % Mismatch of function symbol names
	) ;
	E2 = E1	  % ...otherwise behave as the identity function
	).
	
% If x = x occurs in E1 remove this and return
% the rest of equations in E2
mmgu_ii(E1, E2) :-
	 member(E, E1),
	 (E = eq(var(X), var(X)) % Condition
	  ->
	    subtract(E1, [E], E2);
	  E2 = E1 % ... otherwise behave as the identity function
      )	 
	 . 

% If t=x is in E1 and t is not a variable then replace with x=t and
% return result in E2
mmgu_iii(E1, E2) :-
	 member(E, E1),
	 (E = eq(T, var(X)),
 	 not(T=var(_))
	  ->
	 (
	  subtract(E1, [E], Ep),
	  union(Ep, [eq(var(X), T)], E2)
	 ) ;
	 E2 = E1  % ... otherwise behave as the identity function
	 ). 

mmgu_iv(E1, E2) :-
	member(E, E1),
	(E = eq(var(X), T),
	vars(T, VarsT),
	not(T=var(X)) -> 
	(
		(  not(member(var(X), VarsT)) ->
		 (
		  subtract(E1, [E], Ep),
		  subst(Ep, var(X), T, Epp),
		  E2 = [E|Epp]
		 ) ; E2 = [error_iv]
		)
	) ;
	E2 = E1  % ... otherwise behave as the identity function
	)
	. 

% A composition of the above functions
mmgu_com(Es, Er) :-
	mmgu_i(Es, E1),
	mmgu_ii(E1, E2),
	mmgu_iii(E2, E3),
	mmgu_iv(E3, Er).
	
	
% Fixpoint computation using mmgu_com
mmgu(R1, R2) :-
    mmgu_com(R1, Rp),
	(R1 = Rp
       -> 
	    R2 = Rp  ;
	    mmgu(Rp, R2)
	).	


	








