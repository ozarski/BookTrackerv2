package ozarskiapps.booktracker.OpenLibraryAPI

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.*

class BookSearchService(val okHttpClient: OkHttpClient) {
    private val connectionString = "https://openlibrary.org/search.json?q="
    private val selectionFields = "title,author_name,number_of_pages_median,key"

    fun searchBooksByTitle(title: String, limit: Int): String? {
        // Build the request object
        val request = Request.Builder()
            .url("$connectionString${title.lowercase(Locale.getDefault())}&fields=$selectionFields&limit=$limit")
            .build()

        // Make the request and get the response
        val response: Response
        try{
            response = okHttpClient.newCall(request).execute()
        }
        catch (e: Exception){
            println(e.message)
            return null
        }

        // Check if the request was successful
        return if (response.isSuccessful) {
            // If the request was successful, return the response body as a string
            return response.body?.string().toString()
        } else {
            // If the request was unsuccessful, return null
            null
        }
    }

    fun parseResponse(json: String): BookSearchResponse? {
        val gson = Gson()
        if (json != "") {
            return gson.fromJson(json, BookSearchResponse::class.java)
        }
        return null
    }

    fun filterResponse(docs: List<OpenLibraryAPIBook>): MutableList<OpenLibraryAPIBook> {
        val bookList = mutableListOf<OpenLibraryAPIBook>()

        docs.forEach { book ->
            if (!book.title.contains("Lib/E") && book.numberOfPagesMedian!=0) {
                bookList.add(book)
            }
        }
        return bookList
    }
}