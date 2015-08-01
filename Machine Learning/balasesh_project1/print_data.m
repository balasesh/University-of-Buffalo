function [ ] = print_data(M_cfs, M_gd,lambda_cfs, lambda_gd,rms_cfs, rms_gd)

fprintf('the model complexity M_cfs is %d\n', M_cfs);
fprintf('the model complexity M_gd is %d\n', M_gd);
fprintf('the regularization parameters lambda_cfs is %4.2f\n', lambda_cfs);
fprintf('the regularization parameters lambda_gd is %4.2f\n', lambda_gd);
fprintf('the root mean square error for the closed form solution is %4.2f\n', rms_cfs);
fprintf('the root mean square error for the gradient descent method is %4.2f\n', rms_gd);

end

