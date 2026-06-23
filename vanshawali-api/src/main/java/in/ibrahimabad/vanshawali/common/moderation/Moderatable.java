package in.ibrahimabad.vanshawali.common.moderation;

/** Implemented by every entity that carries an embedded {@link ModerationStatus}. */
public interface Moderatable {

    ModerationStatus getModeration();
}
