package View;

import java.awt.*;
import java.util.Objects;

import javax.swing.*;

import Model.World;

public class WorldFrame extends JFrame {
  private final World w;
  private final WorldPanel worldPanel;

  public WorldFrame(World w) {
    this.setTitle("N Body Problem");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setResizable(true);
    this.setSize(1600, 1000);
    this.getContentPane().setBackground(Color.GRAY); // Doesn't work?

    this.w = Objects.requireNonNull(w);
    this.worldPanel = new WorldPanel(w);
    this.add(worldPanel);

    this.setVisible(true);
  }
}
