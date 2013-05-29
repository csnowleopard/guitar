#!/usr/bin/env ruby
# Written by Whitney Ford
# Written by Siti Mashitah Shamsul-Anuar
# Debugged by Uyen-Truc Nguyen

# to run: ruby menu.rb -a [AUT name] -f [filter type] [values to filter by, separated by spaces] -g [name of graph] -s [sort type] -n [name of test case] 
# filter type: act, cmp, cat, other (you can search by any keyword or regex)
# sort type: act, cmp, cat

	require 'functions.rb'
	require 'xml2dot.rb'
	require 'xml2table.rb'

	i = 0
	length = ARGV.length
	arguments = Hash.new
	validated = false
	sort = "none" # sort option eg "component", "category", "action"
	filterHash = nil # filter option eg filterHash[0]=[act=android.intent.VIEW] 
	outputArray = Array.new # output option eg outputArray = [ ["graph","graph.html] , ["log", "log.html"] ]
	sessionName = nil #name that user is naming testcase
	masterlog = nil # specifies the target masterlog.txt
	graphFile = nil
	appName = nil

########### RUN main code HERE!!! ###########

	#length = ARGV.length

	if(length == 0) then 
		#call GUI
	else

	#parse command be and
		i = 0
		curClassification = "null" #eg: -s, -o, -f
		curAspect = "null" #eg: cmp, act, cat

          #error checking
		while(i < length) do
			
			#sets current classification variable - option on at the time			
			if((ARGV[i] == "-s") || (ARGV[i] == "-f") || (ARGV[i] == "-g") || (ARGV[i] == "-m") || (ARGV[i] == "-n") || 
				(ARGV[i] == "-a"))
				curClassification = ARGV[i]
			end

			if(curClassification == "-s")
				#"IN SORT CLASSIFICATION"
        i = i + 1 #advance to the sort argument
        sort = ARGV[i]
        if(sort == "cmp" || sort == "act" || sort == "cat")
          Functions.sort(ARGV[i], sessionName, masterlog)
        elsif(sort == "none") #not sorting so don't care
				else # sort but no option selected
          	abort("invalid input for -s")
				end
				masterLog = "OUTPUT/sorted_#{sessionName}.txt" #sets masterlog to be newly sorted txt
			elsif(curClassification == "-f")
			  filterHash = Hash.new
			  #In FILTER CLASSIFICATION
				i = i +1
				while(i<length && ARGV[i]!="-s") do
					curAspect = ARGV[i]
					#save other filters as their strings, non-other filters as type=filter
					filterHash[curAspect] = ARGV[i+1]
					i = i + 2 #advance to the next set of type/filter
				end
				i = i-1
			elsif(curClassification == "-g")
				graphFile = ARGV[i]
			elsif(curClassification == "-m")
				masterlog = ARGV[i]
			elsif(curClassification == "-n")
				sessionName = ARGV[i]
			elsif(curClassification == "-a")
				appName = ARGV[i]
			end # end if
			i = i+1	
		end # end while(i < length) do

		#source 
		#Errors checks:  Error thrown if appName or masterlog is not specified
		if((masterlog == nil)  || (appName == nil))	
			if((masterlog == nil) && (appName == nil))
				abort("Please specify masterlog and Application Under Test.")
			elsif(masterlog == nil)
				abort("Please specify masterlog.")			
			elsif(appName == nil)			
				abort("Please specify Application Under Test.")
			end
		else
			#make calls, sort should come first
			puts "\nOUTPUT: "		
			#XML is generated/LOG is generated/ filtering & sorting is taken care of			
			if(sort != "none")
				Functions.sort(sort, sessionName, masterlog)
			end
			if(filterHash != nil)
			  puts "filter hash"
				out = File.new("filtered_#{sessionName}.txt", "w+") # reset filtered.txt to an empty file
				copy = File.open(masterlog, "r").readlines()
				out.write(copy)	# make a copy of masterlog (write to out)
				out.close
				Functions.filter(filterHash, sessionName)
				masterLog = "filtered_#{sessionName}.txt" #masterlog is not the filtered log
			end# if(filterHash != nil)


			# create xml file from masterlog, the sorted log, or the filtered log
			Functions.log2xml(sessionName, masterlog, appName)

			# makes human readable event log (html table)
			Xml2table.makeTable("intents_#{sessionName}.xml", "table_#{sessionName}.html", appName)
			# make the graph			
			if (graphFile != nil) 
				Xml2dot.graph("intents_#{sessionName}.xml", "blah", appName)	# make dot file
				#dead code = %x[dot -Goverlap=prism -Tpng -o blah.png blah.dot]	# call Graphviz tool
				%x[circo -Goverlap=prism -Tpng -o blah.png blah.dot]	# call Graphviz tool. changed format on Wed 3/28 10:37pm.
				File.rename("blah.png", "OUTPUT/#{graphFile}.png")
				File.rename("blah.dot", "OUTPUT/#{graphFile}.dot")

			#DEAD CODE - LEFT FOR HARD CODE EXAMPLE
			#Xml2dot.graph("intents_openSudoku.xml", "blah", appName)	# make dot file
			#%x[circo -Goverlap=prism -Tpng -o blah.png blah.dot]	# call Graphviz tool. changed format on Wed 3/28 10:37pm.
		      	#File.rename("blah.png", "openSudoku.png")
		       	#File.rename("blah.dot", "openSudoku.dot")
			end

		
                end # end nil checks and file creation
		# used for debugging
	
		#cleanup
		system('mv intents_* OUTPUT/')
		system('mv table_* OUTPUT/')
		if (File.exists?("filtered_#{sessionName}.txt"))
		  system('mv filtered_* OUTPUT/')
		end
		if (length <=6) #no options selected, make a masterlog copy for this testcase
	    system("cp " + masterlog+ " OUTPUT/master_#{sessionName}.txt")
	  end
    if (File.exists?("OUTPUT/filtered_#{sessionName}.txt"))
        #if filtered exists, just move it and remove copy
        system("mv OUTPUT/filtered_#{sessionName}.txt OUTPUT/master_#{sessionName}.txt")
        if (File.exists?("OUTPUT/sorted_#{sessionName}.txt")) 
          system("rm OUTPUT/sorted_#{sessionName}.txt") 
        end
    end
    if (File.exists?("OUTPUT/sorted_#{sessionName}.txt") && !File.exists?("OUTPUT/filtered_#{sessionName}.txt"))
      #if sorted exists, but not filtered, move sort->filtered
      system("mv OUTPUT/sorted_#{sessionName}.txt OUTPUT/master_#{sessionName}.txt")
    end
    #remove extra files used to generate others
    
	end #if (length == 0)
