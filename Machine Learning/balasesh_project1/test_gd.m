function [ ERMS_Min, W_Min ] = test_gd( M, W_Min, ERMS_Min, Phi_Mat)
M_cfs = 7;              % phimatsize
M_gd = 7;               %phimatsize
lambda_cfs = 5;         %Lambda value
lambda_gd = 5;          %Lambda value
rms_cfs = 0.6693;       %calculated during testing
rms_gd = ERMS_Min;
print_data(M_cfs, M_gd,lambda_cfs, lambda_gd,rms_cfs, rms_gd);

end
