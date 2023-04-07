package ozarskiapps.booktracker

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.fail
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import ozarskiapps.booktracker.OpenLibraryAPI.BookSearchService
import ozarskiapps.booktracker.OpenLibraryAPI.OpenLibraryAPIBook
import ozarskiapps.booktracker.book.Book

class OpenLibraryAPITests {
    private lateinit var bookSearchService: BookSearchService
    private lateinit var okHttpClient: OkHttpClient
    private val query = "Harry Potter"
    private val searchResponse = """
        {
            "numFound": 3226,
            "start": 0,
            "numFoundExact": true,
            "docs": [
                {
                    "key": "/works/OL82563W",
                    "title": "Harry Potter and the Philosopher's Stone",
                    "number_of_pages_median": 303,
                    "author_name": [
                        "J. K. Rowling"
                    ]
                },
                {
                    "key": "/works/OL82586W",
                    "title": "Harry Potter and the Deathly Hallows",
                    "number_of_pages_median": 673,
                    "author_name": [
                        "J. K. Rowling"
                    ]
                }
            ],
            "num_found": 3226,
            "q": "harry potter",
            "offset": null
        }        
        """

    @Before
    fun setUp() {
        okHttpClient = mock()
        bookSearchService = BookSearchService(okHttpClient)
    }

    @Test
    fun testSearchBookSuccess() {
        val call = mock<Call>()
        val response = Response.Builder()
            .request(Request.Builder().url("https://openlibrary.org/search.json?q=$query").build())
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(searchResponse.toResponseBody())
            .build()
        whenever(okHttpClient.newCall(any())).thenReturn(call)
        whenever(call.execute()).thenReturn(response)

        val mockResponse = bookSearchService.searchBooksByTitle(query, 2)

        assertEquals(mockResponse, searchResponse)
    }

    @Test
    fun testSearchBookFailure() {
        val call = mock<Call>()
        val response = Response.Builder()
            .request(Request.Builder().url("https://openlibrary.org/search.json?q=$query").build())
            .protocol(Protocol.HTTP_1_1)
            .code(404)
            .message("Not Found")
            .body(searchResponse.toResponseBody())
            .build()
        whenever(okHttpClient.newCall(any())).thenReturn(call)
        whenever(call.execute()).thenReturn(response)

        val mockResponse = bookSearchService.searchBooksByTitle(query, 2)

        assertEquals(mockResponse, null)
    }

    @Test
    fun parseResponse() {
        val mockResponse = bookSearchService.parseResponse(searchResponse)
        assertEquals(mockResponse?.numFound, 3226)
        assertEquals(mockResponse?.docs?.size, 2)
        assertEquals(mockResponse?.docs?.get(0)?.key, "/works/OL82563W")
        assertEquals(mockResponse?.docs?.get(0)?.title, "Harry Potter and the Philosopher's Stone")
        assertEquals(mockResponse?.docs?.get(0)?.numberOfPagesMedian, 303)
        assertEquals(mockResponse?.docs?.get(0)?.authorName?.size, 1)
        assertEquals(mockResponse?.docs?.get(0)?.authorName?.get(0), "J. K. Rowling")
        assertEquals(mockResponse?.docs?.get(1)?.key, "/works/OL82586W")
        assertEquals(mockResponse?.docs?.get(1)?.title, "Harry Potter and the Deathly Hallows")
        assertEquals(mockResponse?.docs?.get(1)?.numberOfPagesMedian, 673)
        assertEquals(mockResponse?.docs?.get(1)?.authorName?.size, 1)
        assertEquals(mockResponse?.docs?.get(1)?.authorName?.get(0), "J. K. Rowling")
        assertEquals(mockResponse?.numFound, 3226)
    }

    @Test
    fun filterResponse() {
        val mockResponse = bookSearchService.parseResponse(searchResponse)
        mockResponse?.docs?.toMutableList()?.add(
            OpenLibraryAPIBook(
                "/works/OL82586W",
                "Lib/E Harry Potter and the Deathly Hallows",
                673,
                listOf("J. K. Rowling")
            )
        )
        mockResponse?.docs?.toMutableList()?.add(
            OpenLibraryAPIBook(
                "/works/OL82586W",
                "Harry Potter and the Deathly Hallows",
                0,
                listOf("J. K. Rowling")
            )
        )

        if(mockResponse?.docs != null) {
            val filteredResponse = bookSearchService.filterResponse(mockResponse.docs)
            assertEquals(filteredResponse.size, 2)
            assertEquals(filteredResponse[0].key, "/works/OL82563W")
            assertEquals(filteredResponse[0].title, "Harry Potter and the Philosopher's Stone")
            assertEquals(filteredResponse[0].numberOfPagesMedian, 303)
            assertEquals(filteredResponse[0].authorName.size, 1)
            assertEquals(filteredResponse[0].authorName[0], "J. K. Rowling")
            assertEquals(filteredResponse[1].key, "/works/OL82586W")
            assertEquals(filteredResponse[1].title, "Harry Potter and the Deathly Hallows")
            assertEquals(filteredResponse[1].numberOfPagesMedian, 673)
            assertEquals(filteredResponse[1].authorName.size, 1)
            assertEquals(filteredResponse[1].authorName[0], "J. K. Rowling")
        }
        else {
            fail()
        }
    }

    @Test
    fun convertOpenLibraryAPIBookToBook(){
        val mockResponse = bookSearchService.parseResponse(searchResponse)
        if(mockResponse?.docs != null) {
            val book = Book(mockResponse.docs[0])
            assertEquals(book.title, "Harry Potter and the Philosopher's Stone")
            assertEquals(book.numberOfPages, 303)
            assertEquals(book.author, "J. K. Rowling")
        }
        else {
            fail()
        }
    }
}
