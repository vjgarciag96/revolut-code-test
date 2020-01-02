# Revolut code test

In this repo you'll see the project correspondent to Revolut's code test. The main requirements of the test are the next:

* 


## Architecture

### Presentation layer

In presentation layer, MVVM patter has been used to organize the code. 
It has been implemented with android jetpack's ViewModel and LiveData classes to take advantage of the lifecycle handling provided by those components.
Other relevant points on this layer are the use of Android 'ListAdapter' in order to perform list diffing in a background thread 