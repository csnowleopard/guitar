To run adr-replayer for your application:

	1. Modify the parameters starting with "application." in file adr-replayer.properites  
	for you application under test (see the example in adr-replayer.properties).
	
	2. Run the ant command:
		ant -Dproperties=<Your replayer properties file>  -f adr-replayer.xml
		
		Example: ant -Dproperties=adr-replayer.properties -f adr-replayer.xml 
		  
