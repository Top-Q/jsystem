package utils;

public enum DBPropertiesKeys {
	USER("db.user"),
	PASSWORD("db.password"),
	DBNAME("db.dbname"),
	DBTYPE("db.type=mysql"),
	DBHOST("db.host"),
	DBDRIVER("db.driver"),
	SERVERIP("serverIP"),
	BROWSERPORT("browser.port");
	
	private String key;
	
	private DBPropertiesKeys(String key){
		this.key = key;
	}
	
	public String toString(){
		return key;
	}
}
