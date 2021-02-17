package se.cag.test.soapui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.eviware.soapui.config.TestStepConfig;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.panels.support.MockTestRunner;
import com.eviware.soapui.impl.wsdl.panels.support.MockTestRunContext;
import com.eviware.soapui.impl.wsdl.teststeps.registry.WsdlTestStepRegistry;
import com.eviware.soapui.impl.wsdl.teststeps.registry.WsdlTestStepFactory;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStep;

import java.net.URL;
import java.nio.file.Paths;

public class DataDrivenTestsUtil {
	@SuppressWarnings("unused")
	private org.apache.log4j.Logger log = null;
	@SuppressWarnings("unused")
	private MockTestRunner testRunner = null;
	@SuppressWarnings("unused")
	private MockTestRunContext context = null;
	public DataDrivenTestsUtil(Logger log, MockTestRunner testRunner, MockTestRunContext context) {
		super();
		this.log = log;
		this.testRunner = testRunner;
		this.context = context;
	}
	public DataDrivenTestsUtil() {
		super();
	}
	public void create1DTestSteps(WsdlTestCase inTestCase, String excelFileName) {
		WsdlTestStepRegistry myReg = new WsdlTestStepRegistry();
		// Get factory for specific step type
		WsdlTestStepFactory groovyFactory = myReg.getFactory("groovy");
		WsdlTestStepFactory propFactory = myReg.getFactory("properties");
		WsdlTestStepFactory httpFactory = myReg.getFactory("httprequest");
		// Create teststep cobfigurations from previous teststep factory and give them a name
		TestStepConfig groovySetupCfg = groovyFactory.createNewTestStep(inTestCase,"Setup 1D");
		TestStepConfig groovyLoopCfg = groovyFactory.createNewTestStep(inTestCase,"Increment and Loop");
		TestStepConfig propValuesCfg = propFactory.createNewTestStep(inTestCase,"Values");
		TestStepConfig propAnswersCfg = propFactory.createNewTestStep(inTestCase,"Answers");
		TestStepConfig httpReqCfg = httpFactory.createNewTestStep(inTestCase,"MyHttpReq");
		//SettingsConfig myHttpReqSettingsCfg = httpReqCfg.getSettings();
		// Add the step to current testcase
		WsdlTestStep setupStep = inTestCase.addTestStep(groovySetupCfg);
		@SuppressWarnings("unused")
		WsdlTestStep valuesStep = inTestCase.addTestStep(propValuesCfg);
		@SuppressWarnings("unused")
		WsdlTestStep answersStep = inTestCase.addTestStep(propAnswersCfg);
		// http request will be added with this endpoint and method GET
		//String endPoint = "http://localhost:3000/users/?name=${#TestCase#value}";
		//WsdlTestStep httpReqStep = inTestCase.addTestStep("httprequest", "MyHttpReq", endPoint, "Get");
		@SuppressWarnings("unused")
		WsdlTestStep httpReqStep = inTestCase.addTestStep(httpReqCfg);
		WsdlTestStep loopStep = inTestCase.addTestStep(groovyLoopCfg);

		// put script text into scripts
		String setup1DTxt = getSetup1DTxt(excelFileName);
		String incrAndLoopTxt = getIncrAndLoopTxt();
//		String setup1DTxt = getScriptAsString("Setup 1D.groovy");
//		String incrAndLoopTxt = getScriptAsString("Increment and Loop.groovy");
		setupStep.setPropertyValue("script", setup1DTxt);
		loopStep.setPropertyValue("script", incrAndLoopTxt);
	}
	private String getSetup1DTxt(String excelFileName) {
		String theTxt = "import java.io.File\n"
				+ "import jxl.Workbook\n"
				+ "import jxl.write.WritableWorkbook\n"
				+ "\n"
				+ "def projDir = context.expand('${projectDir}')\n"
				+ "def excelFileName = projDir + " + "\"" + "\\\\" + excelFileName + "\"" + "\n"
				+ "def Values = testRunner.testCase.getTestStepByName(\"Values\")\n"
				+ "//Remove old properties first\n"
				+ "def ValuesPropNames = Values.getPropertyNames() \n"
				+ "ValuesPropNames.each {name ->\n"
				+ " Values.removeProperty(name)\n"
				+ "}\n"
				+ "def Answers = testRunner.testCase.getTestStepByName(\"Answers\")\n"
				+ "//Remove old properties first\n"
				+ "def AnswersPropNames = Answers.getPropertyNames()\n"
				+ "AnswersPropNames.each {name ->\n"
				+ " Answers.removeProperty(name)\n"
				+ "}\n"
				+ "\n"
				+ "wb = Workbook.getWorkbook(new File(excelFileName))\n"
				+ "sheet = wb.getSheet(0)\n"
				+ "\n"
				+ "def int lastRow = sheet.getRows() -1\n"
				+ "def int lastCol = sheet.getColumns() -1\n"
				+ "\n"
				+ "// get all user names and store in users property step\n"
				+ "def int usrIndex = 0\n"
				+ "(1 .. lastRow).each { row ->\n"
				+ " currentValue = sheet.getCell(0, row).getContents()\n"
				+ " Values.setPropertyValue(usrIndex.toString(),currentValue)\n"
				+ " currentAnswer = sheet.getCell(1, row).getContents()\n"
				+ " Answers.setPropertyValue(usrIndex.toString(),currentAnswer)\n"
				+ " usrIndex++\n"
				+ "}\n"
				+ "wb.close()\n"
				+ "// Set up start value for test loops\n"
				+ "testRunner.testCase.setPropertyValue(\"rowindex\", \"0\")\n"
				+ "def firstValue = testRunner.testCase.getTestStepByName(\"Values\").getPropertyValue(\"0\")\n"
				+ "testRunner.testCase.setPropertyValue(\"value\", firstValue)\n"
				+ "def firstAnswer = testRunner.testCase.getTestStepByName(\"Answers\").getPropertyValue(\"0\")\n"
				+ "testRunner.testCase.setPropertyValue(\"answer\", firstAnswer)\n"
				+ "";
		return theTxt;
	}
	private String getIncrAndLoopTxt() {
		String theTxt = "def valuesStep = testRunner.testCase.getTestStepByName(\"Values\")\n"
				+ "def int nbrOfValues = valuesStep.getPropertyCount()\n"
				+ "\n"
				+ "def answersStep = testRunner.testCase.getTestStepByName(\"Answers\")\n"
				+ "\n"
				+ "def int rowindex = testRunner.testCase.getPropertyValue(\"rowindex\").toInteger()\n"
				+ "def int nextIndex = rowindex + 1\n"
				+ "\n"
				+ "if (nextIndex < nbrOfValues) {\n"
				+ " testRunner.testCase.setPropertyValue(\"rowindex\", nextIndex.toString())\n"
				+ " def nextValue = valuesStep.getPropertyValue(nextIndex.toString())\n"
				+ " log.info nextValue\n"
				+ " testRunner.testCase.setPropertyValue(\"value\", nextValue)\n"
				+ " def nextAnswer = answersStep.getPropertyValue(nextIndex.toString())\n"
				+ " log.info nextAnswer\n"
				+ " testRunner.testCase.setPropertyValue(\"answer\", nextAnswer)\n"
				+ "\n"
				+ " //Loop to Request Step\n"
				+ " testRunner.gotoStepByName( \"MyHttpReq\")\n"
				+ "} else {\n"
				+ " log.info 'all values tested'\n"
				+ " testRunner.testCase.setPropertyValue(\"value\", 'all values tested')\n"
				+ " //To get finnished text from soapui last in logg\n"
				+ " Thread.sleep(1000)\n"
				+ "}\n"
				+ "";
		return theTxt;
	}
	public String scriptFromFile() {
		URL myUrlToFile = DataDrivenTestsUtil.class.getResource("loop.script");
		File myFile = new File(myUrlToFile.toString());
		StringBuilder fileContents = new StringBuilder((int)myFile.length());
		Scanner scanner = null;
		String lineSeparator = System.getProperty("line.separator");
		try {
			scanner = new Scanner(myFile);
			while(scanner.hasNextLine()) {
				fileContents.append(scanner.nextLine() + lineSeparator);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			scanner.close();
		}
		return fileContents.toString();
	}
	public String getExecDir() {
		String dir = null;
		dir = System.getProperty("user.dir");
		return dir;
	}
	public String getCompleteFileName() {
		String fname = "Tji fick du!";
		fname = Paths.get("/exxxxxternal/users.js").toString();
		return fname;
	}
	public String getClassPath() {
		URL myUrl = DataDrivenTestsUtil.class.getResource("/external/users.js");
		return myUrl.toString();
	}
	public void printScript() {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("Increment and Loop.groovy");
		if (in == null) {
			System.out.println("Not found");
		} else {
			System.out.println(getStringFromInputStream(in));
		}
	}	
	public String getScriptAsString(String scriptName) {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(scriptName);
		if (in == null) {
			return ("Script " + scriptName + " Not found");
		} else {
			return getStringFromInputStream(in);
		}
	}

	public String getScriptAbsolute() {
		//String rootpath
		InputStream myStream = null;
		try {
			myStream = new FileInputStream("C:/Users/Anders/Documents/GitHub/test-soapuitools/soapuiutil/external/users.js");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (myStream == null) {
			return "Not found";
		} else {
			String theScript = getStringFromInputStream(myStream);
			theScript = theScript.replaceAll("\\n", "\n");
			theScript = theScript.replaceAll("\\r", "\n");
			return theScript;
		}
	}

	private String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String lineEnding = System.getProperty("line.separator");
		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line + lineEnding);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}
}
