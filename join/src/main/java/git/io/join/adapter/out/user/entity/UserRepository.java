package git.io.join.adapter.out.user.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository {
    void init(String email, String uuid);

    Long timer(String email);

    @Repository
    @RequiredArgsConstructor
    class UserRdbms implements UserRepository {
        private final UserJpaRepository repository;

        @Override
        @Transactional(readOnly = true)
        public Long timer(String email) {
                return repository.findByEmail(email)
                        .timer();
        }

        @Override
        @Transactional
        public void init(String email, String uuid) {
            if (repository.existsByEmail(email)){
                repository.findByEmail(email)
                        .renew(uuid);
                return;
            }
            repository.save(UserEntity.init(uuid,email));
        }
    }

}
