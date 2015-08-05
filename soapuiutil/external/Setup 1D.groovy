import java.io.File
import jxl.Workbook
import jxl.write.WritableWorkbook

def projDir = context.expand('${projectDir}')
def excelFileName = projDir + "\\Users.xls"
def Values = testRunner.testCase.getTestStepByName("Values")
//Remove old properties first
def ValuesPropNames = Values.getPropertyNames() 
ValuesPropNames.each {name ->
 Values.removeProperty(name)
}
def Answers = testRunner.testCase.getTestStepByName("Answers")
//Remove old properties first
def AnswersPropNames = Answers.getPropertyNames()
AnswersPropNames.each {name ->
 Answers.removeProperty(name)
}

wb = Workbook.getWorkbook(new File(excelFileName))
sheet = wb.getSheet(0)

def int lastRow = sheet.getRows() -1
def int lastCol = sheet.getColumns() -1

// get all user names and store in users property step
def int usrIndex = 0
(1 .. lastRow).each { row ->
 currentValue = sheet.getCell(0, row).getContents()
 Values.setPropertyValue(usrIndex.toString(),currentValue)
 currentAnswer = sheet.getCell(1, row).getContents()
 Answers.setPropertyValue(usrIndex.toString(),currentAnswer)
 usrIndex++
}
wb.close()
// Set up start value for test loops
testRunner.testCase.setPropertyValue("rowindex", "0")
def firstValue = testRunner.testCase.getTestStepByName("Values").getPropertyValue("0")
testRunner.testCase.setPropertyValue("value", firstValue)
def firstAnswer = testRunner.testCase.getTestStepByName("Answers").getPropertyValue("0")
testRunner.testCase.setPropertyValue("answer", firstAnswer)
