/*
 * Created on 15/10/2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import junit.framework.Test;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * this class defines a test. each test has fields such as "comment" and
 * "user-doc" which are added to the XML scenario file. if you add a new field
 * to a test you must update the following:<br>
 * 1. fieldNum (JTest) <br>
 * 2. getAllXmlFields method <br>
 * 3. setXmlFields method <br>
 * 4. writeAllFieldsToElement method <br>
 * 5. load method
 * 
 */
public interface JTest extends Test {

	public static final String commentString = "Comment";

	public static final String userDocString = "User-Doc";

	public static final int fieldNum = 2;
	
	final static String MEANINGFULNAME_PREFIX = "Meaningful name:";
	final static MessageFormat MEANINGFULNAME_FORMAT = new MessageFormat(MEANINGFULNAME_PREFIX+"{0};");  
	final static Pattern MEANINGFULNAME_PATTERN = Pattern.compile(MEANINGFULNAME_PREFIX+"(.*?)\\;");

	/**
	 * gets this test parent
	 * 
	 * @return Scenario parent
	 */
	public JTestContainer getParent();

	/**
	 * sets this test parent
	 * 
	 * @param parent
	 *            the Scenario parent
	 * TODO: parent can be scenario or ant flow control
	 */
	public void setParent(JTestContainer parent);

	/**
	 * check if this test's status is error
	 * 
	 * @return true if error
	 */
	public boolean isError();

	/**
	 * check if this test's status is fail
	 * 
	 * @return true if fail
	 */
	public boolean isFail();

	/**
	 * check if this test's status is running
	 * 
	 * @return true if running
	 */
	public boolean isRunning();

	/**
	 * check if this test's status is success
	 * 
	 * @return true if success
	 */
	public boolean isSuccess();

	/**
	 * check if this test's status is warning
	 * 
	 * @return true if Warning
	 */
	public boolean isWarning();
	
	/**
	 * check if this test's status is not running
	 * 
	 * @return true if Test wasn't ran
	 */
	public boolean isNotRunning();
	
	/**
	 * loads this test attributes from the xml file
	 * 
	 * @throws Exception
	 */
	public void load() throws Exception;

	/**
	 * return an element of this test for xml writing
	 * 
	 * @param doc
	 * @return the XML element represent the object
	 */
	public void addTestsXmlToRoot(Document doc, Integer[] indexes);

	/**
	 * return an executor element of this test for xml writing
	 * TODO: explanation, changed via flow control
	 * @param doc
	 * @return the XML element represent the object
	 */
	public Element addExecutorXml(Element targetScenario, Document doc);
	
	/**
	 * checks if a test is disabled
	 * 
	 * @return true if disabled
	 */
	public boolean isDisable();

	/**
	 * sets a test to be disabled/enabled
	 * 
	 * @param disable
	 *            (true = disable , false= enable)
	 */
	public void setDisable(boolean disable);

	/**
	 * get the test parameters
	 * 
	 * @return a Parameter array containing all relevant parameters
	 */
	public Parameter[] getParameters();
	
	/**
	 * In order to filter some of parameters
	 * @param recursively 
	 * @return
	 */
	public Parameter[] getVisibleParamters() ;
	
	/**
	 * In order to filter some of parameters
	 * @param recursively 
	 * @return
	 */
	public Parameter[] getVisibleParamters(boolean recursively) ;

	/**
	 * set the test Parameters
	 * 
	 */
	public void setTestClassParameters();

	/**
	 * get the User Documentation for this test
	 * 
	 * @return Documentation
	 */
	public String getDocumentation();

	/**
	 * set the User Documentation for this test
	 * 
	 * @param documentation
	 *            the text to write
	 */
	public void setDocumentation(String documentation);

	/**
	 * get the test name + comment
	 * 
	 * @return current node name
	 */
	public String getTestName();

	/**
	 * the name to be written on the tree node saved to file under attribute
	 * "comment"
	 * 
	 * @param comment
	 */
	public void setTestComment(String comment);

	/**
	 * get the tests comment
	 */
	public String getComment();

	/**
	 * setting the sorting Object for the ParametersPanel use
	 * 
	 * @param sort
	 *            the sorting object
	 */
	public void setPresentationObject(PresentationDefinitions sort);

	/**
	 * getting the sorting object for the ParametersPanel use
	 * 
	 * @return the sorting definitions
	 */
	public PresentationDefinitions getPresentationObject();

	/**
	 * 
	 * @return the ID of the test/scenario
	 */
	public String getTestId();

	/**
	 * Set the ID of the test
	 * @param testId the test ID
	 */
	public void setTestId(String testId);

	/**
	 * get the test fields
	 * 
	 * @return properties containing all the JTest fields
	 */
	public Properties getAllXmlFields();

	/**
	 * sets all the JTest XML fields (Comment,User-Doc....)
	 * 
	 */
	public void setXmlFields(Properties fields);

	
	/**
	 * Sets the fixtures which is associated to the test entity. 
	 * @param fixtureClassName
	 */
	public void setFixture(String fixtureClassName) throws Exception;
	
	/**
	 * Updates test parameters with params
	 * Note that only parameters which are marked with the isDirty
	 * flag will be updated.
	 * @param recursively
	 */
	public void setParameters(Parameter[] params);
	
	/**
	 * Updates test parameters with params
	 * Note that only parameters which are marked with the isDirty
	 * flag will be updated.
	 * @param recursively
	 */
	public void setParameters(Parameter[] params, boolean recursively);
	
	/**
	 * Signals the test entity to populate it's parameters.
	 */
	public void loadParametersAndValues();
	/**
	 * Universal unique ID for all test objects
	 * @return the unique ID.
	 */
	public String getUUID();

	/**
	 * clone the test. Needed to support having the same scenario multiple times
	 * See: MultipleScenarioOps 
	 * @return
	 */
	public JTest cloneTest() throws Exception;
	
	/**
	 * return the full path (from scenario down) ID
	 */
	public String getFullTestId();
	

	// TODO: is there a way to declare an implementation must have a static field ?
	// Currently we will have XML_TAG in every JTest, without forcing it...
	/**
	 * set the test Unique Id
	 * 
	 * @param uuid
	 */
	public void setUUID(String uuid);
	
	/**
	 * Universal unique Full path ID for all test objects
	 * @return the unique ID.
	 */
	public String getFullUUID();
	
	/**
	 * Universal unique Full path ID Up to a given scenario
	 * @return the unique ID.
	 */
	public String getUUIDUpTo(JTest toStopAt);

	/**
	 * Associates test with distributed execution parameters 
     */
	public void setDistributedExecutionParameters(DistributedExecutionParameter[] parameters) throws Exception;

	/**
	 * Returns tests distributed execution parameters
     */	
	public DistributedExecutionParameter[] getDistributedExecutionParameters() throws Exception;
	
	/**
	 * Returns test parameters as ant properties
     */
	public Properties getPropertiesInAntCanonicalFormat() throws Exception;

	/**
	 * Add the Ant properties to the Ant element
	 * Used so only the properties of the tests in the whole scenario (but not inside sub scenarios)
	 * will be added
	 * @param ant
	 * @param doc
	 */
	public void addAntPropertiesToTarget(Element ant, Document doc);
	
	/**
	 * @return the First parent scenario
	 */
	public Scenario getMyScenario();
	
	/**
	 * @return the First parent scenario
	 */
	public Scenario getParentScenario();
	
	public Scenario getRoot();
	
	public void update() throws Exception;
	
	public void markAsKnownIssue(boolean isKnownIssue);
	
	public boolean isMarkedAsKnownIssue();
	
	/**
	 * Mark\UnMark a test is a Negative test (Failures will pass, pass will fail)
	 * 
	 * @param isNegativeTest	True wil mark as Negative
	 */
	public void markAsNegativeTest(boolean isNegativeTest);
	
	/**
	 * @return	true if a test is marked as Negative test
	 */
	public boolean isMarkedAsNegativeTest();

	public void hideInHTML(boolean isHideInHTML);
	
	public boolean isHiddenInHTML();

	public void addValidationError(ValidationError error);
	
	public List<ValidationError> getValidationErrors();
	
	public boolean isValidationErrorsFound();
	
	/**
	 * 
	 * @param meaningfulName	the test meaningful name
	 * @param saveToPropFile	true will be used when value should be saved to properties file (was changed in root scenario context)
	 */
	public void setMeaningfulName(String meaningfulName, boolean saveToPropFile);

	public String getMeaningfulName();
	/**
	 * Implement the return to default option in the right menu of the scenario tree, when running this method the parameter of the test\Scenario will be removed from the property file
	 * @throws Exception
	 */
	//Limor Bortman
	public void resetToDefault() throws Exception;
}

