export JAVA=java
export LIB_DIR=lib//

export CLASSPATH="bin:lib/args-1.0.jar:lib/java_uno.jar:lib/juh.jar:lib/jurt.jar:lib/ridl.jar:lib/sandbox.jar:lib/unoil.jar:lib/unoloader.jar"

echo $CLASSPATH

export ROOT_WIN_TITLE="Untitled 1 - OpenOffice.org Writer"
export IGNORED_DIR="data/ignore/"

TESTPATH="java -cp $CLASSPATH edu.umd.cs.guitar.ripper.RipperMain --root-window $ROOT_WIN_TITLE --port 5678 -i $IGNORED_DIR"

echo $TESTPATH

java -cp $CLASSPATH edu.umd.cs.guitar.ripper.RipperMain --root-window $ROOT_WIN_TITLE --port 5678 -i $IGNORED_DIR
