/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver.telemetry;

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

/**
 *
 * @author osyniaev
 */
public enum ServerResponseTimeAnalizer {
    ANALIZER;

    private volatile static HashSet<ServerStateModel> serverResponseTimeStatistic = new HashSet<>();

    public void getResponseTimeStatistic() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (ServerStateModel model : serverResponseTimeStatistic) {
            System.err.println("На дату " + sdf.format(model.getCurrentDate().getTime()) + " количество живых подключений " + model.getAliveConnectionsNumber()
                    + " время ответа сервера " + model.getResponseTime() + " милисекунд");
        }
    }

    public void growStatisticsGrafic() {

        JFrame jf = new JFrame("Статистика времени ответа прокси-сервера в зависимости от количества живых подключений");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setSize(800, 800);
        jf.add(new GraficDrower());
        jf.setVisible(true);

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
        double ox = 100, oy = 700; // ось симметрии параболы
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
            gr.setStroke(new BasicStroke((float) 2));
            //gr.draw(new Line2D.Double(ox, 0, ox, this.getHeight()));
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

}
