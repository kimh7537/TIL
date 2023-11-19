#DFS,BFS

**코딩테스트 문제 풀때 반례 생각하기**

- **최소/최대**
- **없음/있음**

## ✏️ 인접행렬, 인접리스트
**공간복잡도**

 - 인접행렬 : O(V^2)
 - 인접리스트 : O(V + E)

// 인접행렬
bool adj[V][V];
// 인접리스트
vector<int> adj[V]; 
 

**시간복잡도 : 간선 한개 찾기**

 - 인접행렬 : O(1)
 - 인접리스트 : O(V)(최악의 경우로 생각)

// 인접행렬
for(int i = 0;i < V; i++){
    for(int j = 0; j < V; j++){
        if(a[i][j]){ 
        }
    }
}

// 인접리스트
for(int j = 0; j < adj[i].size(); j++){
    cout << adj[i][j] << " ";
} 

**시간복잡도 : 모든 간선찾기**

 - 인접행렬 : O(V^2)
 - 인접리스트 :  O(V + E)  

---
## ✏️ 맵
### ✔️ connected component(DFS)
```cpp
#include<bits/stdc++.h>
using namespace std; 
int dy[4] = {-1, 0, 1, 0};
int dx[4] = {0, 1, 0, -1}; 
int m, n, k, y, x, ret, ny, nx, t;
int a[104][104];
bool visited[104][104]; 
void dfs(int y, int x){
    visited[y][x] = 1;
    for(int i = 0; i < 4; i++){
        ny = y + dy[i];
        nx = x + dx[i];
        if(ny < 0 || nx < 0 || ny >=n || nx >= m) continue;
        if(a[ny][nx] == 1 && !visited[ny][nx]){
            dfs(ny, nx);
        }
    }
    return;
}

int main(){ 
    cin.tie(NULL);
    cout.tie(NULL);
    cin >> n >> m; 
    for(int i = 0; i < n; i++){
    	for(int j = 0; j < m; j++){
    		cin >> a[i][j];
		}
	}
	for(int i = 0; i < n; i++){
    	for(int j = 0; j < m; j++){
    		if(a[i][j] == 1 && !visited[i][j]){
    			ret++; dfs(i, j);
			} 
		}
	}
	cout << ret << '\n'; 
    return 0;
}
```


### ✔️ 최단거리 구하기(BFS)
```cpp
#include<bits/stdc++.h>
using namespace std;     
vector<int> adj[100];
int visited[100]; 
int nodeList[] = {10, 12, 14, 16, 18, 20, 22, 24};
void bfs(int here){
    queue<int> q; 
    visited[here] = 1; 
    q.push(here);
    while(q.size()){
        int here = q.front(); q.pop();
        for(int there : adj[here]){
            if(visited[there]) continue;
            visited[there] = visited[here] + 1;
            q.push(there);
        }
    }
}
int main(){
    adj[10].push_back(12);
    adj[10].push_back(14);
    adj[10].push_back(16);
    
    adj[12].push_back(18);
    adj[12].push_back(20);


    adj[20].push_back(22);
    adj[20].push_back(24);
    bfs(10);
    for(int i : nodeList){
        cout << i << " : " << visited[i] << '\n';
    }
    cout << "10번으로부터 24번까지 최단거리는 : " << visited[24] - 1 << '\n';
    return 0; 
} 
/*
10 : 1
12 : 2
14 : 2
16 : 2
18 : 3
20 : 3
22 : 4
24 : 4
10번으로부터 24번까지 최단거리는 : 3
*/
```
- 시작지점이 여러개라면 큐에 푸시하는 지점도 다수가 되어야 하며 해당 지점들의 visited를 모두 1로 만들면서 시작


---
## ✏️ DFS
### ✔️ 1. 미리 검증
(1)
```cpp
void dfs(int here){
    visited[here] = 1; 
    for(int there : adj[here]){
        if(visited[there]) continue;
        dfs(there);
    }
}
```
(2)
```cpp
void dfs(int here){ 
    for(int there : adj[here]){
        if(visited[there]) continue;
        visited[there] = 1; 
        dfs(there);
    }
}
```
- (2)방법 사용하려면 dfs함수 호출 전에 방문처리 해주기

### ✔️ 2. 순회 후 검증
```cpp
void dfs(int here){
    if(visited[here]) return;
    visited[here] = 1;
    for(int there : adj[here]){ 
        dfs(there);
    }
}
```
---
## ✏️ BFS
### ✔️ BFS
1. 최단거리 구하기
```cpp
BFS(G, u)
    u.visited = 1
    q.push(u);
    while(q.size()) 
        u = q.front() 
        q.pop()
        for each v ∈ G.Adj[u]
            if v.visited == false
                v.visited = u.visited + 1//->최단거리
                // v.visited = true->일반 BFS
                q.push(v) 
```
2. 시작지점이 여러개라면 큐에 푸시하는 지점도 다수가 되어야 하며 해당 지점들의 visited를 모두 1로 만들면서 시작
3. 가중치가 같은 그래프만 BFS사용
  - 가중치가 다른 그래프는 다익스트라, 벨만포드 등 사용
---
**DFS와 BFS비교**

시간복잡도는 인접리스트로 이루어졌다면 `O(V + E)`이고 인접행렬의 경우 `O(V^2)`가 되는 것은 동일

>**DFS**<br>
1. 메모리를 덜 씀. 
2. 절단점 등 구할 수 있음. 
3. 코드가 좀 더 짧음.
4. 완전탐색의 경우에 많이 씀.

>**BFS**<br>
1. 메모리를 더 씀. 
2. 가중치가 같은 그래프내에서 최단거리를 구할 수 있음.
3. 코드가 더 김

**"퍼져나간다", "탐색한다" 이 2글자가 있으면 반드시  DFS, BFS 생각해보기**
---



---

## ✏️ 그래프
### ✔️ 용어 정리
- 정점(vertex)
- 간선(edge)
   - 양방향 간선(화살표 서로서로)
   - 단방향 간선(화살표 한 방향)
- indegree(들어오는 간선 수)
- outdegree(나가는 간선 수)
- 가중치

**정점과 간선들로 이루어진 집합: 그래프(Graph)**

---
## ✏️ 트리
### ✔️ 개념
- 무방향 그래프/사이클 없음
- `V - 1 = E`
- 루트노드/내부노드/리프노드

### ✔️ 이진 탐색 트리(binary search tree)
- 탐색, 삽입, 삭제, 수정 모두 O(logN) <br>
(균형잡힌 구조일때)
- map 탐색, 삽입, 삭제, 수정 모두 O(logN)
<br>균형잡힌 트리인 레드블랙트리를 기반으로 구현되어있기 때문

---
## ✏️ 트리순회
```cpp
#include <bits/stdc++.h>
using namespace std; 
vector<int> adj[1004]; 
int visited[1004];

void postOrder(int here){ 
  	if(visited[here] == 0){ 
  		if(adj[here].size() == 1)postOrder(adj[here][0]);
  		if(adj[here].size() == 2){
  			postOrder(adj[here][0]); 
  			postOrder(adj[here][1]);
		}
  		visited[here] = 1; 
  		cout << here << ' ';
	} 
} 
void preOrder(int here){
  	if(visited[here] == 0){
  		visited[here] = 1; 
  		cout << here << ' ';
  		if(adj[here].size() == 1)preOrder(adj[here][0]);
  		if(adj[here].size() == 2){
  			preOrder(adj[here][0]); 
  			preOrder(adj[here][1]);
		}
	}
}  
void inOrder(int here){   	
	if(visited[here] == 0){ 
  		if(adj[here].size() == 1){ 
  			inOrder(adj[here][0]); 
	  		visited[here] = 1; 
	  		cout << here << ' ';
		}else if(adj[here].size() == 2){
  			inOrder(adj[here][0]); 
	  		
			visited[here] = 1; 
	  		cout << here << ' ';
  			
			inOrder(adj[here][1]);
		}else{
	  		visited[here] = 1; 
	  		cout << here << ' '; 
		}
	}

} 
int main(){
	adj[1].push_back(2);
	adj[1].push_back(3);
	adj[2].push_back(4);
	adj[2].push_back(5); 
	int root = 1;
    cout << "\n 트리순회 : postOrder \n";
    postOrder(root); memset(visited, 0, sizeof(visited));
    cout << "\n 트리순회 : preOrder \n"; 
    preOrder(root); memset(visited, 0, sizeof(visited)); 
    cout << "\n 트리순회 : inOrder \n"; 
    inOrder(root); 
    return 0;
}
/*
 트리순회 : postOrder
4 5 2 3 1
 트리순회 : preOrder
1 2 4 5 3
 트리순회 : inOrder
4 2 5 1 3
*/
[출처] [알고리즘 강의] 2주차. 그래프이론, 인접행렬, 인접리스트, DFS, BFS, 트리순회|작성자 큰돌

```

---
> 참고 https://blog.naver.com/jhc9639/222289089015

