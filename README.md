# raptor-mock
Java  tcp server and client basedon NIO reactor model, here is the diagram for reator model

```
    -----------------------------------------------------------------------------
    ↓                                                                           ↓    
----------                 ----------------------------              ----------------------
| client |   <------->     |                          |              |   worker thread 0  |
----------                 |      Main Thread         |              ----------------------
| client |   <------->     | listen and accept Client |   ------->   |   worker thread 1  |
----------                 |                          |              ----------------------
| client |   <------->     |                          |              |   worker thread 2  |
----------                 ----------------------------              ----------------------
    ↑                                                                         	↑
    -----------------------------------------------------------------------------
```

### run tcp server
```
mvn clean package
java -jar ./target/raptor-mock-server-1.0.jar
```

### run tcp client
```
mvn clean package
java -jar ./target/raptor-mock-client-1.0.jar localhost 20000
```
