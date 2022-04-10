# based on https://github.com/jschendel/python-32bit-testing/blob/master/python_info.py

import platform
import struct
import sys

# display this prior to running python_script.py
print(
    "#" * 100,
    f"python.command={}",
    f"python.platform.name={}",
    f"python.path={}",
    f"python.name={'.'.join(map(str, sys.version_info))}",
    f"python: {'.'.join(map(str, sys.version_info))}",
    f"python.os: {platform.uname().system.lower()}",
    f"python.platform.bits: {struct.calcsize('P') * 8}",
    "#" * 100,
    sep="\n"
)