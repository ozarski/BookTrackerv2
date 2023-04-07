package ozarskiapps.booktracker.OpenLibraryAPI

import com.google.gson.annotations.SerializedName

data class BookSearchResponse(
    @SerializedName("numFound") val numFound: Int,
    @SerializedName("docs") val docs: List<OpenLibraryAPIBook>
)

class OpenLibraryAPIBook(
    @SerializedName("key") val key: String,
    @SerializedName("title") val title: String,
    @SerializedName("number_of_pages_median") val numberOfPagesMedian: Int,
    @SerializedName("author_name") val authorName: List<String>
){
    override fun toString(): String {
        return "Title: $title \t" +
                "Author: $authorName \t" +
                "Number of pages: $numberOfPagesMedian"
    }

    fun authorsToString(): String{
        var authorString = ""
        authorName.forEach {
            authorString+=it
        }
        return authorString
    }
}
