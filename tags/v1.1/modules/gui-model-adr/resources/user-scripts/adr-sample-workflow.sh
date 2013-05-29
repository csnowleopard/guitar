#!/bin/bash

# This is a sample script to demonstrate 
# GUITAR general workflow 
# The output can be found in Demo directory  

#------------------------
# application directory 
# aut_dir=$SCRIPT_DIR/jfc-aut/RadioButton/

# application classpath 
# aut_classpath=$aut_dir/bin

# application main class
# mainclass="Project"

# Change the following 2 lines for the classpath and the main class of your 
# application. The example is for CrosswordSage, another real world example
# in the jfc-aut directory (http://crosswordsage.sourceforge.net/)

#aut_classpath=$SCRIPT_DIR/jfc-aut/CrosswordSage/bin:$SCRIPT_DIR/jfc-aut/CrosswordSage/bin/CrosswordSage.jar
#mainclass="crosswordsage.MainScreen"

#------------------------
# Sample command line arguments 
SCRIPT_DIR=`dirname $0`
aut_classpath=.

aut=com.aut.HelloAUTActivity
port=10737

args=""

# configuration for the application
# you can specify widgets to ignore during ripping 
# and terminal widgets 
# configuration="$aut_dir/guitar-config/configuration.xml"

# intial waiting time
# change this if your application need more time to start
intial_wait=3000

# delay time between two events during ripping 
ripper_delay=500

# the length of test suite
tc_length=2

# delay time between two events during replaying  
# this number is generally smaller than the $ripper_delay
replayer_delay=1000

#------------------------
# Output artifacts 
#------------------------

# Directory to store all output of the workflow 
output_dir="./Demo"

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

#------------------------
# Main workflow 
#------------------------

# turn off posix mode
set +o posix

# Preparing output directories
mkdir -p $output_dir
mkdir -p $testcases_dir
mkdir -p $states_dir
mkdir -p $logs_dir

# Ripping
found=$(which android)
if [ $found ]; then
    echo -e "==> You already set up the Android SDK youself so we will not try to install.\n"
fi

if [ $found ]; then
    echo -e "==> Android SDK Exists. We skip the installation.\n"
else
    if [ -f android-sdk-linux_x86/tools/android ]; then
        echo -e "==> Android SDK detected.\n"
    else
        echo -e "==> Download Android SDK"
        wget http://dl.google.com/android/android-sdk_r13-linux_x86.tgz

        echo -e "==> Uncompress Android SDK"
        tar xvfz android-sdk_r13-linux_x86.tgz
    fi
    tools_path="`pwd`/android-sdk-linux_x86/tools:`pwd`/android-sdk-linux_x86/platform-tools"
    original_PATH=${PATH}
    export PATH=${PATH}:${tools_path}
    android update sdk --no-ui -t platform-tool
    android update sdk --no-ui -t tool
    android update sdk --no-ui -t platform
fi

echo -e "==> Kill the emulator process if running.\n"
pkill emulator-arm
killall emulator-arm

echo "==> Delete the AVD if its name is ADRGuitarTest."
android delete avd -n ADRGuitarTest
echo -e "\n"

echo "==> Create an AVD. Its name will be ADRGuitarTest."
id=100
if [[ `android list targets | grep "android-8"` =~ [0-9]+ ]]; then
    id=${BASH_REMATCH[0]}
fi

if [[ id -ne 100 ]]; then
    echo | android create avd -n ADRGuitarTest -t $id -s WQVGA432
else
    echo "    Android 2.2 is not installed."
    echo "    Quit ADRGuitar."
    exit
fi
echo -e "\n"

# Run an emulator process
echo -e "==> Run the created emulator.\n"
emulator -avd ADRGuitarTest -cpu-delay 0 -netfast -no-snapshot-save &

# Install ADR-Server
echo "==> Install ADR-Server."
cont=true
while $cont ;
do
    while read line
    do
        found=$(echo $line | grep Success)
        if [ "$found" ]; then
            cont=false
        fi
#    done < <( adb install adr-server/adr-server.apk )
    done < <( adb install adr-aut/adr-server.apk )

    if $cont ; then
        echo "  The emulator is booting."
        echo "  We will retry."
        adb kill-server
        adb start-server
    fi
    sleep 10
done
echo -e "\n"

# Install AUT
echo "==> Install AUT."
cont=true
while $cont ;
do
    while read line
    do
        found=$(echo $line | grep Success)
        if [ "$found" ]; then
            cont=false
        fi
    done < <( adb install adr-aut/HelloAUT.resigned.apk )

    if $cont ; then
        echo "    The emulator is booting."
        echo "    We will retry."
    fi
    sleep 10
done
echo -e "\n"

echo "About to rip the application " 
read -p "Press ENTER to continue..."
cmd="$SCRIPT_DIR/adr-ripper.sh -cp $aut_classpath -c $aut -pt $port -g $gui_file -l $log_file"

# Adding application arguments if needed 
if [ ! -z $args ] 
then 
    cmd="$cmd -a \"$args\"" 
fi
echo $cmd
eval $cmd

# Converting GUI structure to EFG
echo ""
echo "About to convert GUI structure file to Event Flow Graph (EFG) file" 
read -p "Press ENTER to continue..."
cmd="$SCRIPT_DIR/gui2efg.sh $gui_file $efg_file"
echo $cmd
eval $cmd

# Generating test cases
echo ""
echo "About to generate test cases to cover all possible $tc_length-way event interactions" 
read -p "Press ENTER to continue..."
cmd="$SCRIPT_DIR/tc-gen-sq.sh -e $efg_file -l $tc_length -m 0 -d $testcases_dir"
echo $cmd
eval $cmd 

# Replaying generated test cases
echo ""
echo "About to replay test case(s)" 
echo "Enter the number of test case(s): "
read testcase_num

for testcase in `find $testcases_dir -name "*.tst"| head -n$testcase_num`  
do
    # getting test name 
    test_name=`basename $testcase`
    test_name=${test_name%.*}

    cmd="$SCRIPT_DIR/adr-replayer.sh -cp $aut_classpath -c $aut -pt $port -g $gui_file -e $efg_file -t $testcase -i $intial_wait -d $replayer_delay -l $logs_dir/$test_name.log -gs $states_dir/$test_name.sta"

    # adding application arguments if needed 
    if [ ! -z $args ] 
    then 
        cmd="$cmd -a \"$args\" " 
    fi    
    echo $cmd 
    eval $cmd
done

pkill emulator-arm
killall emulator-arm
android delete avd -n ADRGuitarTest
export PATH=${original_PATH}
