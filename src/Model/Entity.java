package Model;

import java.awt.*;
import java.util.Objects;

import Model.Dynamics.Position;
import Model.Dynamics.Velocity;
import Model.Utils.PhysicsUtils;
import View.WorldPanel;

public class Entity {
  private final double mass;
  private final double radius;
  private final double density;
  private final Position position;
  private final Velocity velocity;
  private final Color c;

  public Entity(double mass, double radiusPX, Position position, Velocity velocity) {
    this.mass = mass;
    this.radius = radiusPX * WorldPanel.PX_PER_KM;
    this.position = Objects.requireNonNull(position);
    this.velocity = Objects.requireNonNull(velocity);
    this.density = mass * 3 / (4 * Math.PI * Math.pow(radius, 3));
    this.c = new Color((int) (Math.random() * 255), (int) (Math.random() * 255),
        (int) (Math.random() * 255), (int) (Math.random() * 155 + 100));
  }

  public Entity(double mass, double radiusPX, Position position, Velocity velocity, Color c) {
    this.mass = mass;
    this.radius = radiusPX * WorldPanel.PX_PER_KM;
    this.position = Objects.requireNonNull(position);
    this.velocity = Objects.requireNonNull(velocity);
    this.density = mass * 3 / (4 * Math.PI * Math.pow(radius, 3));
    this.c = c;
  }

  public void applyGravity(Entity e, double G) {
    double GM = G * e.mass;

    double rX = e.position.getX() - this.position.getX();
    double rY = e.position.getY() - this.position.getY();
    double rZ = e.position.getZ() - this.position.getZ();

    double absR = PhysicsUtils.distance(this.position, e.position);
    double absAcc = GM / Math.pow(absR, 2);

    double aX = absAcc * (rX / absR);
    double aY = absAcc * (rY / absR);
    double aZ = absAcc * (rZ / absR);

    this.velocity.changeVelocity(aX, aY, aZ);
  }

  public boolean isCollide(Entity e) {
    return this.distance(e) < Math.max(this.radius, e.radius);
  }

  public Entity collide(Entity e) {
    double cMass = this.mass + e.getMass();
    double radiusPX = Math.cbrt(cMass * 3 / (4 * Math.PI * Math.max(this.density, e.getDensity())))
        / WorldPanel.PX_PER_KM;
    double vX = (this.mass * this.velocity.getVX() + e.getMass() * e.velocity.getVX()) / cMass;
    double vY = (this.mass * this.velocity.getVY() + e.getMass() * e.velocity.getVY()) / cMass;
    double vZ = (this.mass * this.velocity.getVZ() + e.getMass() * e.velocity.getVZ()) / cMass;
    double x = (this.mass * this.position.getX() + e.getMass() * e.position.getX()) / cMass;
    double y = (this.mass * this.position.getY() + e.getMass() * e.position.getY()) / cMass;
    double z = (this.mass * this.position.getZ() + e.getMass() * e.position.getZ()) / cMass;
    Color c = this.mass > e.getMass() ? this.getColor() : e.getColor();
    return new Entity(cMass, radiusPX, new Position(x, y, z), new Velocity(vX, vY, vZ), c);
  }

  public double distance(Entity e) {
    return PhysicsUtils.distance(this.position, e.position);
  }

  public void changePosition() {
    this.position.applyVelocity(this.velocity);
  }

  public double getMass() {
    return mass;
  }

  public double getRadius() {
    return radius;
  }

  public double getDensity() {
    return density;
  }

  public Color getColor() {
    return c;
  }

  public Position getPosition() {
    return position;
  }
}
