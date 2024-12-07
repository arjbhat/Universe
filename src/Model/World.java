package Model;

import java.util.ArrayList;

public class World {

  private static double time = Math.pow(86400, 2);
  // G = Constant * Math.pow(86400, 2) / (Kilometers cubed)
  private static final double G = 6.67e-11 * time / Math.pow(1000, 3);
  private final ArrayList<Entity> entities;

  public World(ArrayList<Entity> entities) {
    this.entities = entities;
  }

  public void addEntity(Entity e) {
    entities.add(e);
  }

  public void onTick() {

    ArrayList<Integer> toRemove = new ArrayList<>();
    for (int i = 0; i < entities.size(); i += 1) {
      for (int j = i + 1; j < entities.size(); j += 1) {
        Entity eI = entities.get(i);
        Entity eJ = entities.get(j);
        if (eI.isCollide(eJ)) {
          toRemove.add(i);
          entities.set(j, eJ.collide(eI));
          break;
        }
      }
    }

    for (int i = toRemove.size() - 1; 0 <= i; i--) {
      entities.remove((int) toRemove.get(i));
    }

    for (int i = 0; i < entities.size(); i += 1) {
      for (int j = i + 1; j < entities.size(); j += 1) {
        Entity eI = entities.get(i);
        Entity eJ = entities.get(j);
        eI.applyGravity(eJ, G);
        eJ.applyGravity(eI, G);
      }
    }

    for (Entity e : entities) {
      e.changePosition();
    }

  }

  public ArrayList<Entity> getEntities() {
    return new ArrayList<Entity>(entities);
  }

}
