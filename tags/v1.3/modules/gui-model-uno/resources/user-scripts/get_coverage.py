#! /usr/bin/env python

import sys
import string

f = open('lcov_out.txt', 'r')

filetext_raw = f.read()
f.close()
sentences = filetext_raw.split('\n')

found = 0

for s in sentences:
    l = s.strip().split(' ')
    for i in range(0, len(l)):
        if l[i].startswith('lines'):
            print l[i+1].replace('%', ''),
            found = 1
            break
        #end if
    #end for
    if found == 1:
        break
#end for

if found == 0:
	print '0.0'
