function[n] = test_lr(W,X)
P = exp(X*W);
Denom = sum(P,2);

for j=1:1500
    Y(j,:) = P(j,:)/Denom(j);
end
[m,n] = max(Y,[],2);
for j=1:1500
   n(j) = n(j)-1;
end
end
