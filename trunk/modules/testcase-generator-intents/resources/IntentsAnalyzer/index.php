<html>
    <head>
        <title>Android Intents Guitar</title>
        <link rel="stylesheet" href="intent.css" type="text/css" />
    </head>
    <body>
        <div id="cntnt">
            <h1>Welcome to Intents GUITAR</h1>
            <p>Select a testing option:</p>
            <div id="options">
                <a href="intent.php"><input type="submit" value="Ripper on Emulator" class="index-btn" id="rip-btn"/></a>
                <form><input type="submit" value="Capture/Replay on Emulator" class="index-btn" id="cre-btn"/></form>
                <form><input type="submit" value="Capture/Replay on Device" class="index-btn" id="cr-btn"/></form>
            </div>
            
            <h4>Ripper and Emulator</h4>
            <p>After clicking on the ”Ripper” button, the application will load and store the generated intents into the master log file. Then, you can filter, sort, and display the intents into a better representation. </p>
            
            <h4>Capture/Replay (Device or Emulator)</h4>
            <p>After clicking on the “Capture/Replay” button, this application utilizes capturing user testing sessions, generating test cases, and replaying </p>
            
            <?php
                $connected = exec('../detect.sh');
                if($connected == "y"){
                    echo "<img class=\"index-img\" align=\"middle\" src=\"images/devicedetect.png\"/>";
                }else{
                    echo "<div id=\"index-warning\"><br />WARNING: The Capture/Replay tool with the device will not work if the device isn't detected.</div>";
                    echo "<img class=\"index-img\" align=\"middle\" src=\"images/devicenotdetected.png\"/>";
                    echo "<div id=\"index-descr\"></div>";
                }
            ?>

            <h4>Device Detector Image</h4>
            <p>The image above will tell you whether or not the device has been detected.  If the device is detected, then you can click on the image to see a detailed report of the device.</p>
            <?php
                $file=fopen("devices.txt","r");
                while(!feof($file)){
                    echo fgets($file). "<br />";
                }
            ?>
        </div>
    </body>
</html>
