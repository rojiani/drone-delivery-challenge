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

`$ ./gradlew run --args="input=<INPUT_FILE_PATH>"`
**NOTE**: For Windows users, use `gradlew.bat` instead of `gradlew`.

to run the sample input file:
`$ ./gradlew run --args="--input=<PROJECT_ROOT>/src/main/resources/input/test-input-1"`
or
`$ ./gradlew run --args="-i <PROJECT_ROOT>/src/main/resources/input/test-input-1"`

After running the application, the output filepath will be printed to the console.

Additional (optional) arguments:

Exit if input is invalid (by default, true). If false, any invalid lines are ignored and all valid lines are processed.
```
-x false
--exitOnInvalidInput=false
```

Scheduler to use:
TODO
```
"-s", "--scheduler",
```

To run the unit tests:
`$ ./gradlew test`

### Assumptions Made ###

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

| Category  |    Time (hrs) |  Time (min) |        Time (s) |
|:--------- | -------------:| -----------:| ---------------:|
| Promoter  |    `[0, 1.5)` |   `[0, 90)` |     `[0, 5400)` |
| Neutral   | `[1.5, 3.75)` | `[90, 225)` | `[5400, 13500)` |
| Detractor |   `[3.75, ∞)` |  `[225, ∞)` |    `[13500, ∞)` | 


**Assumption 4 - Delivery Time ignores non-operational hours**
* For orders placed outside of drone operation hours, the delivery time is calculated by starting not at the time the order was placed, but rather the start time is at the start of the drone's operational hours.
    * Example: If an Order is placed at 22:05:00 and delivered the following day at 07:00:00, the delivery time is 1 hour (6 am - 7 am).
    * *Rationale*: If the delivery time is calculated as `deliveryTime - timeOrderPlaced`, then all orders placed between 22:00 and 02:15 would automatically lead to Detractor status even if delivery was instantaneous after drone operating hours recommenced.
    * *Note*: The solution is designed in such a way that it is flexible. The dependency inversion principle is utilized so that the current implementation of the `DeliveryTimeCalculator` interface, `OperatingHoursDeliveryTimeCalculator`, could easily be swapped out for a new implementation that uses a different method to calculate delivery time.
* See also **Assumption 5**

**Assumption 5 - Next-day Rollover.** 
* If there is insufficient time remaining in the current operational hours for a package to be delivered to the customer *and* for the drone to return to the facility, the package will be rescheduled to be delivered the next day.
    * Example: order placed at 9 pm, and will take 2 hours to deliver
        * drone delivery occurs only between 6 am - 10 pm.
        * it's conceivable that a drone could start the delivery at 9 pm and travel until 10 pm, and then the following morning travel the remainder from 6-7 am. But that would mean that the drone must land somewhere at 10 pm and remain there until the drone delivery window reopens the next day. I would assume people wouldn't be too happy about a drone parking in their front yard overnight, so this strategy would only work if the drone delivery company also had several drone-parking stations. This would significantly complicate the problem, since the scheduler would need to consider paths other than the most direct one to customer, ensure that drone can reach the drone-parking station by cutoff, etc.
        * This strategy is also likely unrealistic - establishing numerous drone-parking stations would probably face resistance from the town.
    * Therefore, if the _full_ trip (from launch facility to customer _and back_) by 10pm, then the delivery will be rescheduled for the following day.
* As in the case of orders placed during off-hours (_Assumption 4_), delivery time for these packages does not include the time between 10pm-6am when drones are non-operational.
    * It does include all operational hours on both days:
        * If order is placed at 21:00 on 1/1, and is scheduled to be delivered at 09:00 on 1/2, the total delivery time (in hours) is 4: 
            * 1 hour on 1/1 (21:00-22:00), and 3 hours on 1/2 (06:00-09:00)

**Assumption 6 - Input File.**  
5A. Doesn't exceed 2GB
5B. Is UTF-8 format
5C. Contains only valid data (all order lines are in expected format)
* Fail rather than ignore if invalid input
5D. Assume orders are ordered from earliest order placement time to latest.

**Assumption 7 - Start Time.**
TODO - start at earliest delivery

TODO: Other Assumptions:
* Algorithm will always schedule to maximize NPS (not some other metric)
* Drone can only carry 1 package, & any single order can be fit completely in package.
    * Note that this isn't realistic. However, perhaps the input (the orders) are not all orders placed, but the subset of orders whose delivery can be fulfilled by drone - i.e., a separate service is used to produce this list of orders. 
* Assume that drone can move in any direction at same rate of 1 block / min
    * Prompt only gives speed for horizontal & vertical, but no reason to think that the drone's movement is restricted.
    * 1 block = 1 grid unit
* Assume that drone is constantly in motion at same speed.
    * For simplicity, ignore the following:
        * acceleration from static to max velocity.
        * time to ascend/descend in altitude to make delivery
        * time to pick up next package
    * These are not necessarily negligible for a realistic model.

TODO DeliveryScheduler input all placed on same day.
* Rationale: TODO

TODO 6am-10pm UTC

### Design Decisions/Notes ###

TODO
* prompt has launch facility in center, but since that could change, designed not
to rely on that. 
* Drone speed is 1 vertical or horizontal grid block per minute.
    * diagonal

TODO In problem, town owns 1 drone. 
* This would likely increase.
* Operating hours could also change

#### TODO ####

TODO - scheduling algorithm choice
* Multiple options

TODO - Poll service
* IRL, scheduler needs to poll some service to get newly placed orders with some frequency.
