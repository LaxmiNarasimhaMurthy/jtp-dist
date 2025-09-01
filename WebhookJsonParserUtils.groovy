package com.ceilfors.jenkins.plugins.jiratrigger.webhook

import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONException
import org.codehaus.jettison.json.JSONObject

import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.constant.Constant.EXPAND_KEY
import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.constant.Constant.ISSUE_KEY
import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.constant.Constant.FIELDS_KEY
import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.constant.Constant.TIME_TRACKING_KEYS
import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.constant.Constant.TIME_TRACKING_KEY
/**
 * @author ceilfors
 */
class WebhookJsonParserUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private WebhookJsonParserUtils() {
        // Utility class; do not instantiate
    }

    /**
     * Fills details needed by JRC JSON Parser that are missing in Webhook events.
     */
    static void satisfyRequiredKeys(JSONObject webhookEvent) {
        JSONObject issue = webhookEvent.getJSONObject(ISSUE_KEY)
        putIfAbsent(issue, EXPAND_KEY, '')
    }

    static void putIfAbsent(JSONObject jsonObject, String key, Object value) {
        if (!jsonObject.opt(key)) {
            jsonObject.put(key, value)
        }
    }

    /**
     * Sanitizes time tracking fields in the issue JSON by removing null-valued keys.
     *
     * @param issueJson the issue JSON object
     */
    static void sanitizeTimeTrackingFields(JSONObject issueJson) {
        JSONObject timeTracking = issueJson?.optJSONObject(FIELDS_KEY)?.optJSONObject(TIME_TRACKING_KEY)
        if (timeTracking != null) {
            sanitize(timeTracking, TIME_TRACKING_KEYS, false)
        }
    }

    /**
     * Sanitizes the given {@link JSONObject} by removing specified keys if their values are null.
     * Optionally performs recursive sanitization on nested {@link JSONObject} and
     * {@link org.codehaus.jettison.json.JSONArray} values.
     *
     * @param jsonObject the JSON object to sanitize
     * @param keysToSanitize list of keys to check for null values
     * @param recursive whether to sanitize nested objects and arrays
     */
    static void sanitize(JSONObject jsonObject, List<String> keysToSanitize, boolean recursive = false) {
        if (jsonObject == null || keysToSanitize == null) {
            return
        }

        keysToSanitize.findAll { key -> jsonObject.isNull(key) }
                .each { key -> jsonObject.remove(key) }

        if (recursive) {
            Iterator<String> keys = jsonObject.keys()
            while (keys.hasNext()) {
                String key = keys.next()
                Object value = jsonObject.opt(key)
                if (value?.getClass() == JSONObject) {
                    sanitize(value as JSONObject, keysToSanitize, true)
                } else if (value?.getClass() == JSONArray) {
                    sanitizeArray(value as JSONArray, keysToSanitize)
                }
            }
        }
    }

    /**
     * Recursively sanitizes the given {@link JSONArray} by applying key-based null removal
     * to any nested {@link JSONObject} or {@link JSONArray} elements.
     *
     * @param jsonArray the JSON array to sanitize
     * @param keysToSanitize list of keys to check for null values
     * @throws org.codehaus.jettison.json.JSONException if the JSON structure is invalid
     */
    private static void sanitizeArray(JSONArray jsonArray, List<String> keysToSanitize) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i)
            if (value?.getClass() == JSONObject) {
                sanitize(value as JSONObject, keysToSanitize, true)
            } else if (value?.getClass() == JSONArray) {
                sanitizeArray(value as JSONArray, keysToSanitize)
            }
        }
    }

}

