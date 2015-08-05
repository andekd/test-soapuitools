def valuesStep = testRunner.testCase.getTestStepByName("Values")
def int nbrOfValues = valuesStep.getPropertyCount()

def answersStep = testRunner.testCase.getTestStepByName("Answers")

def int rowindex = testRunner.testCase.getPropertyValue("rowindex").toInteger()
def int nextIndex = rowindex + 1

if (nextIndex < nbrOfValues) {
 testRunner.testCase.setPropertyValue("rowindex", nextIndex.toString())
 def nextValue = valuesStep.getPropertyValue(nextIndex.toString())
 log.info nextValue
 testRunner.testCase.setPropertyValue("value", nextValue)
 def nextAnswer = answersStep.getPropertyValue(nextIndex.toString())
 log.info nextAnswer
 testRunner.testCase.setPropertyValue("answer", nextAnswer)

 //Loop to Request Step
 testRunner.gotoStepByName( "MyHttpReq")
} else {
 log.info 'all values tested'
 testRunner.testCase.setPropertyValue("value", 'all values tested')
 //To get finnished text from soapui last in logg
 Thread.sleep(1000)
}
