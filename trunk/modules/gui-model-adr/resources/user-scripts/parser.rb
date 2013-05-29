# Author: Emily Berk
# updates on 2/18: 
#	added date-time parsing (modified catchers and regexp back references)
#	added ARGV[1] option (might need to change in the future to work for other apps; so far only Countdown Timer and ContactManager seem to have any application events)
# to use: ruby parser.rb [name of dump file] [name of app]
#	i.e. ruby parser.rb tryingParserAgain-dump_t_e1139051464_e1139051464.txt Countdown
#	i.e. ruby parser.rb ContactManager-dump_t_e1139124760_e1139124760.txt ContactManager

def printIfNotNil (var)
  var.nil? ? " - " : var
end

app_name = ""	# added on 2/18

# added on 2/18
if (ARGV[1] == "Countdown") then
   app_name = "Countdown Alarm"
elsif (ARGV[1] == "com.aut") then
   app_name = "com\\.aut "
   puts "#{app_name}"
else
   app_name = ARGV[1]	
end

intentsCatcher = /([0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}\.[0-9]{3}) I\/ActivityManager\(\s..[0-9]*\): Starting: Intent \{( act=(android\.intent\.action\.[A-Z]+))?( cat=(\[android.intent.category.[A-Z]+\]))?( dat=([A-z]|[0-9]|\.|\/|:)*)?( flg=(0x([0-9]|[a-f])*))?( cmp=(([A-z]|[0-9]|\.|\/|:)*))?( bnds=(\[[0-9]+,[0-9]+\]\[[0-9]+,[0-9]+\]))? (.*)?\}[0-9]*/
guitarCatcher = /([0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}\.[0-9]{3}) [IDVWE]\/edu\.umd\.cs\.guitar\(\s.[0-9]+\): (.+android.widget.+|click.+)/

#catch buttonids from app
buttonCatcher = /([0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}\.[0-9]{3}) D\/LotsoIntents16\([0-9]*\): ButtonID:([0-9]*)/

#appEventsCatcher = /([0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}\.[0-9]{3}) [IDVWE]\/Countdown Alarm\(\s.[0-9]+\): (.+)/ #TODO make Countdown Alarm represent an application name passed via ARGV[1]
appEventsCatcher = /([0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}\.[0-9]{3}) [IDVWE]\/#{app_name}\(\s.[0-9]+\): (.+)/ #TODO make Countdown Alarm represent an application name passed via ARGV[1]

dumpFile = File.open(ARGV[0], "r")
logFile = File.new("masterLog.txt", "w+");
masterString = String.new

lines = dumpFile.readlines
masterString << "Date & Time | Intent Action | Intent Category | Flag | Launching Component \n"		#added Date & Time on 2/18
lines.each{ |line| 
 temp = String.new
 if line =~ intentsCatcher
   temp << "INTENT: " << printIfNotNil($1) << " " << printIfNotNil($3) << " " << printIfNotNil($5) << " "<< printIfNotNil($6)  << " " << printIfNotNil($7)<< " " << printIfNotNil($8)<< " "  << printIfNotNil($12)<< " " << printIfNotNil($14) << " " << printIfNotNil($15) << " " << printIfNotNil($16) << " " << printIfNotNil($17)   
   masterString << temp << "\n"

  #puts "intentsCatcher: $0 = #{$0}, $1 = #{$1}, $3 = #{$3}, $8 = #{$8}, $9 = #{$9}, $10 = #{$10}, $12 = #{$12}"
  elsif line =~ guitarCatcher
    temp << "GUITAR: " << printIfNotNil($1) << " " << printIfNotNil($2)
    masterString << temp << "\n"
  elsif line =~ appEventsCatcher
    temp << "APPLICATION EVENT: " << printIfNotNil($1) << " " << printIfNotNil($2)
    masterString << temp << "\n"

  elsif line =~buttonCatcher
    temp << "BUTTON PRESS: " << printIfNotNil($1) << " " << printIfNotNil($2)
    masterString << temp << "\n"
 end

}
dumpFile.close
logFile.write(masterString)
logFile.close

