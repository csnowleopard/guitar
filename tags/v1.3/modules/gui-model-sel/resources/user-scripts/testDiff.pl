#!/usr/bin/perl

if($#ARGV < 3)
{
	print "Usage : perl testDiff.pl <dir1> <filePrefix><fileSuffix> <numFault>\n";
	print "Example Run : perl testDiff.pl ./STA GUITAR-Default STA 5\n";
	exit;
}

$dirPath1 = $ARGV[0];
$dirPath2 = $ARGV[1]."/";
$filePrefix = $ARGV[1];
$fileSuffix = $ARGV[2];
$numFault = $ARGV[3];

$l = `ls -la $dirPath1/*.$fileSuffix* | wc`;
$l =~ /\s+(\d+)/;
$fileCount = $1;

@arr = ();
$numTest = $fileCount ;

for($j=1;$j<$numFault+1;$j++){
for($i=1;$i<$fileCount+1;$i++)
{
	$fault = 0 ;
	$file1 = $dirPath1."/".$filePrefix.".".$fileSuffix.$i;
	$file2 = $dirPath1.$j."/".$filePrefix.".".$fileSuffix.$i;
	$command = "cat $file1 | grep -v Value > tmp1 && cat $file2 | grep -v Value > tmp2 && diff tmp1 tmp2 && rm tmp1 && rm tmp2";
	#$command = "diff $file1 $file2";

	#print $command."\n";
	
print $command."\n";

	$op = `$command | wc`;
	$op =~ /\s+(\d+)/;
	$line = $1;
	if($1>0)
	{
		$fault = 1 ;
	}

	$arr[$i-1][$j-1] = $fault;
	
}
}

open FM, ">fault.matrix";

for($i=0;$i<$numTest;$i++)
{
	for($j=0;$j<$numFault;$j++)
	{
		print FM $arr[$i][$j]." ";
	}
	print FM "\n";
}

#print $fileCount."\n";

