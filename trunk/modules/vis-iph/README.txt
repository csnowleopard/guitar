GraphBuilder README

This module parses EFG and GUI Structure XML representation into Graph
representation before the Graph is displayed in GUI.

==============FOR USERS=======================================================

Running the application:
(Using the graphbuilder.jar JAR file)
1. 	Make sure graphbuilder.jar is placed in the same directory as other needed
	JAR files which are:
	- antlr-2.7.6.jar
	- commons-logging.jar
	- commons-logging-1.1.1.jar
	- JAXB2_20120218.jar
	- piccolo2d-core-1.3.1.jar
	- piccolo2d-extras-1.3.1.jar
	- piccolo2d-swt-1.3.1.jar
	- spring.jar
	
	Don't worry, these JAR files are distributed together with graphbuilder.jar

2.	Double-click on graphbuilder.jar. The initial setup dialog will pop up.
3.	Select Working Dir 		= Directory/folder of the iGUITAR testing application
	Enter Output Dir Name 	= Name of top directory of copied iGUITAR stuff
	Select Application Dir	= Subdirectory of the iGUITAR testing application 
							  contains all the iGUITAR testing application material
							  usually named as "Demo"
							  
4. From there, select either EFG verifier or Test Case verifier.
5. EFG or Test Case Visualizer will show up and ready to verify iGUITAR application.

(Using distributable .exe)
- Coming soon


=======================FOR DEVELOPERS=======================================
Building the module in terminal:
	1. First, you need Apache Ant installed in your machine. Go to http://ant.apache.org/bindownload.cgi to download
	2. Using terminal, go to the directory where this module is checked-out.
	3. Run "Ant" or "Ant build.dist"
	4. Distributable JAR file will be in /dist directory and /lib directory.

Module structure:
	GraphBuilder
		/src
			/edu
				/cs
					/guitar
						/gen (Contains all generated classes from GUITAR XML schema)
						/graphbuilder (Contains EFG, GUI structure and test cases builders)
						/gui (Contains main class of the application and application GUI front-end)
						/helper (Contains utility classes)
						/parser	(Contains XML parser which parses GUI, EFG and Test Cases)
		/docs
			/schema (Contains iGUITAR XML schema)
			/javadoc (Contains Javadoc of application)
		/file (Contains files for testing purpose)
		/lib (Contains dependencies of application, placed in CLASSPATH)
		README.txt (Brief user guide for users and developers)
		build.xml (Build script for the application)
