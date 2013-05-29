#Author: Rohan Pathare
#to run: ruby xml2table.rb filename.xml
#outputs to table.html
#cosmetic modifications were made 4/12/2012

class Xml2table
	
	def self.makeTable(inputFile, outputFile, appName)
		xmlfile = File.new(inputFile, "r")
		#xmlfile = File.new(ARGV[0], "r")
		htmlfile = File.new(outputFile, "w+")
		masterString = String.new
		masterString << "<center><b>" +appName + " Event - Intent Log </b>\n"
		masterString << "</tr>"
		masterString << "<table border=\"1\">\n"
		masterString << "<tr>\n"
		masterString << "<th>Events</th>\n"
		masterString << "<th>Intents</th>\n"
		masterString << "</tr>\n"

		line = xmlfile.gets
		temp = String.new
		nextRow = false

		#read each line of the xml file
		while line != nil
			#if the line is the beginning of guitar output
			if line =~ /\<event\>/
			line = xmlfile.gets 	# <date-time>. added 3/18
			line = xmlfile.gets 	# <info>
			#if guitar clicks a button
			if line =~ /\<info\>click: (.*)\<\/info\>/
				#create new row in table
				temp << "<tr>\n"
				#write click and name of button
				temp << "<td>Click: " << $1 << "</td>\n"
				masterString << temp
				nextRow = true
			end
			#if this line is the beginning of an intent
			elsif line =~ /\<intent\>/
			#skips through lines regarding other intent info
			line = xmlfile.gets	#<date-time>.  added 3/18
			line = xmlfile.gets	#<action>
			line = xmlfile.gets	#<category>
			line = xmlfile.gets	#<flag>
			line = xmlfile.gets	#<component>
			#match intent component info
			if line =~ /\<component\>(.*)\<\/component\>/
				#if component is directly preceded by an intent				
			if nextRow
				#write component info
				temp << "<td>Component: " << $1 << "</td>\n" << "</tr>\n"
				#if component is not directly preceded by and intent
				else
						#create new row and component info
					temp << "<tr>\n<td></td>\n<td>Component: " << $1 << "</td>\n" << "</tr>\n"
				end
				masterString << temp
					
			end
			nextRow = false
		end
		#advance line in xml file
		line = xmlfile.gets
		temp = ""

	end

	masterString << "</table></center>"
	
	xmlfile.close
	htmlfile.write(masterString)
	htmlfile.close
	end
end
