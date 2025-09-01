package com.ceilfors.jenkins.plugins.jiratrigger.webhook

import com.atlassian.jira.rest.client.internal.json.CommentJsonParser
import com.atlassian.jira.rest.client.internal.json.IssueJsonParser
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser
import org.codehaus.jettison.json.JSONException
import org.codehaus.jettison.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.WebhookJsonParserUtils.sanitize
import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.WebhookJsonParserUtils.sanitizeTimeTrackingFields
import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.constant.Constant.CREATED_KEY
import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.constant.Constant.FIELDS_KEY
import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.constant.Constant.UPDATED_KEY
import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.constant.Constant.ISSUE_KEY
import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.constant.Constant.TIMESTAMP_KEY
import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.constant.Constant.WEBHOOK_EVENT_KEY
import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.constant.Constant.COMMENT_KEY
import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.WebhookJsonParserUtils.putIfAbsent
import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.WebhookJsonParserUtils.satisfyRequiredKeys

/**
 * @author ceilfors
 */
class WebhookCommentEventJsonParser implements JsonObjectParser<WebhookCommentEvent> {

    private static final DATE_FIELD_NOT_EXIST = '1980-01-01T00:00:00.000+0000'

    private final IssueJsonParser issueJsonParser = new IssueJsonParser(new JSONObject([:]), new JSONObject([:]))
    private static final Logger LOG = LoggerFactory.getLogger(WebhookCommentEventJsonParser)

    /**
     * Fills details needed by JRC JSON Parser that are missing in JIRA Cloud Webhook events.
     */
    private static void satisfyCloudRequiredKeys(JSONObject webhookEvent) {
        JSONObject fields = webhookEvent.getJSONObject(ISSUE_KEY).getJSONObject(FIELDS_KEY)
        putIfAbsent(fields, CREATED_KEY, DATE_FIELD_NOT_EXIST)
        putIfAbsent(fields, UPDATED_KEY, DATE_FIELD_NOT_EXIST)
    }

    @Override
    WebhookCommentEvent parse(JSONObject webhookEvent) throws JSONException {
        satisfyRequiredKeys(webhookEvent)
        satisfyCloudRequiredKeys(webhookEvent)

        JSONObject issueJson = webhookEvent.getJSONObject(ISSUE_KEY)
        JSONObject commentJson = webhookEvent.getJSONObject(COMMENT_KEY)
        LOG.info('Before sanitizing: {}', issueJson)
        sanitizeTimeTrackingFields(issueJson)
        sanitize(commentJson, CommentJsonParser.VISIBILITY_KEY)
        LOG.info('After sanitizing: {}', issueJson)

        new WebhookCommentEvent(
                webhookEvent.getLong(TIMESTAMP_KEY),
                webhookEvent.getString(WEBHOOK_EVENT_KEY),
                issueJsonParser.parse(issueJson),
                new CommentJsonParser().parse(commentJson)
        )
    }
}
