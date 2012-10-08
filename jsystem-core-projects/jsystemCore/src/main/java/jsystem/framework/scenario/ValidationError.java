/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.util.ArrayList;
import java.util.List;

public class ValidationError {
	public enum Originator {
		TEST, RUNNER;
	}

	private Originator originator;
	private String title;
	private String message;
	private JTest test;

	public Originator getOriginator() {
		return originator;
	}

	public void setOriginator(Originator originator) {
		this.originator = originator;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public JTest getTest() {
		return test;
	}

	public void setTest(JTest test) {
		this.test = test;
	}

	public static String getTitlesAsString(ArrayList<ValidationError> errors, boolean numbered, boolean removeDuplicates) {
		StringBuffer buf = new StringBuffer();

		try {

			if (removeDuplicates) {
				errors = removeDuplicates(errors);
			}

			for (int i = 0; i < errors.size(); i++) {
				ValidationError validationError = errors.get(i);
				if (numbered) {
					buf.append((i + 1) + ". ");
				}

				JTest test = validationError.getTest();

				if (test != null) {
					buf.append("Test=" + validationError.getTest().getTestName());
					Scenario firstScenarioAncestor = ScenarioHelpers.getFirstScenarioAncestor(test);
					if (firstScenarioAncestor != null) {
						buf.append(", In Scenario= " + firstScenarioAncestor.getName() + " ,");
					}
				}
				buf.append(validationError.getTitle());
				buf.append("\n");
			}
		} catch (Exception e) {
			buf.append("failed to get the validation error - " + e.getMessage());
		}

		return buf.toString();

	}

	/**
	 * will remove all the Duplicates ValidationError form the array list. the
	 * array will be unique with Test name and Scenario name
	 * 
	 * @param errors
	 *            - array list of errors ,@see {@link ValidationError}
	 * @return ValidationError with no duplicates items(by Test name and
	 *         Scenario name).
	 * @author Liel.Ran
	 * 
	 */
	private static ArrayList<ValidationError> removeDuplicates(ArrayList<ValidationError> errors) {
		ArrayList<ValidationError> errorsWithoutDups = new ArrayList<ValidationError>();
		// remove Dup's
		for (int i = 0; i < errors.size(); i++) {
			ValidationError validationError = errors.get(i);
			JTest test = validationError.getTest();
			Scenario firstScenarioAncestor = ScenarioHelpers.getFirstScenarioAncestor(test);
			for (int j = 0; j < errors.size(); j++) {
				ValidationError validationErrorCompareTo = errors.get(j);
				JTest compareToTest = validationErrorCompareTo.getTest();
				Scenario compareToFirstScenarioAncestor = ScenarioHelpers.getFirstScenarioAncestor(compareToTest);

				try {

					if (!validationError.equals(validationErrorCompareTo)
							&& test.getTestName().equals(validationErrorCompareTo.getTest().getTestName())
							&& firstScenarioAncestor.getName().equals(compareToFirstScenarioAncestor.getName())) {
						errors.remove(j);
					}

				} catch (Exception e) {
				}

			}
			errorsWithoutDups = errors;
		}
		return errorsWithoutDups;

	}

	public static String collectErrorsDescriptions(ArrayList<ValidationError> errors, boolean full) {
		StringBuffer buf = new StringBuffer();
		for (ValidationError error : errors) {
			buf.append(error.getTitle());
			buf.append("\n");
			if (full) {
				buf.append(error.getMessage());
				buf.append("\n");
			}
		}
		return buf.toString();
	}

	public static void clearValidatorsWithOriginator(List<ValidationError> errors, Originator originator) {
		if (errors != null) {
			for (int i = 0; i < errors.size(); i++) {
				if (originator.equals(errors.get(i).getOriginator())) {
					errors.remove(errors.get(i));
					i--;
				}
			}
		}
	}
}
