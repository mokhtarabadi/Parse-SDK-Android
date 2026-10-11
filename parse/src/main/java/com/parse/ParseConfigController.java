/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;

import com.parse.boltsinternal.Task;
import org.json.JSONObject;

class ParseConfigController {

    private final ParseHttpClient restClient;
    private final ParseCurrentConfigController currentConfigController;

    public ParseConfigController(
            ParseHttpClient restClient, ParseCurrentConfigController currentConfigController) {
        this.restClient = restClient;
        this.currentConfigController = currentConfigController;
    }

    /* package */ ParseCurrentConfigController getCurrentConfigController() {
        return currentConfigController;
    }

    public Task<ParseConfig> getAsync(String sessionToken) {
        final ParseRESTCommand command = ParseRESTConfigCommand.fetchConfigCommand(sessionToken);
        return command.executeAsync(restClient)
                .onSuccessTask(
                        task -> {
                            JSONObject result = task.getResult();

                            final ParseConfig config =
                                    ParseConfig.decode(result, ParseDecoder.get());
                            return currentConfigController
                                    .setCurrentConfigAsync(config)
                                    .continueWith(task1 -> config);
                        });
    }

    /**
     * Fetches config with server-side experiment evaluation ({@code abEvaluate}) WITHOUT persisting
     * anything: the returned config is read-only and its verdicts never reach the current-config
     * disk cache. Callers must invoke {@code ParseUser.invalidateCachedSessionToken} themselves
     * when handling auth failures — there is no central 209 hook in the SDK.
     *
     * @param sessionToken the caller's session token, may be null when logged out.
     * @return a Task resolving to the evaluated config with transient verdicts attached.
     */
    public Task<ParseConfig> getAbEvaluateAsync(String sessionToken) {
        final ParseRESTCommand command =
                ParseRESTConfigCommand.fetchConfigAbEvaluateCommand(sessionToken);
        return command.executeAsync(restClient)
                .onSuccess(
                        task -> {
                            JSONObject result = task.getResult();
                            ParseConfig config = ParseConfig.decode(result, ParseDecoder.get());
                            config.setAbEvaluated(result.optJSONObject("abEvaluated"));
                            return config;
                        });
    }
}
