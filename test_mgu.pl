%% Tests of mmgu


:- use_module(parser).
:- use_module(mgu).
:- use_module(io).


:- p('f(X,Y,Z)=g(A,B,C)', R1), 
   mmgu([R1], R2).

%:- p('X=X', R1), mmgu([R1], R2), print('mmgu: ['), pp(R1), print(']  becomes ['), pp(R2), print(']'), nl, nl.


h(and(E1,E2), R) :- h(E1, R1),  h(E2, R2), union(R1, R2, R).
h(T, [T]).


:- p('X=Y&Y=Z', R1), h(R1, R2), mmgu(R2, R3), print('mmgu: ['), pp(R2), print(']  becomes ['), pp(R3), print(']'), nl, nl.

