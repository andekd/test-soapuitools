# test-soapuitools
Repository for externa java tools for the soapui testtool

## Add jars to SoapUI
Copy the two jars in folder 'jars', jxl-2.6.jar and soapuitools.jar to SoapUI:s 'ext' folder.
In a typical installation this folder is locatet at 'C:\Program Files\SmartBear\SoapUI-5.6.0\bin\ext'

jxl-2.6.jar - java library for handling Excel sreadsheets
soapuitools.jar - Utilities defined in this repo. Contains functions for fast setup of data driven tests in SoapUI


## Example of data driven test in soapui (Open Source)
1. Open SoapUI and import the project, soapuitools-soapui-project.xml
2. Open testcase Datadriven Test (in testsuite 'Utils Demo')
