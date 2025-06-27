package V1Learn.spring.Repostiory;

import V1Learn.spring.Entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ChapterRepository extends JpaRepository<Chapter, String> {

    @Query("SELECT MAX(c.orderIndex) FROM Chapter c WHERE c.course.id = :courseId")
    Optional<Integer> findMaxOrderIndexByCourseId(String courseId);
    void deleteAllByCourseId(String courseId);
    List<Chapter> findByCourseId(String courseId);
}

