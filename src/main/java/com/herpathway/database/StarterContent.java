package com.herpathway.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * A small starter library so the Learning page isn't empty on a fresh install.
 * Inserted once, with no author (shown as "HerPathway team"); mentors and organizations add the rest.
 */
final class StarterContent {

    private StarterContent() {
    }

    static void insertIfEmpty(Connection c) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM learning_content")) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }
        String sql = "INSERT INTO learning_content (authorId, type, title, summary, body, url) VALUES (NULL, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            // Inserted oldest first; the library lists newest first.
            link(ps, "book", "Pro Git",
                    "The standard free book on Git, from your first commit to branching workflows used by real teams.",
                    "https://git-scm.com/book/en/v2");
            link(ps, "book", "Think Python",
                    "A gentle, free introduction to programming and to thinking like a computer scientist.",
                    "https://greenteapress.com/wp/think-python-2e/");
            link(ps, "book", "Automate the Boring Stuff with Python",
                    "Practical Python for beginners: automate spreadsheets, files and other everyday tasks. Free to read online.",
                    "https://automatetheboringstuff.com/");
            link(ps, "video", "MIT OpenCourseWare",
                    "Free lecture videos and course materials from MIT across engineering, mathematics and science.",
                    "https://ocw.mit.edu/");
            link(ps, "video", "Teach girls bravery, not perfection",
                    "Reshma Saujani's TED talk on why taking risks and being comfortable with imperfection matters in tech.",
                    "https://www.ted.com/talks/reshma_saujani_teach_girls_bravery_not_perfection");
            link(ps, "video", "CS50: Introduction to Computer Science",
                    "Harvard's free introductory course. Lectures cover algorithms, data structures, web and more.",
                    "https://cs50.harvard.edu/x/");
            article(ps, "Asking better questions as a mentee",
                    "Specific questions get specific answers. A simple structure for getting more out of every session.",
                    ASKING_QUESTIONS);
            article(ps, "Dealing with impostor feelings in technical work",
                    "Feeling like you don't belong is common and says little about your ability. What actually helps.",
                    IMPOSTOR);
            article(ps, "How to get the most out of a mentorship",
                    "Mentorship works best when the mentee drives it. Five habits that make the relationship useful.",
                    MENTORSHIP);
        }
    }

    private static void article(PreparedStatement ps, String title, String summary, String body) throws SQLException {
        insert(ps, "article", title, summary, body, null);
    }

    private static void link(PreparedStatement ps, String type, String title, String summary, String url) throws SQLException {
        insert(ps, type, title, summary, null, url);
    }

    private static void insert(PreparedStatement ps, String type, String title, String summary, String body, String url)
            throws SQLException {
        ps.setString(1, type);
        ps.setString(2, title);
        ps.setString(3, summary);
        if (body == null) {
            ps.setNull(4, Types.VARCHAR);
        } else {
            ps.setString(4, body.trim());
        }
        if (url == null) {
            ps.setNull(5, Types.VARCHAR);
        } else {
            ps.setString(5, url);
        }
        ps.executeUpdate();
    }

    private static final String MENTORSHIP = """
            A mentor can shorten your learning curve by years, but only if you treat the relationship as something you run, not something that happens to you.

            1. Arrive with a goal
            "I want to grow" gives your mentor nothing to work with. "I want to move from QA into backend development within a year" does. Write down one goal for the next three months and share it in your first conversation.

            2. Bring something concrete to every session
            A piece of code, a CV draft, a decision you are stuck on. Concrete material turns general advice into advice about your situation.

            3. Do the work between sessions
            If your mentor suggests reading something or trying an approach, do it and report back, even when it did not work. Nothing builds a mentor's commitment faster than seeing their advice acted on.

            4. Ask about their mistakes
            Success stories are tidy. The useful details are in what went wrong: the job they should not have taken, the skill they learned too late.

            5. Close the loop
            End each conversation by repeating what you will do next and when you will check in. Then do exactly that.

            Mentors are volunteering their time. Being prepared, specific and reliable is the best way to thank them.
            """;

    private static final String IMPOSTOR = """
            Many capable people in technical fields carry a quiet belief that they got here by luck and will soon be found out. It is common among students, career changers and senior engineers alike, and it is especially common when you are one of few people like you in the room.

            The feeling is not evidence
            Feeling underqualified and being underqualified are different things. In a field this large, everyone is a beginner at most of it. Not knowing something is the normal condition of technical work, not a sign that you do not belong.

            Keep a record
            Write down what you ship, fix and learn each week. On bad days your memory will offer only the mistakes. A written record is harder to argue with.

            Say "I don't know yet" out loud
            People who ask questions early are usually seen as more competent, not less, because they get unstuck faster. Asking in public also gives quieter colleagues permission to do the same.

            Compare with your past self
            Measuring yourself against someone with ten more years of experience tells you nothing. Compare today's work with what you could do a year ago.

            Talk about it
            Bring it up with your mentor. Almost every one of them has a story about feeling the same way, and hearing it from someone you respect takes away much of its weight.
            """;

    private static final String ASKING_QUESTIONS = """
            The quality of the help you get depends a great deal on how you ask. A vague question forces the other person to guess what you need. A well-formed one can often be answered in a minute.

            Give context first
            Say what you are trying to achieve before describing the problem. "I am building a small API for a class project and want to store user sessions" lets your mentor spot a better approach you had not considered.

            Show what you tried
            List the two or three things you already attempted and what happened. It saves time and shows where your understanding stops, which is exactly where the answer needs to start.

            Ask one question at a time
            "How do I become a data scientist?" is ten questions in one. "Should I learn statistics or SQL first, given I already know Python?" can be answered.

            Prefer questions about decisions
            "What would you look at first to choose between these two offers?" teaches you how an experienced person thinks, which you can reuse long after the specific answer stops being relevant.

            Follow up
            Tell the person what you did with their answer. It closes the loop, and it makes them more likely to help the next time.
            """;
}
