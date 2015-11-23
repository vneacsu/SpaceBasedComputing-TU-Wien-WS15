package ws15.sbc.factory.assembly.steps;

public class AssemblyOperationUtils {

    public static void simulateAssemblyOperationDelay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) {
        }
    }
}
