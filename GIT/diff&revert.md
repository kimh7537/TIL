#diff&revert

---
## ✏️ `git diff`

- git diff는 변경내역들끼리의 비교 결과를 보여줌

> 10번 commit 중 1번 commit과 2번 commit을 비교하고 싶어!<br>
> 지금 push한 commit과 이전 commit을 비교하고 싶어!<br>
> 현재 staging area에 있는 변경 내역과 직전의 commit을 비교하고 싶어!<br>
> branch1 브랜치에 있는 commit과 master(=main) 브랜치에 있는 commit을 비교하고 싶어!

### ✔️ 두 커밋 간의 비교
- 현재 저장소에 test.txt 달랑 하나 있고, 1번부터 5번 commit이 있으며, 각각의 commit은 다음과 같이 작성되어 있다고 가정

>1번 commit : test.txt에 A 라는 문자열 하나 저장<br>
>2번 commit : test.txt에 B문자 추가<br>
>3번 commit : test.txt에 C문자 추가<br>
>4번 commit : test.txt에 D문자 추가<br>
>5번 commit : test.txt에 E문자 추가<br>

```shell
$ git log
commit f7fe32715c4bd705110196134271d8f873384316 (HEAD -> master)
Author: Kang Minchul <tegongkang@gmail.com>
Date:   Tue Dec 1 23:06:03 2020 +0900

    5

commit 2845ce53054627b3381c9f2515dc7545cff2347b
Author: Kang Minchul <tegongkang@gmail.com>
Date:   Tue Dec 1 23:05:49 2020 +0900

    4

commit de6d5c1148981e15617999c7ecaa6ec2ea21ff29
Author: Kang Minchul <tegongkang@gmail.com>
Date:   Tue Dec 1 23:05:38 2020 +0900

    3

commit 1d7fce052aafb388ddf092ea315835f5154683f7
Author: Kang Minchul <tegongkang@gmail.com>
Date:   Tue Dec 1 23:04:52 2020 +0900

    2

commit 6958b6b21e15aa0be36736016c5bc955b57a61be
Author: Kang Minchul <tegongkang@gmail.com>
Date:   Tue Dec 1 23:04:38 2020 +0900

    1
```

- commit 옆의 임의의 문자열: `commit hash`, commit을 가리키는 말
- git diff를 이용해 5번 commit이 4번 commit을 기준으로 어떤점에서 차이가 발생했는지 확인

- `git diff 비교대상commit 기준commit`
- `git diff <이 commit에 비해> <이 commit은 무엇이 달라졌니?>`
- 4번 commit에 비해 5번 commit이 무엇이 달라졌는가?
```shell
$ git diff 2845ce53054627b3381c9f2515dc7545cff2347b(4번) f7fe32715c4bd705110196134271d8f873384316(5번)

$ git diff 2845ce53054627b3381c9f2515dc7545cff2347b f7fe32715c4bd705110196134271d8f873384316
diff --git a/test.txt b/test.txt
index 8422d40..8fda00d 100644
--- a/test.txt
+++ b/test.txt
@@ -2,3 +2,4 @@ A
 B
 C
 D
+E          # 4번 Commit에 비해 E라는 문자열이 추가되었다(+ 표시)
```
---
### ✔️ 원격 저장소와 로컬 저장소 간의 비교

`git diff <비교대상 branch이름> origin/<branch 이름> `

---
### ✔️ 이전 commit과 전전 commit의 비교


- commit hash를 쓰는게 귀찮을때
- `HEAD`는 현재 branch의 가장 최근 commit, HEAD^는 현재 branch의 가장 최근 commit에서 하나 이전 commit을 가리킴
- `HEAD^^` 라고 쓰면 가장 최근 commit에서 두 개 이전 commit을 의미

`git diff HEAD HEAD^` 
- 이전 commit (5번)과 전전 commit (4번)을 비교하라는 명령


---
### ✔️ 이전 commit과 현재 수정된 내용 비교


- 아직 commit하지 않은, 수정된 내용과 이전 commit (5번 commit)과 비교하기 위해 아래 명령어 사용
- `git diff HEAD`
- 예를 들어 test.txt에 F라는 문자를 추가하고, 명령어 사용
```shell
$ git diff HEAD
diff --git a/test.txt b/test.txt
index 8fda00d..cead32e 100644
--- a/test.txt
+++ b/test.txt
@@ -3,3 +3,4 @@ B
 C
 D
 E
+F
```
- 아직 commit하지 않은 현재 작업 중인 내용을 가장 최근 commit한 내용과 비교한 결과

---
### ✔️ 브랜치간의 비교

`git diff <비교대상 branch 이름> <기준 branch 이름> `

---
---
## ✏️ `git revert`

- git revert도 reset과 동일하게 commit을 되돌리는 명령어
- `git revert <되돌리고 싶은 commit>`
- 3번 commit을 revert하여 되돌리고 싶다면
`git revert de6d5c1148981e15617999c7ecaa6ec2ea21ff29`


**reset과의 차이점**

- git reset과 git revert는 commit을 과거 시점으로 되돌려준다는 점에서 결과적으로 같은 결과를 냄
- 그러나 큰 차이가 있다면 되돌리는 commit까지의 이력이 사라지느냐의 여부
- `git reset`은 되돌린 버전 이후의 버전들이 모두 사라짐
- `git revert`는 되돌린 버전 이후의 버전들은 모두 유지되고, revert되었다는 사실을 담은 commit만 새로 추가됨

<br>

- 3번 commit으로 reset을 하면 4, 5번 commit은 삭제
- 3번 commit으로 revert를 하면 4,5번 commit은 그대로 유지
- 즉, reset은 과거 자체를 바꾸는 명령어이고, revert는 과거를 변경시켰다는 새로운 commit으로써 새로운 commit을 만드는 명령어

<br>

- 실용적인 면으로 따져 보았을 때, revert는 reset보다 더 안전하게 commit을 되돌리는 방법이고,
- reset은 revert보다 commit log를 깔끔하게 유지해주며 commit을 되돌리는 방법이라고 할 수 있음

<br>

- `-n` 옵션을 이용하면 revert를 써도 commit을 남기지 않을 수 있음

