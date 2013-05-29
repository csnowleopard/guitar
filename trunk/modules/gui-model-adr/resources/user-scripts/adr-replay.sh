#!/bin/bash

#------------------------
# Sample command line arguments
SCRIPT_DIR=`dirname $0`
aut_classpath=.

#aut_directory=TippyTipper
#aut_package=net.mandaria.tippytipper
#aut_main=activities.TippyTipper

aut_directory=$1
aut_package=$2
aut_main=$3
aut=$aut_package.$aut_main
port=10737

args=""

# configuration for the application
# you can specify widgets to ignore during ripping
# and terminal widgets
# configuration="$aut_dir/guitar-config/configuration.xml"

# intial waiting time
# change this if your application need more time to start
intial_wait=3000

# the length of test suite
tc_length=2

# delay time between two events during replaying  
# this number is generally smaller than the $ripper_delay
replayer_delay=1000

#------------------------
# Output artifacts 
#------------------------

# Directory to store all output of the workflow 
output_dir="data/$aut_directory"

# GUI structure file
gui_file="$output_dir/$aut_directory.GUI"

# EFG file 
efg_file="$output_dir/$aut_directory.EFG"

# Log file for the ripper 
# You can examine this file to get the widget 
# signature to ignore during ripping 
log_file="$output_dir/$aut_directory.log"

# Test case directory  
testcases_dir="$output_dir/testcases"

# GUI states directory  
states_dir="$output_dir/states"

# Replaying log directory 
logs_dir="$output_dir/logs"

# Screenshot directory
screenshots_dir="$output_dir/screenshots"

rip_minute=`awk '/Elapsed:/ { print $7 }' $log_file | sed 's/^0*//'`
rip_second=`awk '/Elapsed:/ { print $9 }' $log_file | sed -n 's/:/ /p' | sed 's/^0*//'`

let rip_second=rip_minute*60+rip_second
let rip_second=rip_second*1000*3

# Generating test cases
echo ""
echo "About to generate test cases to cover all possible $tc_length-way event interactions" 
#read -p "Press ENTER to continue..."
cmd="$SCRIPT_DIR/tc-gen-sq.sh -e $efg_file -l $tc_length -m 0 -d $testcases_dir"
echo $cmd
eval $cmd 

testcase_num=`find $testcases_dir/*.tst | wc -l`

rm -rf `find adr-aut/$aut_directory -name '*.res'`
rm -rf `find adr-aut/$aut_directory -name '*.ec'`
rm -rf `find adr-aut/$aut_directory -name '*.log'`

# Replaying generated test cases
echo ""
echo "About to replay test case(s)" 
echo "Enter the number of test case(s): $testcase_num"

for testcase in `find $testcases_dir -name "*.tst"| head -n$testcase_num`
do
    # getting test name 
    test_name=`basename $testcase`
    test_name=${test_name%.*}

    rm -rf $logs_dir/$test_name.log

    cmd="$SCRIPT_DIR/adr-replayer.sh -cp $aut_classpath -c $aut -pt $port -g $gui_file -e $efg_file -t $testcase -i $intial_wait -d $replayer_delay -to $rip_second -so $rip_second -l $logs_dir/$test_name.log -gs $states_dir/$test_name.sta"

    # adding application arguments if needed 
    if [ ! -z $args ] 
    then 
        cmd="$cmd -a \"$args\" " 
    fi

    if [ -f adr-aut/$aut_directory/bin/no_fault/$test_name.res ]; then
        echo "This testcase was already run so we will skip"
    else
        echo $cmd 
        eval $cmd

        while [ ! -f adr-aut/$aut_directory/bin/no_fault/$test_name.ec ]
        do
            adb pull /data/data/$aut_package/files/coverage.ec adr-aut/$aut_directory/bin/no_fault/$test_name.ec
            sleep 2
        done

        emma_dir=`which android`
        emma_dir=${emma_dir%/*}
        emma_dir=${emma_dir}/lib/emma.jar
        java -cp $emma_dir emma report -r xml -in adr-aut/$aut_directory/bin/no_fault/coverage.em,adr-aut/$aut_directory/bin/no_fault/$test_name.ec
        java -cp $emma_dir emma report -r html -in adr-aut/$aut_directory/bin/no_fault/coverage.em,adr-aut/$aut_directory/bin/no_fault/$test_name.ec
        mv coverage $test_name
        rm -rf adr-aut/$aut_directory/bin/no_fault/$test_name
        mv -f $test_name -t adr-aut/$aut_directory/bin/no_fault/
        mv coverage.xml adr-aut/$aut_directory/bin/no_fault/$test_name/
        cp ${logs_dir}/${test_name}.log adr-aut/$aut_directory/bin/no_fault/
        cp $states_dir/$test_name.sta adr-aut/$aut_directory/bin/no_fault/
    fi
done

python cv_rpt.py adr-aut/$aut_directory/bin/no_fault
python ft_rpt.py adr-aut/$aut_directory/bin/result
cp -rf *.html adr-aut/$aut_directory
