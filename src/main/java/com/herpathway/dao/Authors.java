package com.herpathway.dao;

/**
 * SQL fragments that resolve a user's public name, whichever role they have.
 * Use with a {@code User} row aliased {@code u}: {@code SELECT ..., NAME AS authorName FROM x JOIN User u ... JOINS}.
 */
final class Authors {

    /** Profile tables, joined to {@code u}. */
    static final String JOINS =
            " LEFT JOIN form_Mentee am ON am.userId = u.id"
            + " LEFT JOIN form_Mentor ao ON ao.userId = u.id"
            + " LEFT JOIN form_Organization ag ON ag.userId = u.id ";

    /** Display name, falling back to the username when no profile exists yet. */
    static final String NAME =
            "COALESCE(NULLIF(am.displayName, ''), NULLIF(ao.displayName, ''), NULLIF(ag.organizationName, ''), u.username)";

    private Authors() {
    }
}
