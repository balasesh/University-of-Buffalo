function[n] = test_nn(w1, w2, X)
aj = X*w1;
zj = tanh(aj);
yn = zj*w2';
for i = 1:1500
    p = exp(yn(i,:));
    q = sum(p,2);
    Y(i,:) = p/q;
end

[m,n] = max(Y,[],2);
for j=1:1500
   n(j) = n(j)-1;
end
end
