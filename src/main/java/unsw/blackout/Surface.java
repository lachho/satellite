package unsw.blackout;

import java.util.ArrayList;

import unsw.utils.MathsHelper;

public class Surface {
    private ArrayList<Slope> slopes;

    public Surface() {
        slopes = new ArrayList<Slope>();
    }

    public void addSlope(int start, int end, int gradient) {
        double startHeight = MathsHelper.RADIUS_OF_JUPITER;

        for (Slope slope : slopes) {
            if (start == slope.getEnd()) {
                startHeight = slope.getEndHeight();
            }
        }
        slopes.add(new Slope(start, end, gradient, startHeight));
    }

    public double getHeight(double position) {
        double result = MathsHelper.RADIUS_OF_JUPITER;

        for (Slope slope : slopes) {
            if (slope.liesBetween(position)) {
                result = slope.getHeight(position);
            }
        }

        if (result < MathsHelper.RADIUS_OF_JUPITER) {
            result = MathsHelper.RADIUS_OF_JUPITER;
        }

        return result;
    }

}
