package in.ibrahimabad.vanshawali.common.moderation;

/** Optional admin notes attached to an approve/reject decision; reused across all moderated entities. */
public record ModerationDecisionRequest(String notes) {
}
