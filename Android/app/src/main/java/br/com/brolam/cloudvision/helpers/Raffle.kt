package br.com.brolam.cloudvision.helpers

import java.util.*

/**
 * Created by brenomarques on 30/03/2018.
 *
 */
class Raffle {
    companion object {
        fun <T> chooseOne(competitors: List<T>): T? {
            val winner = Random().nextInt(competitors.size)
            //if (competitors.elementAtOrNull(winner) == null ) return null
            return competitors.get(winner)
        }
    }
}