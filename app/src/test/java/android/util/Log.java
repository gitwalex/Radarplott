package android.util;

public class Log {
    public static int d(String tag, String msg) {
        System.out.printf("D/%1s: %2s %n%n", tag, msg);
        return 0;
    }

    public static int e(String tag, String msg) {
        System.out.printf("E/%1s: %2s %n%n", tag, msg);
        return 0;
    }

    public static int i(String tag, String msg) {
        System.out.printf("I/%1s: %2s %n%n", tag, msg);
        return 0;
    }

    public static int w(String tag, String msg) {
        System.out.printf("w/%1s: %2s %n%n", tag, msg);
        return 0;
    }
}

