# 지하철 노선도 단위 테스트 미션
## ✨ 1단계

### 미션 요구사항
- 지하철 구간 인수 테스트를 단위 테스트로 작성 
  - 구간 단위 테스트
  - 구간 서비스 단위 테스트
  - 구간 서비스 단위 테스트 With Mock 
- 단위 테스트 기반으로 비지니스 로직 리팩토링
- 비지니스 로직을 도메인 클래스로 옮기기

## ✨ 2단계

### 미션 요구사항
- 추가된 요구사항을 정의한 인수 조건 도출
- 인수 조건을 검증하는 인수 테스트 작성
- 예외 케이스 검증 추가
- 인수 테스트 이후 기능 구현은 TDD로 진행하기
- 도메인 레이어 테스트 필수 !

### 지하철 구간 추가 리팩터링
- 기존 구간의 역을 기준으로 새로운 구간을 추가
  - 상행 종점, 하행 종점, 구간 사이에 새로운 구간 등록 가능하게 구현
  - (기존 구간의 거리 - 새로운 구간의 거리) = 변경된 구간의 거리
- 예외 케이스
  - 새로운 구간에 등록된 역이 없을 경우 추가 불가능
  - 새로운 구간에 역이 모두 등록되어있을 경우 추가 불가능
  - 기존 역 사이 길이보다 크거나 같으면 등록 불가능

## ✨ 3단계

### 미션 요구사항
- 추가된 요구사항을 정의한 인수 조건 도출
- 인수 조건을 검증하는 인수 테스트 작성
- 예외 케이스 검증 추가
- 인수 테스트 이후 기능 구현은 TDD로 진행하기
- 도메인 레이어 테스트 필수 !

### 지하철 구간 삭제 리팩터링
- 위치와 상관없이 구간 제거가 가능하도록
- 예외 케이스
  - 구간이 하나만 존재할 경우 삭제할 수 없음
  - 노선에 등록되어있지 않은 역을 삭제할 수 없음