/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.parse.http.ParseHttpRequest;
import java.util.HashMap;
import java.util.Map;

class ParseRESTConfigCommand extends ParseRESTCommand {

    public ParseRESTConfigCommand(
            String httpPath,
            ParseHttpRequest.Method httpMethod,
            Map<String, ?> parameters,
            String sessionToken) {
        super(httpPath, httpMethod, parameters, sessionToken);
    }

    public static ParseRESTConfigCommand fetchConfigCommand(String sessionToken) {
        return new ParseRESTConfigCommand(
                "config", ParseHttpRequest.Method.GET, null, sessionToken);
    }

    /**
     * Builds an opt-in config fetch that asks the server to evaluate experiments ({@code
     * abEvaluate}). Parameters travel in the method-override body like every other GET in this SDK
     * (see {@code ParseRESTCommand} GET handling) — never in the query string. The default {@link
     * #fetchConfigCommand} path stays byte-identical.
     *
     * @param sessionToken the caller's session token, may be null when logged out.
     * @return a GET {@code config} command carrying {@code abEvaluate=true}.
     */
    @NonNull
    public static ParseRESTConfigCommand fetchConfigAbEvaluateCommand(
            @Nullable String sessionToken) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("abEvaluate", true);
        return new ParseRESTConfigCommand(
                "config", ParseHttpRequest.Method.GET, parameters, sessionToken);
    }

    public static ParseRESTConfigCommand updateConfigCommand(
            final Map<String, ?> configParameters, String sessionToken) {
        Map<String, Map<String, ?>> commandParameters = null;
        if (configParameters != null) {
            commandParameters = new HashMap<>();
            commandParameters.put("params", configParameters);
        }
        return new ParseRESTConfigCommand(
                "config", ParseHttpRequest.Method.PUT, commandParameters, sessionToken);
    }
}
