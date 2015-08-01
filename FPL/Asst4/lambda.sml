datatype term = V of string | L of string * term | A of term * term

fun show(V(s)) = s
| show(L(s1,t)) = "L"^s1^"."^show(t)
| show(A(t1,t2)) = "("^show(t1)^" "^show(t2)^")";

show(A(A(L("f", L("x", A(V("f"), V("x")))) , V("a")) , V("b")));

fun check_var(i,j,[]) = if i = j then true else false
| check_var(i,j,(h1,h2)::tl) =  if h1 = i andalso h2 = j then true else if h1 = i andalso h2 <> j then false 
else if h1 <> i andalso h2 = j then false else check_var(i,j,tl);

fun alpha2(V(s1),V(s2),env) = check_var(s1,s2,env)
| alpha2(L(s1,t1),L(s2,t2),env) = let val env = (s1,s2)::[] in alpha2(t1,t2,env) end
| alpha2(A(t1,t2),A(t3,t4),env) = alpha2(t1,t3,env) orelse alpha2(t2,t4,env)
| alpha2(L(s,t1),A(t2,t3),env) = false
| alpha2(A(t1,t2),L(s,t3),env) = false
| alpha2(V(s1),L(s2,t),env) = false
| alpha2(L(s1,t),V(s2),env) = false
| alpha2(A(t1,t2),V(s),env) = false
| alpha2(V(s),A(t1,t2),env) = false;

fun alpha(t1,t2) = alpha2(t1,t2,[]);

alpha(V("a"),V("a"));
alpha(V("b"),V("b"));
alpha(L("x", V("x")),L("y", V("y")));
alpha(A(L("x", V("x")), V("a")),A(L("y", V("y")), V("a")));
alpha(A(L("x", V("x")), V("x")),A(L("y", V("y")), V("x")));
alpha(L("f", L("x", A(V("f"), V("x")))),L("x", L("f", A(V("x"), V("f")))));
alpha(L("f", L("x", A(V("f") , A(V("f") , V("x"))))),L("g", L("y", A(V("g") , A(V("g") , V("y"))))));
alpha(A(A(L("f", L("x", A(V("f"), V("x")))) , V("a")) , V("b")),A(A(L("g", L("y", A(V("g"), V("y")))) , V("a")) , V("b")));
alpha(V("a"),V("b"));
alpha(L("x", V("x")),L("y", V("b")));
alpha(L("f", L("x", A(V("f"), V("x")))),L("x", L("f", A(V("x"), V("k")))));
alpha(L("f", L("x", A(V("f"), V("x")))),L("f", A(V("x"), V("k"))));
alpha(L("x",L("y",V("x"))),L("x",L("x",V("x"))));