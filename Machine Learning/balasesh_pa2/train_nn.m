function[w1, w2, Error, E] = train_nn(X,T)
w1= rand(513,20)-0.5;
w2= rand(10,20)-0.5;
T_Size = size(T);
eta = 0.0001;       %Learning Rate
for k = 1:500
    %Activation Function
    aj = X*w1;
    zj = tanh(aj);
    yk = zj*w2';
    for i=1:T_Size(1)
        for j=1:T_Size(2)
            p = exp(yk(i,j));
            q = sum(exp(yk(i,:)));
            yk(i,j)=p/q;
        end
    end
    
    Error = 0;
    for i=1:T_Size(1)
        for j=1:T_Size(2)
            Error = (Error+(T(i,j)*log(yk(i,j))));
        end
    end
    Error = -1*(Error/19978);
    E(k) = Error;
    %plot(k,E(k));
    %fprintf('error: %f\n',Error);
    
    del1 = yk-T;
    del2 = (1-(zj.^2)).*(del1*w2);
    Egrad1 = X' * del2;
    Egrad2 = del1' * zj;
    w1_temp = w1 - eta*Egrad1;
    w2_temp = w2 - eta*Egrad2;
    w1= w1_temp;
    w2= w2_temp;
end
end