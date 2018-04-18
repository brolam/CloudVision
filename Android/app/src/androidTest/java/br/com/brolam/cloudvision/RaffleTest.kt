package br.com.brolam.cloudvision

import br.com.brolam.cloudvision.helpers.Raffle
import org.junit.Assert
import org.junit.Test

/**
 * Created by brenomarques on 30/03/2018.
 *
 */

class RaffleTest {
    @Test
    fun chooseOne(){
        val competitors: List<Int> = 1.rangeTo(10).distinct()
        (0..1000).forEach {
            val oneWinner = Raffle.chooseOne(competitors)
            Assert.assertNotNull(oneWinner)
        }
    }

    @Test
    fun chooseOneWithoutCompetitors(){
        val competitors = ArrayList<Int>()
        val oneWinner = Raffle.chooseOne(competitors)
        Assert.assertNull(oneWinner)
    }
}