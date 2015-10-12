package io.sterodium.rmi.protocol.server;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 06/10/2015
 *         <p/>
 */
public class ObjectLocatorTest {

    ObjectLocator objectLocator;

    @Before
    public void setUp() {
        objectLocator = new ObjectLocator();
    }

    @Test
    public void shouldReturnNothing() {
        Object o = objectLocator.get("");
        assertThat(o, nullValue());
    }

    @Test
    public void shouldFindByObjectId() {
        for (int i = 0; i < 10000; i++) {
            Object o = new Object();
            String put = objectLocator.put(o);
            assertThat(objectLocator.get(put), is(o));
        }
    }

    @Test
    public void shouldEvictCacheByObjectId() {
        Object willClear = new Object();
        String id = objectLocator.put(willClear);
        for (int i = 0; i < 10000; i++) {
            Object o = new Object();
            String put = objectLocator.put(o);
            assertThat(objectLocator.get(put), is(o));
        }
        assertThat(objectLocator.get(id), nullValue());
    }

    @Test
    public void shouldKeepPermanentObjects() {
        Object permanent = new Object();
        objectLocator.addPermanentObject("key", permanent);
        for (int i = 0; i < 10000; i++) {
            Object o = new Object();
            String put = objectLocator.put(o);
            assertThat(objectLocator.get(put), is(o));
        }
        assertThat(objectLocator.get("key"), is(permanent));
    }

    @Test
    public void shouldReplaceOldObjectByKey() {
        Object first = new Object();
        Object second = new Object();
        String id = objectLocator.put(first);
        objectLocator.put(id, second);

        assertThat("Put by id did not replace object", objectLocator.get(id), is(second));
    }

    @Test
    public void resetShouldNotRemovePermanentObjects() {
        List<String> permanentIds = Lists.newArrayList("id1", "id2", "id3");
        for (String permanentId : permanentIds) {
            objectLocator.addPermanentObject(permanentId, new Object());
        }

        List<String> ids = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            String id = objectLocator.put(new Object());
            ids.add(id);
        }

        objectLocator.reset();

        for (String id : ids) {
            assertThat("Temp objects should be cleaned", objectLocator.get(id), nullValue());
        }

        for (String permanentId : permanentIds) {
            assertThat("Permanent objects should not be cleaned by reset",
                    objectLocator.get(permanentId), notNullValue());
        }
    }
}
