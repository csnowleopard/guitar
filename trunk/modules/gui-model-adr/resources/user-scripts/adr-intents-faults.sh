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
    if [ -f $HOME/android-sdk-linux/tools/android ]; then
        echo -e "==> Android SDK detected.\n"
    else
        echo -e "==> Download Android SDK"
        downloader=$(which wget)
        if [ $downloader ]; then
            wget http://dl.google.com/android/android-sdk_r15-linux.tgz
        else
            curl http://dl.google.com/android/android-sdk_r15-linux.tgz > android-sdk_r15-linux.tgz
        fi

        cp android-sdk_r15-linux.tgz $HOME
        rm android-sdk_r15-linux.tgz
        cd $HOME

        echo -e "==> Uncompress Android SDK"
        tar xvfz android-sdk_r15-linux.tgz
    fi
    tools_path="$HOME/android-sdk-linux/tools:$HOME/android-sdk-linux/platform-tools"
    original_PATH=${PATH}
    export PATH=${PATH}:${tools_path}
    android update sdk --no-ui -t tool
    android update sdk --no-ui -t platform-tool
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
    #echo | android create avd -n ADRGuitarTest -t $id -s WQVGA432
    echo | android create avd -n ADRGuitarTest -t $id -s WQVGA432
else
    echo "    Android 2.2 is not installed."
    echo "    Quit ADRGuitar."
    exit
fi
echo -e "\n"

# ADR-Server Building
echo -e "==> Build ADR-Server"
cd adr-aut
rm -f adr-server.apk
cd adr-server

android update project --name adr-server --target android-8 --path .
python rename.py AndroidManifest.xml $aut_package
ant debug
../resign.sh ./bin/adr-server-debug.apk ../adr-server.apk
cd ../../

# AUT Building
echo -e "==> Build AUT"
cd adr-aut/$aut_directory

rm build.xml
android update project --target android-8 -p .
cp -rf src src.orig
if [ ! -f ./bin/no_fault/aut-resigned.apk ] || [ ! -f ./bin/no_fault/coverage.em ]; then
    ant instrument
    ../resign.sh ./bin/*-instrumented.apk ./bin/aut-resigned.apk
    mkdir -p ./bin/no_fault
    cp ./bin/aut-resigned.apk ./bin/no_fault
    cp coverage.em ./bin/no_fault
fi
while read line
do
    filename=${line##*/}
    pathname=${line%/*}
    original_path=`find src -name $filename`
    if [ ! -f ./bin/$pathname/aut-resigned.apk ] || [ ! -f ./bin/$pathname/coverage.em ]; then
        echo "==> Fault seeded source file: $line"
        cp $line $original_path
        ant instrument
        ../resign.sh ./bin/*-instrumented.apk ./bin/aut-resigned.apk
        mkdir -p ./bin/$pathname
        cp ./bin/aut-resigned.apk ./bin/$pathname
        cp coverage.em ./bin/$pathname
    fi
done < <( find $4 -name *.java )
rm -rf src
mv src.orig src
rm -rf bin/*.apk
cd ../../

# Run an emulator process
echo -e "==> Run the created emulator.\n"
#xvfb-run -a 
emulator -avd ADRGuitarTest -cpu-delay 0 -netfast $5 -no-snapshot-save &

touch debug.txt
adb logcat > debug.txt &

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
	adb devices
        adb start-server
    fi
    sleep 10
done
echo -e "\n"

touch debug.txt
adb logcat > debug.txt &

apk_file=`find adr-aut/$aut_directory -name *.apk | sed -n '/no_fault/p'`

# Install AUT
echo "==> Install AUT: $apk_file"
cont=true
while $cont ;
do
    while read line
    do
        found=$(echo $line | grep Success)
        if [ "$found" ]; then
            cont=false
        fi
    done < <( adb install $apk_file )

    if $cont ; then
        echo "    The emulator is booting."
	adb devices
        echo "    We will retry."
    fi
    sleep 10
done
echo -e "\n"

touch debug.txt
adb logcat > debug.txt &
echo "About to rip the application " 
#read -p "Press ENTER to continue..."
cmd="$SCRIPT_DIR/adr-ripper.sh -cp $aut_classpath -c $aut -pt $port -g $gui_file -l $log_file"

# Adding application arguments if needed 
if [ ! -z $args ] 
then 
    cmd="$cmd -a \"$args\"" 
fi
echo $cmd
eval $cmd
rip_minute=`awk '$7=="Elapsed:" { print $10 }' $log_file | sed 's/^0*//'`
rip_second=`awk '$7=="Elapsed:" { print $12}' $log_file | sed -n 's/:/ /p' | sed 's/^0*//'`

let rip_second=rip_minute*60+rip_second
let rip_second=rip_second*1000*3

# Converting GUI structure to EFG
echo ""
echo "About to convert GUI structure file to Event Flow Graph (EFG) file" 
#read -p "Press ENTER to continue..."
cmd="$SCRIPT_DIR/gui2efg.sh -g $gui_file -e $efg_file"
echo $cmd
eval $cmd

# Generating test cases
echo ""
echo "About to generate test cases to cover all possible $tc_length-way event interactions" 
#read -p "Press ENTER to continue..."
rm -rf $testcases_dir
cmd="$SCRIPT_DIR/tc-gen-sq.sh -e $efg_file -l $tc_length -m 0 -d $testcases_dir"
echo $cmd
eval $cmd 

testcase_num=`find Demo/testcases/*.tst | wc -l`
testcase_num=2
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

	 #create new name based on the test being done
	    ls
	    echo "REACHED!!!!!!!!!!!!!!!!!!!"
	    [[ -d logs ]] && echo "bla" || mkdir logs
	    dumpName="dump"
	    txt=".txt"
	    newName=logs/${dumpName}_${test_name}${txt}
	    [[ -f "$newName" ]] && rm "$newName" || echo "making new file"
	    touch $newName
	    adb logcat -v time > $newName &

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

	##########################################################################################
	    #adding parser here, after the test on the emulator has been
	    #successfully run
	    # added ARGV[1] to parser (application name)
	    app_name=""
	    if [ "$aut_directory" = "CountdownTimer" ]; then
		app_name="Countdown"
	    elif [ "$aut_directory" = "HelloAUT" ]; then
		app_name="com.aut"
	    else
	    	app_name="$aut_directory"
	    fi

	    ruby parser.rb $newName $app_name	#added $app_name
	    #ruby parser.rb $newName

	    #filteredLog
	    filtered=filteredLog_${test_name}.txt
	    touch $filtered
	    egrep -i "intent|edu.umd.cs.guitar|\/$app_name" $newName > $filtered
	    
	    #rename output to masterLog + test_name
	    newName=logs/masterLog_${test_name}.txt
	    cp masterLog.txt $newName
	    rm masterLog.txt
	    #cp $newName ../../../$newName
    fi
done

while read line
do
    pathname=${line%/*}
    tested=1

    # Check this apk was already tested
    for testcase in `find $testcases_dir -name "*.tst"| head -n$testcase_num`  
    do
        # getting test name 
        test_name=`basename $testcase`
        test_name=${test_name%.*}

        if [ ! -f $pathname/$test_name.res ]; then
            tested=0
            break
       	fi
    done

    [ $tested -eq 1 ] && continue

    # Install AUT
    echo "==> Install AUT: $line"
    cont=true
    while $cont ;
    do
 adb devices
        adb uninstall $aut_package
        while read line
        do
            found=$(echo $line | grep Success)
            if [ "$found" ]; then
                cont=false
            fi
        done < <( adb install $line )

        if $cont ; then
            echo "    The emulator is booting."
            echo "    We will retry."
        fi
        sleep 10
    done
    echo -e "\n"

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

        if [ -f $pathname/$test_name.res ]; then
            echo "This testcase was already run so we will skip"
        else
	     #create new name based on the test being done
	    ls
	    echo "REACHED!!!!!!!!!!!!!!!!!!!"
	    [[ -d logs ]] && echo "bla" || mkdir logs
	    dumpName="dump"
	    txt=".txt"
	    newName=logs/${dumpName}_${test_name}faults${txt}
	    [[ -f "$newName" ]] && rm "$newName" || echo "file already exists, deleting file"
	    touch $newName
	    adb logcat -v time > $newName &

            echo $cmd 
            eval $cmd

            cp $states_dir/$test_name.sta $pathname/
	    rm -rf $states_dir/$test_name.sta
            fault_found=`cat ${logs_dir}/${test_name}.log | grep "NORMALLY TERMINATED" | sed -e 's;^.* ;;'`
	    fault_found2=`diff  $pathname/$test_name.sta adr-aut/$aut_directory/bin/no_fault/$test_name.sta`
            if [[ $fault_found ]] && [[ ! $fault_found2 ]]; then
                echo success > $pathname/$test_name.res

                while [ ! -f $pathname/$test_name.ec ]
                do
                    adb pull /data/data/$aut_package/files/coverage.ec $pathname/$test_name.ec
                    sleep 2
                done

                emma_dir=`which android`
                emma_dir=${emma_dir%/*}
                emma_dir=${emma_dir}/lib/emma.jar
                java -cp $emma_dir emma report -r xml -in $pathname/coverage.em,$pathname/$test_name.ec
                java -cp $emma_dir emma report -r html -in $pathname/coverage.em,$pathname/$test_name.ec
                mv coverage $test_name
                rm -rf $pathname/$test_name
                mv -f $test_name -t $pathname/
                mv coverage.xml $pathname/$test_name/
                cp ${logs_dir}/${test_name}.log $pathname/
            else
               echo fail > $pathname/$test_name.res
            fi
	    ##########################################################################################
	    #adding parser here, after the test on the emulator has been
	    #successfully run
	    
	    # added ARGV[1] to parser (application name)
	    app_name=""
	    if [ "$aut_directory" = "CountdownTimer" ]; then
		app_name="Countdown"
	    elif [ "$aut_directory" = "HelloAUT" ]; then
		app_name="com.aut"
	    else
	    	app_name="$aut_directory"
	    fi
	    echo "app_name = ${app_name}"

	    ruby parser.rb $newName $app_name	#added $app_name
	    #ruby parser.rb $newName

	    #filteredLog
	    filtered=filteredLog_${test_name}faults.txt
	    touch $filtered
	    egrep -i "intent|edu.umd.cs.guitar|\/$app_name" $newName > $filtered

	    #rename output to masterLog + test_name
	    newName=logs/masterLog_${test_name}faults.txt	#added "faults" to the file name
	    cp masterLog.txt $newName
	    rm masterLog.txt
        fi
    done
done < <( find adr-aut/$aut_directory/bin/result -name *.apk | sed '/original/d' )

python cv_rpt.py adr-aut/$aut_directory/bin/no_fault
python ft_rpt.py adr-aut/$aut_directory/bin/result
cp -rf *.html adr-aut/$aut_directory

pkill emulator-arm
killall emulator-arm
android delete avd -n ADRGuitarTest
export PATH=${original_PATH}

#ruby menu.rb #add other stuff later
