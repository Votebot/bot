package space.votebot.bot.util

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.requests.RestAction

/**
 * Convenience alias for [Message.MentionType].
 */
typealias MentionType = Message.MentionType

/**
 * Utility for resolving Discord entities by their name, id, or mention.
 */
object EntityResolver {

    /**
     * Resolves a [Role] by its mention, id, or name.
     * @param guild the [Guild] the role is on
     * @param input the [String] to resolve the Role from
     * @param ignoreCase whether to ignore the case of the name or not
     *
     * @return the resolved [Role] or `null` if none was found
     */
    suspend fun resolveRole(guild: Guild, input: String, ignoreCase: Boolean = false): Role? =
            resolveEntity(input, MentionType.ROLE, null, guild::getRoleById, guild::getRolesByName, ignoreCase)

    /**
     * Resolves a [TextChannel] by its mention, id, or name.
     * @param guild the [Guild] the channel is on
     * @param input the [String] to resolve the Role from
     * @param ignoreCase whether to ignore the case of the name or not
     *
     * @return the resolved [TextChannel] or `null` if none was found
     */
    suspend fun resolveTextChannel(guild: Guild, input: String, ignoreCase: Boolean = false): TextChannel? =
            resolveEntity(input, MentionType.CHANNEL, null, guild::getTextChannelById, guild::getTextChannelsByName, ignoreCase)

    /**
     * Resolves a [User] by its mention, id, or name.
     * @param jda the [JDA] instance to resolve the User from
     * @param input the [String] to resolve the Role from
     * @param ignoreCase whether to ignore the case of the name or not
     *
     * @return the resolved [User] or `null` if none was found
     */
    suspend fun resolveUser(jda: JDA, input: String, ignoreCase: Boolean = false): User? =
            resolveTaggable(input, jda::retrieveUserById, null, jda::getUsersByName, ignoreCase, jda::getUserByTag)

    /**
     * Resolves a [Member] by its mention, id, or name.
     * @param guild the [Guild] the member is on
     * @param input the [String] to resolve the Role from
     * @param ignoreCase whether to ignore the case of the name or not
     *
     * @return the resolved [Member] or `null` if none was found
     */
    suspend fun resolveMember(guild: Guild, input: String, ignoreCase: Boolean = false): Member? =
            resolveTaggable(input, guild::retrieveMemberById, null, guild::getMembersByName, ignoreCase, guild::getMemberByTag)

    private suspend fun <T : IMentionable> resolveTaggable(input: String,
                                                           idResolver: ((String) -> RestAction<T>)? = null,
                                                           idCacheResolver: ((String) -> T?)? = null,
                                                           nameResolver: (String, Boolean) -> Collection<T>,
                                                           ignoreCase: Boolean = false, tagResolver: (String) -> T?): T? {
        return try {
            resolveEntity(input, MentionType.USER, idResolver, idCacheResolver, nameResolver, ignoreCase)
                    ?: tagResolver(input)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    private suspend fun <T : IMentionable> resolveEntity(
            input: String,
            type: MentionType,
            idResolver: ((String) -> RestAction<T>)? = null,
            idCacheResolver: ((String) -> T?)? = null,
            nameResolver: (String, Boolean) -> Collection<T>,
            ignoreCase: Boolean = false
    ): T? {
        require(idResolver != null || idCacheResolver != null)
        require(input.isNotBlank()) { "Input string cannot be blank" }
        val matcher = type.pattern.matcher(input)
        return if (matcher.matches()) {
            println("x")
            if (idResolver != null) {
                idResolver(matcher.group(1)).await()
            } else {
                idCacheResolver?.let { it(matcher.group(1)) }
            }
        } else {
            if (input.isNumeric()) {
                if (idResolver != null) {
                    idResolver(matcher.group(1)).await()
                }
                idCacheResolver?.let { it(matcher.group(1)) }
            } else {
                nameResolver(input, ignoreCase).firstOrNull()
            }
        }
    }
}
