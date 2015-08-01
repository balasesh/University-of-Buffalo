How to run?
Please run only from project1.m, as this is the source of all te funtions. Please make sure the data file Querylevelnorm.txt is available in the same directory. There is no necessity of any inputs to be given in the execution.

Please find below the parameters for the functions.


Parameters:
project1.m - Just calling the train_cfs(M) - Where M is th main data matrix.

train_cfs.m - Take M data matrix as the input and sends " WML,phimatsize,Val_Lambda,Val_ERMS,M ". Here WML is the Weight matrix to be sent to test file, the number of columns in the design matrix, the final ERMS value and the Lambda values.

test_cfs.m - WML,phimatsize,Val_Lambda,M are the inputs which are the weight matrix lambda, the number of columns in the design matrix.

train_g.m - M, Phi_Mat,Val_Lambda. M is the data matrix, the lambda value and the design matrix from the train_cfs.m, matrix to be sent to test file. The design matrix size, the final ERMS value and the Lambda values.

test_gd.m - ERMS_Min, W_Min,M These are the ERMS minimum value in the training set., The minimum Weight matrix and the original matrix.