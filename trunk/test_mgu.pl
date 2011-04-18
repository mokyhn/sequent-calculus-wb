%% Tests of mmgu


:- use_module(parser).
:- use_module(mgu).
:- use_module(io).


h(and(E1,E2), R) :- h(E1, R1),  h(E2, R2), union(R1, R2, R).
h(T, [T]).


:- p('f(X,Y,Z)=g(A,B,C)',   R1), mmgu([R1], [error]), print('test1 passed...'), nl.
:- p('f(X,Y,Z)=g(A,B,C,D)', R1), mmgu([R1], [error]), print('test2 passed...'), nl.
:- p('f(a,b,c)=g(A,B,e)',   R1), mmgu([R1], [error]), print('test3 passed...'), nl.
:- p('X=X',                 R1), mmgu([R1], []),      print('test4 passed...'), nl.
:- p('a=a',                 R1), mmgu([R1], []),      print('test5 passed...'), nl.
:- p('X=a&b=X',		    R1), h(R1, R2), mmgu(R2, [error]), print('test6 passed...'), nl.
:- p('X=a&a=Y&X=Y',	    R1), h(R1, R2), mmgu(R2, [eq(var('X'), const(a)), eq(var('Y'), const(a))]), print('test7 passed...'), nl.

:- p('f(g(Y))=f(X)',	    R1), mmgu([R1], R2), p('X=g(Y)', R3), R2 = [R3], print('test8 passed'),nl.
:- p('f(X,a)=f(g(Z),Y)&h(X,Z)=h(u,d)', R1), h(R1, R2), mmgu(R2, R3), pp(R3), nl. % last equation wrong order?!...
:- p('f(X,a)=f(g(Z),Y)&h(X,Z)=h(d,u)', R1), h(R1, R2), mmgu(R2, R3), print(R3),nl. % should fail?!...

%:- p('X=X', R1), mmgu([R1], R2), print('mmgu: ['), pp(R1), print(']  becomes ['), pp(R2), print(']'), nl, nl.



% :- p('X=Y&Y=Z', R1), h(R1, R2), mmgu(R2, R3), print('mmgu: ['), pp(R2),
% print('] becomes ['), pp(R3), print(']'), nl, nl.

