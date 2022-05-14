# python4nb

This is Python Plugin for [Apache Netbeans](https://netbeans.apache.org/) which leverages textmate grammar and LSP based functionality.

The source is being published for further community support and development opportunities.

This is in early development with more work to follow.  

Once more development is more mature, expectations is a release will be available here and/or be published to the [Netbeans Plugin Portal]( https://plugins.netbeans.apache.org/).

## Available Features:
- Python Colorization (via textmate grammar)
- Auto completion leveraging the Python LSP 
- Python Hints/Tips leveraging the Python LSP

# How to install it
- Installation requires [Python](https://www.python.org/) to be installed
- Installation of following python LSP related module to be installed in g
-- python -m pip install python-language-server[all]
--- For additional details on the [Python LSP server](https://pypi.org/project/python-language-server/)
- Compile python4nb from source and install module within given Netbeans setup 

# Change process
- Changes will be tracked using [project's github issues](https://github.com/ebresie/python4nb/issues).
- Upon completion of updates for given issues, a pull request will be made, reviewed, and upon completion integrated into the master branch in github for coming release.

# Change log
## Release 0.1
- Python Colorization (via textmate gramer)
- Auto completion leveraging the Python LSP 
- Python Hints/Tips leveraging the Python LSP

For version, will attempt to comply with major/minor/patch versioning as defined [here](https://semver.org/)

## TODO (For more specifics details, see [project issue area](https://github.com/ebresie/python4nb/issues):
- [Complete Python Project functionality](https://github.com/ebresie/python4nb/issues/8)
- [Complete Python Platform Configuration and Installation](https://github.com/ebresie/python4nb/issues/3)
- [Complete Python Execution Functionality](https://github.com/ebresie/python4nb/issues/4)
- [Complete Python Debugging](https://github.com/ebresie/python4nb/issues/5)
- [Complete Python Module Management](https://github.com/ebresie/python4nb/issues/6)
- [Complete Unit Test functionality](https://github.com/ebresie/python4nb/issues/7)
- [Complete Implement CI build/packaging for release](https://github.com/ebresie/python4nb/issues/9)
- [Complete Help and documentation for the python4nb plugin](https://github.com/ebresie/python4nb/issues/10)
- [Complete Development "New Project" Templates](https://github.com/ebresie/python4nb/issues/11)

# License
Project is being developed leveraging [Apache-2.0 license]( https://github.com/ebresie/python4nb/blob/main/LICENSE ) or applicable licenses.
All due diligence has been made to attribute proper licensings where applicable.

Some functionality is based on or leverages functionality from other projects and attempts to comply as applicable.   
- http://nbpython.org
- https://netbeans.apache.org/about/oracle-transition.html
- https://pypi.org/project/python-language-server/

# Contributors
- [Eric Bresie](https://github.com/ebresie)

