# parse the .GUI file

file = File.open("Demo.GUI", "r") #change this to the gui file needed to be parsed
lines = file.readlines
i=0
data = Hash.new
id =""
name = ""
while i< lines.length do
  
  if lines[i].include?("<Window>") #start searching for id/invokelist
    while(!lines[i].include?("Title")) do
          i= i +1
    end #advance to title
    lines[i+1].match(/\<Value\>(.+)\<\/Value\>/i)
    name = $1
  end #this gets the current title
  if lines[i].include?("<Name>ID</Name>")
    i = i+1
    lines[i].match(/\<Value\>(.+)\<\/Value\>/i)
    id = $1
  end
  if lines[i].include?("Invokelist")
    i = i+1
    lines[i].match(/\<Value\>(.+)\<\/Value\>/i)
    il = $1
    #puts name
    #puts il
    #puts id

    #puts "data[name]: #{data[name].inspect}\n"

    #data[name]? data[name].merge({il=>id}) : data[name] = {il=>id}
    data[name]? data[name][il] = id : data[name] = {il=>id}

    #puts "data[name]: #{data[name].inspect}\n\n"
  end
  i=i+1
end

puts data.inspect
puts "\n"



# generates test cases based on sequence of intents

#a = Hash.new
endSequence = false
#concatenate into testcase name eg: testcase_1.tst
testcasename = 1
#array of already existed testcases
tstString = Array.new
File.open(ARGV[0], "r").each{ |line|
	#has steps / sequence
	hasStep = false
	#initializing the testcase string
	testcase = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<TestCase>"
	if(line.include?"Sequence 1" || endSequence == true)
		endSequence = true
		break
	end

	while (line.include?"/")
		line = line.sub(/\//, "")
	end

	#puts "line: '#{line}'"

	line = line.strip
	arrSequence = line.split(" --> ")

	#puts "arrSequence: #{arrSequence.inspect}"

	i = 0
	arrSequence.each{ |curr|
		curr = curr.strip
		#puts "curr: '#{curr}'"

		if(i == (arrSequence.length - 1))
			break
		end
		nextt = arrSequence[i+1]

		#adds step to testcase
		if(data[curr] == nil)
			break
		end
		if(data[curr][nextt] == nil)
			break
		end

		temp = (data[curr][nextt]).sub(/w/, "e")
		testcase << "\n<Step>\n\t<EventId>#{temp}</EventId>\n\t<ReachingStep>true</ReachingStep>\n</Step>"		
		hasStep = true
		i += 1
	}

	if (hasStep && (!tstString.include?((testcase << "\n</TestCase>"))))
		#testcase << "\n</TestCase>"
		index = tstString.length
		tstString[index] = testcase
		f = File.open("testcase_#{testcasename}.tst", "w")
		f.write(testcase)
		f.close

		puts "#{testcase}\n\n"
	
		#increase the name of testcase file
		testcasename += 1
	end
}
