# Written by Siti Mashitah Shamsul-Anuar
# Written by Emily Berk
# Written by Elijah Yoon
# Debugged by Uyen-Truc Nguyen

# type : act, cat, flg, cmp
# filter : some keyword you want to filter by
#ex. type:cat filter:HOME  -> only show intents with HOME category

class Functions

	def self.filter(filters, graphFile)
	#filterHash.keys are the types
	  masterString = ""
	  #opens file and gets all the lines
	 # lines = File.open("masterLog.txt", "r").readlines()
	  file = File.open("filtered_#{graphFile}.txt", "r")
	  lines = file.readlines()
	  file.close
	 # lines = File.open("filtered.txt", "r").readlines()
	  lines.each{ |line| #for each line, if its an intent, will add to masterString iff the type specified contains the filter given .   	    
	    includeme = false
	    tmp = line.split
	    case tmp[0]
	    when "INTENT:"
		tmp = line.split
		if( filters["act"])
			if tmp[3] =~ Regexp.new(filters["act"], true)
			  includeme = true
			 # masterString << line << "\n" # if the line matches the filter, keep it (we could add a negate option that would mean if it matches, then filter OUT)
			end
		end
		if(filters["cat"])
			if tmp[4] =~ Regexp.new(filters["cat"], true)
			  includeme = true
			#  masterString << line << "\n" # if the line matches the filter, keep it (we could add a negate option that would mean if it matches, then filter OUT)
			end
		end
		if(filters["cmp"])
			if tmp[8] =~ Regexp.new(filters["cmp"], true)
			  includeme = true
			#  masterString << line << "\n" # if the line matches the filter, keep it (we could add a negate option that would mean if it matches, then filter OUT)
			end
		end
		if(filters["other"])
			if line =~ Regexp.new(filters["other"], true)
			  includeme = true
			#	masterString << line << "\n" # if the line matches the filter, keep it (we could add a negate option that would mean if it matches, then filter OUT)
			end
		end
		if (includeme)
		  masterString << line << "\n" # if the line matches the filter, keep it (we could add a negate option that would mean if it matches, then filter OUT)
		end
	   when "APPLICATION"
	      #TODO add fuctionality for Application Events
		if(filters["other"])
		    if line =~ Regexp.new(filters["other"], true)
		      masterString << line << "\n" # if the line matches the filter, keep it (we could add a negate option that would mean if it matches, then filter OUT)
		    end
	        end
	    when "GUITAR:"
		if(filters["other"])
		    if line =~ Regexp.new(filters["other"], true)
		      masterString << line << "\n" # if the line matches the filter, keep it (we could add a negate option that would mean if it matches, then filter OUT)
		    end
	        end
	    end
	  }
	  out = File.new("filtered_#{graphFile}.txt", "w+")  
	  out.write(masterString)
	  out.close

	end

	#type : act, cat, flg, cmp
	def self.sort (type, sessionName, masterlog)
	  masterString = ""
	  lineArray = []
	  appArray = []
	  guitarArray = []
	  #opens file and gets all the lines
	  lines = File.open(masterlog, "r").readlines()				# added on 2/26 7:59pm
	  lines.each{ |line|
	    tmp = line.split
	    case tmp[0]
	    when "INTENT:"
	      #add all lines to array
	      lineArray = lineArray + [line]
	    when "APPLICATION"
	      #TODO add fuctionality for Application Events
	      appArray = appArray + [line]
	    when "GUITAR:"
	      #TODO add fuctionality for Guitar Events
	      guitarArray = guitarArray + [line]
	      
	    end
	  }
	  #sort array based on custom comparison
	  case type
	    when "act"
	      lineArray.sort! {|x, y|  x.split[3] <=> y.split[3] }
	    when "cat"
	      lineArray.sort! {|x, y|  x.split[4] <=> y.split[4] }
	    when "flg"
	      lineArray.sort! {|x, y|  x.split[7] <=> y.split[7] }
	    when "cmp"
	      lineArray.sort! {|x, y|  x.split[8] <=> y.split[8] }
	  end
	  #insert sorted lines into master string
	  lineArray.each{ |line|
		  masterString << line << "\n"
	  }
	  appArray.sort!
	  appArray.each{ |line|
		  masterString << line << "\n"
	  }
	  guitarArray.sort!
	  guitarArray.each{ |line|
		  masterString << line << "\n"
	  }
	  out = File.new("OUTPUT/sorted_#{sessionName}.txt", "w+")  
	  out.write(masterString)
	  puts out.readlines
	  out.close
	end


# converts masterlog to xml file
	# TODO: add command line argument for masterLog file (specific to each test case)
	def self.log2xml(graphFile, masterlog, appName)
	  intentTag = "intent"
	  appinfoTag = "application-event"
	  guitarTag = "event"
	  actionTag = "action"
	  categoryTag = "category"
	  cmpTag = "component"
	  timeTag = "time"
	  flgTag = "flag"
	  dataTag = "data"
	  datetimeTag = "date-time"

    #open the new master log (at this point it will be sorted, and/or filtered)
		f = File.open(masterlog, "r")
	  lines = f.readlines()
		f.close
	  

	  masterString = "<"+appName+">\n"
	  lines.each{ |line|
	    tmp = line.split

	    # debugging
	    #output = ""
	    #tmp.each {|e| output << " " << "#{e}"}
	    #puts output

	    case tmp[0]
	    when "INTENT:"
	      #TODO add data, bnds (bounds)
	      #added date/time tag
	       masterString << "<"+intentTag+">\n" 
	       masterString << "\t<"+datetimeTag+">" << tmp[1] << " " << tmp[2] << "</"+datetimeTag+">\n"
	       masterString << "\t<"+actionTag+">" << tmp[3] << "</"+actionTag+">\n"
	       masterString << "\t<"+categoryTag+">" << tmp[4] << "</"+categoryTag+">\n"
	       masterString << "\t<"+flgTag+">" << (tmp[7]=="-"? "-" : tmp[7].delete!("flg=")) << "</"+flgTag+">\n"
	       masterString << "\t<"+cmpTag+">" << tmp[8] + "</"+cmpTag+">\n"
	       masterString << "</"+intentTag+">\n"
	    when "APPLICATION"
	      masterString << "<"+appinfoTag+">\n" 
	      masterString << "\t<"+datetimeTag+">" << tmp[2] << " " << tmp[3] << "</"+datetimeTag+">\n"
	      #masterString << "\t<info>" << tmp[4]+(tmp[5]=="-"? "" : " "<< tmp[5])<<"</info>\n"

	      # added to get the entire info string (from tmp[5] to the rest of the array)
	      masterString << "\t<info>" << tmp[4]
	      if (tmp[5]=="-") then
		masterString << "-"
	      else
		for i in 5..(tmp.length-1)
		   masterString << " " << "#{tmp[i]}"
		end
	      end

	      masterString <<"</info>\n"
	      masterString << "</"+appinfoTag+">\n"
	    when "GUITAR:"
	      masterString << "<"+guitarTag+">\n" 
	      masterString << "\t<"+datetimeTag+">" << tmp[1] << " " << tmp[2] << "</"+datetimeTag+">\n"
	      #masterString << "\t<info>" << tmp[3] << " " << tmp[4] <<"</info>\n"

	      # added to get the entire info string (from tmp[5] to the rest of the array)
	      masterString << "\t<info>" << tmp[3]
	      if (tmp[4]=="-") then
		masterString << "-"
	      else
		for i in 4..(tmp.length-1)
		   masterString << " " << "#{tmp[i]}"
		end
	      end

	      masterString <<"</info>\n"
	      masterString << "</"+guitarTag+">\n"
	    end

	  }
	  masterString << "</"+appName+">\n"
	  out = File.new("intents_#{graphFile}.xml", "w+")  
	  out.write(masterString)
	  out.close

	end
end 
