import java.util.*;
import java.util.concurrent.*;

import Model.Dynamics.Position;
import Model.Dynamics.Velocity;
import Model.Entity;
import Model.World;
import View.WorldFrame;
import View.WorldPanel;

public class Main {

  private static void bigBang(ArrayList<Entity> e, int maxWidth, int maxHeight) {
    Position center = new Position(
        maxWidth * WorldPanel.PX_PER_KM / 2,
        maxHeight * WorldPanel.PX_PER_KM / 2,
        maxWidth * WorldPanel.PX_PER_KM / 2);

    double maxRadius = Math.min(maxWidth, maxHeight) * WorldPanel.PX_PER_KM / 2;

    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    ArrayList<Future<?>> futures = new ArrayList<>();

    for (int i = 0; i < 10000; i++) {
      Future<?> future = executor.submit(() -> {
        double r = Math.random() * maxRadius;
        double theta = Math.random() * 2 * Math.PI;
        double phi = Math.random() * Math.PI;

        double x = r * Math.sin(phi) * Math.cos(theta) + center.getX();
        double y = r * Math.sin(phi) * Math.sin(theta) + center.getY();
        double z = r * Math.cos(phi) + center.getZ();

        Position randomPosition = new Position(x, y, z);

        // Calculate direction vector (normalized)
        double dx = randomPosition.getX() - center.getX();
        double dy = randomPosition.getY() - center.getY();
        double dz = randomPosition.getZ() - center.getZ();
        double magnitude = Math.max(r, 1e-5); // Avoid division by zero
        double directionX = dx / magnitude;
        double directionY = dy / magnitude;
        double directionZ = dz / magnitude;

        // Introduce angular velocity for a spiral-like motion
        double angularSpeed = 3.14e7; // Reduced angular speed for visibility
        double angularVelocityX = -angularSpeed * Math.sin(theta);
        double angularVelocityY = angularSpeed * Math.cos(theta);
        double angularVelocityZ = (Math.random() - 0.5) * angularSpeed * 0.05; // Smaller Z component

        // Combine outward and angular velocities
        double outwardSpeed = 3.14e7; // Reduced outward speed for visibility
        double velocityX = directionX * outwardSpeed + angularVelocityX;
        double velocityY = directionY * outwardSpeed + angularVelocityY;
        double velocityZ = directionZ * outwardSpeed + angularVelocityZ;

        synchronized (e) {
          e.add(new Entity(
              1.989e30, // Mass of the particle
              5 + Math.random() * 10, // Randomized radius to create diversity
              randomPosition, // Position
              new Velocity(velocityX, velocityY, velocityZ) // Velocity
          ));
        }
      });
      futures.add(future);
    }

    for (Future<?> future : futures) {
      try {
        future.get();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    executor.shutdown();
  }

  public static void main(String[] args) throws InterruptedException {
    ArrayList<Entity> e = new ArrayList<>();

    World myWorld = new World(e);
    WorldFrame frame = new WorldFrame(myWorld);

    bigBang(e, frame.getWidth(), frame.getHeight());

    for (int i = 0; i < 100000; i += 1) {
      myWorld.onTick();
      System.out.println("Day: " + i);
      System.out.println("Stars: " + myWorld.getEntities().size());
      frame.repaint();
      TimeUnit.MILLISECONDS.sleep(38);
    }
  }
}
