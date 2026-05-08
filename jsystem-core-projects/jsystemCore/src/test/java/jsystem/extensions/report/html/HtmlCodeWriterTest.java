package jsystem.extensions.report.html;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;

import org.junit.Test;

public class HtmlCodeWriterTest {

	@Test
	public void testFormatSourceAsHtmlUsesJHighlight() throws Exception {
		File source = File.createTempFile("HtmlCodeWriterTest", ".java");
		try {
			FileWriter writer = new FileWriter(source);
			try {
				writer.write("public class HtmlCodeWriterTest {\n");
				writer.write("\tpublic void sampleMethod() {\n");
				writer.write("\t\tString value = \"hello\";\n");
				writer.write("\t}\n");
				writer.write("}\n");
			} finally {
				writer.close();
			}

			String html = HtmlCodeWriter.formatSourceAsHtml(source);

			assertTrue(html.contains("sampleMethod"));
			assertTrue(html.contains("<span"));
			assertFalse(html.contains("de.java2html"));
		} finally {
			source.delete();
		}
	}
}
