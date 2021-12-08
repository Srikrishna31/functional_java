package com.misc;

import com.util.Result;
import com.util.List;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryPoolMXBean;
import java.util.function.Consumer;

public class MemoryMonitor {
    private static Consumer<Void> callback;


    public static void monitorMemory(double threshold, Consumer<Void> callback) {
        MemoryMonitor.callback = callback;

        //findPSOldGenPool().forEachOrThrow(poolMxBean -> poolMxBean.setCollectionUsageThreshold((int)Math.floor
        // (poolMxBean.getUsage().getMax() * threshold)));
        var emitter = (NotificationEmitter) ManagementFactory.getMemoryMXBean();
        emitter.addNotificationListener(notificationListener, null, null);
    }

    private static NotificationListener notificationListener =
            (Notification notification, Object handBack) -> {
                if(notification.getType().equals(MemoryNotificationInfo.MEMORY_COLLECTION_THRESHOLD_EXCEEDED)) {
                    //cleanly shutdown the application.
                    callback.accept(null);
                }
            };

    private static Result<MemoryPoolMXBean>  findPSOldGenPool() {
        return List.fromCollection(ManagementFactory.getMemoryPoolMXBeans())
                .first(x -> x.getName().equals("PS Old Gen"));
    }
}
