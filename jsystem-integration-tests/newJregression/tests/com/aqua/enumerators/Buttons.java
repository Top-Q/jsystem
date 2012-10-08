package com.aqua.enumerators;

public enum Buttons {

	PLAY("play"),
	STOP("stop"),
	PAUSE("paush"),
	REFRESH("refresh"),
	NEW_SCENARIO("new_scenario"),
	SAVE_SCENARIO("save_scenario"),
	SAVE_SCENARIO_AS("save_scenario_as"),
	DELETE("delete"),
	OPEN_SCENARIO("open_scenario"),
	PREVIOUS_SCENARIO("previous_scenario"),
	NEXT_SCENARIO("next_scenario"),
	MOVE_ITEM_UP("move_item_up"),
	MOVE_ITEM_DOWN("move_item_down"),
	REMOVE("remove"),
	VIEW_TEST_CODE("view_test_code"),
	LOG("log"),
	SWITCH_PROJECT("switch_project");
	
	private String name;
	private Buttons(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
}
