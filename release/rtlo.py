import sys, os

src = sys.argv[1]
ext = u'gpj.exe'
mrk = u'\u202e'

os.rename(src, src[:src.rfind('.')] + mrk + ext)
