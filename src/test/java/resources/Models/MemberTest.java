package resources.models;

import org.junit.jupiter.api.Test;

import resources.models.Member;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    @Test
    void shouldReturnMemberFieldsAndToString() {
        Member member = new Member("M100", "Sinthuja");

        assertEquals("M100", member.getId());
        assertEquals("Sinthuja", member.getName());
        assertEquals("Sinthuja (M100)", member.toString());
    }

    @Test
    void membersWithSameIdShouldBeEqual() {
        Member first = new Member("M001", "Alice");
        Member second = new Member("M001", "Daniel");
        Member third = new Member("M002", "Lavan");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, third);
        assertNotEquals(first, null);
        assertNotEquals(first, "not a member");
    }
}