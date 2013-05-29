import sys, os, glob, copy
import xml.etree.ElementTree as ET
import functools

if len(sys.argv) < 2:
  sys.exit("uage: python {0} <results folder name>".format(__file__))

docs = []
for f in glob.glob(os.path.join(sys.argv[1],"*/*.xml")):
  docs.append(ET.parse(f))

# factory
class _E(object):
  def __call__(self, tag, *children, **attrib):
    elem = ET.Element(tag, attrib)
    for item in children:
      if isinstance(item, dict):
        elem.attrib.update(item)
      elif isinstance(item, list):
        for item_ in item:
          elem.append(item_)
      elif isinstance(item, basestring):
        if len(elem):
          elem[-1].tail = (elem[-1].tail or "") + item
        else:
          elem.text = (elem.text or "") + item
      elif ET.iselement(item):
        elem.append(item)
      else:
        raise TypeError("bad argument: %r" % item)
    return elem

  def __getattr__(self, tag):
    return functools.partial(self, tag)

E = _E()

cls = "class"
mtd = "method"
blk = "block"
lne = "line"

typ = "type"
val = "value"
nam = "name"

dat = "data"
cvg = "coverage"

aLL = "all"
dLL = dat + '/' + aLL
pkg = "package"
src = "srcfile"
cLS = "cLASS" # to avoid being conflict with cls

tcs  = [E.th()]
all_ = {cls: [E.td(cls)], mtd: [E.td(mtd)], blk: [E.td(blk)], lne: [E.td(lne)]}
pkg_ = {}

def upd_dic(dic, node):
  ty = node.attrib[typ]
  ev = E.td(node.attrib[val])
  if cls in ty:
    dic[cls].append(ev)
  elif mtd in ty:
    dic[mtd].append(ev)
  elif blk in ty:
    dic[blk].append(ev)
  elif lne in ty:
    dic[lne].append(ev)

for tc in range(len(docs)):
  tcs.append(E.th("tc %4d" % tc))

  pkgs = docs[tc].findall(dLL+'/'+pkg)
  if tc == 0: # initialize pkg_
    for p in pkgs:
      p_nam = p.attrib[nam]
      pkg_[p_nam] = copy.deepcopy(all_)
      pkg_[p_nam][src] = {}

  for p in pkgs:
    p_nam = p.attrib[nam]
    for c in p:
      # package coverage
      if cvg in c.tag:
        upd_dic(pkg_[p_nam], c)
      elif src in c.tag:
        s_nam = c.attrib[nam]
        if tc == 0: #initialize pkg_/src_
          pkg_[p_nam][src][s_nam] = copy.deepcopy(all_)
          pkg_[p_nam][src][s_nam][cLS] = {}
        for cc in c:
          # srcfile coverage
          if cvg in cc.tag:
            upd_dic(pkg_[p_nam][src][s_nam], cc)
          elif cls in cc.tag:
            c_nam = cc.attrib[nam]
            if tc == 0:
              pkg_[p_nam][src][s_nam][cLS][c_nam] = copy.deepcopy(all_)
            for ccc in cc:
              # class coverage
              if cvg in ccc.tag:
                upd_dic(pkg_[p_nam][src][s_nam][cLS][c_nam], ccc)

  # all coverage
  for a in docs[tc].findall(dLL+'/'+cvg):
    upd_dic(all_, a)

e_trs = []
def add_dic(dic):
  e_trs.append(E.tr(dic[cls]))
  e_trs.append(E.tr(dic[mtd]))
  e_trs.append(E.tr(dic[blk]))
  e_trs.append(E.tr(dic[lne]))

for p in pkg_.keys():
  e_trs.append(E.tr(E.td(pkg+": "+p)))
  add_dic(pkg_[p])
  for s in pkg_[p][src].keys():
    e_trs.append(E.tr(E.td(src+": "+s)))
    add_dic(pkg_[p][src][s])
    for c in pkg_[p][src][s][cLS].keys():
      e_trs.append(E.tr(E.td(cls+": "+c)))
      add_dic(pkg_[p][src][s][cLS][c])

report = (
  E.html(
    E.head(
      E.title("Android GUITAR coverage report"),
      E.link({"rel": "stylesheet", "href": "style.css", "type": "text/css"})
    ),
    E.body(
      E.table( {"border": "1"},
        E.tr(tcs), # headers: tc1, tc2, etc.
        E.tr(E.td(aLL)),
        E.tr(all_[cls]), E.tr(all_[mtd]), E.tr(all_[blk]), E.tr(all_[lne]),
        e_trs
      )
    )
  )
)

ET.ElementTree(report).write("coverage.html")
