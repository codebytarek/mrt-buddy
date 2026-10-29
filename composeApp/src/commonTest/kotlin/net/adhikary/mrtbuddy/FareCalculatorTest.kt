package net.adhikary.mrtbuddy

import net.adhikary.mrtbuddy.data.model.FareCalculator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FareCalculatorTest {

    @Test
    fun testStationList() {
        val stations = FareCalculator.getAllStations()
        assertEquals(18, stations.size)
        assertEquals("Recharge", stations[0].name)
        assertEquals("Uttara North", stations[1].name)
        assertEquals("Kamalapur", stations[17].name)
    }

    @Test
    fun testFareCalculation() {
        val uttaraNorth = FareCalculator.getStation(1)!!
        val kamalapur = FareCalculator.getStation(17)!!
        val farmgate = FareCalculator.getStation("Farmgate")!!

        // Test maximum fare
        val forwardFare = FareCalculator.calculateFare(uttaraNorth, kamalapur)
        assertTrue(forwardFare > 0)
        // Test reverse direction (should be same due to symmetry)
        assertEquals(forwardFare, FareCalculator.calculateFare(kamalapur, uttaraNorth))
        // Test intermediate station fare
        assertEquals(
            FareCalculator.calculateFare(uttaraNorth, farmgate),
            FareCalculator.calculateFare(farmgate, uttaraNorth)
        )
        // Test same station (should be free)
        assertEquals(0, FareCalculator.calculateFare(uttaraNorth, uttaraNorth))
    }

    @Test
    fun testStationLookup() {
        // Test lookup by name
        val stationByName = FareCalculator.getStation("Farmgate")
        assertNotNull(stationByName)
        assertEquals(11, stationByName.id)

        // Test lookup by ID
        val stationById = FareCalculator.getStation(11)
        assertNotNull(stationById)
        assertEquals("Farmgate", stationById.name)

        // Test invalid lookups
        assertNull(FareCalculator.getStation("Invalid Station"))
        assertNull(FareCalculator.getStation(99))
    }
}
