package Model;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class World {

  private static final double time = Math.pow(86400, 2);
  private static final double G = 6.67e-11 * time / Math.pow(1000, 3);
  private final ArrayList<Entity> entities;
  private final ExecutorService executor;

  public World(ArrayList<Entity> entities) {
    this.entities = entities;
    this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
  }

  public void addEntity(Entity e) {
    synchronized (entities) {
      entities.add(e);
    }
  }

  public void onTick() {
    try {
      // Step 1: Detect and handle collisions
      ArrayList<Integer> toRemove = new ArrayList<>();
      ArrayList<Future<?>> collisionFutures = new ArrayList<>();

      for (int i = 0; i < entities.size(); i++) {
        final int indexI = i;
        Future<?> future = executor.submit(() -> {
          for (int j = indexI + 1; j < entities.size(); j++) {
            Entity eI = entities.get(indexI);
            Entity eJ = entities.get(j);
            if (eI.isCollide(eJ)) {
              synchronized (toRemove) {
                toRemove.add(indexI);
              }
              synchronized (entities) {
                entities.set(j, eJ.collide(eI));
              }
              break;
            }
          }
        });
        collisionFutures.add(future);
      }

      // Wait for all collision tasks to complete
      for (Future<?> future : collisionFutures) {
        future.get();
      }

      // Remove collided entities
      synchronized (entities) {
        toRemove.sort((a, b) -> b - a); // Sort in descending order
        for (int index : toRemove) {
          entities.remove(index);
        }
      }

      // Step 2: Parallelize gravity calculations
      ArrayList<Future<?>> gravityFutures = new ArrayList<>();
      for (int i = 0; i < entities.size(); i++) {
        final int indexI = i;
        Future<?> future = executor.submit(() -> {
          Entity eI = entities.get(indexI);
          for (int j = indexI + 1; j < entities.size(); j++) {
            Entity eJ = entities.get(j);
            eI.applyGravity(eJ, G);
            eJ.applyGravity(eI, G);
          }
        });
        gravityFutures.add(future);
      }

      // Wait for all gravity tasks to complete
      for (Future<?> future : gravityFutures) {
        future.get();
      }

      // Step 3: Parallelize position updates
      ArrayList<Future<?>> positionFutures = new ArrayList<>();
      for (Entity e : entities) {
        Future<?> future = executor.submit(e::changePosition);
        positionFutures.add(future);
      }

      // Wait for all position update tasks to complete
      for (Future<?> future : positionFutures) {
        future.get();
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public ArrayList<Entity> getEntities() {
    synchronized (entities) {
      return new ArrayList<>(entities);
    }
  }

  public void shutdown() {
    executor.shutdown();
  }
}
