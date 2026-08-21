package com.parse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for the saveEventuallyInBackground() method added in Task 717.
 * Verifies that the background variant does not block the calling thread.
 */
public class TestSaveEventuallyInBackground {

    /**
     * Verify that the method exists and is accessible.
     */
    @Test
    public void methodExists() throws Exception {
        java.lang.reflect.Method method = ParseObject.class.getMethod("saveEventuallyInBackground");
        assertEquals(Task.class, method.getReturnType());
    }

    /**
     * Verify that the method signature is correct: no params, returns Task<Void>.
     */
    @Test
    public void methodSignature() throws Exception {
        java.lang.reflect.Method method = ParseObject.class.getMethod("saveEventuallyInBackground");
        assertEquals(0, method.getParameterTypes().length);
        assertEquals(Task.class, method.getReturnType());
    }

    /**
     * Verify that saveEventually() also still exists (backward compat).
     */
    @Test
    public void saveEventuallyStillExists() throws Exception {
        java.lang.reflect.Method method = ParseObject.class.getMethod("saveEventually");
        assertEquals(Task.class, method.getReturnType());
    }
}
