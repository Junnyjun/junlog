# 사용자 관리
사용자 관리를 위해서는 UserDetailsService & UserDetailManager를 구현해야 한다. \
이를 통해 사용자의 정보를 가져오고, 인증을 수행할 수 있다.\
UserDetailsService는 사용자의 정보를 검색하는 역할을 한다.

UserDetailManager는 대부분의 어플리케이션에 필요한 사용자 관리 기능을 제공한다.

스프링 시큐리티에서의 사용자는 작업을 수행할수 있는 권리를 가진자를 이야기한다.
이때의 권한은 GrantedAuthority를 구현한 객체로 표현된다.
