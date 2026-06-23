package in.ibrahimabad.vanshawali.person;

import in.ibrahimabad.vanshawali.person.dto.PersonCreateRequest;
import in.ibrahimabad.vanshawali.person.dto.PersonDetailDto;
import in.ibrahimabad.vanshawali.person.dto.PersonFlagsRequest;
import in.ibrahimabad.vanshawali.person.dto.PersonNodeDto;
import in.ibrahimabad.vanshawali.person.dto.PersonSummaryDto;
import in.ibrahimabad.vanshawali.person.dto.PersonUpdateRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Tree read endpoints are public; create/update/delete/flags require an admin JWT (enforced in SecurityConfig). */
@RestController
@RequestMapping("/api")
public class PersonController {

    private final PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }

    @GetMapping("/tree")
    public PersonNodeDto getTree() {
        return service.getTree();
    }

    @GetMapping("/persons/{id}")
    public PersonDetailDto getPerson(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/search")
    public List<PersonSummaryDto> search(@RequestParam("q") String q) {
        return service.search(q);
    }

    @PostMapping("/persons")
    @ResponseStatus(HttpStatus.CREATED)
    public PersonDetailDto createPerson(@Valid @RequestBody PersonCreateRequest req, Authentication auth) {
        return service.createPerson(req, auth.getName());
    }

    @PutMapping("/persons/{id}")
    public PersonDetailDto updatePerson(
            @PathVariable Long id, @Valid @RequestBody PersonUpdateRequest req, Authentication auth) {
        return service.updatePerson(id, req, auth.getName());
    }

    @PatchMapping("/persons/{id}/flags")
    public PersonDetailDto updateFlags(
            @PathVariable Long id, @RequestBody PersonFlagsRequest req, Authentication auth) {
        return service.updateFlags(id, req, auth.getName());
    }

    @DeleteMapping("/persons/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePerson(@PathVariable Long id, Authentication auth) {
        service.deletePerson(id, auth.getName());
    }
}
