function [W, Error, E] = train_lr(X,Tn)
W=rand(513,10);
T = size(Tn);           %19978*10
eta =0.0001;            %Learning Rate
for c = 1:500
    yn= X*W;			%data X * W        
    Y_size= size(yn);
    for i=1:Y_size(1)           %19978
        for j=1:Y_size(2)       %10
            yn(i,j) = exp(yn(i,j))/sum(exp(yn(i,:)));
        end
    end
    Egrd = X'*(yn-Tn);
    wnew = W - eta*Egrd;
    Error=0;
    for i=1:T(1)
        for j=1:T(2)
            Error= Error+Tn(i,j)*log(yn(i,j));
        end
    end
    Error = -1*(Error/19978);
    E(c) = Error;
    %plot(c,E(c));
    %fprintf('error: %f\n',Error);
    W=wnew;
end
end