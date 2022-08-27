# Copyright 2022 Eric Bresie and friends. All rights reserved.
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#       http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#  Portions of this code are based on 
#     https://github.com/jschendel/python-32bit-testing/blob/master/python_info.py

import platform
import struct
import sys

# display this prior to running python_script.py
print(
    "#" * 100,
#    f"python.command={}",
#    f"python.platform.name={}",
#    f"python.path={}",
    f"python.name={'.'.join(map(str, sys.version_info))}",
    f"python.version_info: {'.'.join(map(str, sys.version_info))}",
    f"python.os: {platform.uname().system.lower()}",
    f"python.platform.version: {platform.version()}",
    f"python.platform.bits: {struct.calcsize('P') * 8}",
    "#" * 100,
    sep="\n"
)