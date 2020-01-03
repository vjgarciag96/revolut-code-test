# Revolut code test

In this repo you'll see the project correspondent to Revolut's code test. The main requirements of the test are the next:

* Show a list with the rates of the different currencies provided by the given API (one per row)
* Update these rates every second
* Each row has an input where you can enter any amount of money
* When you tap on currency row it should slide to top and its input becomes first responder
* When you’re changing the amount the app must simultaneously update the corresponding value for other currencies


## Architecture
The idea of the implemented architecture was to follow clean architecture principles but not in a strict way in order to be pragmatic.

### Presentation layer
In presentation layer, MVVM pattern has been used to organize the code.

It has been implemented with android jetpack's `ViewModel` and `LiveData` components to take advantage of its lifecycle's management.

Other relevant point on this layer is the use of Android `ListAdapter` in order to perform lists' diff calculation in a background thread because the app is going to need updating them really frequently and doing this calculation on main thread could have an impact on the app's performance.

The model consist of an LCE pattern to represent the possible states of the view and another sealed class to represent the possible effects or events needed to be dispatched to the view.

### Domain layer
Due to the reactive nature of the problem, we've decided to use RxJava to model the solution. 

The main class of this layer is `CurrenciesBusinessLogic`. This class exposes a stream of curriencies through `currenciesStream` method (the stream is represented with an Rx Observable). The exposed stream is composed by other two streams combined with `merge` operator. One stream emits rates updates every second and the other emits events coming from upper layers like amount updates or base currency changes. Also, this class internally holds an state that represents the state of the business logic. The state starts with an `Initializing` value and is updated when new events arrive to the mentioned stream. The transition from one state to another depending on the received event is performed inside the `CurrenciesReducer` class that aims to represent the same reducer concept of redux architecture without an store. 

We also have some use cases that encapsulates the interactions from upper layers with `CurrenciesBusinessLogic`. There is one to get the currencies stream and other two to propagate base currency changes and amount changes coming from UI.

Another relevant point of this layer is the class used to represent currency values. We've decided to use java's `BigDecimal` class to work with them in order to have more control on the currencies operations (e.g. control on rounding strategies). This class have been wrapped into another one in order to simplify work with the same `MathContext` on all of them.

### Data layer
In this layer we've used the repository pattern in order to hide the data origin to upper layers. However, in this case we only have a network data source providing the latest rates data from the API endpoint given in the code test. To implement this network data source we've used `okHttp` library to deal with network HTTP work and `moshi` to deal with API responses serialization. Also we've used `Either` datatype to model the error/success cases.

### Dependency Inversion
We've decided to create a manual service locator because we've considered that the complexity of the project wasn't enough to start using DI frameworks like `Dagger` or other service locators like `Koin`.

### Testing
Some unit tests have been added to the fundamental parts of the app like `CurrenciesBusinessLogic` and `CurrenciesReducer`. Also, an integration test has been added for the api client.
