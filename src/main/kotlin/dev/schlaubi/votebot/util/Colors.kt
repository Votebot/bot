/*
 * Votebot - A feature-rich bot to create votes on Discord guilds.
 *
 * Copyright (C) 2019-2021  Michael Rittmeister & Yannick Seeger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package dev.schlaubi.votebot.util

import dev.kord.common.Color

/**
 * Wrapper for [Discordapp.com/branding][https://discordapp.com/branding] colors and some other colors:
 */
@Suppress("KDocMissingDocumentation", "unused", "MagicNumber")
object Colors {
    // Discord
    val BLURLPLE: Color = Color(114, 137, 218)
    val FULL_WHITE: Color = Color(255, 255, 255)
    val GREYPLE: Color = Color(153, 170, 181)
    val DARK_BUT_NOT_BLACK: Color = Color(44, 47, 51)
    val NOT_QUITE_BLACK: Color = Color(33, 39, 42)

    // Other colors
    val LIGHT_RED: Color = Color(231, 76, 60)
    val DARK_RED: Color = Color(192, 57, 43)
    val LIGHT_GREEN: Color = Color(46, 204, 113)
    val DARK_GREEN: Color = Color(39, 174, 96)
    val BLUE: Color = Color(52, 152, 219)
    val YELLOW: Color = Color(241, 196, 15)
}