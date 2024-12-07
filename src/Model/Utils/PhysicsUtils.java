package Model.Utils;

import Model.Dynamics.Position;

public class PhysicsUtils {
    public static double distance(Position p1, Position p2) {
        double dx = p2.getX() - p1.getX();
        double dy = p2.getY() - p1.getY();
        double dz = p2.getZ() - p1.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}