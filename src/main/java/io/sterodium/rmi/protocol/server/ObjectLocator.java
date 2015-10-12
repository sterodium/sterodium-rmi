package io.sterodium.rmi.protocol.server;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 23/09/2015
 */
class ObjectLocator {

    private static final Map<Object, String> KEYS = new HashMap<>();

    private static final RemovalListener<String, Object> REMOVAL_LISTENER = new RemovalListener<String, Object>() {
        @Override
        public void onRemoval(RemovalNotification<String, Object> notification) {
            KEYS.remove(notification.getValue());
        }
    };

    private static final Cache<String, Object> OBJECTS = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .removalListener(REMOVAL_LISTENER)
            .build();

    private static final Map<String, Object> PERMANENT_OBJECTS = new HashMap<>();

    Object get(String objectId) {
        Object permanentObject = PERMANENT_OBJECTS.get(objectId);
        return permanentObject == null ? OBJECTS.getIfPresent(objectId) : permanentObject;
    }

    String put(Object object) {
        if (KEYS.containsKey(object)) {
            return KEYS.get(object);
        }

        String objectId = UUID.randomUUID().toString();
        put(objectId, object);
        return objectId;
    }

    void addPermanentObject(String objectId, Object object) {
        PERMANENT_OBJECTS.put(objectId, object);
    }

    void put(String objectId, Object object) {
        Object oldValue = OBJECTS.getIfPresent(objectId);
        KEYS.remove(oldValue);

        OBJECTS.put(objectId, object);
        KEYS.put(object, objectId);

        Preconditions.checkState(OBJECTS.size() == KEYS.size());
    }

    public void reset() {
        OBJECTS.invalidateAll();
        OBJECTS.cleanUp();

        Preconditions.checkState(OBJECTS.size() == 0);
        Preconditions.checkState(KEYS.size() == 0);
    }

}
