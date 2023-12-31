package org.prkguides.blog.repository;

import org.prkguides.blog.entity.*;
import org.springframework.data.jpa.repository.*;

public interface PostRepository extends JpaRepository<Post,Long> {
}
