#!/bin/bash

# Script for running testcases on multiple initial states and fault seeded versions with UNOGuitar
# Author : Wikum Dinalankara
# Date : 2011-12-03

source set_variables.sh

STARTFILES=*.odt
CORRECT_OOEXEC=/media/Data/coveragesrc/OOO330_m20
#NUM_TESTCASES=5
WAIT_FOR_START=40
WAIT_FOR_KILL=30
#CORRECT_FOLDER=/media/Data/nofaultversion
MIN_NUM=1

#Download GUITAR using svn
#svn co https://guitar.svn.sourceforge.net/svnroot/guitar/trunk guitar
#cd guitar
#ant uno.dist
#cd dist
#cd guitar
#chmod +x uno-aut/*.sh
#chmod +x *.sh

#Fault seeded versions are in this folder
#FOLDERS=/media/Data/faultversions/*

#SRC_ROOT=/media/Data/srctest/OOO330_m20

#Current folder 
SCRIPT_DIR=`dirname $0`

#Application directory 
aut_dir=$SCRIPT_DIR

#Classpath 
for file in `find $SCRIPT_DIR/jars/ -name "*.jar"`
do
    classpath=${file}:${classpath}
done
classpath=$SCRIPT_DIR:$classpath

#Root_window
root_window_main=" - OpenOffice.org Writer"

#Sample command line arguments 
args=""
jvm_options=""

#Configuration for the application
configuration="$aut_dir/configuration.xml"
data_ignore="$aut_dir/data/ignore/"

#Initial waiting time
#Change this if your application needs more time to start
intial_wait=10

#Delay time between two events during ripping 
ripper_delay=30

#The length of test suite
tc_length=2

#Delay time between two events during replaying  
#This number is generally smaller than the $ripper_delay
relayer_delay=10

#Directory to store all output of the workflow 
output_dir="./OO_out"

#GUI structure file
gui_file="$output_dir/writer.GUI"

#EFG file 
efg_file="$output_dir/writer.EFG"

#Log file for the ripper 
#You can examine this file to get the widget signature to ignore during ripping 
log_file="$output_dir/writer.log"

#Test case directory  
testcases_dir="$output_dir/testcases"

#GUI states directory  
states_dir="$output_dir/states"

#Replaying log directory 
logs_dir="$output_dir/logs"

#Remove any existing results.txt file
rm -f vals.txt

#Remove any existing coverage.txt file
rm -f percentages.txt

#echo "Running testcases on correct version"

#rm -rf selected_testcases
#mkdir selected_testcases

echo "Seeding faults"

CURRENT=$(pwd)

#Copy run.sh
cp run.sh $SRC_ROOT

#Save JAVA_HOME and PATH for later use
jhome=$JAVA_HOME
path=$PATH

#Loop over each fault seeded version
for f in $FOLDERS 
do
	#Copy correct sw/source files to SRC_ROOT/sw/, so we begin with a clean slate
	cp_cmd="cp -apr $CORRECT_FOLDER/source $SRC_ROOT/sw/"
	eval $cp_cmd

    	#Copy the fault seeded sw module over it
	cp_cmd="cp -r $f/source $SRC_ROOT/sw/"
	eval $cp_cmd

	#Go to SRC_ROOT
	cd $SRC_ROOT

	#Build the fault seeded version
	echo "Building from " $(pwd)
    	#source LinuxX86Env.Set.sh
	source run.sh

	#Check for success
	if [ $? -gt 0 ] 
	then
		#Error - continue with next f
		echo "Error : Build failed"
		continue
	else
		echo "Built successfully"
	fi
	
	#Install
	echo "Installing..."

	#Note: we should be in SRC_ROOT/sw folder now - change to instsetoo_native
	cd ../instsetoo_native

    	#Remove any existing installations
	rm -rf ./instsetoo_native/unxlngi6.pro/OpenOffice/installed

    	#Install using dmake
	cd util
	dmake openoffice_en-US PKGFORMAT=installed

	#Check for success
	if [ $? -gt 0 ]
	then
		#Error - continue with next f
		echo "Error : Installation failed"
		continue
	else
		echo "Installed successfully"
	fi

	#The executables are in SRC_ROOT/instsetoo_native/unxlngi6.pro/OpenOffice/installed/install/en-US/openoffice.org3/program/
	EXEC_FOLDER=$SRC_ROOT/instsetoo_native/unxlngi6.pro/OpenOffice/installed/install/en-US/openoffice.org3/program
	echo "Executables are in " $EXEC_FOLDER
	
	#Go back to our original folder from which we started the script
	cd $CURRENT

    	#Restore JAVA_HOME and PATH
	export JAVA_HOME=$jhome
	export PATH=$path

    	#Ready to run testcases on fault-seeded version
	echo "Running testcases from " $CURRENT
	
    	#RUNNING indicates whether OpenOffice is currently running or not
	RUNNING=0

	for startf in $STARTFILES
	do
		root_window=${startf}${root_window_main}

		#Start office
		${EXEC_FOLDER}/soffice -writer -nofirststartwizard -norestore "-accept=socket,host=localhost,port=5678;urp;" $startf &
		#Check for success
		if [ $? -eq 0 ]
		then
			echo "Started successfully"
	
			#Give it some time to start
	        	sleep $WAIT_FOR_START
		else
			echo "Cannot start office"
	    	fi

		#Rip
		# Preparing output directories
		rm -f $output_dir		
		mkdir -p $output_dir
		mkdir -p $testcases_dir
		mkdir -p $states_dir
		mkdir -p $logs_dir
		#cp -r uno-aut/data .
		# Ripping
		echo ""
		echo "About to rip the application " 
		#read -p "Press ENTER to continue..."
		cmd="$SCRIPT_DIR/uno-ripper.sh -cp $classpath -cf $configuration --root-window $root_window --port 5678 -i $data_ignore"

		# Adding application arguments if needed 
		if [ ! -z $args ] 
		then 
			cmd="$cmd -a \"$args\"" 
		fi
		#echo $cmd
		eval $cmd

		cp $SCRIPT_DIR/GUITAR-Default.GUI $SCRIPT_DIR/OO_out/writer.GUI
		# Converting GUI structure to EFG
		echo ""
		echo "About to convert GUI structure file to Event Flow Graph (EFG) file" 
		#read -p "Press ENTER to continue..."
		cmd="$SCRIPT_DIR/gui2efg.sh $gui_file $efg_file"
		#echo $cmd
		eval $cmd

		# Generating test cases
		echo ""
		echo "About to generate test cases to cover all possible $tc_length-way event interactions" 
		#read -p "Press ENTER to continue..."
		cmd="$SCRIPT_DIR/tc-gen-sq.sh -e $efg_file -l $tc_length -m 0 -d $testcases_dir"
		#echo $cmd
		eval $cmd 

		#Kill office
		kill -9 $(pgrep soffice)

		#Wait for a bit
		sleep $WAIT_FOR_KILL

		loop_ctr=0
	    	#Loop over testcases
		for testcase in `find $testcases_dir -name "*.tst"| head -n$NUM_TESTCASES`
		do
			loop_ctr=$((loop_ctr+1))
	
			if [ $loop_ctr -lt $MIN_NUM ]
			then
				continue
			fi
		
			echo $loop_ctr
	
	    		#Detect if writer is running
	    		RUNNING=$(pgrep soffice | wc -l)
	
	    		#If swriter is running, kill it
	    		if [ $RUNNING -gt 0 ] 
	    		then
				echo "Stopping openoffice "
				wmctrl -c "Untitled 1 - OpenOffice.org Writer"
	
				sleep 4
	
				RUNNING=$(pgrep soffice | wc -l)
				if [ $RUNNING -gt 0 ] 
	    			then
					kill -9 $(pgrep soffice)
	
					#Wait for a bit
					sleep $WAIT_FOR_KILL
				fi
			fi

			#Delete existing gcda files
			find $SRC_ROOT -name '*.gcda' | xargs rm -f

			#Start open office
			if [ $startf = "file0.odt" ]
			then
				${EXEC_FOLDER}/soffice -writer -nofirststartwizard -norestore "-accept=socket,host=localhost,port=5678;urp;" &
			else
	        		${EXEC_FOLDER}/soffice -writer -nofirststartwizard -norestore "-accept=socket,host=localhost,port=5678;urp;" $startf &
			fi

	        	#Check for success
			if [ $? -eq 0 ]
			then
				echo "Started successfully"
	
	        	        #Give it some time to start
	        	        sleep $WAIT_FOR_START
			else
				echo "Cannot start office"
	    		fi

			#Run testcase and redirect output to results.txt
			test_name=`basename $testcase`
			test_name=${test_name%.*}

			cmd="$SCRIPT_DIR/uno-replayer.sh -cp $classpath -g $gui_file -e $efg_file -t $testcase -i $intial_wait -d $relayer_delay -l $logs_dir/$test_name.log -gs $states_dir/$test_name.sta -cf $configuration -cap" 

			#Add application arguments if needed 
			if [ ! -z $args ] 
			then 
				cmd="$cmd -a \"$args\" " 
			fi	

		        #Run testcase
			echo "Running testcase"
			eval $cmd > test_out.txt

			#wmctrl -c "Untitled 1 - OpenOffice.org Writer"
	
			sleep 6
	
			RUNNING=$(pgrep soffice | wc -l)
			if [ $RUNNING -gt 0 ] 
	    		then
				kill -9 $(pgrep soffice)
	
				#Wait for a bit
				sleep $WAIT_FOR_KILL
			fi
	
			# Check for "can't connect to office" error	
	        	connected=$(grep "can't connect to office" test_out.txt | wc -l)
			if [ $connected -gt 0 ]
			then
				echo "Could not connect to office"
			fi

			# check for success of testcase and store result for fault matrix
			if [ $(grep "Could not execute testcase" test_out.txt | wc -l) -gt 0 ]
			then
				success=3
			elif [ $(grep "Component is disabled" test_out.txt | wc -l) -gt 0 ]
			then
				success=2
			elif [ $(grep "Could not find component" test_out.txt | wc -l) -gt 0 ]
			then
				success=1
			else
				success=0
			fi
			echo "Testcase done"
			echo -ne $success >> vals.txt
			echo -ne " " >> vals.txt

		        #Process gcno files
        		rm -f oo_base.info
        		lcov -c -i -d $SRC_ROOT -o oo_base.info

        		#Process gcda files
        		rm -f oo_test.info
        		lcov -c -d $SRC_ROOT -o oo_test.info

        		#Get coverage information
        		rm -f oo_total.info
        		lcov -a oo_base.info -a oo_test.info -o oo_total.info > lcov_out.txt

        		#Get line coverage value and append it to coverage.txt
        		./get_coverage.py >> percentages.txt
			
		done
		echo "" >> vals.txt
		echo " " >> percentages.txt
	done

    	#Add newline to results.txt
	echo "" >> vals.txt

    	#Add empty line to coverage.txt
    	echo "" >> percentages.txt

done

#All fault-seeded versions done

#Process results.txt to create fault-matrix
./get_fault_matrix.py > fault_matrix.txt
./get_coverage_matrix.py > coverage_matrix.txt


