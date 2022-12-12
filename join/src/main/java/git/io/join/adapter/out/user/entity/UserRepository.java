package git.io.join.adapter.out.user.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository {
    void init(String email, String uuid);

    @Repository
    @RequiredArgsConstructor
    class UserRdbms implements UserRepository {
        private final UserJpaRepository repository;

        @Override
        @Transactional
        public void init(String email, String uuid) {
            if (repository.existsByEmail(email)){
                repository.findByEmail(email)
                        .renew(uuid);
                return;
            }
            repository.save(UserEntity.init(email, uuid));
        }
    }

}
