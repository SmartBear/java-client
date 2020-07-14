package io.split.engine.sse.dtos;

import io.split.engine.sse.NotificationManagerKeeper;
import io.split.engine.sse.NotificationProcessor;

public class ControlNotification extends IncomingNotification implements PresenceNotification {
    private final ControlType controlType;

    public ControlNotification(GenericNotificationData genericNotificationData) {
        super(Type.CONTROL, genericNotificationData.getChannel());
        this.controlType = genericNotificationData.getControlType();
    }

    public ControlType getControlType() {
        return controlType;
    }

    @Override
    public void handler(NotificationProcessor notificationProcessor) {
        notificationProcessor.processPresence(this);
    }

    @Override
    public void handlerPresence(NotificationManagerKeeper notificationManagerKeeper) {
        notificationManagerKeeper.handleIncomingControlEvent(this);
    }
}
