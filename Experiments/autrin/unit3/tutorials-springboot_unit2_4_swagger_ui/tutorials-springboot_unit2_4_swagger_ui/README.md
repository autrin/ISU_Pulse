# Android API Documentation Creation

### JavaDoc API

1. In `build.gradle (Module:...)` file, add the following between sections `android` and `dependencies`:

```
plugins {
    ...
}

android {
    ...
}

task javadoc(type: Javadoc) {

    doFirst {
        configurations.implementation
                .filter { it.name.endsWith('.aar') }
                .each { aar ->
                    copy {
                        from zipTree(aar)
                        include "**/classes.jar"
                        into "$buildDir/tmp/aarsToJars/${aar.name.replace('.aar', '')}/"
                    }
                }
    }

    configurations.implementation.setCanBeResolved(true)
    source = android.sourceSets.main.java.srcDirs
    classpath += project.files(android.getBootClasspath().join(File.pathSeparator))
    classpath += configurations.implementation
    classpath += fileTree(dir: "$buildDir/tmp/aarsToJars/")

    android.applicationVariants.all { variant ->
        if (variant.name == 'release') {
            owner.classpath += variant.javaCompileProvider.get().classpath
        }
    }

    destinationDir = file("${project.buildDir}/outputs/javadoc/")
    options.memberLevel = JavadocMemberLevel.PRIVATE
    failOnError false
    exclude '**/BuildConfig.java'
    exclude '**/R.java'
}

dependencies {
    ...
}
```

2. Reduce Android Gradle version to 7.4.2 (if necessary)

- On Windows, Android Gradle version must be below 8.

![Change project structure](/screenshots/project_structure.jpg)
![Change Gradle version](/screenshots/gradle_version.jpg)

3. In Android Studio, hit `ctrl` twice to run command `gradle javadoc`

4. The javadoc folder will appear at `app/build/outputs/javadoc`, double-click on `index.html` to view JavaDoc.

** Please be advised that the script only works for **

- Groovy-based gradle (without `.kts` extentions)
- Android Gradle version below 8 (if on Windows);
- Android Gradle Plugin (AGP) version below 8.1 (on any platform);
- To change the AGP version of your project, go to the `build.gradle (Project:...)` file and change the version to a version below 8.1, e.g., 7.4.2
- To change the Gradle version of your project, go to `File->Project Structure->Project`
- Sync after the modification
- In case the gradle script for JavaDoc above runs into "data-binding" related issues, you can find the alternative script [here](https://git.las.iastate.edu/cs309/tutorials/-/blob/springboot_unit2_4_swagger_ui/AndroidExample/app/build.gradle) (line 62-107).
  - Run gradle task `task myassemble(type: GradleBuild)` first to generate databinding files.
  - Make sure to modify the paths at lines 81, 82, and 105 to where your project generates the data-binding files.

** To change Kotlin-based gradle to Groovy-based **

1. refactor -> rename
   `build.gradle.kts (Project...)`
   `build.gradle.kts (Module...)`
   `settings.gradle.kts`

REMOVE `.kts` from the file names

2. in `build.gradle (Module...)`, change `isMinifyEnabled = False` to `minifyEnabled false`

3. sync gradle (rebuild project if it doesn't update the file names) - should convert most Kotlin gradle builds to Groovy

---

# SpringBoot API Documentation Creation

### Swagger-UI - Springboot v2

Swagger UI is an open source library which can be used to document all the API end points in a SpringBoot application. In addition to being a very good tool for documentation, it can also serve as a makeshift manual testing framework. The process for enabling Swagger in a springboot project is trivially easy.

1. Observe the `pom.xml` file in this example, and add the dependency to your project.

   ```
       <dependency>
           <groupId>io.springfox</groupId>
           <artifactId>springfox-boot-starter</artifactId>
           <version>3.0.0</version>
       </dependency>
   ```

2. Check the `Main` class and follow the exact steps to create a Docket Bean. This Bean exposes the API and its documentation as a webpage.
3. Below `@SpringBootApplication`, add the `@EnableSwagger2` annotation
4. Run the application.
5. Check the URL: http://localhost:8080/swagger-ui/ (replace `localhost` and `8080` with your server settings).

### Swagger-UI - Springboot v3

1. Add the following dependency to your `pom.xml`.
   ```
       <dependency>
           <groupId>org.springdoc</groupId>
           <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
           <version>2.0.2</version>
       </dependency>
   ```
2. Run the application.
3. Check the URL: http://localhost:8080/swagger-ui/index.html (replace `localhost` and `8080` with your server settings).

#

## Helpful links:

[Android JavaDoc issue](https://stackoverflow.com/questions/69563407/android-studio-javadoc-error-throws-a-nullpointerexception) \
[Android JavaDoc show private members](https://stackoverflow.com/questions/69563407/android-studio-javadoc-error-throws-a-nullpointerexception) \
[SpringBoot + Swagger](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api) \
[Springboot v3 (1)](https://stackoverflow.com/questions/74614369/how-to-run-swagger-3-on-spring-boot-3) \
[Springboot v3 (2)](https://springdoc.org/v2/#Introduction) \
[Springboot v3 (3) swgger3 annotations](https://www.bezkoder.com/swagger-3-annotations/)

#

### Note :

### 1. /laptops APIs are defined in Laptops/LaptopController,

### 2. /users APIs are defined in the Users/UserController

### 3. /phones APIs are defined in the Phones/PhoneController

### To Run the project

1. Command Line (Make sure that you are in the folder containing pom.xml)</br>
   <code> mvn clean package</code></br>
   <code>java -jar target/onetoone-1.0.0.jar</code>
2. IDE : Right click on Application.java and run as Java Application

### Available End points from POSTMAN: CRUDL

1. CREATE requests -
   POST request: 1. /laptops - Create a new Laptop. The request has to be of type application/JSON input format
   {
   "cpuClock" : "1.7",
   "cpuCores" : "4",
   "ram" : "4",
   "manufacturer" : "apple",
   "cost" : "1000000"
   } 2. /users - Create a new User with laptop. Request Format : application/json
   {
   "name" : "John",
   "email" : "johndoe@somemail.com",
   laptop : {
   "cpuClock" : "2.3",
   "cpuCores" : "4",
   "ram" : "8",
   "manufacturer" : "Hp",
   "cost" : "600"
   }
   }. 3. /users - Create user without laptop. Request Format : application / json
   {
   "name" : "John",
   "email" : "johndoe@somemail.com"
   } 4. /users - create the user and associate it with an existing phone.
   {
   "name" : "John",
   "email" : "johndoe@somemail.com",
   "phones" : [
   { "id" : "1"},
   { "id" : "2" }
   ]
   }. Note that you cannot create phones(cascade option not present on @OneToMany), but you can associate a user to the phone which will overwrite the previous user for the phone. 4. /phones - Create a new Phone. cannot create a new user(no cascade option), but you can associate an existing user to the phone
   {
   "name" : "iPhone X",
   "price" : "2000",
   "cameraQuality": "13",
   "manufacturer: : "apple",
   "battery" : "3000",
   "user" : {
   "id" : 3
   }
   }
2. READ requests -
   GET request: 1. /laptops/{id} - Get laptop object for provided id 2. /users/{id} - Get user object with associated laptop and phones for provided user id 3. /phones/{id} - Get phone objects with provided id

3. UPDATE requests -
   PUT request : 1. /laptops/{id} - Update info on a laptop whose id is provided on the URL. The modified info is sent in the body of the message
   {
   "cpuClock" : "2.7",
   "cpuCores" : "4",
   "ram" : "4",
   "manufacturer" : "apple",
   "cost" : "2000000"
   } 2. /users/{id} - Update User and optionally laptop and phone associated with him/her for id provided in the url
   {
   "name" : "John",
   "email" : "johndoe@newmail.com",
   laptop : {
   "id" : "5"
   "cpuClock" : "3.3",
   "cpuCores" : "8",
   "ram" : "16",
   "manufacturer" : "new Laptop",
   "cost" : "1000"
   },
   "phones" : [{"id":1},{"id":2} ]
   }. 3. /phones/{id} - Update the Phone for given id and optionally modify the user_id associated with phone
   {
   "name" : "iPhone X",
   "price" : "2000",
   "cameraQuality": "13",
   "manufacturer: : "apple",
   "battery" : "3000",
   "user" : {
   "id" : 5
   }
   }
4. DELETE a record -
   DELETE request:

   1. /laptops/{id} - Delete the entry with id in te url
   2. /users/{id} - delete a user with the id provided in the url
   3. /phones/{id} - delete the phone for the given id

5. LIST all -  
   GET request 1. /laptops - Get/List all the laptops stored in the db 2. /users - Get/List all the users along with their associated laptops 3. /phones = Get/List all the phones stores in the database
