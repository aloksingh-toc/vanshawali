package in.ibrahimabad.vanshawali.relation;

import in.ibrahimabad.vanshawali.relation.dto.RelationResultDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RelationController {

    private final RelationService service;

    public RelationController(RelationService service) {
        this.service = service;
    }

    @GetMapping("/relation")
    public RelationResultDto findRelation(@RequestParam Long from, @RequestParam Long to) {
        return service.findRelation(from, to);
    }
}
