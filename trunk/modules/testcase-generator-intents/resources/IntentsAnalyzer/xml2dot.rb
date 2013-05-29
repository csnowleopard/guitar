# Author: Uyen-Truc Nguyen
# Uses some code from xml2table.rb by Rohan.
# creates dot file (outfile) representing the graph using information from an xml file (infile)
# The graph will have nodes of only click events (guitar) and intents.

require 'rubygems'
require 'graphviz'
require 'rexml/document'

class Xml2dot
	
	def self.graph(infile, outfile, appName)	
		@intentsG = GraphViz.digraph( "Intents Graph" ) 
		@intentsG.edge["color"] = "cyan3"
		@node_name = "00000"   
		root_node_name = "00000" 

		# create root node
		@intentsG.add_nodes( root_node_name, "label" => "#{appName}", "color" => "black", "shape" => "ellipse", "style" => "filled", "fillcolor" => "cyan3" )
		@node_name.succ!
		prev_node_name = root_node_name

		xmlfile = File.new(infile, "r")
		line = xmlfile.gets	# <Events>

		#read each line of the xml file
		while line != nil
			#if the line is the beginning of event output
			if line =~ /\<event\>/
				local_node_name = @node_name.clone
		   		@node_name.succ!
		   		label = "{ {event} "

				line = xmlfile.gets 	# <date-time>. added 3/18
				if line =~ /\<date-time\>(.*)\<\/date-time\>/ 	
					label << "| { date-time | #{$1} } "		
				end
				line = xmlfile.gets 	# <info>
				#if event clicks a button
				if line =~ /\<info\>click: (.*)\<\/info\>/
					label << "| { info | click: #{$1} } "
					label << "}"
					prev_node_name = local_node_name

					#add graph node
					@intentsG.add_nodes( local_node_name, "label" => label, "color" => "black", "shape" => "record", "style" => "filled", "fillcolor" => "darkolivegreen1" )
				
					#add edge
					@intentsG.add_edges( root_node_name, local_node_name )  
				 
				end
			#if this line is the beginning of an intent
			elsif line =~ /\<intent\>/
				local_node_name = @node_name.clone
		   		@node_name.succ!
		   		label = "{ {intent} "	

				#skips through lines regarding other intent info
				line = xmlfile.gets	#<date-time>.  added 3/18
				if line =~ /\<date-time\>(.*)\<\/date-time\>/ 	
					label << "| { date-time | #{$1} } "		
				end
				line = xmlfile.gets	#<action>
				if line =~ /\<action\>(.*)\<\/action\>/ 	
					label << "| { action | #{$1} } "			
				end
				line = xmlfile.gets	#<category>
				if line =~ /\<category\>(.*)\<\/category\>/ 	
					label << "| { category | #{$1} } "			
				end
				line = xmlfile.gets	#<flag>
				if line =~ /\<flag\>(.*)\<\/flag\>/ 	
					label << "| { flag | #{$1} } "			
				end
				line = xmlfile.gets	#<component>
				if line =~ /\<component\>(.*)\<\/component\>/ 	
					label << "| { component | #{$1} } "			
				end
				label << "}"				

				@intentsG.add_nodes( local_node_name, "label" => label, "color" => "black", "shape" => "record", "style" => "filled", "fillcolor" => "orange" )

				# add edge		
				@intentsG.add_edges( prev_node_name, local_node_name )  
				prev_node_name = root_node_name 
			end
			#advance line in xml file
			line = xmlfile.gets

		end
	
		xmlfile.close

		# create dot file
		@intentsG.output( :dot => "#{outfile}.dot" )
	end
end
