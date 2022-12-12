package git.io.join.adapter.out.user.entity;

import git.io.join.adapter.out.user.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.AUTO;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    private String uuid;
    private Integer history;
    @Column(unique = true)
    private String email;
    private Boolean isVerified;


    private UserEntity(String uuid, Integer history, String email, Boolean isVerified) {
        this.uuid = uuid;
        this.history = history;
        this.email = email;
        this.isVerified = isVerified;
    }

    public static UserEntity init(String uuid, String email) {
        return new UserEntity(uuid, 1, email, Boolean.FALSE);
    }

    public void renew(String uuid) {
        if (this.history > 5) {
            throw new IllegalArgumentException("차단된 이메일 입니다.\n문의해주세요");
        }
        this.history++;
        this.uuid = uuid;
    }
}
