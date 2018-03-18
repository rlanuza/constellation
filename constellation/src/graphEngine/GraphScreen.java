package graphEngine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import orbitEngine.Engine;
import userInterface.Parameters;

/**
 *
 * @author rlanuza
 */
public class GraphScreen extends JComponent {

    public static int screenWidth;
    public static int screenHeight;
    private double anchorX;
    private double anchorY;
    private double zoom = 1.0;
    private double ZOOM_FACTOR = 1.5;

    /*@Todo manage point of view reference to transform */
    private GraphRotation rotation;

    private GraphConstellation gc;

    public GraphConstellation getGraphConstellation() {
        return this.gc;
    }

    private void center(double zoomCenterX, double zoomCenterY) {
        anchorX += (screenWidth / 2) - zoomCenterX;
        anchorY += (screenHeight / 2) - zoomCenterY;
        repaint();
    }

    private synchronized void zoom(double factor) {
        zoom *= factor;
        gc.rescaleGrConstellation(zoom);
        anchorX += (anchorX - (screenWidth / 2)) * (factor - 1.0);
        anchorY += (anchorY - (screenHeight / 2)) * (factor - 1.0);
        repaint();
    }

    private synchronized void pitch(int steps) {
        rotation.updateCoeficients(steps, 0, 0);
        gc.rotateGrConstellation();
        repaint();
    }

    private synchronized void yaw(int steps) {
        rotation.updateCoeficients(0, steps, 0);
        gc.rotateGrConstellation();
        repaint();
    }

    private synchronized void roll(int steps) {
        rotation.updateCoeficients(0, 0, steps);
        gc.rotateGrConstellation();
        repaint();
    }

    public synchronized void updateConstellation() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(0, (screenHeight / 2), screenWidth, (screenHeight / 2));
        g.drawLine((screenWidth / 2), 0, (screenWidth / 2), screenHeight);
        g.setColor(new Color(32, 178, 208));
        g.drawLine(0, (int) anchorY, screenWidth, (int) anchorY);
        g.drawLine((int) anchorX, 0, (int) anchorX, screenHeight);

        if (gc != null) {
            g.setColor(Color.RED);
            g.setFont(new Font("Courier New", Font.BOLD, 22));
            g.drawString(Engine.dateString(), 10, 22);
            g.setColor(Color.BLUE);
            g.drawString(gc.getScaleString(), 10, 44);
            g.setColor(Color.MAGENTA);
            g.drawString(rotation.getRotationString(), 10, 66);
            g.setFont(new Font("Verdana", Font.PLAIN, 12));
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(anchorX, anchorY);
            gc.paintConstellation(g2d);
        }
    }

    public GraphScreen(Engine eng) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double screenPortion = Parameters.SCREEN_PERCENT / 100.0;
        if (screenPortion > 1.0) {
            screenPortion = 5.00;
        }
        screenHeight = (int) (screenSize.height * screenPortion);
        screenWidth = (int) (screenSize.width * screenPortion);
        anchorX = screenWidth / 2;
        anchorY = screenHeight / 2;
        JFrame screen = new JFrame();
        screen.setTitle("Constellation");
        screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GraphScreen comp = this;
        comp.setPreferredSize(new Dimension(screenWidth, screenHeight));
        screen.setResizable(Parameters.SCREEN_RESIZABLE);

        screen.getContentPane().add(comp, BorderLayout.CENTER);
        JPanel buttonsPanel = new JPanel();
        JButton resetButton = new JButton("R");
        JButton inButton = new JButton("+");
        JButton outButton = new JButton("-");

        JButton goUpButton = new JButton("▲");
        JButton goDownButton = new JButton("▼");
        JButton goLeftButton = new JButton("◄");
        JButton goRightButton = new JButton("►");
        JButton goClockWiseButton = new JButton("↻");
        JButton goAntiClockWiseButton = new JButton("↺");

        buttonsPanel.add(resetButton);
        buttonsPanel.add(inButton);
        buttonsPanel.add(outButton);

        buttonsPanel.add(goUpButton);
        buttonsPanel.add(goDownButton);
        buttonsPanel.add(goLeftButton);
        buttonsPanel.add(goRightButton);
        buttonsPanel.add(goClockWiseButton);
        buttonsPanel.add(goAntiClockWiseButton);

        screen.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

        screen.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                screenWidth = screen.getWidth();
                anchorX = screenWidth / 2;
                screenHeight = screen.getHeight();
                anchorY = screenHeight / 2;
            }
        });

        goUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comp.pitch(1);
            }
        });
        goDownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comp.pitch(-1);
            }
        });
        goLeftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comp.yaw(1);
            }
        });
        goRightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comp.yaw(-1);
            }
        });

        goClockWiseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comp.roll(1);
            }
        });
        goAntiClockWiseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comp.roll(-1);
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoom = 1;
                gc.rescaleGrConstellation(zoom);
                anchorX = screenWidth / 2;
                anchorY = screenHeight / 2;
                rotation.resetCoeficients();
                gc.rotateGrConstellation();
                repaint();
            }
        });
        inButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comp.zoom(ZOOM_FACTOR);
            }
        });
        outButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comp.zoom(1.0 / ZOOM_FACTOR);
            }
        });

        comp.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.out.println("Number of click: " + e.getClickCount());
                System.out.println("Click position (X, Y):  " + e.getX() + ", " + e.getY());
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (e.getClickCount() == 2) {
                        comp.center(e.getX(), e.getY());
                    }
                }
            }
        });

        screen.pack();
        screen.setVisible(true);

        rotation = new GraphRotation();
        gc = new GraphConstellation(rotation);
    }

    private Color Color(int i, int i0, int i1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
