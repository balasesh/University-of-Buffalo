function [ WML,phimatsize,Val_Lambda,Val_ERMS,M ] = train_cfs(M)
Training_Matrix = M(1:55698,2:47);          % 80 percent
Validation_Matrix = M(55699:62658,2:47);    % 10 Percent

Training_Relevence = M(1:55698,1);          % 80 percent
Validation_Relevence = M(55699:62658,1);    % 10 percent

Val_Y = size(Validation_Matrix);
Y = size(Training_Matrix);

s = 0.95;
mu_mat = mean(M(:,2:47));
Val_mu_mat = mean(M(:,2:47)); %mean(Validation_Matrix);
%Lambda = 0.9;
%Value = [0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9 1.0];
for p = 1:10
    phimatsize = p;
    for r = 1:10
        Val_Lambda = r;          % Lambda = Value(r);
        
        Phi_Mat(1:55698,1) = 1;
        
        Val_Phi_Mat(1:6960,1) = 1;
        
        for j = 1:Y(1)
            for i = 1:phimatsize-1
                Q = (Training_Matrix(j,:)) - (1.125*mu_mat);         %exp(-(x- mu)/2s^2
                Phi_Mat(j,i) = exp(-(Q*Q')/(2*s.^2));
            end
        end
        
        for j = 1:Val_Y(1)
            for i = 1:phimatsize-1
                Val_Q = (Validation_Matrix(j,:)) - (1.5*Val_mu_mat);         %exp(-(x- mu)/2s^2
                Val_Phi_Mat(j,i)= exp(-(Val_Q*Val_Q')/(2*s.^2));
            end
        end
        
        Temp = (Phi_Mat'* Phi_Mat);
        I = eye(size(Temp));
        Temp2 = Phi_Mat' * Training_Relevence;
        WML = inv((Val_Lambda*I) +Temp)*Temp2;
        
        if(phimatsize == 7 && Val_Lambda == 5)
            Test_WML = WML;
            Test_Phi_Mat = Phi_Mat;
        end
        
        Val_N = size(Val_Phi_Mat);
        Val_EWtemp = ((Val_Phi_Mat*WML) - Validation_Relevence);
        Val_ED = (0.5*(Val_EWtemp'*Val_EWtemp)) + (0.5*Val_Lambda*(WML'*WML));
        Val_ERMS = sqrt(2*(Val_ED)/Val_N(1));
        Z(Val_Lambda,phimatsize) = Val_ERMS;
    end
end
save W_cfs.mat Test_WML;
test_cfs(Test_WML,7,5,M);
train_gd(M, Test_Phi_Mat,5);
end
