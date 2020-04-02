

package space.votebot.bot.constants

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

/**
 * Miscellaneous constants used in the bot.
 */
object Constants {

    /**
     * Array of the bot owners Discord ids.
     */
    val BOT_OWNERS: LongArray = longArrayOf(416902379598774273, 227817074976751616)

    /**
     * The prefix used for commands.
     */
    const val prefix: String = "v!"

    /**
     * URL that is used for pasting text.
     */
    val hastebinUrl: HttpUrl = "https://hasteb.in/".toHttpUrl()

    /**
     * Dateformat used in the bot.
     */
    val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.SHORT)
            .withLocale(Locale.ENGLISH)
            .withZone(ZoneId.of("US/Eastern")) // Too lazy to set server timezone :P

}
