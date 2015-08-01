yourubitname = 'balasesh';
yournumber = 50097470;
fprintf('My ubit name is %s\n',yourubitname);
fprintf('My student number is %d \n',yournumber);
M = load('Querylevelnorm.txt');
save project1_data.mat M
train_cfs(M);