<!--Authors: Kerese Wright and Emily Berk-->
<html>
    <head>
        <title>Android Intents</title>
        <link rel="stylesheet" href="intent.css" type="text/css" />
    </head>
    <body>
      <div id="mleft">
        <div id="content" >
           <form method="post" action="<?php echo $_SERVER['PHP_SELF']; ?>"> 
            <div id="header">
                <div id="logo">
                    <img src="intents.png" alt="" />
                </div>
                <div id="instr">
                    <h1>INTENTS GUITAR</h1>
                    <p>Welcome to the IntentsGuitar Analyzer! This application will allow you to filter, sort, and display the current Android Intents into a better representation.</p>
                    <p> <u>Note:</u> Master Log Name requires .txt extension </p>
                </div>
            </div>            
            <div id="main">
                <div id="filter">
                    <h2>FILTER IN</h2>
		    <h5>Select a category you'd like your filter to apply to. Then supply a string to match the data you want the filter to keep. For example, if you wanted all intents that are in the default category you would click 'category' and enter 'DEFAULT' in the box.</h5>
                    <input type="checkbox" id="filt-act" name="filt-act" value="Action" />Action<br />
                    <input type="text" id="act-text" name="act-text" /><br /><br />
                    <input type="checkbox" id="filt-cat" name="filt-cat" value="Category" />Category<br />
                    <input type="text" id="cat-text" name="cat-text"/><br /><br />
                    <input type="checkbox" id="filt-cmp" name="filt-cmp" value="Component"/>Component<br />
                    <input type="text" id="cmp-text" name="cmp-text"/><br /><br />
                    <input type="checkbox" id="filt-oth" name="filt-oth" value="Other"/>Other<br />
                    <input type="text" id="oth-text" name="oth-text"/><br /><br />
                    <h2>ENTER APP NAME</h2>
		    <h5>the name of your application</h5>
                    <input type="text" id="autName" name="autName"/><br /><br />
                </div>
                <div id="category">


		    <div id="sort">
                        <h2>SORT BY</h2>
			<h5>Select which category you'd like your data sorted by</h5>
                        <select name="sort-by">
                            <option value="none">None</option>
                            <option value="act">Action</option>
                            <option value="cat">Category</option>
                            <option value="cmp">Component</option>
                        </select>
                    </div>
                    <div id="test">
                        <h2>ENTER TEST CASE NAME</h2>
			    <h5>The name you'd like this run to have. <br/>
				Note: test files will be named like this and will be located in the scripts/OUTPUT directory.<br/>
				<span class="special">master_[TESTCASENAME].txt</special>
				<span class="special">intents_[TESTCASENAME].xml</special>
				<span class="special">table_[TESTCASENAME].html</special>
			    </h5>
                        <input type="text" id="testName" name="test"/><br /><br />
                    </div>
                    <div id="master">
                        <h2>ENTER SOURCE LOG NAME</h2>
			<h5>Enter the name of the data file you wish to use. For example, the master log for a testcase is called "master_[TESTCASENAME].txt"</h5>
                        <input type="text" id="masterLog" name="masterLog"/><br /><br />
                    </div>
                 <!--   <div id="graph">
                        <input type="checkbox" name="graph" />Include Graph
                    </div> -->
                    <div id="btn">
                        <input type="submit" value="SUBMIT" name="submit" id="submit" /><br /><br />
                    </div>
                </div>
            </div>
            </form>
        </div>
      </div>
	   <?php 
			$filtAct = $_POST["filt-act"];
			$filtCat = $_POST["filt-cat"];
			$filtCmp = $_POST["filt-cmp"];
			$filtOth = $_POST["filt-oth"];
			
			$actText = $_POST["act-text"];
			$catText = $_POST["cat-text"];
			$cmpText = $_POST["cmp-text"];
			$othText = $_POST["oth-text"];

			$sortBy = $_POST["sort-by"];	
			//$graph = $_POST["graph"];
			$test = $_POST["test"];
			echo '
			<script type="text/javascript">
			    var test = "' . $test . '";
			</script>';
			$masterlog = $_POST["masterLog"];
			$aut = $_POST["autName"];

			// $cmd = shell_exec('ruby menu.rb -m masterLog.txt -n test1');
			// echo($cmd);

			// when submit is clicked
			if (isset($_POST["submit"])) {

			    if ($test != NULL and $masterlog != NULL and $aut != NULL) {					
				$command = "ruby menu.rb -a $aut -n $test -m $masterlog";
			    	// include graph is checked (use test case name for graph name)
				/*if (isset($_POST["graph"])) {
				    $command = "$command -g $test";
				}*/
			        // filter options
			   if ($filtAct or $filtCat or $filtCmp or $filtOth) {
				    $command = "$command -f";
				    if ($filtAct == "Action") {
					$command = "$command act $actText";
				    }	
				    if ($filtCat == "Category") {
					$command = "$command cat $catText";
				    }	
				    if ($filtCmp == "Component") {
					$command = "$command cmp $cmpText";
				    }	
				    if ($filtOth == "Other") {
					$command = "$command other $othText";
				    }	
			    }

				// sort option
			    if ($sortBy != "none") {
				    $command = "$command -s $sortBy";
			    }				
			} else {
			    if ($test == NULL) {
				    echo "<script type=\"text/javascript\">";
				    echo "alert(\"Please Enter Test Case Name\")";
				    echo "</script>";
			    }
				
				if ($masterlog == NULL) {
				    echo "<script type=\"text/javascript\">";
				    echo "alert(\"Please Enter Master Log Name\")";
				    echo "</script>";
				}	

                                if($aut == NULL) {
                                    echo "<script type=\"text/javascript\">";
				    echo "alert(\"Please Enter Application Name\")";
				    echo "</script>";
				}

			    }
			    echo "<div id=\"mright\">";
			    echo "<h2>Results and output</h2>";
			    echo "<h4>Run the following command or click the buttons to view results.</h4>";
			    echo "<div id=\"command\">".$command."</div><br/>";
			    exec('cd ../scripts');
			    exec($command);
			}			
		?>
	    <?php
		if(isset($_POST["submit"])){
		    echo "<div id=\"btns\">";
		    echo "<input type=\"button\" name=\"log-btn\" id=\"log-btn\" value=\"LOG\" onClick=\"showlog()\"/>";
		    echo "<input type=\"button\" name=\"log-btn\" id=\"log-btn\" value=\"TABLE\" onClick=\"showtable()\"/>";
		    echo "<input type=\"button\" name=\"xml-btn\" id=\"xml-btn\" value=\"XML\" onClick=\"showxml()\"/>";
		    echo "<input type=\"button\" name=\"graph-btn\" id=\"graph-btn\" value=\"GRAPH\" onClick=\"showgraph()\"/>";
		    echo "</div><br/>";
		    echo "<div id=\"output\">";
		    echo "</div>";
		    echo "<br/>";			    
		    echo "</div>";
		}
	    ?>
	    <script type="text/javascript">
		function showlog(){
		    var log = "<iframe src=\"OUTPUT/master_" + test + ".txt\"></iframe>";
		    document.getElementById("output").innerHTML = log;
		}
		function showtable(){
		    var log = "<iframe src=\"OUTPUT/table_" + test + ".html\"></iframe>";
		    document.getElementById("output").innerHTML = log;
		}
		function showxml(){
		    var xml = "<iframe src=\"OUTPUT/intents_" + test + ".xml\"></iframe>";
		    document.getElementById("output").innerHTML = xml;

		}
		function showgraph(){
		    document.getElementById('output').innerHTML = "<img class=\"graph\" src=\"../script/Lotsointents.png\"/>";
		}
	    </script>
	    
    </body>
</html>
