### For the CMSC 714 project: IphoneGuitar
### Purpose: Fault Seeding
### Input - SourceCode in APP; Output-FaultMatrix
### Part 2 is usually the place that needs to be modified for more fault patterns.

use warnings;
use strict;

### Make Sure Currently It Is in The Target APP Directory.
### Print any input arguments
my @command;
my $cmd="";
my $currentApp;
foreach(@ARGV){
	if($_ eq $ARGV[0]){
		$currentApp = $_;	
		next;
	}
	if(/xcodebuild/){
		$cmd =~ s/\s+$//;
		push @command, $cmd;
		$cmd="";
	}
	$cmd.=$_." " if($_ ne $ARGV[-1]);	
}
my $processid = "ps aux | grep 'i[pP]hone' | grep -v 'perl' | grep -v 'iph\-fault\-seeding' | awk '{print \$2}'";
my $replayid = "ps aux | grep 'replay.sh' | grep -v 'perl' | grep -v 'iph\-fault\-seeding' | awk '{print \$2}'";
my $kill_pss = "ps aux | grep i[pP]hone | grep -v 'perl' | grep -v 'iph\-fault\-seeding' | awk '{print \$2}' | xargs -n 1 -I {} kill -9 {} &> /dev/null";

$cmd =~ s/\s+$//;
push @command, $cmd;

### Number of Tests Required.
#my $numberTest = $ARGV[-1]; 

push @command, $ARGV[-1];
if(@command != 3){
#	foreach(@command){
#		print;
#	}
	print "[ERROR]COMMANDS ARE NOT ENOUGH\n";
	exit;
}
### You Need To CHANGE THIS IF ON MAC.
### LONG DELAY IS TO MAKE SURE THE IPHONE SERVER IS UP
my $delayTimer = $command[2]; 	

### Open GUI source code
unless(-d "./Classes/BackupFiles"){
	`mkdir ./Classes/BackupFiles`;
}

print "Step 1/3: Please Specify Your Target Sourcecode File Name in Directory ./Classes for Fault Seeding (e.g. HelloWorldViewController.m):";

my $ffName = <STDIN>;

chomp($ffName); #"HelloWorldViewController.m"; 
my $fileName = "./Classes/$ffName";
unless(-f $fileName){
	print "\nYour Target Souce Code Is Not Found In ./Class Directory!\nPlease Recheck Your File Location\n";
	exit;
}
print "\n";
my $fileBack = "./Classes/BackupFiles/$ffName.oringinal.backup";
`cp $fileName  $fileBack`;

open CODE, "< $fileName";
my $flag=0;
### Save code in an array
my @oringinalCode = <CODE>;
my @newCodeWithoutComments;
close CODE;

### Part 1: This part of code get rid of all comments in the oringinal source code
foreach(@oringinalCode){
	chomp;
	my $newStr="";
	my @tokens = split(/\s+/,$_);
	my $lineflag=0;
	foreach my $token (@tokens){
		if($token =~ /(\S*)\/\//){
			$newStr.=$1." ";
			$lineflag =1;
			next;
		}elsif($token =~ /\/\*/){
			$flag =1;			
		}
		
		if($token =~ /\*\//){
			$flag =0;
		}elsif($flag ==0 and $lineflag ==0){
			$newStr.= $token." ";
		}
	}
	push @newCodeWithoutComments,$newStr;		
}

### Part 2: Look for possible places to make a change
### Several patterns recognized, more can be added
my %faultDescription;
my %change_index;
my @finalCodeWithChanges;
my $index=0;
my $maxiIndex=0;
my $numInjections=0;
for(my $i =0;$i<@newCodeWithoutComments;$i++){
	my $line = $newCodeWithoutComments[$i];
	if($newCodeWithoutComments[$i] eq ""){
		next;
	}	
	##index=1 - == -> !=
	$index++;
	$faultDescription{$index} = "FAULT Type $index:[==->!=]" if(not exists $faultDescription{$index});
	if($line =~ /("[^"]*?")*==/){
		$change_index{$index}.=$i." ";
		$numInjections++;
	}
	##index=2 - x=10 -> x=random number
	$index++;
	$faultDescription{$index} = "FAULT Type $index:[x=number->x=randomNumber]" if(not exists $faultDescription{$index});
	if($line =~ /\s*=\s*\d+/){	
		$change_index{$index}.=$i." ";
		$numInjections++;
	}	
	##index=3 - if( && ) -> if( || )
	$index++;  
	$faultDescription{$index} = "FAULT Type $index:[if(&&)->if(||)]" if(not exists $faultDescription{$index});
	if($line =~ /if\s*\(.*?\&\&.*?\)/){
		$change_index{$index}.=$i." ";
		$numInjections++;
	}
	##index=4 - if( || ) -> if( && )
	$index++;
	$faultDescription{$index} = "FAULT Type $index:[if(||)->if(&&)]" if(not exists $faultDescription{$index});
	if($line =~ /if\s*\(.*?\|\|.*?\)/){
		$change_index{$index}.=$i." ";
		$numInjections++;
	}
	##index=5 - true|false swap
	$index++;
	$faultDescription{$index} = "FAULT Type $index:[true->false]" if(not exists $faultDescription{$index});
	if($line =~ /true/){
		$change_index{$index}.=$i." ";
		$numInjections++;
	}
	##index=6 - false|true swap
	$index++;
	$faultDescription{$index} = "FAULT Type $index:[false->true]" if(not exists $faultDescription{$index});
	if($line =~ /false/){
		$change_index{$index}.=$i." ";
		$numInjections++;
	}
	##index=7 - Load A Null Pointer
	$index++;
	$faultDescription{$index} = "FAULT Type $index:[Null]" if(not exists $faultDescription{$index});
	if($line =~ /selectBlueButton:\(id\)sender \{/){
		$change_index{$index}.=$i." ";
		$numInjections++;
	}
	##index=8 - if() -> if(!)
	$index++;
	$faultDescription{$index} = "FAULT Type $index:[if()->if(!)]" if(not exists $faultDescription{$index});
	if($line =~ /if\s*\([^=!]*?\)/){
		$change_index{$index}.=$i." ";
		$numInjections++;
	}
	##index=9 - Negative Integer
	$index++;
	$faultDescription{$index} = "FAULT Type $index:[LineSizeRandom]" if(not exists $faultDescription{$index});
	if($line =~ /,\s*(\d+)\s*,/){
		$change_index{$index}.=$i." ";
		$numInjections++;
	}
	##index=10 - Line Integer
	$index++;
	$faultDescription{$index} = "FAULT Type $index:[IntegerSwap]" if(not exists $faultDescription{$index});
	if($line =~ /,\s*(\d+)\s*,\s*(\d+)\s*,/){
		$change_index{$index}.=$i." ";
		$numInjections++;
	}
	##MODIFY: MORE PATTERNS HERE IF NEEDED, Follow the examples above
	
	
	##MODIFY: ONLY ADD THINGS AT ABOVE LOCATIONS
	$maxiIndex = $index;
	$index=0;
}

### Select Fault
print "Step 2/3: Please Select Fault Types to Be Seeded Into the Source Code.\n";
print "Faults Available in the Code Have Been Identified, Type Number 1-10 to Choose. Input Format Shoule Be Seperated by Space, e.g. 1 2 3 4\n";
print "If All Faults Are Needed, Simply Leave It Blank, Push the Enter Key and Continue.\n";
foreach(sort keys %faultDescription){
	if(exists $change_index{$_}){
		print $faultDescription{$_}."\n";	
	}
}
print "Your Selection:";
my $usrInput = "";

$usrInput = <STDIN>;
chomp($usrInput);
print "\n";
my %example;
my $allFlag = "true";
if($usrInput =~ /\d/){
	my @tokens = split(/\s+/, $usrInput); 
	foreach(@tokens){
		if($_<1 or $_ >=11){
			next;
		}
		$example{$_}=-1;
	}
	$allFlag = "false";
}

#print "New Fault %example \n";

### Part 3: First RUN. Initilization
### Initial Run to COPY EVERYTHING to TEMP directory.
unless(-d "./temp"){
	`mkdir temp`;
}

my @statesfiles = `ls ./Demo/states`;
if(@statesfiles >= 1 and $statesfiles[0] =~ /sta/){
	`cp -r ./Demo/states/*.sta temp/`;
}else{
	print "\nMake Sure The States Files From Initial Runnning Are Available in ./Demo/states/ directory\n";
	print "Fault Seeding Program Will Stop For Now\n";
	print "Please ReRun Your Initial Guitar Program\n";
	exit;
}

`rm -rf ./Demo/states/*`;
### Wait for the Copy Operation Finishes
`sleep 7`;
my @testCases = `ls ./Demo/replayer`;
foreach(@testCases){
	chomp;
}
my @states = `ls ./Demo/states`;
foreach(@states){
	chomp;
}

### Select TestCase
print "Step 3/3: Please Select Test Cases to Be Executed.\n";
print "Type Number to Choose the Test Cases Below. Input Format Shoule Be Seperated by Space, e.g. 1 2 3 4\n";
print "If All Test Cases Are Needed, Simply Leave It Blank, Push the Enter Key and Continue.\n";
my $testIn = 1;
foreach(@testCases){	
	print $testIn." ".$_."\n";
	$testIn++;	
}
print "Your Selection:";
my $usrInput1 = <STDIN>;
print "\n";
chomp($usrInput1);
my @newtestCases;
if($usrInput1 =~ /\d/){
	my @tokens = split(/\s+/, $usrInput1); 
	foreach(@tokens){
		if(not exists $testCases[$_-1]){
			next;
		}
		push @newtestCases, $testCases[$_-1];
	}	
}else{
	@newtestCases = @testCases;
}

#print "New CAses: @newtestCases\n";
### Part 4: Make the change in the code and run the program
### Based on previous generated possible places, changes are going to be made.
### A single change everytime, get the GUI file, compare it and draw FaultMatrix.
my @result_faultseeding;
push @result_faultseeding, "Fault Seeding File Name: $ffName";
my $firstHead = "TESTCASE\t>\t";
foreach(@newtestCases){
	$firstHead.=$_."\t";
}
push @result_faultseeding, $firstHead;

my @backup = @newCodeWithoutComments;
my $iteration=0;
print "Generating Fault Matrix ...\n";
for(my $i=1; $i <= $maxiIndex; $i++){
	if(exists $change_index{$i}){
		if($allFlag eq "false" and not exists $example{$i}){
			next;
		}
		
		my @lines = split(/\s+/,$change_index{$i});
		foreach my $lineNo (@lines){
			$iteration++;
			if($i == 1){
				$newCodeWithoutComments[$lineNo] =~ s/(("[^"]*?")*)==/$1!=/;
			}elsif($i == 2){
				my $random = rand(100);
				$newCodeWithoutComments[$lineNo] =~ s/(\s*=\s*)\d+/$1$random/;
			}elsif($i == 3){
				$newCodeWithoutComments[$lineNo] =~ s/(if\s*\(.*?)\&\&(.*?\))/$1||$2/;
			}elsif($i == 4){
				$newCodeWithoutComments[$lineNo] =~ s/(if\s*\(.*?)\|\|(.*?\))/$1&&$2/;
			}elsif($i == 5){
				$newCodeWithoutComments[$lineNo] =~ s/true/false/;
			}elsif($i == 6){
				$newCodeWithoutComments[$lineNo] =~ s/false/true/;
			}elsif($i == 7){ ##
				$newCodeWithoutComments[$lineNo] =~ s/^(.*);\s*$/$1;UIView *otherview;[otherview loadView];/;
			}elsif($i == 8){ ##
				$newCodeWithoutComments[$lineNo] =~ s/(if\s*\()([^=!]*?\))/$1!$2/;
			}elsif($i == 9){ ##				
				$newCodeWithoutComments[$lineNo] =~ s/,\s*(\d+)\s*,/,-$1,/;
			}elsif($i == 10){ ##
				$newCodeWithoutComments[$lineNo] =~ s/,\s*(\d+)\s*,\s*(\d+)\s*,/,$2,$1,/;
			}
			else{
				print "You know, Unrecognized pattern error. Modify the code correspondingly\n";
				exit;
			}
			#print "Fault Injection Test Case No. $iteration - On Line:$lineNo at Fault Index:$i\n";
			open CHANGE, "> $fileName";
			foreach(@newCodeWithoutComments){
				print CHANGE $_."\n";
			}
			close CHANGE;
			
			$fileBack = "./Classes/BackupFiles/$ffName.LINE.$lineNo.FAULT.$i.backup";
			`cp $fileName $fileBack`;
			
			`rm -f ./Demo/states/*`;
			#`rm -rf ./Demo/replayer/*`;
			push @result_faultseeding, $faultDescription{$i}." Line:$lineNo\t>>\t".&execServer();
			#print "[RESULT]".$result_faultseeding[-1]."\n";
			@newCodeWithoutComments = @backup;	
			#my $nameD = "./Demo/states/$i$lineNo";
			#`mkdir $nameD`;
			#`mv ./Demo/states/*.sat $nameD`;
		}
	}
#	else{
#		push @result_faultseeding, $faultDescription{$i}."\t>\tNA";
#		next;
#	}	
}

### Restore everything after the code change/testing/comparison steps.
open RECOVER, "> $fileName";
foreach(@oringinalCode){
	print RECOVER $_."\n";
}
close RECOVER;

### Generate Final Results
#print "Fault Seeding File Generated.\n";
unless(-d "../../Demo/"){
	`mkdir ../../Demo`; 
}
open OUTPUT, "> ../../Demo/faultSeedingResults.txt";
foreach(@result_faultseeding){
	print OUTPUT $_."\n";
}
close OUTPUT;

print "Fault Matrix Construction Complete.  Results in file: ./Demo/faultSeedingResults.txt\n";

`rm -rf Demo/states/*`;
`cp -r temp/*.sta Demo/states/`;
`rm -rf temp/*`;

### Part 5: Start the iPhone Server by using input argument and kill this server.
### This subroutine will be called by the main routine repeatedly to test GUI difference.
sub execServer(){
	
	my @results;
	foreach(@newtestCases){
		my $res = "";
		$res = `ls ./temp/ | grep $_`;
		if($res eq ""){
			push @results, "FAILED";
			next;			
		}
		
		my $replayer = $command[0];
		$replayer =~ s/ToBeReplaced/$_/g;
		$command[1] =~ s/"/\\"/g;

		##############NEW
		`../../singlepass.sh $currentApp $_.tst &> /dev/null`;
		`sleep 10`;
		##############
		#open NEWIN, "> replayer-temp.sh";
		#print NEWIN "#!/bin/bash\ncmd=\"$replayer\"\neval \$cmd\nsleep 2\n";
		#print NEWIN "cmd=\"$command[1]\"\neval \$cmd\nsleep $delayTimer\n";
		#print NEWIN "cmd=\"$kill_pss\"\neval \$cmd\nsleep $delayTimer\n";
		#close NEWIN;
		#`chmod +x replayer-temp.sh`;
		
		#`./replayer-temp.sh`;
		#`rm -rf replayer-temp.sh`;
		#`$replayer`;
		#`$command[1]`;
		#`sleep $delayTimer`;		
		#print "Starting iPhone...\n";
		#print "$command[1]\n";
		###You Need To CHANGE THIS IF ON MAC.
		###LONG DELAY IS TO MAKE SURE THE IPHONE SERVER IS UP
		
		
		my $id = `$processid`;
		my $id2 = `$replayid`;
		#print "Killing iPhone Server...\n";
		my @ids = split(/\s+|\n/, $id);
		my @idss = split(/\s+|\n/, $id2);
		kill 9, @ids;
		kill 9, @idss;
		#`sleep $delayTimer`;
		
		if(-e "./Demo/states/$_.sta" and -e "./temp/$_.sta"){
			open RES, "diff ./Demo/states/$_.sta ./temp/$_.sta |";			
			my $res = "";
			$res = <RES>;
			
			if(!defined($res) or $res eq ""){
				push @results, "PASSED";	
			}else{
				push @results, "FAILED";
			}	
			close RES;	
		}else{
			#print "No state files are generated.\n";
			push @results, "FAILED";
		}
		
	}

	my $output="";
	foreach(@results){
		$output .=$_." ";
	}
	return $output;
}

