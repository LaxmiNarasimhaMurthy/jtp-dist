package com.ceilfors.jenkins.plugins.jiratrigger.webhook.constant

/**
 * @author Laxmi Narasimha Murthy AMBADIPUDI
 */
class Constant {
    public static final ISSUE_KEY = 'issue'
    public static final CHANGE_LOG_KEY = 'changelog'
    public static final ITEMS_KEY = 'items'
    public static final TIMESTAMP_KEY ='timestamp'
    public static final WEBHOOK_EVENT_KEY = 'webhookEvent'
    public static final COMMENT_KEY = 'comment'
    public static final FIELDS_KEY = 'fields'
    public static final TIME_TRACKING_KEY = 'timetracking'
    public static final EXPAND_KEY = 'expand'
    public static final CREATED_KEY = 'created'
    public static final UPDATED_KEY = 'updated'
    public static final List<String> TIME_TRACKING_KEYS = [
            'originalEstimateSeconds',
            'remainingEstimateSeconds',
            'timeSpentSeconds',
    ]

    private Constant() {
        // do not instantiate
    }
}
