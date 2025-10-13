/*
 * Copyright (C) 2021 Ringosham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ringosham.translationmod.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import com.ringosham.translationmod.client.types.Language;
import com.ringosham.translationmod.client.types.RequestResult;
import com.ringosham.translationmod.common.Log;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class GoogleClient extends RESTClient {
    //Google translate is a paid service. This is the secret free API for use of the web Google translate
    private static boolean accessDenied = false;

    public GoogleClient() {
        super("https://translate.guation.cn/translate");
    }

    public static boolean isAccessDenied() {
        return accessDenied;
    }

    @Override
    public RequestResult translate(String message, Language from, Language to) {
        Map<String, String> queryParam = new HashMap<>();
        String encodedMessage = null;
        //Percent encode message
        try {
            encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ignored) {
        }
        //Necessary query parameters to trick Google translate
        queryParam.put("client", "gtx");
        queryParam.put("sl", from.getGoogleCode());
        queryParam.put("tl", to.getGoogleCode());
        queryParam.put("dt", "t");
        queryParam.put("q", encodedMessage);
        try {
            Response response = sendRequest("GET", queryParam, "application/json");
            //Network error need retry
            if (response.getResponseCode() == 1) response = sendRequest("GET", queryParam, "application/json");
            if (response.getResponseCode() == 1) response = sendRequest("GET", queryParam, "application/json");
            //Usually Google would just return 429 if they deny access, but just in case it gives any other HTTP error codes
            if (response.getResponseCode() != 200) {
                Log.logger.error("google api get error code {}", response.getResponseCode());
                accessDenied = true;
                Thread timeout = new Timeout();
                timeout.start();
                if (response.getResponseCode() == 429) {
                    return new RequestResult(429, "Access to Google Translate denied", null, null);
                } else {
                    Log.logger.error(response.getEntity());
                    return new RequestResult(411, "API call error", null, null);
                }
            }
            String responseString = response.getEntity();
            //This secret API is specifically made for Google translate. So the response contains lots of useless information.
            //Each sentence translated is divided into separate JSON arrays.
            Gson gson = new Gson();
            //In case the server returns something dumb.
            JsonReader reader = new JsonReader(new StringReader(responseString));
            reader.setLenient(true);
            JsonArray json = gson.fromJson(reader, JsonArray.class);
            Language detectedSource = LangManager.getInstance().findLanguageFromGoogle(json.get(2).getAsString());
            JsonArray lines = json.get(0).getAsJsonArray();
            StringBuilder stringBuilder = new StringBuilder();
            for (JsonElement sentenceObj : lines) {
                JsonArray sentence = sentenceObj.getAsJsonArray();
                stringBuilder.append(sentence.get(0).getAsString());
                stringBuilder.append(" ");
            }
            return new RequestResult(200, stringBuilder.toString(), detectedSource, to);
        } catch (Exception e) {
            Log.logger.error(e);
            return new RequestResult(1, "Connection error", null, null);
        }
    }

    //A timeout thread in case Google blocks user access to the hidden API
    //It is unknown how long Google blocks, so I'll assume 5 minutes
    private static class Timeout extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(30000);
                accessDenied = false;
            } catch (InterruptedException ignored) {
            }
        }
    }
}
