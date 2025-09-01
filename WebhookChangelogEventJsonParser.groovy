package com.ceilfors.jenkins.plugins.jiratrigger.webhook

import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
import com.atlassian.jira.rest.client.api.domain.ChangelogItem
import com.atlassian.jira.rest.client.internal.json.ChangelogItemJsonParser
import com.atlassian.jira.rest.client.internal.json.IssueJsonParser
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil
import org.codehaus.jettison.json.JSONException
import org.codehaus.jettison.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.WebhookJsonParserUtils.sanitizeTimeTrackingFields
import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.constant.Constant.ISSUE_KEY
import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.constant.Constant.TIMESTAMP_KEY
import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.constant.Constant.WEBHOOK_EVENT_KEY
import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.constant.Constant.CHANGE_LOG_KEY
import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.constant.Constant.ITEMS_KEY
import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.WebhookJsonParserUtils.satisfyRequiredKeys

/**
 * @author ceilfors
 */
class WebhookChangelogEventJsonParser implements JsonObjectParser<WebhookChangelogEvent> {

    /**
     * Not using ChangelogJsonParser because it is expecting "created" field which is not
     * being supplied from webhook event.
     */
    private final ChangelogItemJsonParser changelogItemJsonParser = new ChangelogItemJsonParser()
    private final IssueJsonParser issueJsonParser = new IssueJsonParser(new JSONObject([:]), new JSONObject([:]))
    private static final Logger LOG = LoggerFactory.getLogger(WebhookChangelogEventJsonParser)

    @Override
    WebhookChangelogEvent parse(JSONObject webhookEvent) throws JSONException {
        satisfyRequiredKeys(webhookEvent)

        Collection<ChangelogItem> items = JsonParseUtil.parseJsonArray(
                webhookEvent.getJSONObject(CHANGE_LOG_KEY).getJSONArray(ITEMS_KEY), changelogItemJsonParser)

        JSONObject issueJson = webhookEvent.getJSONObject(ISSUE_KEY)

        LOG.info('Before sanitizing: {}', issueJson)
        sanitizeTimeTrackingFields(issueJson)
        LOG.info('After sanitizing: {}', issueJson)

        new WebhookChangelogEvent(
                webhookEvent.getLong(TIMESTAMP_KEY),
                webhookEvent.getString(WEBHOOK_EVENT_KEY),
                issueJsonParser.parse(issueJson),
                new ChangelogGroup(null, null, items)
        )
    }
}