#!/bin/bash

current_dir=`pwd`
aut=$1
aut_dir=$current_dir/iph-aut/$aut
output_dir="$aut_dir/Demo"
replayer_dir="$output_dir/replayer"
testcases_dir="$output_dir/testcases"
#cov_output_name="codeCoverage.out"

#echo "Enter 0 for statement coverage or 1 for branch coverage"
#read cov_mode
testcase_count=`ls $replayer_dir | wc -l | sed -e 's/^[ \t]*//'`
echo "The following are the replayed test cases..."

testcases=( `find $replayer_dir -name "t*" | tr '\n' ' '`)
for (( i=0; i<testcase_count; i++ ))
do
    let id=$i+1
    testcase=`basename ${testcases[$i]}`
    echo "$id: $testcase"
done

echo -e "\nWhich testcase(s) do you want to display the code coverage matrix for?"
echo "(Input format should be separated by space, e.g. 1 3 4)"
read -a chosen

echo -e "\nAbout to generate the code coverage matrix"
read -p "Press ENTER to continue..."

for (( i=0; i < ${#chosen[@]}; i++ ))
do
#    test_name=`basename $testcase`
    let index=${chosen[$i]}-1
    test_name=`basename ${testcases[$index]}`
    test_name=${test_name%.*}
    cov_output_name="$current_dir/$test_name.tmp"
    echo "For testcase '$test_name':"
    
    dir="$replayer_dir/$test_name/gcov"
    cd $dir
    rm -f $cov_output_name
    echo $test_name >> $cov_output_name

    #for file in `find . -name "*.gcno"`
    for file in `find -f $replayer_dir/$test_name | grep gcno`
    do
	file_name=`basename $file .gcno`
	echo $file_name >> $cov_output_name
	echo "Processing file: $file_name.m"
	#gcov -n $file | tail -n3 >> $cov_output_name
#	if [ $cov_mode -eq 0 ]; then
	    str=`gcov -n $file | tail -n2 | head -n1 | sed s/Lines\ executed:// | sed 's/\(%\).*/\1/'`
	    printf "%s | " "${str}" >> $cov_output_name
	    # checking whether there's any branch or not
   	    tmp=`gcov -b $file | tail -n4 | head -n1`
	    case $tmp in
		N*) exist=0 ;;
		*) exist=1 ;;
	    esac
	    # if there's no braches at all, then print out "N/A"
	    if [ $exist -eq 1 ]; then
   		tmp=`gcov -b $file | tail -n5 | head -n1`
		echo $tmp | sed s/Branches\ executed:// | sed 's/\(%\).*/\1/' >> $cov_output_name
	    else
		echo "N/A" >> $cov_output_name
	    fi
#	fi
    done
    
    echo -e "Done.\n"
    cd $current_dir
done

# Running the matrix generator in Python
python ../../modules/gui-model-iph/resources/user-scripts/codeCovMatrix.py > ./codeCovMatrix.txt

#rm -f *.gcov
rm -f *.tmp
echo "For code coverage matrix, check file: $current_dir/codeCovMatrix.txt"
