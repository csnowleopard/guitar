##

if [[ "$1" = "" || "$2" = "" ]]; then
	echo "Usage: ./swt-recorder.sh aut_main_class_name aut_classpath"
	echo "Note: The aut_main_class_name should be the fully-qualified name of your main class (e.g. my.package.MainClass)."
else

	recorder_classpath="../../../guitar/dist/guitar/jars/guitar-lib/cr-swt.jar:./jars/log4j-1.2.15.jar:./jars/guitar-lib/gui-model-core.jar:./jars/guitar-lib/gui-model-swt.jar:./jars/guitar-lib/gui-ripper-core.jar:./jars/guitar-lib/gui-ripper-swt.jar:./jars/swt-3.7.1-linux-32.jar"
aut_main_class=$1	
aut_classpath=$2

	java_cmd="java -cp ${aut_classpath}:${recorder_classpath} recorder.RecorderControlPanel ${aut_main_class}";

	echo $java_cmd
	eval $java_cmd
fi

