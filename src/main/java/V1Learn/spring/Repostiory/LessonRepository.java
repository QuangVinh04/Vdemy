package V1Learn.spring.Repostiory;


import V1Learn.spring.Entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface LessonRepository extends JpaRepository<Lesson, String> {
    @Query("SELECT MAX(c.orderIndex) FROM Lesson c WHERE c.chapter.id = :chapterId")
    Optional<Integer> findMaxOrderIndexByChapterId(String chapterId);

    List<Lesson> findByChapterId(String chapterId);

    boolean existsByVideoPublicId(String publicId);
}

