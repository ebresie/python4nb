# Based on  https://pythonhosted.org/an_example_pypi_project/setuptools.html

import os
from setuptools import setup

# Utility function to read the README file.
# Used for the long description.  It's nice, because now 1) we have a top level
# README file and 2) it's easier to type in the README file than to put a raw
# string in below ...
def read(fname):
    return open(os.path.join(os.path.dirname(__file__), fname)).read()

setup(
    name = "<pypi_project>",
    version = "<version>",
    author = "<author name>r",
    author_email = "<author email>",
    description = ("<project description>"),
    license = "<licenses>",
    keywords = "<keywords associated with project",
    url = "<project URL<",
    packages=['<packages>', .., '<test packages>'],
    long_description=read('README'),
    classifiers=[ <classifiers>],
)