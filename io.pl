:- module(io, [pp/1]).

%
% Pretty printing
%
%
pp(A) :-  atom(A), print(A).

pp(id(A)):- print(A).

pp(imp(A,B)) :- print('('),
	         pp(A),
	         print('-->'),
		 pp(B),
		 print(')').

pp(bot) :- print('0').

pp(bimp(A,B)) :- print('('),
	         pp(A),
	         print('<-->'),
		 pp(B),
		 print(')').

pp(not(A)) :- print('-'), pp(A).
pp(and(A,B)) :- print('('), pp(A), print(' & '), pp(B), print(')').
pp(or(A,B))  :- print('('), pp(A), print(' | '), pp(B), print(')').

pp([A|[]]) :- pp(A), !.
pp([A|As]) :- pp(A), print(', '),  pp(As).

pp(eq(A,B)) :- pp(A), print('='), pp(B).

pp(const(A)) :- print(A).
pp(var(A))   :- print(A).

pp(pred(N, A)) :- print(N), print('('), pp(A), print(')').
pp(func(N, A)) :- print(N), print('('), pp(A), print(')').

pp(def(N, A, Phi)) :- print(N), print('('), pp(A), print(')'),
	 print(' > '), pp(Phi).







