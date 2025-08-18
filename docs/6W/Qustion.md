# LoggingFilter

주요 기능
1. 요청 로깅: 모든 HTTP 요청의 정보를 로그로 기록
- 클라이언트 IP 주소
- HTTP 메서드 (GET, POST, PUT, DELETE 등)
- 요청 URL
- 요청 본문 내용
2. 응답 로깅: 모든 HTTP 응답의 정보를 로그로 기록
- 응답 상태 코드
- 응답 본문 내용
- 요청 처리 시간 (duration)
3. 성능 모니터링: 각 요청의 처리 시간을 측정하여 성능을 추적

# lock의 역할

/lock 폴더는 분산 환경에서 동시성 제어를 위한 락(Lock) 시스템을 구현한 패키지입니다. 이는 여러 사용자가 동시에 같은 리소스에 접근할 때 발생할 수 있는 데이터 일관성 문제를 해결하기 위한 것입니다.
주요 구성 요소와 역할

1. 핵심 인터페이스
LockTemplate.java: 락 작업을 위한 템플릿 인터페이스
LockStrategy.java: 다양한 락 전략을 정의하는 인터페이스
LockCallback.java: 락 획득 후 실행할 작업을 정의하는 콜백

2. 락 타입과 전략
LockType.java: 락의 종류를 정의 (예: SpinLock, PubSubLock 등)
LockStrategyRegistry.java: 다양한 락 전략을 등록하고 관리

3. 구현체
DefaultLockTemplate.java: 기본 락 템플릿 구현
DistributedLockAspect.java: AOP를 사용한 분산 락 처리
DistributedLock.java: 분산 락 어노테이션

4. 유틸리티
LockKeyGenerator.java: 락 키 생성 로직
LockIdHolder.java: 락 ID를 ThreadLocal에 저장하여 관리