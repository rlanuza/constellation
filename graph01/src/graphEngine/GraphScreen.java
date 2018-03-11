/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphEngine;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    public static int screenWidth = 1000;
    public static int screenHeight = 600;

    private double percentage = 0.5;
    private static GraphConstellation gc;

    public void zoomIn() {
        zoom += percentage;
        if (zoom > 10000.0) {
            zoom = 10000.0;
        }
        repaint();
    }

    public void zoomOut() {
        zoom -= percentage;
        if (zoom < 0.0001) {
            zoom = 0.0001;
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

    public void updateConstellation(GraphConstellation gc) {
        this.gc = gc;

        int width = gc.lim_right - gc.lim_left;
        int height = gc.lim_bottom - gc.lim_top;
//        if (width > screenWidth) {
//            screenWidth = width + width / 4;
//            this.setPreferredSize(new Dimension(screenWidth, screenHeight));
//        }
//        if (height > screenHeight) {
//            screenHeight = height + height / 4;
//            this.setPreferredSize(new Dimension(screenWidth, screenHeight));
//        }
        repaint();
    }

    synchronized private void paintConstellation(Graphics2D g2d) {
        if (gc != null) {
            for (GraphBody grBody : gc.gBody) {
                if (grBody.orbit.proyectionPointList.size() > 0) {
                    g2d.setColor(grBody.color);
                    int diameter = 2 * grBody.radius + 4;
                    int x0 = grBody.orbit.proyectionPointList.get(0).x;
                    int y0 = grBody.orbit.proyectionPointList.get(0).y;
                    for (Point orbitPoint : grBody.orbit.proyectionPointList) {

                        int x1 = orbitPoint.x;
                        int y1 = orbitPoint.y;
                        g2d.drawLine(x0, y0, x1, y1);
                        x0 = x1;
                        y0 = y1;
                    }
                    g2d.drawOval(x0, y0, diameter, diameter);
                    g2d.drawString(grBody.name, x0, y0);
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        screenWidth = this.getWidth();
        screenHeight = this.getHeight();
        //double zoomWidth = screenWidth * zoom;
        //double zoomHeight = screenHeight * zoom;
        //double anchorX = (screenWidth - zoomWidth) / 2;
        //double anchorY = (screenHeight - zoomHeight) / 2;
        double anchorX = screenWidth / 2;
        double anchorY = screenHeight / 2;

        g2d.translate(anchorX, anchorY);
        g2d.scale(zoom, zoom);
        paintConstellation(g2d);
    }

    public GraphScreen() {

        // TODO code application logic here
        JFrame testFrame = new JFrame();
        testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        GraphScreen comp = this;
        comp.setPreferredSize(new Dimension(screenWidth, screenHeight));

        testFrame.getContentPane().add(comp, BorderLayout.CENTER);
        JPanel buttonsPanel = new JPanel();
        JButton centerButton = new JButton("C");
        JButton resetButton = new JButton("R");
        JButton inButton = new JButton("+");
        JButton outButton = new JButton("-");
        buttonsPanel.add(centerButton);
        buttonsPanel.add(resetButton);
        buttonsPanel.add(inButton);
        buttonsPanel.add(outButton);
        testFrame.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
        centerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoom = 1.0;
                repaint();
            }
        });
        inButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comp.zoomIn();
            }
        });
        outButton.addActionListener(new ActionListener() {
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
