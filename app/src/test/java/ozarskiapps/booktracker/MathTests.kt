package ozarskiapps.booktracker

import junit.framework.TestCase.assertEquals
import org.junit.Test

class MathTests {

    @Test
    fun roundDoubleTest(){
        var value = 1.23456789
        var multiplier = 100
        var roundedValue = roundDouble(value, multiplier)
        assertEquals(roundedValue , 1.23, 0.01)
        value = 24.1837
        multiplier = 10
        roundedValue = roundDouble(value, multiplier)
        assertEquals(roundedValue, 24.1, 0.1)
    }
}