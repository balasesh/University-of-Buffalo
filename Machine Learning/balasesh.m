yourubitname = 'balasesh';
yournumber = 50097470;
fprintf('My ubit name is %s\n',yourubitname);
fprintf('My student number is %d\n',yournumber);

% Question 1
m = [5 4 0 9; 6 3 7 12; 8 3 7 4];
n = [5 4 0; 6 3 7; 8 3 7; 3 1 8];
Sum_M = 0;
for i = 1:3
    for j = 1:4
        Sum_M = Sum_M + m(i,j);
    end
end
Sum_N = 0;
for i = 1:4
    for j = 1:3
        Sum_N = Sum_N + n(i,j);
    end
end
p = m*n;
Sum_Product_MN = 0;
for i = 1:3
    for j = 1:3
        Sum_Product_MN = Sum_Product_MN + p(i,j);
    end
end
e = n';
for i = 1:3
    for j = 1:4
        q(i,j) =  m(i,j)*e(i,j);
    end
end
Sum_Element_MN = 0;
for i = 1:3
    for j = 1:4
        Sum_Element_MN = Sum_Element_MN + q(i,j);
    end
end
r = max(m,[],1);
Largest_M = max(r,[],2);
lambda = eig(p);
Eig_ProductMN = 0;
for i = 1:3
    for j = 1
        Eig_ProductMN = Eig_ProductMN + lambda(i,j);
    end
end
fprintf('Sum_M is %d\n', Sum_M);
fprintf('Sum_N is %d\n', Sum_N);
fprintf('Sum_Product_MN is %d\n', Sum_Product_MN);
fprintf('Sum_Element_MN is %d\n', Sum_Element_MN);
fprintf('Largest_M is %d\n', Largest_M);
fprintf('Eig_ProductMN is %3.2f\n', Eig_ProductMN);

% Question 2
c1 = [1.7 1.6 1.4 1.2 1.8 2 0.9 1.6 1.5 1.6];
Sum_C1 = 0;
for i = 1
    for j = 1:10
        Sum_C1 = Sum_C1 + c1(i,j);
    end
end
Mu_1 = Sum_C1/10;
c2 = [3.2 3.7 3.5 4.3 2.9 3.6 3.3 4.1 2.2 3.6];
Sum_C2 = 0;
for i = 1
    for j = 1:10
        Sum_C2 = Sum_C2 + c2(i,j);
    end
end
Mu_2 = Sum_C2/10;
%Calculate Std Deviation
xyz = 0;
for i = 1
    for j = 1:10
        xyz = xyz + (c1(i,j)-Mu_1)^2;
    end
end
Sigma_1 = xyz/9;
Sigma_11 = sqrt(xyz/9);

abc = 0;
for i = 1
    for j = 1:10
        abc = abc + (c2(i,j)-Mu_2)^2;
    end
end
Sigma_2 = abc/9;
Sigma_22 = sqrt(abc/9);

%Calculating Covariance
pqr = 0;
for i = 1
    for j = 1:10
        pqr = pqr + ((c1(i,j)-Mu_1)*(c2(i,j)-Mu_2));
    end
end
Covariance = pqr/9;


fprintf('Mu_1 is %3.2f\n', Mu_1);
fprintf('Mu_2 is %3.2f\n', Mu_2);
fprintf('Sigma_1 is %3.4f\n', Sigma_1);
fprintf('Sigma_2 is %3.4f\n', Sigma_2);
fprintf('Covariance is %3.4f\n', Covariance);

for i = 1
for j = 1:10 
pointsc1(i,j) = Mu_1 +(Sigma_1*(randn(1)));
pointsc2(i,j) = Mu_2 +(Sigma_2*(randn(1)));
end
end
plot(pointsc1,'Marker','X');
hold ('on');
plot(pointsc2,'Marker','O');
