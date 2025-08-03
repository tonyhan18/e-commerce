# MySQL 초기 데이터 설명

이 디렉토리는 Docker Compose로 MySQL을 실행할 때 자동으로 생성되는 초기 데이터를 포함합니다.

## 파일 구조

- `01-init-data.sql`: 메인 초기 데이터 스크립트

## 데이터 현황

### 1. 사용자 (User)
- **총 20명**의 사용자 데이터
- 한국어 이름으로 구성
- ID: 1~20

### 2. 상품 (Product)
- **총 20개**의 상품 데이터
- 다양한 카테고리: 전자제품, 게임기, 의류, 생활용품 등
- 판매 상태:
  - SELLING: 18개 (판매중)
  - HOLD: 1개 (판매 보류)
  - STOP_SELLING: 1개 (판매 중지)

### 3. 재고 (Stock)
- 각 상품별 재고 수량 관리
- 주문에 따른 재고 감소 반영
- 0개 재고 상품도 포함 (테스트 시나리오)

### 4. 쿠폰 (Coupon)
- **총 12개**의 쿠폰 데이터
- 다양한 할인율 (5%~30%)
- 상태별 분포:
  - PUBLISHABLE: 10개 (발급가능)
  - REGISTERED: 1개 (등록)
  - CANCELED: 1개 (취소)

### 5. 사용자 쿠폰 (UserCoupon)
- 사용자별 발급된 쿠폰 데이터
- 사용/미사용 상태 포함
- 발급일, 사용일 기록

### 6. 잔액 (Balance)
- 사용자별 잔액 정보
- 최대 잔액 제한 (10,000,000원) 반영
- 다양한 잔액 수준 (25만원~180만원)

### 7. 잔액 거래 내역 (BalanceTransaction)
- 충전/사용 거래 기록
- 총 28건의 거래 내역
- 충전: 15건, 사용: 13건

### 8. 주문 (Order)
- **총 15건**의 주문 데이터
- 상태별 분포:
  - PAID: 10건 (결제완료)
  - CREATED: 5건 (주문생성)
- 쿠폰 사용/미사용 케이스 포함

### 9. 주문 상품 (OrderProduct)
- 주문별 상품 상세 정보
- 수량, 단가, 상품명 포함
- 총 25건의 주문 상품 데이터

### 10. 결제 (Payment)
- **총 15건**의 결제 데이터
- 결제 방법별 분포:
  - CARD: 6건
  - CASH: 3건
  - VIRTUAL_ACCOUNT: 2건
  - UNKNOWN: 4건
- 상태별 분포:
  - COMPLETED: 10건
  - WAITING: 3건
  - READY: 2건

## 테스트 시나리오

이 초기 데이터는 다음과 같은 테스트 시나리오를 포함합니다:

1. **정상 주문 시나리오**
   - 쿠폰 사용 주문
   - 일반 주문 (쿠폰 미사용)
   - 다양한 결제 방법

2. **재고 관리 시나리오**
   - 재고 있는 상품 주문
   - 재고 없는 상품 (판매 중지)
   - 재고 부족 상황

3. **쿠폰 관리 시나리오**
   - 사용 가능한 쿠폰
   - 만료된 쿠폰
   - 취소된 쿠폰
   - 수량 부족 쿠폰

4. **잔액 관리 시나리오**
   - 충전/사용 거래
   - 잔액 부족 상황
   - 최대 잔액 초과 상황

5. **결제 시나리오**
   - 완료된 결제
   - 대기 중인 결제
   - 준비 중인 결제

## 사용 방법

1. Docker Compose 실행:
```bash
docker-compose up -d
```

2. MySQL 접속:
```bash
docker exec -it e-commerce-mysql-1 mysql -u application -papplication hhplus
```

3. 데이터 확인:
```sql
-- 사용자 수 확인
SELECT COUNT(*) FROM user;

-- 상품별 재고 확인
SELECT p.name, s.quantity FROM product p JOIN stock s ON p.product_id = s.product_id;

-- 주문 상태별 통계
SELECT order_status, COUNT(*) FROM orders GROUP BY order_status;
```

## 주의사항

- 초기 데이터는 테스트 목적으로만 사용하세요
- 실제 운영 환경에서는 이 데이터를 사용하지 마세요
- 데이터베이스 스키마 변경 시 이 스크립트도 함께 업데이트해야 합니다 