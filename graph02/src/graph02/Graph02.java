/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package graph02;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JPanel;

/**
 *
 * @author rlanuza
 */
class Graph02 extends JPanel {
  private double zoom = 1.0;

  private double percentage;

  private Image image;

 /**
  * @param args the command line arguments
  */
  public static void main(String[] args) {
      // TODO code application logic here
      public Graph02(Image image, double zoomPercentage)
  }
 
  public Graph02(Image image, double zoomPercentage) {
    this.image = image;
    percentage = zoomPercentage / 100;
  }

  public void paintComponent(Graphics grp) {
    Graphics2D g2D = (Graphics2D) grp;
    g2D.scale(zoom, zoom);
    g2D.drawImage(image, 0, 0, this);
  }

  public void setZoomPercentage(int zoomPercentage) {
    percentage = ((double) zoomPercentage) / 100;
  }

  public void originalSize() {
    zoom = 1;
  }

  public void zoomIn() {
    zoom += percentage;
  }

  public void zoomOut() {
    zoom -= percentage;

    if (zoom < percentage) {
      if (percentage > 1.0) {
        zoom = 1.0;
      } else {
        zoomIn();
      }
    }
  }
}