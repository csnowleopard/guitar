import sys, commands, string, os, glob, copy
import xml.etree.ElementTree as ET
import functools

if len(sys.argv) < 2:
  sys.exit("usage: python {0} <AUT folder name>".format(__file__))

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

def getTCName(f_name):
  base = os.path.basename(f_name)
  tcN, _ = os.path.splitext(base)
  return tcN

tcs = {}
tb = {}
init_ = False
cmd = "find %s -name *.apk | sed '/original/d'" % sys.argv[1]
for apk in commands.getoutput(cmd).split('\n'):
  path = os.path.dirname(apk)

  res = glob.glob(os.path.join(path,"*.res"))
  if not init_:
    init_ = True
    for tc in res:
      tcN = getTCName(tc)
      tcs[tcN] = True

  tb[path] = copy.deepcopy(tcs)
  for tc in res:
    tcN = getTCName(tc)
    r = os.path.join(path,"%s.res" % tcN)
    cmd = "cat \"%s\"" % r
    for msg in commands.getoutput(cmd).split('\n'):
      if "fail" in msg:
        tb[path][tcN] = False

e_ths = [E.th()]
e_uls = []
e_trs = []
init_ = False
for ft in tb.keys():

  if not init_:
    init_ = True
    cnt = 0
    for tc in tb[ft].keys():
      e_ths.append(E.th({"title": "%s" % tc}, "tc %4d" % cnt))
      e_uls.append(E.li("tc %04d: %s" % (cnt, tc)))
      cnt = cnt + 1

  e_ft = [E.td(ft)]
  for tc in tb[ft].keys():
    if tb[ft][tc]: # success
      e_ft.append(E.td()) # empty cell
    else: # fail
      e_ft.append(E.td("X"))
  e_trs.append(E.tr(e_ft))

report = (
  E.html(
    E.head(
      E.title("Android GUITAR fault matrix"),
      E.link({"rel": "stylesheet", "href": "style.css", "type": "text/css"})
    ),
    E.body(
      E.table( {"border": "1"},
        E.tr(e_ths), # headers: tc1, tc2, etc.
        e_trs
      ),
      E.ul(e_uls) # testcase num => name mapping
    )
  )
)

ET.ElementTree(report).write("fault.html")
