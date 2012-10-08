/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils.datasource;

import java.util.Random;

/**
 * Support for the following modes: --------------------------------------- INC (<start
 * val>, <step>, <end val>) INC (<start val>, <step>, <end val>) RAND(<start
 * val>, <end val>) ---------------------------------------- FIXED: will be
 * writen as a String number: "6". LIST: will be writen as list of String nums:
 * "6,5,7,15" ----------------------------------------
 * 
 * @author Uri.Koaz
 * 
 */
public class DataSource {
	int type;

	String currentCommand;

	int oneCycleLength = 1;

	double start;

	double step;

	double end;

	double currentVal;

	int currentIndex = 0;

	boolean firstRun = true;

	String[] values = null;

	Random rand;

	protected final static int TYPE_NONE = 0;

	protected final static int TYPE_INC = 1;

	protected final static int TYPE_DEC = 2;

	protected final static int TYPE_RAND = 3;

	protected final static int TYPE_FIXED = 4;

	protected final static int TYPE_LIST = 5;

	public DataSource(String command) {
		type = TYPE_NONE;
		currentCommand = command;

		init();
	}

	public void init() {
		firstRun = true;

		/**
		 * reducing all spaces.
		 */
		String[] temp = currentCommand.split(" ");

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < temp.length; i++) {
			sb.append(temp[i]);
		}

		currentCommand = sb.toString();

		String[] commandParts = null;

		try {
			commandParts = currentCommand.split("\\(");
			values = commandParts[1].split(",");
		} catch (Exception e) {
			if (currentCommand.indexOf(",") == -1) {
				type = TYPE_FIXED;
				currentVal = Double.parseDouble(currentCommand);
			} else {
				type = TYPE_LIST;
				values = currentCommand.split(",");
				currentVal = Double.parseDouble(values[0]);
			}
		}

		if (commandParts != null) {
			if (commandParts[0].equals("INC") || commandParts[0].equals("DEC")) {

				if (commandParts[0].equals("INC")) {
					type = TYPE_INC;
				} else {
					type = TYPE_DEC;
				}

				start = Double.parseDouble(values[0]);
				step = Double.parseDouble(values[1]);
				end = Double.parseDouble(values[2].split("\\)")[0]);

				currentVal = start;
			}

			if (commandParts[0].equals("RAND")) {
				type = TYPE_RAND;

				start = Double.parseDouble(values[0]);
				end = Double.parseDouble(values[1].split("\\)")[0]);

				Random rand = new Random();
				rand.setSeed((long) start);
				currentVal = (rand.nextDouble() * end) + start;
			}
		}

		setOneCycleLength();
	}

	/**
	 * return the current value as int.
	 * 
	 * @return int value
	 * @throws Exception
	 */
	public int getInt() throws Exception {
		if (type == TYPE_NONE) {
			throw new Exception("Invalid DataSource Command: " + currentCommand);
		}

		return (int) currentVal;
	}

	public double getDouble() {
		return currentVal;
	}

	/**
	 * returning the next value for any mode. note that for fixed value the next
	 * value will always be the same.
	 * 
	 * @return next value a as double.
	 * @throws Exception
	 */
	public double getNextValue() throws Exception {
		switch (type) {
		case TYPE_INC:
			currentVal = ((currentVal + step) % (end + 1));
			if (currentVal == 0) {
				currentVal = start;
			}
			break;

		case TYPE_DEC:
			currentVal = ((currentVal - step) % (start));
			if (currentVal < 0) {
				currentVal = start;
			}
			break;

		case TYPE_RAND:
			currentVal = (rand.nextDouble() * end) + start;
			break;

		case TYPE_LIST:
			currentIndex++;

			if (currentIndex >= values.length) {
				currentIndex = 0;
			}
			currentVal = Integer.parseInt(values[currentIndex].trim());

			break;
		}

		if (type == TYPE_NONE) {
			throw new Exception("Invalid DataSource Command: " + currentCommand);
		}

		return currentVal;
	}

	public String getCurrentCommand() {
		return currentCommand;
	}

	public void setCurrentCommand(String currentCommand) throws Exception {
		this.currentCommand = currentCommand;
		init();
	}

	private void setOneCycleLength() {
		if (type == TYPE_INC || type == TYPE_DEC) {
			oneCycleLength = (int) (Math.abs(end - start) / step);
		}
		if (type == TYPE_LIST) {
			oneCycleLength = values.length;
		}
		if (type == TYPE_FIXED) {
			oneCycleLength = 1;
		}
	}

	public int getOneCycleLength() {
		return oneCycleLength;
	}
}