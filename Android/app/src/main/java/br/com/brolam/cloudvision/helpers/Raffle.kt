package br.com.brolam.cloudvision.helpers

import java.util.*

/**
 * Created by brenomarques on 30/03/2018.
 *
 */
class Raffle {
    companion object {
        fun <T> chooseOne(competitors: List<T>): T? {
            if (competitors.isEmpty()) return null
            val winner = Random().nextInt(competitors.size)
            return competitors[winner]
        }
    }
}