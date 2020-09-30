# KTX Migrations

There is one single file in this library: `amalgamation.kt`, which is named after [The SQLite Amalgamation](https://www.sqlite.org/amalgamation.html), and it gathers most of the KTX API/implementation that is officially distributed in multiple artifacts. Each function of the amalgamation file clearly separates the implementation from the API consumption, that way it will is easy searching for calls that can be potentially replaced by the related ktx function.

An example extracted from the `amalgamation.kt` file:

```kotlin
private fun migrateSQLite() {
    lateinit var database: SQLiteDatabase

    // Without KTX
    database.beginTransaction()
    try {
        val newRowId = database.insert("table", null, ContentValues().apply { put("key", 1) })
        database.setTransactionSuccessful()
    } finally {
        database.endTransaction()
    }

    // With KTX
    database.transaction {
        val newRowId = database.insert("table", null, contentValuesOf("key" to 1))
    }
}
```

The rationale behind having one single file is to have a place where one can go and search for potential KTX replacements:

* For the migration of the entire code base, the best thing that one can do is to start from the beginning of the file and search for usages with Android Studio of each method below the comment `// Without KTX`. In the example above, `database.beginTransaction`, `insert`, `setTransactionSuccessful` and `endTransaction` are the methods that need to look for.
* When adding a new piece of code Android related, search for usages of the code that you are using to see if some KTX function is available to replace them.

The `amalgamation.kt` file can added as a Gradle dependency to your project:

Add to top level gradle.build file
```
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

Add to app module gradle.build file
```
dependencies {
    compileOnly 'com.github.cookpad:ktx-migrations:0.0.1'
}
```
