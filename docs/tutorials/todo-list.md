# Todo List Tutorial

> In this tutorial, we'll build a Todo List in Android using Compose and the Bloc library.

![Todo List](todo-list.gif ':size=33%')

## Prerequisites

This tutorial makes use of the [Room](https://developer.android.com/training/data-storage/room) local database library, as well as [Koin](https://insert-koin.io) for dependency injection. We won't be covering these in detail so if you're unfamiliar, head over to the documentation and understand the basics of Room and Koin before coming back to this tutorial.

## Key Topics

* Implementing a Bloc
* Using [BlocComposer](../bloc-compose.md?id=bloccomposer), which handles re-composing a widget in response to new states

## Setup

Start by [creating a new Android Studio project with Compose enabled](https://developer.android.com/jetpack/compose/setup#create-new).

Then add the [Jitpack](https://jitpack.io) repository to your project-level `settings.gradle` file:

```groovy
repositories {
  // ...
  maven { url 'https://jitpack.io' }
}
```

Finally, just add the following dependencies to your module-level `build.gradle` file:

```groovy
dependencies {
  // ...
	implementation 'com.github.ptrbrynt.KotlinBloc:compose:3.0.0'
  
  implementation "androidx.navigation:navigation-compose:2.4.0-alpha09"
  
  implementation "androidx.room:room-runtime:2.3.0"
  implementation "androidx.room:room-ktx:2.3.0"
  kapt "androidx.room:room-compiler:2.3.0"
  
  implementation "io.insert-koin:koin-core:3.1.2"
  implementation "io.insert-koin:koin-androidx-compose:3.1.2"
}
```

Re-sync your project in Android Studio, and you'll be good to go!

## Todo Entity

The first requirement is a class to represent our Todos. We can use a Kotlin data class for this:

```kotlin
@Entity
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val completed: Boolean = false,
)
```

?> Notice we're marking this as an `Entity`, which will create our Room table for us. The `id` field is marked as an auto-incrementing Primary Key, so setting the default value to `0` will automatically create a database ID for each new `Todo` entry.

## Todo DAO

We can now create our data access object (DAO), which will allow us to work with `Todo`s in our Room database:

```kotlin
@Dao
interface TodoDao {
    @Query("SELECT * FROM Todo")
    fun getAllTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM Todo WHERE id = :id")
    suspend fun getTodo(id: Int): Todo

    @Insert
    suspend fun addTodo(todo: Todo)

    @Update
    suspend fun updateTodo(todo: Todo)

    @Delete
    suspend fun deleteTodo(todo: Todo)
}
```

## Database

We can now implement our Database class using Room:

```kotlin
@Database(entities = [Todo::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        fun build(context: Context): TodoDatabase {
            return Room.databaseBuilder(
                context,
                TodoDatabase::class.java,
                "todo-database",
            ).build()
        }
    }
}
```

?> We've created a `build` method within the `TodoDatabase`'s companion object, which gives us a clean and concise way of creating a new database instance.

## TodosBloc

### Events

We need a few kinds of events to represent the different transactions for working with Todos. We can use Kotlin's `sealed class` feature to implement this in a clean, concise, and type-safe way:

```kotlin
sealed class TodosEvent

object TodosInitialized : TodosEvent()

data class TodoAdded(val name: String) : TodosEvent()

data class TodoCompleted(val id: Int) : TodosEvent()

data class TodoUncompleted(val id: Int): TodosEvent()

data class TodoDeleted(val id: Int) : TodosEvent()
```

### States

Again, we can use `sealed class`es to represent our states:

```kotlin
sealed class TodosState

object TodosLoading : TodosState()

data class TodosLoadSuccess(val todos: List<Todo>) : TodosState()
```

?> We haven't included any error handling here; we're assuming that all database transactions will be successful, which is a fairly reasonable assumption in our case.

### Bloc

Now we have our events and states, we can create our `TodosBloc` class:

```kotlin
class TodosBloc(private val todoDao: TodoDao) : Bloc<TodosEvent, TodosState>(TodosLoading) {
    init {
      on<TodosInitialized> {
        emitEach(todoDao.getAllTodos().map { TodosLoadSuccess(it) })
      }
      on<TodoAdded> { event ->
        todoDao.addTodo(Todo(name = event.name))
      }
      on<TodoCompleted> { event ->
        val todo = todoDao.getTodo(event.id)
        todoDao.updateTodo(todo.copy(completed = true))
      }
      on<TodoUncompleted> { event ->
        val todo = todoDao.getTodo(event.id)
        todoDao.updateTodo(todo.copy(completed = false))
      }
      on<TodoDeleted> { event ->
        val todo = todoDao.getTodo(event.id)
        todoDao.deleteTodo(todo)
      }
    }
}
```

Let's briefly talk through how this works.

1. When the `TodosInitialized` event is added, the `TodosBloc` gets a `Flow` of the current list of `Todo`s using the `getAllTodos()` method on the DAO. It then `map`s this flow into `TodosLoadSuccess` states containing the current value.
2. This flow of states is then passed into the `emitEach` method, which provided by the `Emitter` receiver. This will listen to the provided flow and emit each value.
3. For all other events, we are simply calling the relevant DAO method. Thanks to Room's observable query, we don't need to manually retrieve and emit the new list of Todos, as this will be handled by the `emitEach` call we set up in steps 1 and 2.

## Koin Module

We can now create our `todosModule` using Koin:

```kotlin
val todosModule = module {
    single { TodoDatabase.build(androidContext()) }
    single { get<TodoDatabase>().todoDao() }
    single { TodosBloc(get()) }
}
```

## Application Class

We can then initialize Koin in our application class:

```kotlin
class TodoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@CounterApplication)
            modules(listOf(todosModule))
        }
    }
}
```

Don't forget to add this to your `AndroidManifest.xml`:

```xml
<application 
             android:name=".TodoApplication">
  <!-- Other stuff -->
</application>
```

## TodoList Composable

We can now start building our user interface. The first step is to implement a composable which displays our current list of Todos.

Each Todo is a dismissable list item, with a Checkbox allowing the user to complete or uncomplete the todo. When a Todo is swiped away, it is deleted.

```kotlin
@ExperimentalMaterialApi
@Composable
fun TodoList(
    onClickAdd: () -> Unit,
    todosBloc: TodosBloc = get(), // Injected by Koin
) {
    LaunchedEffect(todosBloc) {
        todosBloc.add(TodosInitialized)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Todos") })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Todo") },
                icon = { Icon(Icons.Default.Add, "Add Todo") },
                onClick = onClickAdd,
            )
        }
    ) {
        // Use BlocComposer to automatically update the content based on the current list of Todos
        BlocComposer(bloc = todosBloc) { todosState ->

            when (todosState) {
                is TodosLoadSuccess -> {
                    if (todosState.todos.isNotEmpty()) {
                        LazyColumn(
                            contentPadding = PaddingValues(vertical = 8.dp),
                        ) {

                            items(todosState.todos, key = { it.id }) { todo ->
                                val dismissState = rememberDismissState()

                                if (
                                    dismissState.isDismissed(DismissDirection.StartToEnd) ||
                                    dismissState.isDismissed(DismissDirection.EndToStart)
                                ) {
                                    todosBloc.add(TodoDeleted(todo.id))
                                }

                                SwipeToDismiss(
                                    dismissState,
                                    background = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Red)
                                                .padding(horizontal = 16.dp),
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                "Delete",
                                                tint = Color.White,
                                                modifier = Modifier.align(
                                                    if (dismissState.dismissDirection == DismissDirection.StartToEnd)
                                                        Alignment.CenterStart
                                                    else
                                                        Alignment.CenterEnd,
                                                ),
                                            )
                                        }
                                    },
                                ) {
                                    ListItem(
                                        modifier = Modifier.background(
                                            MaterialTheme.colors.background,
                                        ),
                                        text = { Text(todo.name) },
                                        trailing = {
                                            Checkbox(
                                                checked = todo.completed,
                                                onCheckedChange = {
                                                    todosBloc.add(
                                                        if (it)
                                                            TodoCompleted(todo.id)
                                                        else
                                                            TodoUncompleted(todo.id)
                                                    )
                                                },
                                            )
                                        }
                                    )
                                }

                            }
                        }
                    } else {
                        // Empty state
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("Time to add your first todo!")
                        }
                    }
                }
                TodosLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

            }
        }
    }
}
```

## AddTodo Composable

We can now build a composable for adding a new Todo.

```kotlin
@Composable
fun AddTodo(todosBloc: TodosBloc = get(), onSave: () -> Unit) {
    val name = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Todo") },
                actions = {
                    IconButton(
                        onClick = {
                            todosBloc.add(TodoAdded(name.value))
                            onSave()
                        },
                        enabled = name.value.isNotBlank(),
                    ) {
                        Icon(Icons.Default.Check, "Save")
                    }
                },
            )
        },
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
        ) {

            OutlinedTextField(
                label = { Text("Name") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    autoCorrect = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        todosBloc.add(TodoAdded(name.value))
                        onSave()
                    }
                ),
                modifier = Modifier.fillMaxWidth(),
                value = name.value,
                onValueChange = {
                    name.value = it
                },
            )

        }

    }
}
```

## Navigation

We can now bring our TodoList and AddTodo composables together and implement our navigation.

```kotlin
@ExperimentalMaterialApi
@Composable
fun TodosNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "todoList",
    ) {
        composable("todoList") {
            TodoList(
                onClickAdd = {
                    navController.navigate("addTodo")
                }
            )
        }
        composable("addTodo") {
            AddTodo(
                onSave = {
                    navController.navigateUp()
                },
            )
        }
    }
}
```

## MainActivity

Finally, let's launch our `TodosNavHost` from our `MainActivity`.

```kotlin
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoListTheme {
                TodosNavHost()
            }
        }
    }
}
```

