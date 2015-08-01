#!/util/bin/perl
my $ubit_name = 'balasesh';

my $is_name, $is_num, $is_Sum_M, $is_Sum_N, $is_Sum_Product_MN, $is_Sum_Element_MN, $is_Largest_M, $is_Eig_ProductMN, $is_Mu_1, $is_Mu_2, $is_Sigma_1, $is_Sigma_2, $is_Covariance;
open RESU,"matlab -nojvm -nodisplay -nosplash -r \"$ubit_name;quit\" 2>&1 |" or die "$!\n"; # change to your own ubitname here
while(<RESU>){
    if ($_=~/My\s+ubit\s+name\s+is\s+(.*)/){
	$name = $1;
	$is_name = 1;
	print "Name is collected\n";
    }
    if ($_=~/My\s+student\s+number\s+is\s+(\d+)/){
	$num = $1;
	$is_num = 1;
	print "Student number is collected\n";
    }
    if ($_=~/Sum_M\s+is\s+(\d+)/){
        $Sum_M = $1;
	$is_Sum_M = 1;
	print "Sum_M is collected\n";
    }
    if ($_=~/Sum_N\s+is\s+(\d+)/){
        $Sum_N = $1;
	$is_Sum_N = 1;
	print "Sum_N is collected\n";
    }
    if ($_=~/Sum_Product_MN\s+is\s+(\d+)/){
        $Sum_Product_MN = $1;
	$is_Sum_Product_MN = 1;
	print "Sum_Product_MN is collected\n";
    }
    if ($_=~/Sum_Element_MN\s+is\s+(\d+)/){
        $Sum_Element_MN = $1;
	$is_Sum_Element_MN = 1;
	print "Sum_Element_MN is collected\n";
    }
    if ($_=~/Largest_M\s+is\s+(\d+)/){
        $Largest_M = $1;
	$is_Largest_M = 1;
	print "Largest_M is collected\n";
    }
    if ($_=~/Eig_ProductMN\s+is\s+(\d+\.\d+)/){
        $Eig_ProductMN = $1;
	$is_Eig_ProductMN = 1;
	print "Eig_ProductMN is collected\n";
    }
    if ($_=~/Mu_1\s+is\s+(\d+\.\d+)/){
        $Mu_1 = $1;
	$is_Mu_1 = 1;
	print "Mu_1 is collected\n";
    }
    if ($_=~/Mu_2\s+is\s+(\d+\.\d+)/){
        $Mu_2 = $1;
	$is_Mu_2 = 1;
	print "Mu_2 is collected\n";
    }
    if ($_=~/Sigma_1\s+is\s+(\d+\.\d+)/){
        $Sigma_1 = $1;
	$is_Sigma_1 = 1;
	print "Sigma_1 is collected\n";	
    }
    if ($_=~/Sigma_2\s+is\s+(\d+\.\d+)/){
        $Sigma_2 = $1;
	$is_Sigma_2 = 1;
	print "Sigma_2 is collected\n";
    }
    if ($_=~/Covariance\s+is\s+(-*\d+\.\d+)/){
        $Covariance = $1;
	$is_Covariance = 1;
	print "Covariance is collected\n";
    }
}
close RESU;
if ($is_name && $is_num && $is_Sum_M && $is_Sum_N && $is_Sum_Product_MN && $is_Sum_Element_MN && $is_Largest_M && $is_Eig_ProductMN && $is_Mu_1 && $is_Mu_2 && $is_Sigma_1 && $is_Sigma_2 &&  $is_Covariance){
    print "Success!!! All data are collected!!!\n"    
} else {
    print "Failed!!! Please check the previous printed messages\n";
}
