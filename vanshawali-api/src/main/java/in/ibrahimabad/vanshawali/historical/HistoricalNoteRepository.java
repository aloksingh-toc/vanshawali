package in.ibrahimabad.vanshawali.historical;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricalNoteRepository extends JpaRepository<HistoricalNote, Long> {

    List<HistoricalNote> findByNoteMonthAndNoteDay(int noteMonth, int noteDay);

    List<HistoricalNote> findAllByOrderByNoteMonthAscNoteDayAsc();
}
