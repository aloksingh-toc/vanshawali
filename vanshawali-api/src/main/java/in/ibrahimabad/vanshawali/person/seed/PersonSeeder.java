package in.ibrahimabad.vanshawali.person.seed;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.ibrahimabad.vanshawali.person.Person;
import in.ibrahimabad.vanshawali.person.PersonRepository;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * One-time migration: imports the 299 people exported from vanshawali-editor_2.html
 * (via scripts/export-persons-json.mjs -> resources/data/tree-seed.json) into the
 * persons table. Runs automatically on startup, but only when the table is empty,
 * so it is safe to leave in place and re-run the app any number of times.
 */
@Component
public class PersonSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PersonSeeder.class);
    private static final String SEED_RESOURCE = "data/tree-seed.json";

    private final PersonRepository repository;
    private final ObjectMapper objectMapper;

    public PersonSeeder(PersonRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        if (repository.count() > 0) {
            log.info("persons table already has {} rows, skipping seed", repository.count());
            return;
        }

        PersonSeedNode root;
        try (var in = new ClassPathResource(SEED_RESOURCE).getInputStream()) {
            root = objectMapper.readValue(in, PersonSeedNode.class);
        }

        int imported = insertSubtree(root, null, 0, 0);
        log.info("Seeded {} people from {}", imported, SEED_RESOURCE);
    }

    private int insertSubtree(PersonSeedNode node, Person parent, int siblingOrder, int generation) {
        Person person = new Person();
        person.setParent(parent);
        person.setName(node.n());
        person.setAliasNote(node.note());
        person.setDirectLine(node.isDirectLine());
        person.setIssueless(node.isIssueless());
        person.setUnconfirmed(node.isUnconfirmed());
        person.setPending(node.isPending());
        person.setSiblingOrder(siblingOrder);
        person.setGeneration(generation);
        Person saved = repository.save(person);

        int count = 1;
        var children = node.children();
        for (int i = 0; i < children.size(); i++) {
            count += insertSubtree(children.get(i), saved, i, generation + 1);
        }
        return count;
    }
}
