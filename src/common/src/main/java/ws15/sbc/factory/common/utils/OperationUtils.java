package ws15.sbc.factory.common.utils;

public class OperationUtils {

    public static void simulateDelay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignore) {
        }
    }
}
