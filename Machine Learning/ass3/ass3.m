myname = 'balasesh';

answer{1} = sym('[0.2818,0.7182; -0.1140,0.1140; 0.1316,-0.1316]');

answer{2}= sym('0.30');

answer{3}= sym('[-0.4103;0.4739]');

answer{4}= sym('-0.7856');

answer{5} = sym('symprod((sigmoid(a(n))^t(n))*((1-sigmoid(a(n)))^(1-t(n))),n,1,N)');

answer{6} = sym('-symsum(t(n) * ln(sigmoid(a(n)))+(1-t(n)) * ln(1 - sigmoid(a(n))),n,1,N)');

answer{7} = sym('transpose(w) - eta * symsum((sigmoid(a(n)) - t(n))*x(n),n,1,N)');

answer{8} = sym('-symsum(t(n) * ln(sigmoid(a(n)))+(1-t(n)) * ln(1-sigmoid(a(n))),n,1,N) + (lambda/2)* w * transpose(w)');

answer{9} = sym('(1/2)*(symsum(transpose(sigmoid(a(n))-t(n))*(sigmoid(a(n))- t(n)),n,1,N)+(lambda*(transpose(w)*w)))');

answer{10} = sym('w(0) - eta*(symsum((sigmoid(a(n))-t(n))*sigmoid(a(n))*(1-sigmoid(a(n))),n,1,N)+ (lambda*w(0)))');

answer{11} = sym('w(1) - eta*(symsum((sigmoid(a(n))-t(n))*sigmoid(a(n))*(1-sigmoid(a(n)))*x(1,n),n,1,N)+(lambda*w(1)))');

answer{12} = sym('w(2) - eta*(symsum((sigmoid(a(n))-t(n))*sigmoid(a(n))*(1-sigmoid(a(n)))*x(2,n),n,1,N)+(lambda*w(2)))');

answer{13} = sym('sigmoid( symsum(w2(k,j)*tanh(symsum(w1(j,i)*x(i),i,0,D)),j,0,M) )');

answer{14} = sym('symsum(symsum(t(n,k)*ln(y(n,k)),k,1,K),n,1,N)');

answer{15} = sym('symsum(t(n,k)/y(n,k),n,1,N)');

answer{16} = sym('symsum(w(k,j)*(y(n,k) - t(n,k)),k,1,K)');

answer{17} = sym('((1-(tanh(symsum(w(j,i)*x(i),i,0,D)))^2)*(symsum(w(k,j)*(y(n,k) - t(n,k)),k,1,K)))*x(i)');

answer{18} = sym('(y(k) - t(k))*(tanh(symsum(w(j,i)*x(i),i,0,D)))');

for i =1:18
   pretty(answer{i});
end

save ass3.mat myname answer