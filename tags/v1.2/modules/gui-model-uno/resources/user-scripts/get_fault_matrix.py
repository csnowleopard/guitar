#! /usr/bin/env python

f = open('vals.txt', 'r');
f_text = f.read()
f_text = f_text.replace('_', '')
lines = f_text.split('\n\n')

list = []
for l in lines:
    if len(l) == 0:
        continue
    ll = l.split('\n')
    #if len(ll) < 2:
        #continue
    for i in range(0, len(ll)):
        t = ll[i]
        if len(list) < i+1:
            new_list = [t]
            list.append(new_list)
        else:
            list[i].append(t)

#print list

for i in range(0, len(list)):
    ll = list[i]
    for j in range(0, len(ll)):
        ll[j] = ll[j].strip().split(' ')

#print list
#quit()

#if len(list) < 2:
#    quit()

ctr = 0
print '\t\t\t\tTestcases (Fault Detected = 1, Fault Not Detected = 0)'
for t in list:
    if len(t) < 2:
        continue
    correct = t.pop(0)
    ctr = ctr + 1
    print 'Initial State ', ctr
    v = 0
    for l in t:
        if len(l) != len(correct):
            continue
        row = []
        v = v + 1
        print 'Fault seeded version ', v, '\t',
        for i in range(0, len(correct)):
            x = abs( int(correct[i]) - int(l[i]) )
            if x > 0:
                print '1 ',
            else:
                print '0 ',
        print ''
    print ''
