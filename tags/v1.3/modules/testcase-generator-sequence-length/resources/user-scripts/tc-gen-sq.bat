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
:: GUI to EFG launching script
::
::	By	baonn@cs.umd.edu
::	Date: 	12/14/2012
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
@ECHO OFF


IF ""%8""=="""" GOTO ERROR

rem SETs the basedir to the directory where this batch file is locaed
SET base_dir=%~dp0
SET guitar_lib=%base_dir%jars

:: get guitar classpath
SET guitar_classpath=%basedir%
SETlocal ENABLEDELAYEDEXPANSION
FOR /R  "%guitarlib%" %%G IN (*.jar) DO SET guitar_classpath=!guitar_classpath!;%%G

SET classpath=%base_dir%;%guitar_classpath%

:: Change GUITAR_OPTS variable to run with the clean log file  
SET GUITAR_OPTS=%GUITAR_OPTS% -Dlog4j.configuration=log/guitar-clean.glc

SET JAVA_CMD_PREFIX=java
SET launcher=edu.umd.cs.guitar.testcase.TestCaseGenerator

SET  plugin=SequenceLengthCoverage

::echo %classpath%
%JAVA_CMD_PREFIX% %GUITAR_OPTS% -cp "%classpath%" %launcher%  -p  %plugin% %*


GOTO EOF
:ERROR
echo Invalid parameter(s)
echo Usage: %0  -e [EFG file] -l [length] -m [maximum number - 0 for all] -d  [tc-dir]
:EOF
