Drone Delivery Challenge
========================

## About ##

### Overview ###
The program is written in [Kotlin](https://kotlinlang.org). In case you are not familiar with Kotlin, it is a JVM language designed to be fully interoperable with Java. With Kotlin, you get the benefit of the Java ecosystem (e.g., libraries), but with [many additional language features](https://medium.com/@magnus.chatt/why-you-should-totally-switch-to-kotlin-c7bbde9e10d5) that greatly enhance productivity, safety, and expressiveness. It is now the [fastest-growing programming language](https://octoverse.github.com/projects#languages) used on GitHub. If you are not familiar with Kotlin, the syntax is fairly similar to Java, so hopefully it will not be too hard to understand, but if any parts are unclear I would be happy to explain them.

Gradle is used as the build system. See Instructions below for how to run the application & its tests.

Libraries used - TODO why selected

## Instructions ##
### Importing into IntelliJ IDEA ###


### Running ###
To run the application, you must have the following installed:

[JDK 1.8](https://www.oracle.com/technetwork/java/javaee/downloads/jdk8-downloads-2133151.html)

Navigate to project directory.

A Gradle Wrapper is provided, so that when a task is run with `gradlew`, a version of Gradle (5.4) will be downloaded for you.

Gradle Tasks can be viewed with:  
`$ ./gradlew tasks`

**NOTE**: For Windows users, use `gradlew.bat` instead of `gradlew`.

Running unit tests:
`$ ./gradlew test`


TODO instructions CLI

[Using Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html#sec:using_wrapper)

See [Detailed Guide to Using an Existing Gradle Build](https://guides.gradle.org/using-an-existing-gradle-build/)

* For Step 4, "Identifying the type of project", the type is a JVM project.

### Assumptions Made ###
Can move diagonally

Houses laid out at integer coordinates? Prob. best to assume no.

Algorithm will always schedule to maximize NPS (not some other metric)

TODO not delaying deliveries already exceeding max. delivery time

**Assumption 1.**
Scaling not as clear & unambiguous as it could be. I'm assuming that in the
diagram:
* the first row of numbers (`0, 1, ..., 10`) corresponds to the customer's
rating for how likely they are to recommend the service.
* The second row (`10, 9, ..., 1, < 1`) is the delivery time (in hours)


**Assumption 2.**
Following from _Assumption 1_, I'm assuming that the likelihood of recommending is directly correlated with delivery time, and there are no other covariates.

* _Rationale_:
    * This is not particularly realistic. In real life, other factors would influence the customer score. For example, whether the package contents were delivered undamaged, or how the actual delivery time compared to the promised or estimated delivery time.
    * However, the prompt does not provide any other metrics.

**Assumption 3.**
* Unclear what the rating should be for non-integer delivery times.
    * If delivery time is exactly 2 hour, then rating is 8.
    * But if delivery time is 2 hours, 15 mins, does this correspond to 8? Or does it correspond to 7?
        * There are several approaches:
            * `floor(2.25) = 2`
            * `ceiling(2.25) = 3`
            * `round(2.25) = 2` (rounding to nearest integer)
* exact cutoffs unclear, but are fairly important for optimization
    * assume cutoff between Promoter & Neutral is 1.5 hours, & the cutoff between
    Neutral & Detractor is 3.75 hours.

| Category  |  Time (Hours) | Time (Minutes) | 
|:--------- | -------------:| --------------:|
| Promoter  |    `[0, 1.5)` |      `[0, 90)` |
| Neutral   | `[1.5, 3.75)` |    `[90, 225)` |
| Detractor |   `[3.75, ∞)` |     `[225, ∞)` |


### Design Decisions/Notes ###

* prompt has launch facility in center, but since that could change, designed not
to rely on that. 
* Drone speed is 1 vertical or horizontal grid block per minute.
    * diagonal

In problem, town owns 1 drone. This would likely increase.
* Operating hours could also change
