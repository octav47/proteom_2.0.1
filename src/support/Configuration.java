package support;

/**
 * Created by Kir on 18.02.14.
 */
public class Configuration {
    public static String VERSION = "2.0.1_u180114";
    private static String NAME = "proteom";

    public static String getHeader() {
        return NAME + " " + VERSION;
    }

    public static String NATIVE_LIBS_PATH = "null";
    public static String PATH = "null";
    public static String TREE_PATH = "null";
    public static String TMP_PATH = "null";
    public static String TMP20_PATH = "null";
    public static String PTNEWICKS_PATH = "null";

}
