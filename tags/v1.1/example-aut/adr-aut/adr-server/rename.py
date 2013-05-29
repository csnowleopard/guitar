import sys
import xml.etree.ElementTree as ET

if len(sys.argv) < 3:
  sys.exit("usage: python {0} <manifest> <AUT>".format(__file__))

prefix = "android"
uri = "http://schemas.android.com/apk/res/android"
try:
  ET.register_namespace(prefix, uri)
except AttributeError:
  ET._namespace_map[uri] = prefix

doc = ET.parse(sys.argv[1])
node = doc.find("instrumentation")
for key in node.keys():
  if "targetPackage" in key:
    node.attrib[key] = sys.argv[2]
doc.write(sys.argv[1])
