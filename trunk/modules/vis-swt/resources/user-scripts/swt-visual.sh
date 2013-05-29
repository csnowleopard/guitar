##

if [[ "$1" = "" || "$2" = "" ]]; then
	echo ""
	echo "For visualization: ./swt-visual.sh <path/fileName.EFG> <path/fileName.GUI>"
	echo ""	
	echo "For visualization with test case: ./swt-visual.sh <path/fileName.EFG> <path/fileName.GUI> <path/fileName.tst>"
	echo ""
else

  
	if ["$3" = ""]; then
		java -jar ./jars/guitar-lib/vis-swt.jar $1 $2
	else
		cp $3 GUITAR-Default.tst
		java -jar ./jars/guitar-lib/vis-swt.jar $1 $2 $3
	fi
fi
