/**
 * Created by Kir on 03.02.14.
 */
public class Logger {
    private static long startTime;
    private static long endTime;
    private static String loggerSmallPrefix = "$logger->";

    public static void logStart() {
        startTime = System.nanoTime();
    }

    public static void logEnd() {
        endTime = System.nanoTime();
    }

    public static void showEnding() {
        System.out.println(loggerSmallPrefix + "ended@" + (endTime - startTime) / 1000000000);
    }

    public static String[] setParameter() {
        String[] res = null;
        try {
            throw new Exception();
        } catch (Exception e) {
            if (e.getStackTrace()[1].getClassName().equals("CompareTreesDiff")) {
                res = new String[]{"-ct"};
            }
        }
        return res;
    }
}
