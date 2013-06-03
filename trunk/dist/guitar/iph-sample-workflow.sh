#!/bin/bash

# This is a sample script to demonstrate 
# GUITAR general workflow 
# The output can be found in Demo directory  

#------------------------
# Running in script dir 
SCRIPT_DIR=`pwd`

#------------------------
# iphone project name
iph_project=$1

# application directory 
aut_dir=$SCRIPT_DIR/iph-aut/$iph_project

# application classpath 
aut_classpath=$aut_dir/bin

# num test cases to run
testcase_num=$4

if [ -z $testcase_num ]; then
    testcase_num=5
fi

# server port number
port=8081

# application main class
mainclass=$iph_project

#------------------------
# Sample command line arguments 
args=""
jvm_options=""

# configuration for the application
# you can specify widgets to ignore during ripping 
# and terminal widgets 
configuration="$aut_dir/guitar-config/configuration.xml"

# xcode startup and compile time.
xcode_build_time=$2
xcode_replay_time=$3

if [ -z $xcode_build_time ]; then
    xcode_build_time=10
fi
if [ -z $xcode_replay_time ]; then
    xcode_replay_time=10
fi

# intial waiting time
# change this if your application need more time to start
initial_wait=100

# delay time between two events during ripping 
ripper_delay=100

# the length of test suite
tc_length=2

# delay time between two events during replaying  
# this number is generally smaller than the $ripper_delay
replayer_delay=1000

# delay time between two steps during replaying
replayer_so=10000

#------------------------
# Output artifacts 
#------------------------

# Directory to store all output of the workflow 
output_dir="$aut_dir/Demo"

# Directory to store screenshots
screenshots_dir="$output_dir/screenshots"

# GUI structure file
gui_file="$output_dir/Demo.GUI"

# EFG file 
efg_file="$output_dir/Demo.EFG"

# Log file for the ripper 
# You can examine this file to get the widget 
# signature to ignore during ripping 
log_file="$output_dir/Demo.log"

# Test case directory  
testcases_dir="$output_dir/testcases"

# GUI states directory  
states_dir="$output_dir/states"

# Replaying log directory 
logs_dir="$output_dir/logs"

# Iph Replayer/Code Coverage directory
replayer_dir="$output_dir/replayer"

# Command to run iphonesim
path=`pwd`
#run_iphonesim="../iphonesim/Build/Release/iphonesim launch /Users/cmhill/tmp/guitar/example-aut/iph-aut/${iph_project}/build/Debug-iphonesimulator/${iph_project}.app &> /dev/null"
run_iphonesim="../ios-sim/Build/Release/ios-sim launch $aut_dir/build/Debug-iphonesimulator/${iph_project}.app -exit &> /dev/null"

#------------------------
# Main workflow 
#------------------------

# Preparing output directories
rm -rf ./Demo &> /dev/null
mkdir -p $output_dir
mkdir -p $testcases_dir
mkdir -p $states_dir
mkdir -p $logs_dir
mkdir -p $replayer_dir
mkdir -p $screenshots_dir

# Build iphonesim first
echo "Building iPhone Simulator."
cmd="xcodebuild -project iph-aut/ios-sim/ios-sim.xcodeproj -configuration \"Release\" -target \"ios-sim\" > /dev/null"
eval $cmd

# Ripping
echo ""
echo "About to rip the application " 
#read -p "Press ENTER to continue..."
#cmd="$SCRIPT_DIR/jfc-ripper.sh -cp $aut_classpath -c $mainclass -g  $gui_file -cf $configuration -d $ripper_delay -i $intial_wait -l $log_file"
# Moved -sp localhost
echo "Running ripper."
cmd="$SCRIPT_DIR/iph-ripper.sh -cp $aut_classpath -p $port -g  $gui_file -cf $configuration -d $ripper_delay -i $initial_wait -l $log_file &> ripper.out &"

# Adding application arguments if needed 
if [ ! -z $args ] 
then 
	cmd="$cmd -a \"$args\"" 
fi
# echo $cmd
eval $cmd

# Execute the iphone program.
sleep 1
cd $aut_dir
echo "Building iphone client."
cmd="xcodebuild -configuration \"Debug\" -target \"TestScriptRunner\" -sdk iphonesimulator5.1 &> iph_ripper.out &"
#echo $cmd
eval $cmd
echo "Waiting $xcode_build_time seconds for xcode to build."
#echo

sleep $xcode_build_time

#cmd="$SCRIPT_DIR/iphonesim/Build/Release/iphonesim launch /Users/cmhill/tmp/guitar/example-aut/iph-aut/${iph_project}/build/Debug-iphonesimulator/${iph_project}.app &> /dev/null"

echo "Starting up iphone sdk, please wait $xcode_build_time seconds."
#echo $run_iphonesim

sleep $xcode_build_time
eval $run_iphonesim
kill_iph_skd="ps aux | grep i[pP]hone | awk '{print \$2}' | xargs -n 1 -I {} kill -9 {} &> /dev/null"
echo "Cleaning up iphone sdk/client."
#echo $kill_iph_skd
eval $kill_iph_skd

# Clear code coverage files.
rm build/$iph_project.build/Debug-iphonesimulator/TestScriptRunner.build/Objects-normal/i386/*

# Converting GUI structure to EFG
#echo ""
#echo "About to convert GUI structure file to Event Flow Graph (EFG) file" 
#read -p "Press ENTER to continue..."
cmd="$SCRIPT_DIR/gui2efg.sh -g $gui_file -e $efg_file"
echo $cmd
eval $cmd

# Generating test cases
echo ""
echo "About to generate test cases to cover all possible $tc_length-way event interactions" 
#read -p "Press ENTER to continue..."
cmd="$SCRIPT_DIR/tc-gen-sq.sh -e $efg_file -l $tc_length -m 0 -d $testcases_dir"
echo $cmd
eval $cmd 	
# Replaying generated test cases
echo ""
echo "About to replay test case(s)" 
echo "Enter the number of test case(s): "
#read testcase_num
i=0 
for testcase in `find $testcases_dir -name "*.tst"| head -n$testcase_num`  
    do
    # getting test name 
   	test_name=`basename $testcase`
   	test_name=${test_name%.*}
   
    # Removed -c
    
   	cmd="$SCRIPT_DIR/iph-replayer.sh -cp $aut_classpath -g $gui_file -e $efg_file -t $testcase -i $initial_wait -d $replayer_delay -so $replayer_so -l $logs_dir/$test_name.log -gs $states_dir/$test_name.sta -cf $SCRIPT_DIR/configuration.xml &> replayer.out &"
   	# adding application arguments if needed 
   	if [ ! -z $args ] 
   	then 
   		cmd="$cmd -a \"$args\" " 
   	fi
	echo "Replaying test: $test_name"
   	# echo $cmd 
   	eval $cmd

	# Execute the iphone program.
	sleep 2
	echo "Building iphone client."
	cmd="xcodebuild -configuration \"Debug\" -target \"TestScriptRunner\" -sdk iphonesimulator5.1 &> iph_replayer.out &"
	# echo $cmd
	eval $cmd
	echo "Waiting $xcode_replay_time seconds for xcode to build."
	sleep $xcode_replay_time
	eval $kill_iph_skd
	# cmd="$SCRIPT_DIR/iphonesim/Build/Release/iphonesim launch /Users/cmhill/tmp/guitar/example-aut/iph-aut/${iph_project}/build/Debug-iphonesimulator/${iph_project}.app &> /dev/null"
	# echo $run_iphonesim
	echo "Starting up iphone sdk, please wait $xcode_replay_time seconds."
	sleep $xcode_replay_time
	eval $run_iphonesim
	echo "Cleaning up iphone sdk/client."
	#echo $kill_iph_skd
	eval $kill_iph_skd

	grep "FAILED" iph_replayer.out
	grep "ERROR General Exception thrown" replayer.out
	grep "TERMINATED" replayer.out
	echo
	
	# Move all the output files to the test directory
	mkdir -p $replayer_dir/$test_name/gcov
	mv iph_replayer.out $replayer_dir/$test_name/
	mv errorLogFromLastBuild.txt $replayer_dir/$test_name/
	mv replayer.out $replayer_dir/$test_name/
	mv build/$iph_project.build/Debug-iphonesimulator/TestScriptRunner.build/Objects-normal/i386/*  $replayer_dir/$test_name/gcov/ 
	
	i=$((i+1))
done

echo "For ripper details, check file: ./ripper.out"
echo "For replayer details, check file: ./Demo/replayer/[TESTCASE]/replayer.out"
echo "For iphone std output, check file: ./Demo/replayer/[TESTCASE]/errorLogFromLastBuild.txt"
echo "For code coverage, check file: ./Demo/replayer/[TESTCASE]/gcov/"
echo "Replace [TESTCASE] with the name of the desired testcase from the commands above. Testcases are named t_e*_e*, for example: t_e1060101468_e1473899700."
