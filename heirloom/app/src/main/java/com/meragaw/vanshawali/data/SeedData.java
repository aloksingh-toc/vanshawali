package com.meragaw.vanshawali.data;

import com.meragaw.vanshawali.model.FamilyMember;
import com.meragaw.vanshawali.model.FamilyNotification;
import com.meragaw.vanshawali.model.Memory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Demo Hartwell family used to seed a fresh install. */
final class SeedData {

    private SeedData() {}

    static List<FamilyMember> members() {
        long now = System.currentTimeMillis();
        List<FamilyMember> list = new ArrayList<>();

        list.add(member("eleanor_01", "Eleanor", "Hartwell", 1, null,
                Arrays.asList("robert_01", "margaret_01", "susan_01"),
                "1944-03-15", null, "Savannah, Georgia", "arthur_01", 1, now - 60_000));
        list.add(member("arthur_01", "Arthur", "Hartwell", 0, null,
                Arrays.asList("robert_01", "margaret_01", "susan_01"),
                "1938-01-20", "2015-06-10", "Savannah, Georgia", "eleanor_01", 1, now - 50_000));
        list.add(member("margaret_01", "Margaret", "Hartwell", 2, "eleanor_01",
                Arrays.asList("daniel_01"),
                "1966-05-02", null, null, null, 2, now - 40_000));
        list.add(member("robert_01", "Robert", "Hartwell", 3, "eleanor_01",
                new ArrayList<>(),
                "1969-09-11", null, null, null, 2, now - 30_000));
        list.add(member("susan_01", "Susan", "Hartwell", 4, "eleanor_01",
                new ArrayList<>(),
                "1972-12-30", null, null, null, 2, now - 20_000));

        FamilyMember daniel = member("daniel_01", "Daniel", "Hartwell", 3, "margaret_01",
                new ArrayList<>(),
                "1998-07-04", null, null, null, 3, now - 10_000);
        daniel.isCurrentUser = true;
        list.add(daniel);

        return list;
    }

    private static FamilyMember member(String id, String first, String last, int colorIndex,
                                        String parentId, List<String> childIds,
                                        String birthDate, String deathDate, String birthPlace,
                                        String spouseId, int generation, long createdAt) {
        FamilyMember m = new FamilyMember();
        m.id = id;
        m.firstName = first;
        m.lastName = last;
        m.avatarColorIndex = colorIndex;
        m.parentId = parentId;
        m.childIds = childIds;
        m.birthDate = birthDate;
        m.deathDate = deathDate;
        m.birthPlace = birthPlace;
        m.spouseId = spouseId;
        m.generation = generation;
        m.createdAt = createdAt;
        return m;
    }

    static List<FamilyNotification> notifications() {
        List<FamilyNotification> list = new ArrayList<>();

        list.add(notification("n1", FamilyNotification.Type.BIRTHDAY, "eleanor_01", null,
                "<b>Grandma Eleanor's</b> 82nd birthday is in 3 days. Plan something special.",
                2 * 60 * 60 * 1000L, false, true));
        list.add(notification("n2", FamilyNotification.Type.ANNIVERSARY, "margaret_01", null,
                "<b>Margaret &amp; James</b> celebrate 30 years together today.",
                5 * 60 * 60 * 1000L, false, true));
        list.add(notification("n3", FamilyNotification.Type.PHOTO_ADDED, null, "susan_01",
                "<b>Susan</b> added 12 photos to <b>Summer Reunions</b>.",
                24 * 60 * 60 * 1000L, true, false));
        list.add(notification("n4", FamilyNotification.Type.MEMORY_ADDED, null, "daniel_01",
                "<b>Daniel</b> added a memory to <b>Eleanor's</b> profile.",
                2 * 24 * 60 * 60 * 1000L, true, false));
        list.add(notification("n5", FamilyNotification.Type.MEMBER_ADDED, null, "margaret_01",
                "<b>Margaret</b> invited <b>3 new relatives</b> to the family.",
                4 * 24 * 60 * 60 * 1000L, true, false));

        return list;
    }

    private static FamilyNotification notification(String id, FamilyNotification.Type type,
            String memberId, String actorId, String message, long ageMillis,
            boolean isRead, boolean isHighlighted) {
        FamilyNotification n = new FamilyNotification();
        n.id = id;
        n.type = type;
        n.memberId = memberId;
        n.actorId = actorId;
        n.message = message;
        n.timestamp = System.currentTimeMillis() - ageMillis;
        n.isRead = isRead;
        n.isHighlighted = isHighlighted;
        return n;
    }

    static List<Memory> memories() {
        List<Memory> list = new ArrayList<>();
        list.add(memory("m1", "eleanor_01", "Sunday supper on Magnolia Street", "2019-08-11"));
        list.add(memory("m2", "daniel_01", "First day of college", "2016-09-01"));
        return list;
    }

    private static Memory memory(String id, String memberId, String caption, String date) {
        Memory m = new Memory();
        m.id = id;
        m.memberId = memberId;
        m.caption = caption;
        m.date = date;
        return m;
    }
}
