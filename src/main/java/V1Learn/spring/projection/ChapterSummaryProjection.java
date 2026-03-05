package V1Learn.spring.projection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterSummaryProjection {
    String id;
    String title;
    String description;
    Integer orderIndex;
    Long totalLessons;          // COUNT → Long
    Long totalDurationSeconds;  // SUM → Long

//    public String getTotalDuration() {
//        if (totalDurationSeconds == null || totalDurationSeconds == 0) return "0:00";
//        long hours = totalDurationSeconds / 3600;
//        long minutes = (totalDurationSeconds % 3600) / 60;
//        return String.format("%d:%02d", hours, minutes);
//    }
}