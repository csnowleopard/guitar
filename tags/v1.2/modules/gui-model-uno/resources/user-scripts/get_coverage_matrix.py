#! /usr/bin/env python

import sys
import string

f = open('percentages.txt', 'r')

filetext_raw = f.read()
f.close()
sentences = filetext_raw.split('\n\n')

list = []
for s in sentences:
    if len(s) < 1:
        continue
    t = s.split(' ')
    for i in range(0, len(t)):
        x = t[i]
        x = x.strip()
        if len(x) < 1:
            continue
        x = x.split('\n')
        if len(list) < i+1:
            list.append([x])
        else: 
            list[i].append(x)

#for l in list:
#    for t in l:
#        print t
#    print ''


ctr = 0
print '\t\t\t\tTestcases '
for t in list:
    ctr = ctr + 1
    print 'Initial State ', ctr
    v = 0
    for l in t:
        v = v + 1
        if v == 1:
            print 'No faults seeded ', '\t\t',
        else:
            print 'Fault seeded version ', v, '\t',
        for w in l:
            print w, '% ',
        print ''
    print ''
