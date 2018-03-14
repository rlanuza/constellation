package graphEngine;

import java.awt.BorderLayout;
import java.awt.Dimension;
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

/**
 *
 * @author rlanuza
 */
public class GraphScreen extends JComponent {

    private int zoomCenterX = 0;
    private int zoomCenterY = 0;
    public static int screenWidth = 2000;
    public static int screenHeight = 1200;

    private GraphConstellation gc;

    public GraphConstellation getGraphConstellation() {
        return this.gc;
    }

    synchronized public void zoomIn(int zcX, int zcY) {
        zoomCenterX = zcX;
        zoomCenterY = zcY;
        gc.rescaleGrConstellation(2);
        repaint();
    }

    synchronized public void zoomOut(int zcX, int zcY) {
        zoomCenterX = zcX;
        zoomCenterY = zcY;
        gc.rescaleGrConstellation(.5);
        repaint();
    }

    synchronized public void updateConstellation() {
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
        ///g2d.scale(zoom, zoom);
        if (gc != null) {
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
                repaint();
            }
        });
        inButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comp.zoomIn(0, 0);
            }
        });
        outButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comp.zoomOut(0, 0);
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
