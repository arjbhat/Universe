package Model.Dynamics;

public class Position {
  private double x;
  private double y;
  private double z;

  public Position(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getZ() {
    return z;
  }

  public void applyVelocity(Velocity v) {
    this.x += v.getVX();
    this.y += v.getVY();
    this.z += v.getVZ();
  }

  public Position copy() {
    return new Position(this.x, this.y, this.z);
  }

}
