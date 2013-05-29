# ARGV[0] = android manifest file including path and extension

lines = File.open(ARGV[0], "r").readlines
masterString = ""
intent = false
activity = false
main = false
launcher = false
#name = ""
activityName = false
permission = false

lines.each{ |line|
  if line =~ /.*package=\"(\S*)\".*/
	masterString << "package: " << $1 << "\n"

	# name appended to output file (package name with '_' replacing '.' 
	#name = $1.sub(/\./, '_')
	#while (name.index(".") != nil)
	#    name = name.sub(/\./, '_')
	#end
  elsif line =~ /<activity.*android:name=\"(\S*)\".*><\/activity>/
	temp = $1
	if (temp.index(".") == nil)
	    temp = "." << temp
	end
	masterString << "activity: " << temp << "\n"
	activityName = true
	activity = false	
  elsif line =~ /<activity.*android:name=\"(\S*)\".*\/>/
	temp = $1
	if (temp.index(".") == nil)
	    temp = "." << temp
	end
	masterString << "activity: " << temp << "\n"
	activityName = true
	activity = false	
  elsif line =~ /.*<activity.*android:name=\"(\S*)\".*/
	temp = $1
	if (temp.index(".") == nil)
	    temp = "." << temp
	end
	masterString << "activity: " << temp 
	activity = true
	activityName = true
  elsif line =~ /.*<activity.*/
	activity = true
	activityName = false
 elsif (line =~ /android:name=\"(\S*)\".*\/>/ and activityName == false and activity == true)
	temp = $1
	if (temp.index(".") == nil)
	    temp = "." << temp
	end
	masterString << "activity: " << temp << "\n"
	activityName = true
	activity = false
  elsif (line =~ /android:name=\"(\S*)\"/ and activityName == false and activity == true)
	temp = $1
	if (temp.index(".") == nil)
	    temp = "." << temp
	end
	masterString << "activity: " << temp 
	activityName = true	
  elsif line =~ /.*<intent-filter>.*/
	if (activity == true)
	    intent = true
	end
  elsif line =~ /.*<\/intent-filter>.*/
	intent = false
  elsif line =~ /.*<\/activity>.*/
	activity = false
	masterString << "\n"
  elsif line =~ /.*android\.intent\.action\.MAIN.*/
	main = true
  elsif line =~ /.*android\.intent\.category\.LAUNCHER.*/
	launcher = true
  elsif line =~ /.*<uses-permission.*android:name=\"(.*)\".*/
	masterString << "permission: " << $1 << "\n"
	permission = false
  elsif line =~ /.*<uses-permission.*/
	permission = true
  elsif (line =~ /android:name=\"(\S*)\"/ and permission == true)
	masterString << "permission: " << $1 << "\n"
	permission = false
  end

  if (launcher == true)
	masterString << " (main launcher activity)"
	launcher = false
	main = false
  end
}

puts "\n#{masterString}\n"

output = File.new("parsedManifest.txt", "w+");
output.write(masterString)
output.close

