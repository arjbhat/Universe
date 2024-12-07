package Model.Dynamics;

public class Velocity {
  private double vX;
  private double vY;
  private double vZ;

  public Velocity(double vX, double vY, double vZ) {
    this.vX = vX;
    this.vY = vY;
    this.vZ = vZ;
  }

  public double getVX() {
    return vX;
  }

  public double getVY() {
    return vY;
  }

  public double getVZ() {
    return vZ;
  }

  public void changeVelocity(double aX, double aY, double aZ) {
    this.vX += aX;
    this.vY += aY;
    this.vZ += aZ;
  }

  public Velocity copy() {
    return new Velocity(this.vX, this.vY, this.vZ);
  }

}
