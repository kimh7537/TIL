#Redis

---
---
## ✏️ `Lettuce`

![alt text](image/image-2.png)

- `setnx` 명령어를 활용하여 분산락 구현
- `spin lock`방식: 락을 획득하려는 스레드가 락을 획득할 수 있는지 반복적으로 확인하면서 락획득 시도

## ✏️ `Redisson`

![alt text](image/image-3.png)

- `pub-sub` 기반으로 Lock구현 제공
- 채널을 하나 만들고 락을 점유중인 쓰레드가 락을 획득하려고 대기중인 스레드에게 해제를 알려주면 안내를 받은 스레드가 락 획득을 시도
- `Lettuce`와 다르게 별도의 retry로직이 필요없음



