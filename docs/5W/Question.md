# AOP => Proxy => self-invocation
AOP (Aspect-Oriented Programming)
관점 지향 프로그래밍: 횡단 관심사(cross-cutting concerns)를 분리하는 프로그래밍 패러다임
예: 로깅, 트랜잭션, 보안 등 여러 클래스에서 반복되는 코드를 별도로 관리
---
Proxy (프록시) == 미들웨어
대리 객체: 실제 객체를 감싸는 래퍼 객체
Spring AOP는 기본적으로 JDK Dynamic Proxy 또는 CGLIB Proxy를 사용
프록시가 실제 메서드 호출을 가로채서 AOP 기능(로깅, 트랜잭션 등)을 추가
---
Self-invocation (자체 호출)
같은 클래스 내에서 다른 메서드를 호출할 때 발생하는 문제
프록시를 거치지 않고 직접 메서드를 호출하기 때문에 AOP가 동작하지 않음


# 횡단 관심사(Cross-cutting Concerns)는 애플리케이션 전체에 걸쳐서 반복적으로 나타나는 기능들을 말합니다.