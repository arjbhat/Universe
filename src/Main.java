import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import Model.Dynamics.Position;
import Model.Dynamics.Velocity;
import Model.Entity;
import Model.World;
import View.WorldFrame;
import View.WorldPanel;

public class Main {
  private static void initSuns(ArrayList<Entity> e, int maxWidth, int maxHeight) {
    for (int i = 0; i < 5000; i++) {
      double randomX = (Math.random() * maxWidth) * WorldPanel.PX_PER_KM;
      double randomY = (Math.random() * maxHeight) * WorldPanel.PX_PER_KM;
      double randomZ = (Math.random() * maxWidth) * WorldPanel.PX_PER_KM;
      double maxV = 30e6;
      double minV = -15e6;
      e.add(new Entity(1.989e30, 5, new Position(randomX, randomY, randomZ),
          new Velocity(Math.random() * maxV + minV, Math.random() * maxV + minV,
              Math.random() * maxV + minV)));
    }
  }

  public static void main(String[] args) throws InterruptedException {
    ArrayList<Entity> e = new ArrayList<>();

    World myWorld = new World(e);
    WorldFrame frame = new WorldFrame(myWorld);

    initSuns(e, frame.getWidth(), frame.getHeight());

    for (int i = 0; i < 100000; i += 1) {
      myWorld.onTick();
      System.out.println("Day: " + i);
      System.out.println("Stars: " + myWorld.getEntities().size());
      frame.repaint();
      TimeUnit.MILLISECONDS.sleep(38);
    }
  }
}
