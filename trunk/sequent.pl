/*
Todo: Write derivation rule in ascii art in comments...
      Write description here + name, year + example of usage.
      (syntax, expressive power etc.)

      Syntax:
      A, B := p,q,r, ...         (proposition symbols)
            | bot		 (absurdity)
	    | A --> B		 (implication)

*/


:- module(sequent, [seq/2, prove/2, subst/4, conj/2]).

:- use_module(io).
:- use_module(subst).
:- use_module(mgu).


tp(X) :- print(X), nl.

ppseq(G, D) :- pp(G), print(' |- '), pp(D).



seperate([], [], []).

seperate([and(A, B)|L], EqPart, Rest) :-
	seperate([A, B|L], EqPart, Rest).

seperate([eq(A, B)|L], [eq(A,B)|EqPart], Rest) :-
	seperate(L, EqPart, Rest).


seperate([Obj|L], EqPart, [Obj|Rest]) :-
	seperate(L, EqPart, Rest).



conj([A], A).
conj([A|Rest], and(A,R2)) :-
	conj(Rest, R2).



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
	     D1 = [A|D],
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
	     G1 = [A|G],
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
	     G1 = [A|G],
	     D2 = [B|D1],
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
	     G2 = [B|G1],
	     D1 = [A|D],
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
	     GAB = [A,B|G1],
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
	     DA = [A|D1],
	     DB = [B|D1],
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
	     GA = [A|G1],
	     GB = [B|G1],
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
	     D2 = [A,B|D1],
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
	     D1 = [A|D],
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
	     G1 = [A|G],
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
	     GA = [A|G],
	     GB = [B|G],
             DA = [A|D1],
	     DB = [B|D1],
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
	     GAB = [A,B|G1],
	     DAB = [A,B|D],
	     seq(GAB, D),
	     seq(G1,  DAB),
	     print('Bimp2: '), ppseq(GAB,D), print(' and '), ppseq(G1,DAB), nl. %print info


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Axioms/proof system for equality
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%Reflexivity of =
seq(_, D) :- member(eq(X,X), D), print('ReflEq: '), pp(eq(X,X)), nl.

% Transitivity of =
/*
seq(G, D) :- 
         member(eq(A,C), D),
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
	subst(Phi, Vs, Ts, PhiNew),
	subtract(D, [pred(N, Ts)], D1),
	seq(G, [PhiNew|D1]),
	pp(pred(N, Ts)),
    print('MkuDef: '), ppseq(G, [PhiNew|D1]), nl.

seq(G, D) :-
	member(exists(var(V), Phi), D),
	seperate([Phi],  EqPart, Rest),
	not(EqPart=[]),
	mgu(EqPart, Dom, Rng),
	member(var(V), Dom),
	subst(Rest, Dom, Rng, Rest2),
	seq(G, Rest2), %conj(EqPart, _Eqs),
	print('MkuMgu: '), ppseq(G, Rest2), nl
	.



prove(G, D) :-  seq(G, D), print('Proof of: '), ppseq(G, D).









