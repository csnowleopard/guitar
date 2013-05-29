::  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
::  be obtained by sending an e-mail to atif@cs.umd.edu
:: 
::  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
::  documentation files (the "Software"), to deal in the Software without restriction, including without 
::  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
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

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: JFC ripper launching script
::
::	By	baonn@cs.umd.edu
::	Date: 	06/08/2011
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
@ECHO OFF
::SETs the basedir to the directory where this batch file is locaed
SET basedir=%~dp0

:: check the first parameter


IF ""%1""=="""" GOTO ERROR

IF ""%1""==""-?"" GOTO help

IF "%1"=="-cp" GOTO hasClasspath
	SET additional_classpath=.
GOTO doneAdditionalClasspath

:hasClasspath
IF ""%2""=="""" GOTO ERROR
	SET additional_classpath=%~2
	SHIFT
	SHIFT
:doneAdditionalClasspath
	GOTO doneParameter
:help
	echo Usage %0 -cp [application classpath] [GUITAR tool options]
	echo where [GUITAR tool options] are described below:
	GOTO doneParameter
:doneParameter

SET guitarlib=%basedir%jars

:: get guitar classpath
SET guitar_classpath=%basedir%
SETlocal ENABLEDELAYEDEXPANSION
FOR /R  "%guitarlib%" %%G IN (*.jar) DO SET guitar_classpath=!guitar_classpath!;%%G

SET classpath=%additional_classpath%;%guitar_classpath%

SET launcher=edu.umd.cs.guitar.replayer.JFCReplayerMain



:: TODO - Process more than 10 parameters in a smarter way
SET ONE=%1
SET TWO=%2
SET THREE=%3
SET FOUR=%4
SET FIVE=%5
SET SIX=%6
SET SEVEN=%7
SET EIGHT=%8
SET NINE=%9
SHIFT
SHIFT
SHIFT
SHIFT
SHIFT
SHIFT
SHIFT
SHIFT
SHIFT

SET ONE_1=%1
SET TWO_1=%2
SET THREE_1=%3
SET FOUR_1=%4
SET FIVE_1=%5
SET SIX_1=%6
SET SEVEN_1=%7
SET EIGHT_1=%8
SET NINE_1=%9
SHIFT
SHIFT
SHIFT
SHIFT
SHIFT
SHIFT
SHIFT
SHIFT
SHIFT

java -cp "%classpath%" %launcher% %ONE% %TWO% %THREE% %FOUR% %FIVE% %SIX% %SEVEN% %EIGHT% %NINE% ^
%ONE_1% %TWO_1% %THREE_1% %FOUR_1% %FIVE_1% %SIX_1% %SEVEN_1% %EIGHT_1% %NINE_1% ^
%1 %2 %3 %4 %5 %6 %7 %8 %9


GOTO EOF
:ERROR
echo Invalid parameter(s)
echo Run %0  -? for help
:EOF


