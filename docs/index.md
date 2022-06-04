# python4nb

This is Python Plugin for [Apache Netbeans](https://netbeans.apache.org/) which leverages textmate grammar and LSP based functionality.

The source is being published for further community support and development opportunities.

This is in early development with more work to follow.  

Once more development is more mature, expectations is a release will be available here and/or be published to the [Netbeans Plugin Portal]( https://plugins.netbeans.apache.org/).

## Available Features:
- Python Colorization (via textmate grammar)
- Auto completion leveraging the Python LSP 
- Python Hints/Tips leveraging the Python LSP
- Python Platform Configuration and Installation
- Python Execution Functionality

# How to install it
- Installation requires [Python](https://www.python.org/) to be installed
- Installation of following python LSP related module to be installed in given python environment via:
-- python -m pip install python-language-server[all]
--- For additional details on the [Python LSP server](https://pypi.org/project/python-language-server/)
- Compile python4nb from source and install module within given Netbeans setup 
- Open Tools...Python Platform...select python instance where LSP server module installed

# Change process
- Changes will be tracked using [project's github issues](https://github.com/ebresie/python4nb/issues).
- Upon completion of updates for given issues, a pull request will be made, reviewed, and upon completion integrated into the master branch in github for coming release.

# Change log
## Release 0.2
- [Python Platform Configuration and Installation](https://github.com/ebresie/python4nb/issues/3)
- [Python Execution Functionality](https://github.com/ebresie/python4nb/issues/4)

## Release 0.1
- Python Colorization (via textmate gramer)
- Auto completion leveraging the Python LSP 
- Python Hints/Tips leveraging the Python LSP

For version, will attempt to comply with major/minor/patch versioning as defined [here](https://semver.org/)

## TODO (For more specifics details, see [project issue area](https://github.com/ebresie/python4nb/issues):
- [Complete Python Project functionality](https://github.com/ebresie/python4nb/issues/8)
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

This page is based on README.md for the project and can be modified using [editor on GitHub](https://github.com/ebresie/python4nb/edit/gh-pages/docs/index.md).  Commits to this repository, GitHub Pages will run [Jekyll](https://jekyllrb.com/) to rebuild the pages on the site, based on markdown files.

# References
- For more details see [Basic writing and formatting syntax](https://docs.github.com/en/github/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax) 
- For additional Jekyll themes see [repository settings](https://github.com/ebresie/python4nb/settings/pages) which will be saved in the Jekyll `_config.yml` configuration file.  
- For additional support on github pages content see [documentation](https://docs.github.com/categories/github-pages-basics/) or [contact github support](https://support.github.com/contact).
