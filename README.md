#packageTracker

I have also documented a few things here to help the understanding the code base and the use of the API.

The API has been securely hosted on Heroku.
To get started with the API do the following:
1. Create a package to track at this end point with the package request body:
https://packageguard.herokuapp.com/api/package/create?key=<API-KEY>

2. Track a package using this endpoint. You would need to provide the package ID in the path along with the appropriate tracking details as a request body to:
https://packageguard.herokuapp.com/api/package/tracker/<PACKAGE-ID>?key=<API-KEY>

3. Get all packages tracked by hitting:
https://packageguard.herokuapp.com/api/package/getAll?key=<API-KEY>

4. Get a specific package:
https://packageguard.herokuapp.com/api/package/tracker/getPackage/<PACKAGE-ID>?key=<API-KEY>

5. Get packages by their status using:
https://packageguard.herokuapp.com/api/package/status/<STATUS>?key=<API-KEY> 

6. Get tracking history for package using:
https://packageguard.herokuapp.com/api/package/history/<PACKAGE-ID>?key=<API-KEY> 

At the beginning of every class in the code base I have provided a /**commented*/ guide on its primary responsibility. This should help you understand the code base and my implementation.
I opted not to use Spring Data Mongo for two reasons, query performance and compatibility issues.

There are a total of 29 unit and Integration tests all passing. A few have disabled as they are not idempotent and will alter the test cases if run.

<img src="tests.JPG" width="200">

I have used a MongoDB database along with Spring Boot to implement this API. The Mongo DB is on Mongo Atlas. 

