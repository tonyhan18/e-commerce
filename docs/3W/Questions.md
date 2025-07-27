## Builder 패턴의 목적
유연한 객체 생성: Lombok의 @Builder 어노테이션을 통해 빌더 패턴을 자동 생성
가독성 향상: 체이닝 방식으로 객체를 생성할 수 있어 코드가 더 읽기 쉬움
선택적 매개변수: 필요한 필드만 선택적으로 설정 가능

## create 정적 팩토리 메서드의 목적
비즈니스 규칙 검증: 객체 생성 시 필수적인 유효성 검사를 수행
도메인 로직 캡슐화: 상품 생성에 필요한 비즈니스 규칙을 한 곳에 모음
명확한 의도 표현: create라는 메서드명으로 "새로운 상품을 생성한다"는 의도를 명확히 표현
불변성 보장: 생성 시점에 모든 유효성 검사를 완료하여 안전한 객체 생성

## 두 개를 함께 쓰는 이유
Builder: JPA 엔티티 매핑이나 테스트에서 유연한 객체 생성이 필요할 때
create: 실제 비즈니스 로직에서 새로운 상품을 생성할 때 (유효성 검사 포함)


# enum 에서 description의 역할(feat 린터에러)
생성자 매개변수 불일치 에러

# @Entity, @Table(name = "product"), @Getter, @NoArgsConstructor, @AllArgsConstructor, @EntityListeners(AuditingEntityListener.class)
1. @Getter
기능: 모든 필드에 대한 getter 메서드를 자동으로 생성
예시: getName(), getPrice(), getSellStatus() 등이 자동 생성됨
장점: 수동으로 getter 메서드를 작성할 필요 없음
2. @NoArgsConstructor
기능: 매개변수가 없는 기본 생성자를 자동 생성
예시: Product product = new Product();
필요성: JPA 엔티티는 기본 생성자가 반드시 필요함
3. @AllArgsConstructor
기능: 모든 필드를 매개변수로 받는 생성자를 자동 생성
예시: Product product = new Product(1L, "상품명", 10000L, ProductSellingStatus.SELLING);
4. @Builder
기능: 빌더 패턴을 자동으로 생성
사용법:
