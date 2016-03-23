/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.AuthorizationServer.telemetry;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.io.*;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author osyniaev
 */
public enum ServerResponseTimeAnalizer {
    ANALIZER;

    private volatile static HashSet<ServerStateModel> serverResponseTimeStatistic = new HashSet<>();
    private static final Logger LOGGER = Logger.getLogger(ServerResponseTimeAnalizer.class);

    public void getResponseTimeStatistic() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (ServerStateModel model : serverResponseTimeStatistic) {
            LOGGER.info("На дату " + sdf.format(model.getCurrentDate().getTime()) + " количество живых подключений " + model.getAliveConnectionsNumber()
                    + " время ответа сервера " + model.getResponseTime() + " милисекунд");
        }
    }

    public void growStatisticsGraficByAwt() {

        JFrame jf = new JFrame("Статистика времени ответа прокси-сервера в зависимости от количества живых подключений");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setSize(800, 800);
        jf.add(new GraficDrower());
        jf.setVisible(true);

    }

    public void drowStatisticsChartByJFreeChart() {
        XYLineChart_AWT chart = new XYLineChart_AWT("Minimal Static Chart", "Time proxy's response Statistics");
        chart.pack();
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);
    }

    public void growStatisticsGraficByJFreeChart() throws IOException {
        new BarChart3D();
    }

    public static HashSet<ServerStateModel> getServerResponseTimeStatistic() {
        return serverResponseTimeStatistic;
    }

    public long getAvgResponseTime() {
        long result = 0;
        int count = 0;
        for (ServerStateModel model : serverResponseTimeStatistic) {
            result += model.getResponseTime();
            count++;
        }
        return result / count;
    }

    public void addcurrentResponseTime(long responseTime, int aliveConnections) {
        serverResponseTimeStatistic.add(new ServerStateModel(responseTime, aliveConnections));
    }

    private static class GraficDrower extends JComponent {

        Graphics2D gr;
        double ox = 100, oy = ServerResponseTimeAnalizer.ANALIZER.getAvgResponseTime() / 10; // ось симметрии параболы
        double step = 50;

        public void paintComponent(Graphics g) {
            gr = (Graphics2D) g;
            gr.setPaint(Color.LIGHT_GRAY);
            gr.setStroke(new BasicStroke((float) 0.2));
            for (int y = 0; y <= this.getWidth(); y += 50) {
                gr.draw(new Line2D.Double(0, y, this.getWidth(), y));
                gr.draw(new Line2D.Double(y, 0, y, this.getHeight()));
            }
            // Рисуем оси
            gr.setPaint(Color.GREEN);
            gr.setStroke(new BasicStroke((float) 5));
            gr.draw(new Line2D.Double(0, oy, this.getWidth(), oy));

            // Рисуем отрезок интервала
            gr.setPaint(Color.RED);
            gr.setStroke(new BasicStroke((float) 5));
            // gr.draw(new Line2D.Double(ox - Math.abs(start), oy, ox + Math.abs(end), oy));
            double x = 100, y = 700;
            for (ServerStateModel model : serverResponseTimeStatistic) {
                gr.draw(new Line2D.Double(x, y, x + step, model.getResponseTime() / 10));
                y = model.getResponseTime() / 10;
                x = x + step;
            }
        }
    }

    public class BarChart3D {

        private final DefaultCategoryDataset dataset;

        public BarChart3D() throws IOException {
            dataset = new DefaultCategoryDataset();
            buildChart();

        }

        public void buildChart() throws IOException {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int i = 1;
            for (ServerStateModel model : serverResponseTimeStatistic) {
                dataset.addValue(model.getResponseTime(), "Время ответа Proxy", i + ": " + sdf.format(model.getCurrentDate().getTime()));
                i++;
            }
            JFreeChart barChart = ChartFactory.createBarChart3D(
                    "Time proxy's response Statistics",
                    "Date",
                    "Time, millisecond",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true, true, false);
            int width = 1200;
            /* Width of the image */
            int height = 800;
            /* Height of the image */

            JFrame frame
                    = new JFrame("MinimalStaticChart");
            // Помещаем график на фрейм
            frame.getContentPane()
                    .add(new ChartPanel(barChart));
            frame.setSize(1300, 900);
            frame.setVisible(true);
            frame.show();

            //File barChart3D = new File("barChart3D.jpeg");
            //ChartUtilities.saveChartAsJPEG(barChart3D, barChart, width, height);
        }

    }

    public class XYLineChart_AWT extends ApplicationFrame {

        public XYLineChart_AWT(String applicationTitle, String chartTitle) {
            super(applicationTitle);
            JFreeChart xylineChart = ChartFactory.createXYLineChart(
                    chartTitle,
                    "Date",
                    "Time, miliseconds",
                    createDataset(),
                    PlotOrientation.VERTICAL,
                    true, true, false);

            ChartPanel chartPanel = new ChartPanel(xylineChart);
            chartPanel.setPreferredSize(new java.awt.Dimension(1200, 750));
            final XYPlot plot = xylineChart.getXYPlot();
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            renderer.setSeriesPaint(0, Color.RED);
            renderer.setSeriesPaint(1, Color.GREEN);
            renderer.setSeriesStroke(0, new BasicStroke(4.0f));
            renderer.setSeriesStroke(1, new BasicStroke(3.0f));
            plot.setRenderer(renderer);
            setContentPane(chartPanel);
        }

        private XYDataset createDataset() {
            final XYSeries actual = new XYSeries("Actual time");
            final XYSeries average = new XYSeries("Average time");
            long avgResponseTime = getAvgResponseTime();
            int i = 1;
            for (ServerStateModel model : serverResponseTimeStatistic) {
                actual.add(i, model.getResponseTime());
                average.add(i, avgResponseTime);
                i++;
            }
            final XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries(actual);
            dataset.addSeries(average);
            return dataset;
        }
    }
}
