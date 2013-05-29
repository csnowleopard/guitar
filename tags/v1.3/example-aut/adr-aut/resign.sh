#!/bin/bash
SCRIPT_DIR=`dirname $0`
R="$SCRIPT_DIR/../../../lib/platforms/adr/resign"
java -jar $R/signapk.jar $R/platform.x509.pem $R/platform.pk8 $1 $2
