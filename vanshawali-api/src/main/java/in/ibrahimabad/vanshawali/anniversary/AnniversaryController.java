package in.ibrahimabad.vanshawali.anniversary;

import in.ibrahimabad.vanshawali.anniversary.dto.AnniversaryEntryDto;
import in.ibrahimabad.vanshawali.anniversary.dto.OnThisDayItemDto;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnniversaryController {

    private final AnniversaryService service;

    public AnniversaryController(AnniversaryService service) {
        this.service = service;
    }

    @GetMapping("/api/anniversaries/upcoming")
    public List<AnniversaryEntryDto> upcoming(@RequestParam(defaultValue = "30") int days) {
        return service.upcoming(days);
    }

    @GetMapping("/api/on-this-day")
    public List<OnThisDayItemDto> onThisDay() {
        return service.onThisDay();
    }
}
