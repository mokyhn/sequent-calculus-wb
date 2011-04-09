/*
Todo: Write derivation rule in ascii art in comments...
      Write description here + name, year + example of usage.
      (syntax, expressive power etc.)

      Syntax:
      A, B := p,q,r, ...         (proposition symbols)
            | bot		 (absurdity)
	    | A --> B		 (implication)

*/


:- module(sequent, [seq/2, prove/2, subst/4]).

:- use_module(io).


tp(X) :- print(X), nl.

ppseq(G, D) :- pp(G), print(' |- '), pp(D).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
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




/*************  The sequent calculus, minimal fragment ****************/



/****************************************************
* (Ax)    Gamma, A ~~~~> Delta, A
*
*/
seq(G, D) :- member(A, G),
	     member(A, D),
	     print('ax: '), ppseq(G, D), nl.
%not(var(G)),
/****************************************************/



/****************************************************
*	      Gamma ~~~~> Delta, A
* (Ng1)   ----------------------------
*         Gamma, A --> bot ~~~~> Delta
*
*/
seq(G, D) :- member(imp(A, bot), G),
	     subtract(G, [imp(A,bot)], G1),
	     union(D, [A], D1),
	     seq(G1, D1),
	     print('Ng1: '), ppseq(G1, D1), nl. %info print
/****************************************************/




/****************************************************
*             Gamma, A ~~~~> Delta
* (Ng2)   ----------------------------
*         Gamma ~~~~> Delta, A --> bot
*
*/
seq(G, D) :- member(imp(A, bot), D),
	     subtract(D, [imp(A,bot)], D1),
	     union(G, [A], G1),
	     seq(G1, D1),
	     print('Ng2: '), ppseq(G1,D1), nl. % print info
/****************************************************/



/****************************************************
*	   Gamma, A ~~~~> Delta, B
* (Imp1)  --------------------------
*         Gamma ~~~~> Delta, A --> B

*/
seq(G, D) :- member(imp(A, B), D),
	     subtract(D, [imp(A,B)], D1),
	     union(G, [A], G1),
	     union(D1, [B], D2),
	     seq(G1, D2),
	     print('Imp1: '), ppseq(G1, D2), nl. %print info
/****************************************************/



/***************************************************************************
*            Gamma, B ~~~~> Delta     Gamma ~~~~> Delta, A
* (Imp2)  --------------------------------------------------
*		    Gamma, A --> B ~~~~> Delta
*
*/
seq(G, D) :- member(imp(A, B), G),
	     subtract(G, [imp(A, B)], G1),
	     union(G1, [B], G2),
	     union(D, [A], D1),
	     seq(G2, D),
	     seq(G1, D1),
	     print('Imp2: '), ppseq(G2,D), print(' and '), ppseq(G1,D1), nl. %print info
/****************************************************/





/*************  The sequent calculus, extended fragment ****************/





/****************************************************
 *            Gamma, A, B ~~~~> Delta
 * (And1) --------------------------------
 *          Gamma, A /\ B ~~~~> Delta
 */
seq(G, D) :- member(and(A,B), G),
	     subtract(G, [and(A,B)], G1),
	     union(G1, [A,B], GAB),
	     seq(GAB, D),
	     print('And1: '), ppseq(GAB, D), nl.  % Print info





/****************************************************
 *	  Gamma ~~~~> Delta, A	  Gamma ~~~~> Delta,B
 * (And2) -------------------------------------------
 *		    Gamma ~~~~> Delta, A /\ B

 *
 */
seq(G, D) :- member(and(A,B), D),
	     subtract(D,  [and(A,B)], D1),
	     union(D1, [A], DA),
	     union(D1, [B], DB),
	     seq(G, DA),
	     seq(G, DB),
	     print('And2: '), ppseq(G,DA), print(' and '), ppseq(G, DB), nl. %print info



/****************************************************
 *       Gamma, A ~~~~> Delta   Gamma, B ~~~~> Delta
 * (Or1) -------------------------------------------
 *                 Gamma, A \/ B ~~~~> Delta
 */
seq(G, D) :- member(or(A,B), G),
	     subtract(G, [or(A,B)], G1),
	     union(G1, [A], GA),
	     union(G1, [B], GB),
	     seq(GA, D),
	     seq(GB, D),
	     print('Or1: '), ppseq(GA,D), print(' and '), ppseq(GB,D), nl. %print info



/****************************************************
 *	      Gamma ~~~~> Delta, A, B
 * (Or2) ---------------------------------
 *	      Gamma ~~~~> Delta, A \/ B
 */
seq(G, D) :- member(or(A,B), D),
	     subtract(D, [or(A,B)], D1),
	     union(D1, [A,B], D2),
	     seq(G, D2),
	     print('Or2: '), ppseq(G, D2), nl. %Print proofpart



/****************************************************
*	     Gamma ~~~~> Delta, A
* (Ng3)   ----------------------------
*            Gamma, -A ~~~~> Delta
*
*/
seq(G, D) :- member(not(A), G),
	     subtract(G, [not(A)], G1),
	     union(D, [A], D1),
	     seq(G1, D1),
	     print('Ng3: '), ppseq(G1,D1), nl. %Print proofpart
/****************************************************/



/****************************************************
*             Gamma, A ~~~~> Delta
* (Ng4)   ----------------------------
*            Gamma ~~~~> Delta, -A
*
*/
seq(G, D) :- member(not(A), D),
	     subtract(D, [not(A)], D1),
	     union(G, [A], G1),
	     seq(G1, D1),
	     print('Ng4: '), ppseq(G1,D1), nl. % Print proofpart
/****************************************************/


/****************************************************
*             Gamma, A ~~~~> Delta, B | Gamma, B ~~~~> Delta, A
* (Bimp1)   -----------------------------------------------------
*                          Gamma ~~~~> Delta, A <-> B
*
*/
seq(G, D) :- member(bimp(A,B), D),
	     subtract(D, [bimp(A,B)], D1),
	     union(G,  [A], GA),
	     union(G,  [B], GB),
	     union(D1, [A], DA),
	     union(D1, [B], DB),
	     seq(GA, DB),
	     seq(GB, DA),
	     print('Bimp1: '), ppseq(GA,DB), print(' and '), ppseq(GB,DA), nl. %print info


/****************************************************
 *         Gamma, A, B ~~~~> Delta | Gamma  ~~~~> Delta, A, B
 * (Bimp2) ---------------------------------------------------
 *                      Gamma, A <-> B ~~~~> Delta
 */
seq(G, D) :- member(bimp(A,B), G),
	     subtract(G, [bimp(A,B)], G1),
	     union(G1, [A, B], GAB),
	     union(D,  [A, B], DAB),
	     seq(GAB, D),
	     seq(G1,  DAB),
	     print('Bimp2: '), ppseq(GAB,D), print(' and '), ppseq(G1,DAB), nl. %print info


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Axioms/proof system for equality
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Reflexivity of =
% seq(_, D) :- not(var(X)), member(eq(X,X), D), print('ReflEq: '),
% pp(eq(X,X)), nl.

% Transitivity of =
/*
seq(G, D) :- member(eq(A,C), D),
	     seq(G, [eq(A,B)]),
	     not(B=C),
	     print(eq(A, B)),
	     seq(G, [eq(B,C)]),
	     print(eq(B,C)),
	     print('TransEq: '), pp([eq(A,B), eq(B,C)]), nl.
*/

/****************************************************
 *                    Gamma ~~~~> Phi[t1/x1, ..., tn/xn]
 * (MKDef) ---------------------------------------------------
 *                     Gamma ~~~~> A(t1, ..., tn), Delta
 *  when Delta A(x1,...,xn)>Phi in E
 */
seq(G, D) :-
	member(def(N, Vs, Phi),  G),
	member(pred(N, Ts),	 D),
	length(Vs, No),
	length(Ts, No),
	print('A'),
	subst(Phi, Vs, Ts, PhiNew),
	print('B'),
	subtract(D, [pred(N, Ts)], D1),
	print('MkuDef: '), ppseq(G, [PhiNew|D1]), nl,
	%abort,
	seq(G, [PhiNew|D1]),
	pp(pred(N, Ts)),
	print(' becomes: '), pp(PhiNew), nl.



prove(G, D) :-  seq(G, D), print('Proof of: '), ppseq(G, D).









