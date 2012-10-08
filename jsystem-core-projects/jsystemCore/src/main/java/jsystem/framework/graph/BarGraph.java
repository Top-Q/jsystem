/*
 * Created on Oct 13, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.graph;

import java.io.ByteArrayOutputStream;

import jsystem.framework.report.Reporter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * @author guy.arieli
 * 
 */
public class BarGraph {
	String name;

	String yAxiesName = "Values";

	String xAxiesName = "Category";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getXAxiesName() {
		return xAxiesName;
	}

	public void setXAxiesName(String axiesName) {
		xAxiesName = axiesName;
	}

	public String getYAxiesName() {
		return yAxiesName;
	}

	public void setYAxiesName(String axiesName) {
		yAxiesName = axiesName;
	}

	DefaultCategoryDataset dataset;

	public BarGraph(String name, String yAxiesName) {
		this.name = name;
		this.yAxiesName = yAxiesName;
		this.dataset = new DefaultCategoryDataset();
	}

	public void addValues(String categoryName, String counter, double value) {
		this.dataset.addValue(value, categoryName, counter);
	}

	public void show(Reporter reporter) throws Exception {
		show(reporter, 600, 450);
	}

	public void show(Reporter reporter, int width, int length) throws Exception {

		JFreeChart chart = ChartFactory.createBarChart(name, xAxiesName, yAxiesName, dataset, PlotOrientation.VERTICAL,
				true, true, false);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ChartUtilities.writeChartAsPNG(out, chart, width, length);

		String file = Long.toString(System.currentTimeMillis()) + ".png";
		reporter.saveFile(file, out.toByteArray());
		reporter.addLink(name, file);
		// ChartUtilities.write
	}

	// public byte[] getImageAsByteArray() throws IOException{
	// XYSeriesCollection dataset = new XYSeriesCollection();
	// Iterator iter = series.values().iterator();
	// while(iter.hasNext()){
	// XYSeries s = (XYSeries)iter.next();
	// dataset.addSeries(s);
	// }
	// JFreeChart chart = ChartFactory.createXYLineChart(
	// name,
	// xAxiesName, yAxiesName,
	// dataset,
	// PlotOrientation.VERTICAL,
	// true,
	// true,
	// false
	// );
	// ByteArrayOutputStream out = new ByteArrayOutputStream();
	// ChartUtilities.writeChartAsPNG(out, chart, 600, 450);
	//
	// return out.toByteArray();
	// }
//	public static void tryBarGraph() {
//		Comparable[] seriesNames = new String[] { "2001", "2002" };
//		Comparable[] categoryNames = new String[] { "First Quater", "Second Quater" };
//		double[][] categoryData = new double[][] { { 20, 35 }, { 40, 60 } };
//		CategoryDataset categoryDataset = DatasetUtilities.createCategoryDataset(seriesNames, categoryNames,
//				categoryData);
//
//		JFreeChart chart = ChartFactory.createBarChart("Sample Category Chart", // Title
//				"Quarters", // X-Axis label
//				"Sales", // Y-Axis label
//				categoryDataset, // Dataset
//				PlotOrientation.VERTICAL, true, // Show legend
//				true, false);
//	}

	// /**
	// * turn graph into xml Element and add it to a given document.
	// *
	// * @param graphElem
	// * @throws TransformerException
	// */
	// public void addGraphToDocument(Document doc) throws TransformerException{
	// Iterator iter = series.values().iterator();
	// Element graphElem = doc.createElement("graph");
	// graphElem.setAttribute("name", this.getName());
	//
	// while(iter.hasNext()){
	// XYSeries currentSeries = ((XYSeries)iter.next());
	//
	// for(int i=0 ; i < currentSeries.getItemCount(); i++){
	// Element xAxis = doc.createElement("xAxis");
	// Element yAxis = doc.createElement("yAxis");
	//
	// xAxis.setAttribute("series", (String) (currentSeries.getKey()));
	// yAxis.setAttribute("series", (String) (currentSeries.getKey()));
	//
	// Text xValue = doc.createTextNode(((XYDataItem)
	// currentSeries.getItems().get(i)).getX().toString());
	// Text yValue = doc.createTextNode(((XYDataItem)
	// currentSeries.getItems().get(i)).getY().toString());
	//
	// xAxis.appendChild(xValue);
	// yAxis.appendChild(yValue);
	//
	// graphElem.appendChild(xAxis);
	// graphElem.appendChild(yAxis);
	// }
	// }
	//
	// Element rootElem = (Element)
	// XPathAPI.selectSingleNode(doc,"/testGraphs");
	// rootElem.appendChild(graphElem);
	// }
//	public static void main() {
//		tryBarGraph();
//	}

}
