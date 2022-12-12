package git.io.join.adapter.out.user.entity;

import git.io.join.adapter.out.user.entity.UserEntity;
import org.hibernate.internal.util.MutableInteger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    Boolean existsByEmail(String email);

    UserEntity findByEmail(String email);
}
