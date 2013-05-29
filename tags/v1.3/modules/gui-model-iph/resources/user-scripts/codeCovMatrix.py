import locale
locale.setlocale(locale.LC_NUMERIC, "")

def format_num(num):
    try:
        inum = int(num)
        return locale.format("%.*f", (0, inum), True)

    except (ValueError, TypeError):
        return str(num)

def get_max_width(table, index):
    return max([len(format_num(row[index])) for row in table])

def pprint_table(out, table):
    col_paddings = []
    
    for i in range(len(table[0])):
        col_paddings.append(get_max_width(table, i))

    for row in table:
        # left col
        print >> out, row[0].ljust(col_paddings[0] + 1),
        # rest of the cols
        for i in range(1, len(row)):
            col = format_num(row[i]).rjust(col_paddings[i] + 2)
            print >> out, col,
        print >> out

if __name__ == "__main__":
    import os
    import glob
    
    flag = 0
    path = './'
    for infile in glob.glob( os.path.join(path, '*.tmp') ):
        # print "current file is: " + infile
        fp = open(infile, 'r')

        n = 0
        cov = []
        file = []
        for line in fp:
            line = line.strip()
            if n == 0:
                testname = line
            else:
                if n%2 == 0:
                    cov.append(line)
                else:
                    file.append(line)
            n += 1
    
        a = [""]
        a.extend(file)
        b = [testname]
        b.extend(cov)

        if flag == 0:
            table = [a,b]
            flag = 1
        else:
            table.append(b)

    import sys
    out = sys.stdout
    pprint_table(out, table)
