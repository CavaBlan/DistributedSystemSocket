# DistributedSystemSocket


This is my assignment 3 in a distributed system course.


#### How to run application

To run the Java server (after you build the project) use the following command:
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalServer
```

To run the Java client (after you build the project) use the following command:
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalClient
```

#### Example (2 clients and 1 server)

**Step 1:** start the server:

Server
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalServer
>
```

**Step 2:** start the clients and connect them to the server:

Client 1
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalClient
> connect 127.0.0.1 12345
Connection successful!
```

Client 2
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalClient
> connect 127.0.0.1 12345
Connection successful!
```

**Step 3:** list the connected clients on the server:

Server
```
> list
client1: 127.0.0.1 5746
client2: 127.0.0.1 9677
```

**Step 4:** index files from the clients:

Client 1
```
> index ../datasets/Dataset1/folder1
Completed indexing in 1.386 seconds
> index ../datasets/Dataset1/folder3
Completed indexing in 1.386 seconds
> index ../datasets/Dataset1/folder5
Completed indexing in 1.386 seconds
> index ../datasets/Dataset1/folder7
Completed indexing in 1.386 seconds
> index ../datasets/Dataset1/folder9
Completed indexing in 1.386 seconds
> index ../datasets/Dataset1/folder11
Completed indexing in 1.386 seconds
> index ../datasets/Dataset1/folder13
Completed indexing in 1.386 seconds
> index ../datasets/Dataset1/folder15
Completed indexing in 1.386 seconds
```

Client 2
```
> index ../datasets/Dataset1/folder2
Completed indexing in 1.386 seconds
> index ../datasets/Dataset1/folder4
Completed indexing in 1.386 seconds
> index ../datasets/Dataset1/folder6
Completed indexing in 1.386 seconds
> index ../datasets/Dataset1/folder8
Completed indexing in 1.386 seconds
> index ../datasets/Dataset1/folder10
Completed indexing in 1.386 seconds
> index ../datasets/Dataset1/folder12
Completed indexing in 1.386 seconds
> index ../datasets/Dataset1/folder14
Completed indexing in 1.386 seconds
> index ../datasets/Dataset1/folder16
Completed indexing in 1.386 seconds
```

**Step 5:** search files from the clients:

Client 1
```
> search Worms
Search completed in 2.8 seconds
Search results (top 10):
* client2:folder6/document200.txt 11
* client2:folder14/document417.txt 4
* client2:folder6/document424.txt 4
* client1:folder11/document79.txt 1
* client2:folder12/document316.txt 1
* client1:folder13/document272.txt 1
* client1:folder13/document38.txt 1
* client1:folder15/document351.txt 1
* client1:folder1/document260.txt 1
* client2:folder4/document101.txt 1
```

Client 2
```
> search distortion AND adaptation
Search completed in 3.27 seconds
Search results (top 10):
* client2:folder6/document200.txt 57
* client1:folder7/document476.txt 5
* client1:folder13/document38.txt 4
* client2:folder6/document408.txt 3
* client1:folder7/document298.txt 3
* client2:folder10/document107.txt 2
* client2:folder10/document206.txt 2
* client2:folder10/document27.txt 2
* client2:folder14/document145.txt 2
* client1:folder15/document351.txt 2
> quit
```

**Step 6:** close and disconnect the clients:

Client 1
```
> quit
```

Client 2
```
> quit
```

**Step 7:** close the server:

Server
```
> quit
```
