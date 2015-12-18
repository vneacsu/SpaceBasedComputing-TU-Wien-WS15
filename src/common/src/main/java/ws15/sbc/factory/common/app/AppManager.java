package ws15.sbc.factory.common.app;

public interface AppManager {
    void prepareInfrastructure();

    void shutdown();

    interface CleanupAction {
        void cleanup();
    }

    default void registerShutdownHook(CleanupAction cleanupAction) {
        Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    cleanupAction.cleanup();

                    try {
                        mainThread.join();
                    } catch (InterruptedException ignore) {
                    }

                    shutdown();
                })
        );
    }
}
