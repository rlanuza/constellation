package graphEngine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import orbitEngine.Engine;
import userInterface.Parameter;

/**
 *
 * @author rlanuza
 */
/*@Todo: comentar fichero $$$$$$$$$ESTOY COMENTANDO AQUI $$$$$$$$$*/
public class GraphScreen extends JComponent implements KeyListener {

    public static int screenWidth;
    public static int screenHeight;
    private static final long serialVersionUID = 1L;
    private double anchorX;
    private double anchorY;
    private double zoom = 1.0;
    private double ZOOM_FACTOR = 1.125;

    private GraphRotation rotation;

    private GraphConstellation gc;

    public GraphConstellation getGraphConstellation() {
        return this.gc;
    }

    private void moveCenter(double shiftCenterX, double shiftCenterY) {
        anchorX -= shiftCenterX;
        anchorY -= shiftCenterY;
        repaint();
    }

    private void center(double newCenterX, double newCenterY) {
        anchorX += (screenWidth / 2) - newCenterX;
        anchorY += (screenHeight / 2) - newCenterY;
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

    private void reset() {
        zoom = 1;
        gc.rescaleGrConstellation(zoom);
        anchorX = screenWidth / 2;
        anchorY = screenHeight / 2;
        rotation.resetCoeficients();
        gc.rotateGrConstellation();
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
            g.setColor(Parameter.COLOR_DATE);
            g.setFont(new Font("Courier New", Font.BOLD, 22));
            g.drawString("Date: " + Engine.dateString(), 10, 22);
            g.setColor(Parameter.COLOR_SCALE);
            g.drawString(gc.getScaleString(), 10, 44);
            g.setColor(Parameter.COLOR_ANGLE);
            g.drawString(rotation.getRotationString(), 10, 66);
            g.setFont(new Font("Verdana", Font.PLAIN, 12));
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(anchorX, anchorY);
            gc.paintConstellation(g2d);
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocus();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_DOWN:
                if (e.isControlDown()) {
                    moveCenter(0, -10);
                } else {
                    pitch(-1);
                }
                break;
            case KeyEvent.VK_UP:
                if (e.isControlDown()) {
                    moveCenter(0, 10);
                } else {
                    pitch(1);
                }
                break;
            case KeyEvent.VK_LEFT:
                if (e.isControlDown()) {
                    moveCenter(10, 0);
                } else {
                    if (e.isAltDown()) {
                        roll(1);
                    } else {
                        yaw(1);
                    }
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (e.isControlDown()) {
                    moveCenter(-10, 0);
                } else {
                    if (e.isAltDown()) {
                        roll(-1);
                    } else {
                        yaw(-1);
                    }
                }
                break;
            case KeyEvent.VK_PLUS:
            case KeyEvent.VK_ADD:
                zoom(ZOOM_FACTOR);
                break;
            case KeyEvent.VK_MINUS:
            case KeyEvent.VK_SUBTRACT:
                zoom(1 / ZOOM_FACTOR);
                break;
            case KeyEvent.VK_R:
                reset();
                break;
            default:
            //System.out.println(code);
        }
    }

    public GraphScreen(Engine eng) {
        addKeyListener(this);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double screenPortion = Parameter.SCREEN_PERCENT / 100.0;
        if (screenPortion > 1.0) {
            screenPortion = 0.5;
        }
        screenHeight = (int) (screenSize.height * screenPortion);
        screenWidth = (int) (screenSize.width * screenPortion);
        anchorX = screenWidth / 2;
        anchorY = screenHeight / 2;
        JFrame screen = new JFrame();
        screen.setTitle("Constellation");
        screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setPreferredSize(new Dimension(screenWidth, screenHeight));
        screen.setResizable(Parameter.SCREEN_RESIZABLE);
        screen.getContentPane().setBackground(Parameter.COLOR_SCREEN);

        screen.getContentPane().add(this, BorderLayout.CENTER);
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

        resetButton.setFocusable(false);
        inButton.setFocusable(false);
        outButton.setFocusable(false);
        goUpButton.setFocusable(false);
        goDownButton.setFocusable(false);
        goLeftButton.setFocusable(false);
        goRightButton.setFocusable(false);
        goClockWiseButton.setFocusable(false);
        goAntiClockWiseButton.setFocusable(false);

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

        goUpButton.addActionListener((ActionEvent e) -> {
            pitch(1);
        });
        goDownButton.addActionListener((ActionEvent e) -> {
            pitch(-1);
        });
        goLeftButton.addActionListener((ActionEvent e) -> {
            yaw(1);
        });
        goRightButton.addActionListener((ActionEvent e) -> {
            yaw(-1);
        });
        goClockWiseButton.addActionListener((ActionEvent e) -> {
            roll(1);
        });
        goAntiClockWiseButton.addActionListener((ActionEvent e) -> {
            roll(-1);
        });
        resetButton.addActionListener((ActionEvent e) -> {
            reset();
        });
        inButton.addActionListener((ActionEvent e) -> {
            zoom(ZOOM_FACTOR);
        });
        outButton.addActionListener((ActionEvent e) -> {
            zoom(1.0 / ZOOM_FACTOR);
        });

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.out.println("Number of click: " + e.getClickCount());
                System.out.println("Click position (X, Y):  " + e.getX() + ", " + e.getY());
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (e.getClickCount() == 2) {
                        center(e.getX(), e.getY());
                    }
                }
            }
        });

        screen.pack();
        screen.setVisible(true);

        rotation = new GraphRotation();
        gc = new GraphConstellation(rotation);

        // Double link to let callback graphicEngine from orbitEngine events
        eng.link(this);
    }
}
