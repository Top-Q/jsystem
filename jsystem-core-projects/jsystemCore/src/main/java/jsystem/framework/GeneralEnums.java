/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework;

public class GeneralEnums {
	public enum RunMode{
		DROP_EVERY_RUN(1),DROP_EVERY_TEST(2),DROP_EVERY_SCENARIO(4);
		
		private int num;
		
		private RunMode(int num){
			this.num = num;
		}
		
		public int getNum(){
			return num;
		}
		
		public static RunMode enumFromNum(int num){
			for (RunMode mode : RunMode.values()){
				if (mode.getNum() == num){
					return mode;
				}
			}
			return null;
		}
		
		public static RunMode getMatchingEnum(String value){
			if (value == null){
				return null;
			}
			RunMode mode;
			try{
				mode = RunMode.valueOf(value);
			}catch (Exception e) { // string is not an enum value - backward compatibility
				try{
					int num = Integer.parseInt(value);
					mode = enumFromNum(num);
				}catch (Exception e1) {
					return null;
				}
			}
			return mode;
		}
	}
	
	public enum CmdExecutor {
		SIMPLE_EXECUTOR,
		ADVANCED_EXECUTOR
	}
}
