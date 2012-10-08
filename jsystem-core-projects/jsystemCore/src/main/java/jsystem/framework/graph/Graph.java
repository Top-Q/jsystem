/*
 * Created on Oct 13, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.graph;

import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.transform.TransformerException;

import jsystem.framework.report.Reporter;
import junit.framework.SystemTestCase;

import org.apache.xpath.XPathAPI;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * @author guy.arieli
 * 
 */
public class Graph {
	String name;

	String yAxiesName;

	String xAxiesName = "Second";

	String graphName = "graph";

	NumberTickUnit xAxisTickUnit = null;

	NumberTickUnit yAxisTickUnit = null;

	Font textFont = new Font("sansserif", Font.PLAIN, 14);

	Font tickFont = new Font("sansserif", Font.PLAIN, 8);

	Range xAxisRange = null;

	Range yAxisRange = null;

	HashMap<String, XYSeries> series = new HashMap<String, XYSeries>();

	/**
	 * Get the Graph's name that will appear as Graph's head line
	 * 
	 * @return : name as String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the Graph's name that will appear as Graph's head line
	 * 
	 * @param name :
	 *            as String
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the X Axis name that will appear as X Axis head line
	 * 
	 * @return : name as String
	 */
	public String getXAxiesName() {
		return xAxiesName;
	}

	/**
	 * Set the X Axis name that will appear as X Axis head line
	 * 
	 * @param axiesName :
	 *            as String
	 */
	public void setXAxiesName(String axiesName) {
		xAxiesName = axiesName;
	}

	/**
	 * Get the Y Axis name that will appear as Y Axis head line
	 * 
	 * @return : name as String
	 */
	public String getYAxiesName() {
		return yAxiesName;
	}

	/**
	 * Set the Y Axis name that will appear as Y Axis head line
	 * 
	 * @param axiesName :
	 *            as String
	 */
	public void setYAxiesName(String axiesName) {
		yAxiesName = axiesName;
	}

	/**
	 * Ctor for Graph
	 * 
	 * @param name
	 *            (String) the Graph's name that will appear as Graph's head
	 *            line
	 * @param yAxiesName
	 *            (String) the Y Axis name that will appear as Y Axis head line
	 */
	public Graph(String name, String yAxiesName) {
		this.name = name;
		this.yAxiesName = yAxiesName;
	}

	/**
	 * Ctor for Graph
	 * 
	 * @param name
	 *            (String) the Graph's name that will appear as Graph's head
	 *            line
	 * @param yAxiesName
	 *            (String) the Y Axis name that will appear as Y Axis head line
	 * @param graphName
	 *            (String) a name to be added to the Graph's file-name for
	 *            uniqeness
	 */
	public Graph(String name, String yAxiesName, String graphName) {
		this.name = name;
		this.yAxiesName = yAxiesName;
		this.graphName = graphName;
	}

	/**
	 * Add a new value (point on the graph) to the Graph on a specific series
	 * 
	 * @param seriesName
	 *            (String) the name of the series ("line" in the graph)
	 * @param time
	 *            (long) the X Axis value
	 * @param value
	 *            (double) the Y Axis value
	 */
	public void add(String seriesName, long time, double value) {
		if (seriesName == null) {
			seriesName = "Y";
		}
		XYSeries s = (XYSeries) series.get(seriesName);
		if (s == null) {
			s = new XYSeries(seriesName);
			series.put(seriesName, s);
		}
		s.add(time, value);
	}
	
	/**
	 * Remove a graph (line)
	 * 
	 * @param seriesName	the graph name
	 */
	public void removeGraph(String seriesName){
		series.remove(seriesName);
	}

	/**
	 * Add a new value (point on the graph) to the Graph on a specific series
	 * will add the X Axis as the system's current time millis
	 * 
	 * @param seriesName
	 *            (String) the name of the series ("line" in the graph)
	 * @param value
	 *            (double) the Y Axis value
	 */
	public void add(String seriesName, double value) {
		add(seriesName, System.currentTimeMillis(), value);
	}

	/**
	 * Sets the X Axis ticks to be integers with the requestad gap example:
	 * setXAxisNumberTickUnitInteger(5): will appear as (0,5,10...)
	 * 
	 * @param tickUnit -
	 *            the requestad gap between two ticks
	 */
	public void setXAxisNumberTickUnitInteger(int tickUnit) {
		xAxisTickUnit = new NumberTickUnit(tickUnit, NumberFormat.getIntegerInstance());
	}

	/**
	 * Sets the Y Axis ticks to be integers with the requestad gap example:
	 * setYAxisNumberTickUnitInteger(5): will appear as (0,5,10...)
	 * 
	 * @param tickUnit -
	 *            the requestad gap between two ticks
	 */
	public void setYAxisNumberTickUnitInteger(int tickUnit) {
		yAxisTickUnit = new NumberTickUnit(tickUnit, NumberFormat.getIntegerInstance());
	}

	/**
	 * Sets the range for the the X Axis example: setXAxisBounds(-5,5): the
	 * range will be(-5,-4...4,5)
	 * 
	 * @param lowBound -
	 *            the lowest value for the X Axis
	 * @param highBound -
	 *            the highest value for the X Axis
	 */
	public void setXAxisBounds(double lowBound, double highBound) {
		if (lowBound != highBound) {
			if (highBound < lowBound) {
				double d = lowBound;
				lowBound = highBound;
				highBound = d;
			}
			xAxisRange = new Range(lowBound, highBound);
		}
	}

	/**
	 * Sets the range for the the Y Axis example: setYAxisBounds(-5,5): the
	 * range will be(-5,-4...4,5)
	 * 
	 * @param lowBound -
	 *            the lowest value for the Y Axis
	 * @param highBound -
	 *            the highest value for the Y Axis
	 */
	public void setYAxisBounds(double lowBound, double highBound) {
		if (lowBound != highBound) {
			if (highBound < lowBound) {
				double d = lowBound;
				lowBound = highBound;
				highBound = d;
			}
			yAxisRange = new Range(lowBound, highBound);
		}
	}

	public void show() throws Exception {
		show(SystemTestCase.report);
	}

	/**
	 * build, renders, save graph as file and add a link in the report
	 * 
	 * @param reporter
	 *            the "Reporter" object as declared in the tests
	 * @throws Exception
	 */
	public void show(Reporter reporter) throws Exception {
		XYSeriesCollection dataset = new XYSeriesCollection();
		Iterator<XYSeries> iter = series.values().iterator();
		while (iter.hasNext()) {
			dataset.addSeries(iter.next());
		}

		NumberAxis xAxis = new NumberAxis(xAxiesName);
		xAxis.setLabelFont(textFont);
		xAxis.setTickLabelFont(tickFont);
		if (xAxisTickUnit != null) {
			xAxis.setTickUnit(xAxisTickUnit);
		}
		xAxis.setAutoRange(true);

		if (xAxisRange != null) {
			xAxis.setRange(xAxisRange);
		}

		NumberAxis yAxis = new NumberAxis(yAxiesName);
		yAxis.setLabelFont(textFont);
		yAxis.setTickLabelFont(tickFont);
		if (yAxisTickUnit != null) {
			yAxis.setTickUnit(yAxisTickUnit);
		}

		if (yAxisRange != null) {
			yAxis.setRange(yAxisRange);
		}

		XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
		renderer.setBaseItemLabelFont(textFont);
		renderer.setItemLabelFont(textFont);
		XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
		plot.setOrientation(PlotOrientation.VERTICAL);
		renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		JFreeChart chart = new JFreeChart(name, textFont, plot, true);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ChartUtilities.writeChartAsPNG(out, chart, 600, 450, false, 9);
		String file = graphName + "." + (System.currentTimeMillis() % 1000) + ".png";

		reporter.saveFile(file, out.toByteArray());

		reporter.addLink(name, file);
	}

	public byte[] getImageAsByteArray() throws IOException {
		XYSeriesCollection dataset = new XYSeriesCollection();
		Iterator<XYSeries> iter = series.values().iterator();
		while (iter.hasNext()) {
			dataset.addSeries(iter.next());
		}
		JFreeChart chart = ChartFactory.createXYLineChart(name, xAxiesName, yAxiesName, dataset,
				PlotOrientation.VERTICAL, true, true, false);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ChartUtilities.writeChartAsPNG(out, chart, 600, 450);

		return out.toByteArray();
	}

	/**
	 * turn graph into xml Element and add it to a given document.
	 * 
	 * @param doc
	 * @throws TransformerException
	 */
	public void addGraphToDocument(Document doc) throws TransformerException {
		Iterator<XYSeries> iter = series.values().iterator();
		Element graphElem = doc.createElement("graph");
		graphElem.setAttribute("name", this.getName());

		while (iter.hasNext()) {
			XYSeries currentSeries = iter.next();

			for (int i = 0; i < currentSeries.getItemCount(); i++) {
				Element xAxis = doc.createElement("xAxis");
				Element yAxis = doc.createElement("yAxis");

				xAxis.setAttribute("series", (String) (currentSeries.getKey()));
				yAxis.setAttribute("series", (String) (currentSeries.getKey()));

				Text xValue = doc.createTextNode(((XYDataItem) currentSeries.getItems().get(i)).getX().toString());
				Text yValue = doc.createTextNode(((XYDataItem) currentSeries.getItems().get(i)).getY().toString());

				xAxis.appendChild(xValue);
				yAxis.appendChild(yValue);

				graphElem.appendChild(xAxis);
				graphElem.appendChild(yAxis);
			}
		}

		Element rootElem = (Element) XPathAPI.selectSingleNode(doc, "/testGraphs");
		rootElem.appendChild(graphElem);
	}

	public Font getTextFont() {
		return textFont;
	}

	public void setTextFont(Font textFont) {
		this.textFont = textFont;
	}

	public Font getTickFont() {
		return tickFont;
	}

	public void setTickFont(Font tickFont) {
		this.tickFont = tickFont;
	}
}
