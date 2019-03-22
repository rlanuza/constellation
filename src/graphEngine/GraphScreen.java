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
 * Represents the essential information that characterize a graphical screen
 *
 * @author Roberto Lanuza rolf2000@gmail.com
 * @version 1.0
 */
public class GraphScreen extends JComponent implements KeyListener {

    /**
     * Width of the graphical screen in pixels.
     */
    public static int screenWidth;
    /**
     * Height of the graphical screen in pixels.
     */
    public static int screenHeight;
    /**
     * Avoid Eclipse issues warnings about serialized data.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Graphical x center.
     */
    private double anchorX;
    /**
     * Graphical y center.
     */
    private double anchorY;
    /**
     * Graphical zoom.
     */
    private double zoom = 1.0;
    /**
     * Graphical zoom step.
     */
    private double ZOOM_FACTOR = 1.125;
    /**
     * Graphical rotation tooling.
     */
    private GraphRotation rotation;
    /**
     * Graphical constellation.
     */
    private GraphConstellation gc;

    /**
     * Link getter for graphical constellation.
     *
     * @return graphical constellation.
     */
    public GraphConstellation getGraphConstellation() {
        return this.gc;
    }

    /**
     * Shift the graphical center anchor and repaint the graphical screen. Designed to move with arrow keys.
     *
     * @param shiftCenterX x anchor shift.
     * @param shiftCenterY y anchor shift.
     */
    private void moveCenter(double shiftCenterX, double shiftCenterY) {
        anchorX -= shiftCenterX;
        anchorY -= shiftCenterY;
        repaint();
    }

    /**
     * Move the graphical center anchor and repaint the graphical screen. Designed to move to mouse position
     * click
     *
     * @param newCenterX new x center coordinate.
     * @param newCenterY new y center coordinate.
     */
    private void center(double newCenterX, double newCenterY) {
        anchorX += (screenWidth / 2) - newCenterX;
        anchorY += (screenHeight / 2) - newCenterY;
        repaint();
    }

    /**
     * Zoom and repaint the graphical screen.
     *
     * @param factor zoom factor.
     */
    private synchronized void zoom(double factor) {
        zoom *= factor;
        gc.rescaleGraphicConstellation(zoom);
        anchorX += (anchorX - (screenWidth / 2)) * (factor - 1.0);
        anchorY += (anchorY - (screenHeight / 2)) * (factor - 1.0);
        repaint();
    }

    /**
     * 3D pitch rotation and repaint the graphical screen.
     *
     * @param steps angle rotation steps.
     */
    private synchronized void pitch(int steps) {
        rotation.updateCoeficients(steps, 0, 0);
        gc.rotateGraphicConstellation();
        repaint();
    }

    /**
     * 3D yaw rotation and repaint the graphical screen.
     *
     * @param steps angle rotation steps.
     */
    private synchronized void yaw(int steps) {
        rotation.updateCoeficients(0, steps, 0);
        gc.rotateGraphicConstellation();
        repaint();
    }

    /**
     * 3D roll rotation and repaint the graphical screen.
     *
     * @param steps angle rotation steps.
     */
    private synchronized void roll(int steps) {
        rotation.updateCoeficients(0, 0, steps);
        gc.rotateGraphicConstellation();
        repaint();
    }

    /**
     * Repaint the graphical screen.
     */
    public synchronized void updateConstellation() {
        repaint();
    }

    /**
     * Reset the graphical to initial conditions and repaint the graphical screen.
     */
    private void reset() {
        zoom = 1;
        gc.rescaleGraphicConstellation(zoom);
        anchorX = screenWidth / 2;
        anchorY = screenHeight / 2;
        rotation.resetCoeficients();
        gc.rotateGraphicConstellation();
        repaint();
    }

    /**
     * Implement the paintComponent method for the graphical screen.
     *
     * @param g graphics class.
     */
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

    /**
     * Connects the AWT world to the native windowing world.
     */
    @Override
    public void addNotify() {
        super.addNotify();
        requestFocus();
    }

    /**
     * Key released callback.
     *
     * @param e key event.
     */
    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Unicode character represented by this key is sent by the keyboard to system input callback.
     *
     * @param e key event.
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Key pressed callback.
     *
     * @param e key event.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_DOWN:
                if (e.isControlDown()) {
                    // Key arrow down with control moves down.
                    moveCenter(0, -10);
                } else {
                    // Key arrow down pitch down a angle step.
                    pitch(-1);
                }
                break;
            case KeyEvent.VK_UP:
                if (e.isControlDown()) {
                    // Key arrow up with control moves up.
                    moveCenter(0, 10);
                } else {
                    // Key arrow up pitch up a angle step.
                    pitch(1);
                }
                break;
            case KeyEvent.VK_LEFT:
                if (e.isControlDown()) {
                    // Key arrow left with control moves left.
                    moveCenter(10, 0);
                } else {
                    if (e.isAltDown()) {
                        // Key arrow left with alternate roll up a angle step.
                        roll(1);
                    } else {
                        // Key arrow left yaw up a angle step.
                        yaw(1);
                    }
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (e.isControlDown()) {
                    // Key arrow right with control moves left.
                    moveCenter(-10, 0);
                } else {
                    if (e.isAltDown()) {
                        // Key arrow right with alternate roll down a angle step.
                        roll(-1);
                    } else {
                        // Key arrow right yaw down a angle step.
                        yaw(-1);
                    }
                }
                break;
            case KeyEvent.VK_PLUS:
            case KeyEvent.VK_ADD:
                // Key plus or key add zoom in a step.
                zoom(ZOOM_FACTOR);
                break;
            case KeyEvent.VK_MINUS:
            case KeyEvent.VK_SUBTRACT:
                // Key minus of key substract zoom out a step.
                zoom(1 / ZOOM_FACTOR);
                break;
            case KeyEvent.VK_R:
                // Key R reset the graphical to initial conditions.
                reset();
                break;
            default:
        }
    }

    /**
     * Create a new GraphScreen
     *
     * @param eng Engine.
     */
    public GraphScreen(Engine eng) {
        addKeyListener(this);
        // Get the Screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // Adjust the portion of screen to use
        double screenPortion = Parameter.SCREEN_PERCENT / 100.0;
        if (screenPortion > 1.0) {
            screenPortion = 0.5;
        }
        // Get dimensions and center of screen
        screenHeight = (int) (screenSize.height * screenPortion);
        screenWidth = (int) (screenSize.width * screenPortion);
        anchorX = screenWidth / 2;
        anchorY = screenHeight / 2;
        // Create the screen as a new JFrame and configure the main characteristics
        JFrame screen = new JFrame();
        screen.setTitle("Constellation");
        screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        screen.setResizable(Parameter.SCREEN_RESIZABLE);
        screen.getContentPane().setBackground(Parameter.COLOR_SCREEN);
        screen.getContentPane().add(this, BorderLayout.CENTER);
        // Create the buttons
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
        // Configure the buttons focus
        resetButton.setFocusable(false);
        inButton.setFocusable(false);
        outButton.setFocusable(false);
        goUpButton.setFocusable(false);
        goDownButton.setFocusable(false);
        goLeftButton.setFocusable(false);
        goRightButton.setFocusable(false);
        goClockWiseButton.setFocusable(false);
        goAntiClockWiseButton.setFocusable(false);
        // Add the buttons to the panel
        buttonsPanel.add(resetButton);
        buttonsPanel.add(inButton);
        buttonsPanel.add(outButton);
        buttonsPanel.add(goUpButton);
        buttonsPanel.add(goDownButton);
        buttonsPanel.add(goLeftButton);
        buttonsPanel.add(goRightButton);
        buttonsPanel.add(goClockWiseButton);
        buttonsPanel.add(goAntiClockWiseButton);
        // Add the panel to the screen
        screen.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
        // Add the listener to the screen
        screen.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                screenWidth = screen.getWidth();
                anchorX = screenWidth / 2;
                screenHeight = screen.getHeight();
                anchorY = screenHeight / 2;
            }
        });
        // Add the listeners to the buttons
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
        // Add the mouse events
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
        // Show the screen
        screen.pack();
        screen.setVisible(true);

        // Create the rotation engine
        rotation = new GraphRotation();
        // Create the graphic constellation engine
        gc = new GraphConstellation(rotation);

        // Double link to let callback graphicEngine from orbitEngine events
        eng.link(this);
    }
}
