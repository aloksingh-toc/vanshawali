package in.ibrahimabad.vanshawali.person.seed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/** Mirrors the shape of vanshawali-editor_2.html's DATA object, exported to tree-seed.json. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PersonSeedNode(
        String n,
        Integer hl,
        Integer x,
        Integer q,
        Integer u,
        String note,
        List<PersonSeedNode> k) {

    public boolean isDirectLine() {
        return hl != null && hl == 1;
    }

    public boolean isIssueless() {
        return x != null && x == 1;
    }

    public boolean isUnconfirmed() {
        return q != null && q == 1;
    }

    public boolean isPending() {
        return u != null && u == 1;
    }

    public List<PersonSeedNode> children() {
        return k == null ? List.of() : k;
    }
}
