package in.ibrahimabad.vanshawali.relation;

import in.ibrahimabad.vanshawali.person.Person;
import in.ibrahimabad.vanshawali.person.PersonRepository;
import in.ibrahimabad.vanshawali.relation.dto.RelationResultDto;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * "How are we related?" calculator: walks each person's ancestor chain to the root,
 * finds the lowest common ancestor (the longest matching prefix of both root-to-person
 * paths), and translates the up/down distances from that ancestor into a Hindi kinship
 * description.
 */
@Service
@Transactional(readOnly = true)
public class RelationService {

    private final PersonRepository repository;

    public RelationService(PersonRepository repository) {
        this.repository = repository;
    }

    public RelationResultDto findRelation(Long fromId, Long toId) {
        Person from = repository.findById(fromId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found: " + fromId));
        Person to = repository.findById(toId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found: " + toId));

        List<Person> pathFrom = ancestorPath(from);
        List<Person> pathTo = ancestorPath(to);

        int common = 0;
        while (common < pathFrom.size()
                && common < pathTo.size()
                && pathFrom.get(common).getId().equals(pathTo.get(common).getId())) {
            common++;
        }
        Person lca = pathFrom.get(common - 1);
        int depthFrom = pathFrom.size() - common;
        int depthTo = pathTo.size() - common;
        int generationGap = Math.abs(depthFrom - depthTo);

        String label = buildLabel(from, to, lca, depthFrom, depthTo);

        return new RelationResultDto(
                from.getId(),
                from.getName(),
                to.getId(),
                to.getName(),
                lca.getId(),
                lca.getName(),
                generationGap,
                depthFrom,
                depthTo,
                label,
                pathFrom.stream().map(Person::getName).toList(),
                pathTo.stream().map(Person::getName).toList());
    }

    /** root -> person, inclusive of both ends. */
    private List<Person> ancestorPath(Person person) {
        Deque<Person> stack = new ArrayDeque<>();
        Person cur = person;
        while (cur != null) {
            stack.push(cur);
            cur = cur.getParent();
        }
        return new ArrayList<>(stack);
    }

    private String buildLabel(Person from, Person to, Person lca, int depthFrom, int depthTo) {
        if (from.getId().equals(to.getId())) {
            return "यह एक ही व्यक्ति हैं";
        }
        if (depthFrom == 0) {
            return directLineLabel(depthTo, false);
        }
        if (depthTo == 0) {
            return directLineLabel(depthFrom, true);
        }
        if (depthFrom == depthTo) {
            if (depthFrom == 1) {
                return "सहोदर भाई-बहन (एक ही माता-पिता की संतान)";
            }
            if (depthFrom == 2) {
                return "आपस में कज़िन — चाचा/मामा/बुआ/मौसी के बच्चे";
            }
            return "दूर के कज़िन — समान पीढ़ी, उभयनिष्ठ पूर्वज " + lca.getName() + " से " + depthFrom + " पीढ़ी नीचे";
        }

        int gap = Math.abs(depthFrom - depthTo);
        if (gap == 1 && Math.min(depthFrom, depthTo) == 1) {
            return depthFrom < depthTo
                    ? "चाचा/मामा/बुआ/मौसी जैसा रिश्ता — आप वरिष्ठ पीढ़ी में हैं"
                    : "भतीजा/भतीजी/नाती-नातिन जैसा रिश्ता — आप कनिष्ठ पीढ़ी में हैं";
        }
        return "उभयनिष्ठ पूर्वज " + lca.getName() + " से जुड़ा रिश्ता — " + gap + " पीढ़ी का अंतर";
    }

    private String directLineLabel(int depth, boolean toIsAncestor) {
        String unit = switch (depth) {
            case 1 -> toIsAncestor ? "माता/पिता" : "पुत्र/पुत्री";
            case 2 -> toIsAncestor ? "दादा/दादी या नाना/नानी" : "पौत्र/पौत्री या नाती/नातिन";
            case 3 -> toIsAncestor ? "परदादा/परदादी" : "प्रपौत्र/प्रपौत्री";
            default -> toIsAncestor ? depth + " पीढ़ी ऊपर के पूर्वज" : depth + " पीढ़ी नीचे के वंशज";
        };
        return "सीधी वंश-रेखा — " + unit;
    }
}
