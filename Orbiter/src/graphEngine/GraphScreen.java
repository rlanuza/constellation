/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphEngine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author rlanuza
 */
public class GraphScreen extends JComponent {

    private double zoom = 1.0;
    private int zoomCenterX = 0;
    private int zoomCenterY = 0;

    private static final int screenX = 1000;
    private static final int screenY = 600;

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
        if (zoom > 5.0) {
            zoom = 5.0;
        }
        repaint();
    }

    public void zoomOut() {
        zoom -= percentage;
        if (zoom < 0.02) {
            zoom = 0.02;
        }
        repaint();
    }

    public void zoomIn(int zcX, int zcY) {
        zoomCenterX = zcX;
        zoomCenterY = zcY;
        zoomIn();
    }

    public void zoomOut(int zcX, int zcY) {
        zoomCenterX = zcX;
        zoomCenterY = zcY;
        zoomOut();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Line line : lines) {
            //int x1 = (int) (line.x1 * zoom) + zoomCenterX;
            int x1 = (int) ((line.x1-zoomCenterX) * zoom) + zoomCenterX;
            int y1 = (int) ((line.y1-zoomCenterY) * zoom) + zoomCenterY;
            int x2 = (int) ((line.x2-zoomCenterX) * zoom) + zoomCenterX;
            int y2 = (int) ((line.y2-zoomCenterY) * zoom) + zoomCenterY;
            g.setColor(line.color);
            g.drawLine(x1, y1, x2, y2);
            //g.drawOval((int) (line.x1 * zoom), (int) (line.y1 * zoom), (int) (line.x2 * zoom), (int) (line.x2 * zoom));
        }
        zoomCenterX = 0;
        zoomCenterY = 0;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        JFrame testFrame = new JFrame();
        testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        final GraphScreen comp = new GraphScreen();
        comp.setPreferredSize(new Dimension(screenX, screenY));
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
                double x1 = Math.random() * screenX;
                double y1 = Math.random() * screenY;
                double x2, y2;
                Color randomColor = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
                for (int i = 0; i < 1000; i++) {
                    x2 = Math.random() * screenX;
                    y2 = Math.random() * screenY;
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

        comp.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.NOBUTTON) {

                }
                if (e.getButton() == MouseEvent.BUTTON1) {
                    System.out.println("Left Click!: " + e.getClickCount());
                    comp.zoomIn(e.getX(), e.getY());
                }
                if (e.getButton() == MouseEvent.BUTTON2) {
                    System.out.println("Middle Click!: " + e.getClickCount());
                }
                if (e.getButton() == MouseEvent.BUTTON3) {
                    System.out.println("Right Click!: " + e.getClickCount());
                    comp.zoomOut(e.getX(), e.getY());
                }

                System.out.println("Number of click: " + e.getClickCount());
                System.out.println("Click position (X, Y):  " + e.getX() + ", " + e.getY());
            }
        });

        testFrame.pack();
        testFrame.setVisible(true);
    }

}
