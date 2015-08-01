function [Test_ERMS] = test_cfs(WML,phimatsize,Val_Lambda,M)

Testing_Matrix = M(62660:69623,2:47);       % 10 percent
Testing_Relevence = M(62660:69623,1);       % 10 percent
mu_mat = mean(M(:,2:47));
Test_Y = size(Testing_Matrix);
s = 0.95;

Test_Phi_Mat(1:6960,1) = 1;
for j = 1:Test_Y(1)
    for i = 1:phimatsize-1
        Q = (Testing_Matrix(j,:)) - (1.125*mu_mat);         %exp(-(x- mu)/2s^2
        Test_Phi_Mat(j,i) = exp(-(Q*Q')/(2*s.^2));
    end
end
save Test_Phi_Mat;
Test_N = size(Test_Phi_Mat);
Test_EWtemp = ((Test_Phi_Mat*WML) - Testing_Relevence);
Test_ED = (0.5*(Test_EWtemp'*Test_EWtemp)) + (0.5*Val_Lambda*(WML'*WML));
Test_ERMS = sqrt(2*(Test_ED)/Test_N(1));
end