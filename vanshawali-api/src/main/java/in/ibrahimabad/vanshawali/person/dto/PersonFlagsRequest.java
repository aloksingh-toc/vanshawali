package in.ibrahimabad.vanshawali.person.dto;

/** Partial update for the four boolean flags shown in the tree legend; null fields are left unchanged. */
public record PersonFlagsRequest(
        Boolean directLine,
        Boolean issueless,
        Boolean unconfirmed,
        Boolean pending) {
}
