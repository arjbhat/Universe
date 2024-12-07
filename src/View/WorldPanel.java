package View;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import javax.swing.*;

import Model.Entity;
import Model.World;

public class WorldPanel extends JPanel {
  public static final double PX_PER_KM = 1e7;
  public static final int Z_SORT = 1000;
  private final World w;
  private Point lastMousePosition;
  private int offsetX;
  private int offsetY;
  private boolean isPanning;
  private double zoomLevel;

  public WorldPanel(World w) {
    this.w = Objects.requireNonNull(w);

    this.setBackground(Color.BLACK);

    this.offsetX = 0;
    this.offsetY = 0;
    this.lastMousePosition = new Point();
    this.isPanning = false;

    // Initialize zoom level
    this.zoomLevel = 1.0;

    // Add key listener for zooming
    this.addKeyListener(new ZoomKeyListener());
    this.setFocusable(true);

    this.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          lastMousePosition = e.getPoint();
          isPanning = true;
        }
      }

      public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          isPanning = false;
        }
      }
    });

    this.addMouseMotionListener(new MouseAdapter() {
      public void mouseDragged(MouseEvent e) {
        if (isPanning) {
          int dx = e.getX() - lastMousePosition.x;
          int dy = e.getY() - lastMousePosition.y;
          offsetX += dx;
          offsetY += dy;
          lastMousePosition = e.getPoint();
          repaint();
        }
      }
    });
  }

  public void setZoomLevel(double zoomLevel) {
    // Calculate the zoom center based on the mouse position
    double zoomCenterX = (getMousePosition().getX() - offsetX) / this.zoomLevel;
    double zoomCenterY = (getMousePosition().getY() - offsetY) / this.zoomLevel;

    this.zoomLevel = zoomLevel;

    // Adjust the offset to maintain the zoom center position
    offsetX = (int) (getMousePosition().getX() - zoomCenterX * zoomLevel);
    offsetY = (int) (getMousePosition().getY() - zoomCenterY * zoomLevel);

    repaint();
  }

  private class ZoomKeyListener implements KeyListener {
    @Override
    public void keyPressed(KeyEvent e) {
      if (e.isMetaDown()) { // Command key (Mac)
        if (e.getKeyChar() == '=') {
          // Zoom in
          setZoomLevel(zoomLevel * 1.1);
        } else if (e.getKeyChar() == '-') {
          // Zoom out
          setZoomLevel(zoomLevel / 1.1);
        }
      }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Apply panning
    g2d.translate(offsetX, offsetY);

    // Apply zoom level
    g2d.scale(zoomLevel, zoomLevel);

    ArrayList<Entity> entities = w.getEntities();

    if (entities.size() >= Z_SORT) {
      entities.sort(Comparator.comparingDouble(entity -> entity.getPosition().getZ()));
    }

    for (Entity e : entities) {
      int posX = (int) (e.getPosition().getX() / PX_PER_KM);
      int posY = (int) (e.getPosition().getY() / PX_PER_KM);
      int radiusPX = (int) (e.getRadius() / PX_PER_KM);
      g2d.setColor(e.getColor());
      g2d.fillOval(posX - radiusPX, posY - radiusPX, radiusPX * 2, radiusPX * 2);
    }

    g2d.dispose();
  }
}
