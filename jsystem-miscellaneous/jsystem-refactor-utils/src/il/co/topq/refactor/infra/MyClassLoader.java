package il.co.topq.refactor.infra;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * We need this class for adding the jar to the class path at run time. We has
 * to extend the URLClassLoader because that the addURL method is protected
 * 
 * @author Agmon
 * 
 */
public class MyClassLoader extends URLClassLoader {

	/**
	 * @param urls
	 *            , to carry forward the existing classpath.
	 */
	public MyClassLoader(URL[] urls) {
		super(urls);
	}

	@Override
	/**
	 * add classptah to the loader.
	 */
	public void addURL(URL url) {
		super.addURL(url);
	}

}
