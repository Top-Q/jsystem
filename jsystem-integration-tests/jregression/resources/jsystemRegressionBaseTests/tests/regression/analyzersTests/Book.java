package regression.analyzersTests;

import jsystem.framework.system.SystemObjectImpl;


/**
 * This is a simple example of system object
 * with this system object we will check the analysis tests
 * This system object represents a book object with a book name and a book 
 * author.
 * @author Guy levi
 *
 */
public class Book extends SystemObjectImpl {

	private String name;
	private String author;

	public Book() {

	}

	public Book(String text, String author) {
		setName(text);
		setAuthor(author);
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

