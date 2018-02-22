/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graph01;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author rlanuza
 */
public class Graph01 extends JComponent {

    private double zoom = 1.0;
    private double percentage = .1;

    private static class Line {

        final double x1;
        final double y1;
        final double x2;
        final double y2;
        final Color color;

        public Line(double x1, double y1, double x2, double y2, Color color) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.color = color;
        }
    }

    private final LinkedList<Line> lines = new LinkedList<Line>();

    public void addLine(double x1, double x2, double x3, double x4) {
        addLine(x1, x2, x3, x4, Color.black);
    }

    public void addLine(double x1, double x2, double x3, double x4, Color color) {
        lines.add(new Line(x1, x2, x3, x4, color));
        repaint();
    }

    public void clearLines() {
        lines.clear();
        repaint();
    }

    public void zoomIn() {
        zoom += percentage;
        if (zoom > 1.66) {
            zoom = 1.66;
        }
        //this.setPreferredSize(new Dimension((int)zoom*this.getWidth(),(int)zoom*this.getHeight()));
        //revalidate();     
        repaint();
    }

    public void zoomOut() {
        zoom -= percentage;
        if (zoom < 0.2) {
            zoom = 0.2;
        }
        //this.setPreferredSize(new Dimension((int)zoom*this.getWidth(),(int)zoom*this.getHeight()));
        //this.setSize(new Dimension((int)zoom*this.getWidth(),(int)zoom*this.getHeight()));
        //revalidate();     
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Line line : lines) {
            g.setColor(line.color);
            g.drawLine((int) (line.x1 * zoom), (int) (line.y1 * zoom), (int) (line.x2 * zoom), (int) (line.y2 * zoom));
            //g.drawOval((int) (line.x1 * zoom), (int) (line.y1 * zoom), (int) (line.x2 * zoom), (int) (line.x2 * zoom));
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        JFrame testFrame = new JFrame();
        testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        final Graph01 comp = new Graph01();
        comp.setPreferredSize(new Dimension(1000, 600));
        testFrame.getContentPane().add(comp, BorderLayout.CENTER);
        JPanel buttonsPanel = new JPanel();
        JButton newLineButton = new JButton("New Line");
        JButton clearButton = new JButton("Clear");
        JButton zoomIn = new JButton("+");
        JButton zoomOut = new JButton("-");
        buttonsPanel.add(newLineButton);
        buttonsPanel.add(clearButton);
        buttonsPanel.add(zoomIn);
        buttonsPanel.add(zoomOut);
        testFrame.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
        newLineButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                double x1 = Math.random() * 900 + 50;
                double y1 = Math.random() * 500 + 50;
                double x2, y2;
                Color randomColor = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
                for (int i = 0; i < 100; i++) {
                    x2 = Math.random() * 900 + 50;
                    y2 = Math.random() * 500 + 50;
                    comp.addLine(x1, y1, x2, y2, randomColor);
                    x1 = x2;
                    y1 = y2;
                }
            }
        });
        clearButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                comp.clearLines();
            }
        });
        zoomIn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                comp.zoomIn();
            }
        });
        zoomOut.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                comp.zoomOut();
            }
        });
        testFrame.pack();
        testFrame.setVisible(true);
    }

}
