package ru.savior.rateprojection.shell.tgbot.utils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import ru.savior.rateprojection.core.entity.ProjectionDataResponse;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ChartBuilder {
    private static final String chartFileName = "projection_chart.png";

    private static final List<Color> lineColors = new ArrayList<>() {{
        add(Color.BLUE);
        add(Color.GREEN);
        add(Color.orange);
        add(Color.red);
        add(Color.MAGENTA);
    }};


    public static List<String> buildChartFile(List<ProjectionDataResponse> projectionData) {
        List<String> output = new ArrayList<>();
        JFreeChart chart = buildChart(buildDataset(projectionData));
        String chartFilePath = "";
        try {
            chartFilePath = saveChartToFile(chart);
        } catch (IOException exception) {
            output.add("The errors have occurred during generating chart file");
            return output;
        }
        output.add(chartFilePath);
        return output;
    }

    private static String saveChartToFile(JFreeChart chart) throws IOException {
        String filePath = ChartBuilder.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        filePath += chartFileName;
        ChartUtils.saveChartAsPNG(new File(filePath), chart, 800, 500);
        return filePath;
    }

    private static JFreeChart buildChart(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Currency projection",
                "Days",
                "Currency rates",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        DateAxis axis = new DateAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yyyy"));
        plot.setDomainAxis(axis);
        plot.setRenderer(renderer);
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            renderer.setSeriesPaint(i, lineColors.get(i));
            renderer.setSeriesStroke(i, new BasicStroke(2.0f));
        }
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.gray);
        plot.setRangeGridlinesVisible(false);
        plot.setDomainGridlinesVisible(false);
        chart.getLegend().setFrame(BlockBorder.NONE);

        return chart;
    }

    private static XYDataset buildDataset(List<ProjectionDataResponse> projectionData) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        for (ProjectionDataResponse dataResponse : projectionData) {
            dataset.addSeries(buildSeries(dataResponse));
        }
        return dataset;
    }

    private static TimeSeries buildSeries(ProjectionDataResponse projectionData) {
        TimeSeries series = new TimeSeries(projectionData.getProvidedData().get(0).getCurrency().getCurrencyCode());
        for (DailyCurrencyRate dailyCurrencyRate : projectionData.getProvidedData()) {
            series.add(new Day(dailyCurrencyRate.getRateDate().getDayOfMonth(),
                    dailyCurrencyRate.getRateDate().getMonthValue(),
                    dailyCurrencyRate.getRateDate().getYear()), dailyCurrencyRate.getRate());
        }
        return series;
    }

}
