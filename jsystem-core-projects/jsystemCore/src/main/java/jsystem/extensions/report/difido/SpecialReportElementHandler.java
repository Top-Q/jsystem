package jsystem.extensions.report.difido;

import il.co.topq.difido.model.execution.TestNode;
import jsystem.framework.JSystemProperties;
import jsystem.utils.StringUtils;

/**
 * Since JSystem creates a few annoying elements that are messed with HTML
 * elements, and since some important information like the class
 * documentation is not in the testInfo but received as a regular report
 * element, there is a need for a class to handle all the unusual elements.
 * 
 * @author Itai Agmon
 * 
 */
class SpecialReportElementsHandler {

	private final static String SPAN_OPEN_TAG = "<span class=";
	private final static String SPAN_CLOSE_TAG = "</span>";
	private final static String SPAN_OPEN_CLASS_DOC_TAG = "<span class=\"class_doc\">";
	private final static String SPAN_OPEN_TEST_DOC_TAG = "<span class=\"test_doc\">";
	private final static String SPAN_OPEN_USER_DOC_TAG = "<span class=\"user_doc\">";
	private final static String SPAN_OPEN_BREADCRUMBS_TAG = "<span class=\"test_breadcrumbs\">";

	private final static int NONE = 0;
	private final static int USER_DOC = 1;
	private final static int CLASS_DOC = 2;
	private final static int TEST_DOC = 3;
	private final static int TEST_BREADCUMBS = 4;

	private int elementData = NONE;
	private int spanTrace;
	private boolean skipReportElement;
	private final TestNode test;
	
	

	public SpecialReportElementsHandler(TestNode test) {
		super();
		this.test = test;
	}



	/**
	 * We don't want to add the span class in the title, so we filter it. We
	 * also add all kind of important information that exists inside the
	 * span, like the user doc and such directly to the test details.
	 * 
	 * @param title
	 * @return true of valid element that should be added to the test
	 *         details.
	 */
	boolean isValidAndHandleSpecial(String title) {
		if (skipReportElement) {
			skipReportElement = false;
			return false;
		}

		switch (elementData) {
		case NONE:
			break;
		case CLASS_DOC:
			test.addProperty("Class Documentation", title);
			test.setDescription(title);
			elementData = NONE;
			return false;
		case TEST_DOC:
			test.addProperty("Test Documentation", title);
			test.setDescription(title);
			elementData = NONE;
			return false;
		case USER_DOC:
			test.addProperty("User Documentation", title);
			test.setDescription(title);
			elementData = NONE;
			return false;
		case TEST_BREADCUMBS:
			test.addProperty("Breadcrumb", title.replace("</span>", ""));
			elementData = NONE;
			// This also closes the span
			spanTrace--;
			return false;
		default:
			break;
		}
		if (StringUtils.isEmpty(title)) {
			return false;
		}
		if (title.contains(SPAN_OPEN_TAG)) {
			// ITAI: This is a ugly hack, When we execute from the IDE there
			// is a missing span close tag, so we
			// Never increase the number of the span trace above one.
			if (!(JSystemProperties.getInstance().isExecutedFromIDE() && spanTrace == 1)) {
				spanTrace++;
			}
		}
		if (spanTrace > 0) {
			// In span, let's search for that special elements
			switch (title) {
			case SPAN_OPEN_CLASS_DOC_TAG:
				elementData = CLASS_DOC;
				skipReportElement = true;
				break;
			case SPAN_OPEN_TEST_DOC_TAG:
				elementData = TEST_DOC;
				skipReportElement = true;
				break;
			case SPAN_OPEN_USER_DOC_TAG:
				elementData = USER_DOC;
				skipReportElement = true;
				break;
			case SPAN_OPEN_BREADCRUMBS_TAG:
				elementData = TEST_BREADCUMBS;
				break;
			}
		}
		if (title.contains(SPAN_CLOSE_TAG)) {
			spanTrace--;
			return false;
		}

		// ITAI: When running from the IDE, there are missing span closing
		// tags, so we do not increase the span trace after level one. The
		// result is that the span trace may have a negative value
		return spanTrace <= 0;
	}
}