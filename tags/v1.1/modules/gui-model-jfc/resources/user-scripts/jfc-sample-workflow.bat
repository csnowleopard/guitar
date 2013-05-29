::  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
::  be obtained by sending an e-mail to atif@cs.umd.edu
:: 
::  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
::  documentation files (the "Software"), to deal in the Software without restriction, including without 
::  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and\or sell copies of
::  the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
::  conditions:
:: 
::  The above copyright notice and this permission notice shall be included in all copies or substantial 
::  portions of the Software.
::
::  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
::  LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
::  EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
::  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
::  THE USE OR OTHER DEALINGS IN THE SOFTWARE. 

:: This is a sample script to demonstrate 
:: GUITAR general workflow 
:: The output can be found in Demo directory  

@ECHO OFF
rem SETs the basedir to the directory where this batch file is locaed

::------------------------
:: Running in script dir 
::SET SCRIPT_DIR=%~dp0
SET SCRIPT_DIR=.

::------------------------
:: application directory 
SET aut_dir=%SCRIPT_DIR%\jfc-aut\RadioButton

:: application classpath 
SET aut_classpath=%aut_dir%\bin

:: application main class
SET  mainclass=Project

:: Change the following 2 lines for the classpath and the main class of your 
:: application. The example is for CrosswordSage, another real world example
:: in the jfc-aut directory (http:\\crosswordsage.sourceforge.net\)

::aut_classpath=$SCRIPT_DIR\jfc-aut\CrosswordSage\bin:$SCRIPT_DIR\jfc-aut\CrosswordSage\bin\CrosswordSage.jar
::mainclass="crosswordsage.MainScreen"

::------------------------
:: Sample command line arguments 
SET args=""
SET jvm_options=""

:: configuration for the application
:: you can specify widgets to ignore during ripping 
:: and terminal widgets 
SET configuration=%aut_dir%\guitar-config\configuration.xml

:: intial waiting time
:: change this if your application need more time to start
SET intial_wait=2000

:: delay time between two events during ripping 
SET ripper_delay=500

:: the length of test suite
SET tc_length=3

:: the number of test cases generated
:: change this number to 0 to generate all possible test cases
SET testcase_num=10

:: delay time between two events during replaying  
:: this number is generally smaller than the $ripper_delay
SET relayer_delay=200

::------------------------
:: Output artifacts 
::------------------------

:: Directory to store all output of the workflow 
SET output_dir=%SCRIPT_DIR%\Demo

:: GUI structure file
SET gui_file=%output_dir%\Demo.GUI

:: EFG file 
SET efg_file=%output_dir%\Demo.EFG

:: Log file for the ripper 
:: You can examine this file to get the widget 
:: signature to ignore during ripping 
SET log_file=%output_dir%\Demo.log

:: Test case directory  
SET testcases_dir=%output_dir%\testcases

:: GUI states directory  
SET states_dir=%output_dir%\states

:: Replaying log directory 
SET logs_dir=%output_dir%\logs

::------------------------
:: Main workflow 
::------------------------

:: Preparing output directories
mkdir "%output_dir%"
mkdir "%testcases_dir%"
mkdir "%states_dir%"
mkdir "%logs_dir%"

echo "This script demonstrates a simple testing workflow with GUITAR"
echo "Refer to the document inside the script on how to customize it" 
 

:: Ripping
echo ----------------------------
ECHO About to rip the application 
pause

SET cmd="%SCRIPT_DIR%"\jfc-ripper.bat -cp "%aut_classpath%" -c %mainclass% -g  "%gui_file%" -cf "%configuration%" -d "%ripper_delay%" 
CALL %cmd%

:: Converting GUI structure to EFG
echo ----------------------------
ECHO About to convert GUI structure file to Event Flow Graph (EFG) file
pause

SET cmd="%SCRIPT_DIR%\gui2efg.bat" "%gui_file%" "%efg_file%"
CALL %cmd%

:: Generating test cases
echo ----------------------------
echo About to generate %testcase_num% test cases to cover  %tc_length%-way event interactions
pause

CALL "%SCRIPT_DIR%\tc-gen-random.bat" -e "%efg_file%" -l %tc_length% -m %testcase_num% -d "%testcases_dir%"

:: Use  tc-gen-sq.bat and change the test case number parameter to 0 
:: to systematically cover all possible  event interactions  
:: CALL "%SCRIPT_DIR%\tc-gen-sq.bat" -e "%efg_file%" -l %tc_length% -m 0 -d "%testcases_dir%"


rem Replaying generated test cases
echo ----------------------------
echo About to replay all generated test case(s)
pause

FOR /R  "%testcases_dir%" %%t IN (*.tst) DO (
	set n=%%~nt
	CALL "%SCRIPT_DIR%\jfc-replayer.bat" -cp "%aut_classpath%" ^
-c  "%mainclass%" -g "%gui_file%" -e "%efg_file%"  -t "%%t" ^
-i %intial_wait% -d %relayer_delay% ^
-l "%logs_dir%\%n%.log" -gs "%states_dir%\%n%.sta" -cf "%configuration%" ^
-ts

)

echo "Output directory:  %output_dir%"

