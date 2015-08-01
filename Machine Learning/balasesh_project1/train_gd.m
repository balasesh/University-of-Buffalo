function [ ERMS_Min, W_Min ] = train_gd( M, Phi_Mat,Val_Lambda )
%UNTITLED3 Summary of this function goes here
%   Detailed explanation goes here

Training_Relevence = M(1:55698,1);
S = size(Phi_Mat);
W = zeros(S(2),1);
n = 1;

for i = 1: S(2)
    W = W + (n*(Training_Relevence(i) - (W'*Phi_Mat(i,:)')))*Phi_Mat(i,:)';
    Train_GD_EWtemp = ((Phi_Mat*W) - Training_Relevence);
    Train_GD_ED = (0.5*(Train_GD_EWtemp'*Train_GD_EWtemp)) + (0.5*Val_Lambda*(W'*W));
    Train_GD_ERMS(i,1) = sqrt(2*(Train_GD_ED)/S(1));
    WML(:,i) = W;
        
        if(i-1 > 0 && Train_GD_ERMS(i)> Train_GD_ERMS(i-1))
            n = n*0.5;
            i = i+1;
        end
end
[ERMS_Min,Position] = min(Train_GD_ERMS);
W_Min = WML(:,Position);
save W_gd.mat W_Min;
test_gd( M, W_Min, ERMS_Min,Phi_Mat);
end
