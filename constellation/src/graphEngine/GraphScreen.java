package graphEngine;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import orbitEngine.Engine;

/**
 *
 * @author rlanuza
 */
public class GraphScreen extends JComponent {

    public static int screenWidth = 2000;
    public static int screenHeight = 1200;
    private double anchorX = 0;
    private double anchorY = 0;
    private int zoomCenterX = 0;
    private int zoomCenterY = 0;
    private double zoom = 1;

    private GraphConstellation gc;

    public GraphConstellation getGraphConstellation() {
        return this.gc;
    }

    synchronized public void zoomIn(int zcX, int zcY) {
        zoomCenterX = zcX;
        zoomCenterY = zcY;
        zoom *= 2;
        gc.rescaleGrConstellation(zoom);
        repaint();
    }

    synchronized public void zoomOut(int zcX, int zcY) {
        zoomCenterX = zcX;
        zoomCenterY = zcY;
        zoom *= 0.5;
        gc.rescaleGrConstellation(zoom);
        repaint();
    }

    synchronized public void updateConstellation() {
        int width = gc.lim_right - gc.lim_left;
        int height = gc.lim_bottom - gc.lim_top;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        screenWidth = this.getWidth();
        screenHeight = this.getHeight();

        if (gc != null) {
            g.setFont(new Font("Courier New", Font.BOLD, 24));
            g.drawString(Engine.dateString(), 10, 24);
            g.setFont(new Font("Verdana", Font.PLAIN, 12));
            Graphics2D g2d = (Graphics2D) g;
            anchorX = anchorX + ((screenWidth / 2) - zoomCenterX) * zoom;
            zoomCenterX = screenWidth / 2;
            anchorY = anchorY + ((screenHeight / 2) - zoomCenterY) * zoom;
            zoomCenterY = screenHeight / 2;

            g2d.translate(anchorX, anchorY);
            ///g2d.scale(zoom, zoom);
            gc.paintConstellation(g2d);
        }
    }

    public GraphScreen() {

        // TODO code application logic here
        JFrame testFrame = new JFrame();
        testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
                zoom = 1;
                gc.rescaleGrConstellation(zoom);
                anchorX = screenWidth / 2;
                anchorY = screenHeight / 2;
                repaint();
            }
        });
        inButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comp.zoomIn(zoomCenterX, zoomCenterY);
            }
        });
        outButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comp.zoomOut(zoomCenterX, zoomCenterY);
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

        gc = new GraphConstellation();
    }
}
