yourubitname = 'balasesh';
yournumber = 50097470;
fprintf('My ubit name is %s\n',yourubitname);
fprintf('My student number is %d \n',yournumber);
bias = ones(2000,1);
bias1 = ones(1979,1);
bias2 = ones(1999,1);
m0 = load('features_train\0.txt');      %2000
m0 = horzcat(bias,m0);
m1 = load('features_train\1.txt');      %1979
m1 = horzcat(bias1,m1);
m2 = load('features_train\2.txt');      %1999
m2 = horzcat(bias2,m2);
m3 = load('features_train\3.txt');      %2000
m3 = horzcat(bias,m3);
m4 = load('features_train\4.txt');      %2000
m4 = horzcat(bias,m4);
m5 = load('features_train\5.txt');      %2000
m5 = horzcat(bias,m5);
m6 = load('features_train\6.txt');      %2000
m6 = horzcat(bias,m6);
m7 = load('features_train\7.txt');      %2000
m7 = horzcat(bias,m7);
m8 = load('features_train\8.txt');      %2000
m8 = horzcat(bias,m8);
m9 = load('features_train\9.txt');      %2000
m9 = horzcat(bias,m9);
X = vertcat(m0,m1,m2,m3,m4,m5,m6,m7,m8,m9);
T = zeros(19978,10);
T(1:2000,1) = 1;
T(2001:3979,2)= 1;
T(3980:5978,3)= 1;
T(5979:7978,4)= 1;
T(7979:9978,5)= 1;
T(9979:11978,6)= 1;
T(119790:13978,7)= 1;
T(13979:15978,8)= 1;
T(15979:17978,9)= 1;
T(17979:19978,10)= 1;
%save project2_data.mat M

nbias = ones(150,1);
n0 = load('features_test\0.txt');      %2000
n0 = horzcat(nbias,n0);
n1 = load('features_test\1.txt');      %1979
n1 = horzcat(nbias,n1);
n2 = load('features_test\2.txt');      %1999
n2 = horzcat(nbias,n2);
n3 = load('features_test\3.txt');      %2000
n3 = horzcat(nbias,n3);
n4 = load('features_test\4.txt');      %2000
n4 = horzcat(nbias,n4);
n5 = load('features_test\5.txt');      %2000
n5 = horzcat(nbias,n5);
n6 = load('features_test\6.txt');      %2000
n6 = horzcat(nbias,n6);
n7 = load('features_test\7.txt');      %2000
n7 = horzcat(nbias,n7);
n8 = load('features_test\8.txt');      %2000
n8 = horzcat(nbias,n8);
n9 = load('features_test\9.txt');      %2000
n9 = horzcat(nbias,n9);
X_test = vertcat(n0,n1,n2,n3,n4,n5,n6,n7,n8,n9);


[W,error_lr, error_plot] = train_lr(X,T);
fprintf('Error in Training phase in LR: %f \n',error_lr); 
fprintf('********Starting Testing********\n');
[Index] = test_lr(W,X_test);
fprintf('Saving labels\n');
save Index.mat Index;
fprintf('Labels Saved\n\n');

fprintf('Starting Neural Networks\n');
[w1,w2,error_nn,nn_plot] = train_nn(X,T);
fprintf('Error in Training phase in NN: %f \n',error_nn);
[Index_NN] = test_nn(w1, w2, X_test);
fprintf('Saving labels\n');
save Index_NN.mat Index_NN;
fprintf('Labels Saved\n');