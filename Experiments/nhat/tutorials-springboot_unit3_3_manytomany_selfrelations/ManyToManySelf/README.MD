### To see the database:
To see how the relationship between objects change in realtime, run the server and go to: http://localhost:8080/h2-console
Username: user
Password: password

### Postman tests
[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/26543f3a5c787a4ec895)
Go to the link and either run it directly from the app or download the JSON file and import it.

or

Adding two people:
{"id": 1, "name": "Steve"} and {"id": 2, "name": "!Steve"}. <br>
Then make them friend using the addFriend function in PersonController. The change should be visible on the h2-console

