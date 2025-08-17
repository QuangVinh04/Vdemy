package V1Learn.spring.Repostiory;


import V1Learn.spring.Entity.MediaFile;
import V1Learn.spring.utils.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, String> {
    @Query("""
      SELECT m FROM MediaFile m
       WHERE (:userId IS NULL OR m.uploadedBy = :userId)
         AND (:type IS NULL OR m.resourceType = :type)
         AND (:folder IS NULL OR m.folder = :folder)
       ORDER BY m.createdAt DESC
    """)
    Page<MediaFile> search(@Param("userId") String userId,
                           @Param("type") ResourceType type,
                           @Param("folder") String folder,
                           Pageable pageable);

    Optional<MediaFile> findByPublicId(String publicId);
}

