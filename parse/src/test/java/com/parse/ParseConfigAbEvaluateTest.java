/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.parse.boltsinternal.Task;
import java.net.URL;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** Unit tests for the opt-in abEvaluate config fetch: params, verdicts, and no persistence. */
public class ParseConfigAbEvaluateTest {

    @Before
    public void setUp() throws Exception {
        ParseRESTCommand.server = new URL("https://api.parse.com/1");
    }

    @After
    public void tearDown() {
        ParseRESTCommand.server = null;
    }

    @Test
    public void factoryCarriesAbEvaluateParam() {
        ParseRESTConfigCommand command =
                ParseRESTConfigCommand.fetchConfigAbEvaluateCommand("session");
        assertTrue(command.jsonParameters.optBoolean("abEvaluate", false));
    }

    @Test
    public void defaultFactorySendsNoParams() {
        ParseRESTConfigCommand command = ParseRESTConfigCommand.fetchConfigCommand("session");
        assertNull(command.jsonParameters);
    }

    @Test
    public void verdictsAttachedAndNeverPersisted() throws Exception {
        JSONObject verdicts = new JSONObject();
        verdicts.put("vip_for_new_10", 86);
        JSONObject responseJson = new JSONObject();
        responseJson.put("params", new JSONObject());
        responseJson.put("abEvaluated", verdicts);

        ParseHttpClient restClient =
                ParseTestUtils.mockParseHttpClientWithResponse(responseJson, 200, "OK");
        ParseCurrentConfigController currentConfigController =
                mock(ParseCurrentConfigController.class);
        when(currentConfigController.setCurrentConfigAsync(any(ParseConfig.class)))
                .thenReturn(Task.<Void>forResult(null));
        ParseConfigController controller =
                new ParseConfigController(restClient, currentConfigController);

        ParseConfig config = ParseTaskUtils.wait(controller.getAbEvaluateAsync("session"));

        assertEquals(86, config.getAbEvaluated().optInt("vip_for_new_10", -1));
        verify(currentConfigController, never()).setCurrentConfigAsync(any(ParseConfig.class));
    }

    @Test
    public void offlineErrorNeverReturnsVerdicts() throws Exception {
        ParseHttpClient restClient = mock(ParseHttpClient.class);
        when(restClient.execute(any(com.parse.http.ParseHttpRequest.class)))
                .thenThrow(new java.io.IOException("offline"));
        ParseCurrentConfigController currentConfigController =
                mock(ParseCurrentConfigController.class);
        ParseConfigController controller =
                new ParseConfigController(restClient, currentConfigController);

        com.parse.boltsinternal.Task<ParseConfig> task = controller.getAbEvaluateAsync("session");
        task.waitForCompletion();
        assertTrue(task.isFaulted());
        verify(currentConfigController, never()).setCurrentConfigAsync(any(ParseConfig.class));
    }

    @Test
    public void missingVerdictsYieldNull() throws Exception {
        JSONObject responseJson = new JSONObject();
        responseJson.put("params", new JSONObject());

        ParseHttpClient restClient =
                ParseTestUtils.mockParseHttpClientWithResponse(responseJson, 200, "OK");
        ParseCurrentConfigController currentConfigController =
                mock(ParseCurrentConfigController.class);
        ParseConfigController controller =
                new ParseConfigController(restClient, currentConfigController);

        ParseConfig config = ParseTaskUtils.wait(controller.getAbEvaluateAsync("session"));

        assertNull(config.getAbEvaluated());
        verify(currentConfigController, never()).setCurrentConfigAsync(any(ParseConfig.class));
    }
}
