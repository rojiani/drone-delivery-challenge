Drone Delivery Challenge
========================

## About ##

### Overview ###
The program is written in [Kotlin](https://kotlinlang.org). In case you are not familiar with Kotlin, it is a JVM language designed to be fully interoperable with Java. With Kotlin, you get the benefit of the Java ecosystem (e.g., libraries), but with [many additional language features](https://medium.com/@magnus.chatt/why-you-should-totally-switch-to-kotlin-c7bbde9e10d5) that greatly enhance productivity, safety, and expressiveness. It is now the [fastest-growing programming language](https://octoverse.github.com/projects#languages) used on GitHub. If you are not familiar with Kotlin, the syntax is fairly similar to Java, so hopefully it will not be too hard to understand, but if any parts are unclear I would be happy to explain them.

Gradle is used as the build system. See Instructions below for how to run the application & its tests.

Libraries used:

[Kodein](https://kodein.org/di/) - a Dependency Injection framework for Kotlin
[Kotlin ArgParser](https://github.com/xenomachina/kotlin-argparser) - a simple CLI argument parsing library for Kotlin.

## Instructions ##
### Importing into IntelliJ IDEA ###

### Running ###
To run the application, you must have the following installed:

[JDK 1.8](https://www.oracle.com/technetwork/java/javaee/downloads/jdk8-downloads-2133151.html)

Navigate to project directory.

A Gradle Wrapper is provided, so that when a task is run with `gradlew`, a version of Gradle (5.4) will be downloaded for you.

TODO:
`$ ./gradlew run --args="input=<INPUT_FILE_PATH>"`

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

TODO drone can only carry 1 package, & any single order can be fit completely in package.
* Note that this isn't realistic. However, perhaps the input (the orders) are not all orders placed, but the subset of orders whose delivery can be fulfilled by drone - i.e., a separate service is used to produce this list of orders. 

**Assumption 1 - Diagram Interpretation.**
Scaling not as clear & unambiguous as it could be. I'm assuming that in the
diagram:
* the first row of numbers (`0, 1, ..., 10`) corresponds to the customer's
rating for how likely they are to recommend the service.
* The second row (`10, 9, ..., 1, < 1`) is the delivery time (in hours)

**Assumption 2 - Score determined solely by delivery time.**
Following from _Assumption 1_, I'm assuming that the likelihood of recommending is directly correlated with delivery time, and there are no other covariates.

* _Rationale_:
    * This is not particularly realistic. In real life, other factors would influence the customer score. For example, whether the package contents were delivered undamaged, or how the actual delivery time compared to the promised or estimated delivery time.
    * However, the prompt does not provide any other metrics, so there is no way to account for other factors other than estimation. We could make assumptions - perhaps 1 in 250 packages is lost or damaged, for example - but since I don't have the necessary data to make accurate estimates (and in accordance with the KISS principle), I'm going to ignore all variables aside from delivery time.

**Assumption 3 - Exact cutoff times.**
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


**Assumption 4 - Next-day Rollover.** 
* order placed at 9 pm, and will take 2 hours to deliver
    * drone delivery occurs only between 6 am - 10 pm.
    * it's conceivable that a drone could start the delivery at 9 pm and travel until 10 pm, and then the following morning travel the remainder from 6-7 am. But that would mean that the drone must land at 10 pm and remain somewhere until the drone delivery window reopens the next day. I would assume people wouldn't be too happy about a drone parking in their front yard overnight, so this strategy would only work if the drone delivery company also had several drone-parking stations. This would significantly complicate the problem, since the scheduler would need to consider paths other than the most direct one to customer, ensure that drone can reach the drone-parking station by cutoff, etc.
        * This strategy is also likely unrealistic - establishing numerous drone-parking stations would probably face resistance from the town (unless this takes place in the future, & drone stations are a common occurrence that people are used to)
    * Therefore, if the _full_ trip (from launch facility to customer _and back_) by 10pm, then the delivery will begin the following morning.
    * This also applies to orders placed after 10pm & before 6am.

**Assumption 5 - Input File.**  
5A. TODO Doesn't exceed 2GB (`readLines`). TODO consider `useLines`
5B. UTF-8


### Design Decisions/Notes ###

TODO
* prompt has launch facility in center, but since that could change, designed not
to rely on that. 
* Drone speed is 1 vertical or horizontal grid block per minute.
    * diagonal

TODO In problem, town owns 1 drone. This would likely increase.
* Operating hours could also change

#### TODO ####
TODO - remove this section 

TODO - Poll service
* IRL, scheduler needs to poll some service to get newly placed orders with some frequency.
