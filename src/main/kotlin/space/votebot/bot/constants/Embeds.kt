package space.votebot.bot.constants

import com.i18next.java.I18Next
import space.votebot.bot.command.AbstractCommand
import space.votebot.bot.command.AbstractSubCommand
import space.votebot.bot.command.context.Context
import space.votebot.bot.dsl.EmbedConvention
import space.votebot.bot.dsl.EmbedCreator

/**
 * Some presets for frequently used embeds.
 */
@Suppress("unused")
object Embeds {

    /**
     * Creates a info embed with the given [title] and [description] and applies the [builder] to it.
     * @see EmbedCreator
     * @see EmbedConvention
     */
    fun info(title: String, description: String? = null, builder: EmbedCreator = {}): EmbedConvention =
            EmbedConvention().apply {
                title(Emotes.INFO, title)
                this.description = description
                color = Colors.BLUE
            }.apply(builder)

    /**
     * Creates a success embed with the given [title] and [description] and applies the [builder] to it.
     * @see EmbedCreator
     * @see EmbedConvention
     */
    fun success(title: String, description: String? = null, builder: EmbedCreator = {}): EmbedConvention =
            EmbedConvention().apply {
                title(Emotes.SUCCESS, title)
                this.description = description
                color = Colors.LIGHT_GREEN
            }.apply(builder)

    /**
     * Creates a error embed with the given [title] and [description] and applies the [builder] to it.
     * @see EmbedCreator
     * @see EmbedConvention
     */
    fun error(title: String, description: String?, builder: EmbedCreator = {}): EmbedConvention =
            EmbedConvention().apply {
                title(Emotes.ERROR, title)
                this.description = description
                color = Colors.LIGHT_RED
            }.apply(builder)

    /**
     * Creates a warning embed with the given [title] and [description] and applies the [builder] to it.
     * @see EmbedCreator
     * @see EmbedConvention
     */
    fun warn(title: String, description: String?, builder: EmbedCreator = {}): EmbedConvention =
            EmbedConvention().apply {
                title(Emotes.WARN, title)
                this.description = description
                color = Colors.YELLOW
            }.apply(builder)

    /**
     * Creates a loading embed with the given [title] and [description] and applies the [builder] to it.
     * @see EmbedCreator
     * @see EmbedConvention
     */
    fun loading(title: String, description: String?, builder: EmbedCreator = {}): EmbedConvention =
            EmbedConvention().apply {
                title(Emotes.LOADING, title)
                this.description = description
                color = Colors.DARK_BUT_NOT_BLACK
            }.apply(builder)

    /**
     * Creates a help embed for [command].
     */
    fun command(command: AbstractCommand, context: Context): EmbedConvention {
        val translations = context.translations
        return info("${command.displayName} - ${translations.t("embeds.help.description")}", translations.t(command.description)) {
            addField(translations.t("embeds.help.aliases"), command.aliases.joinToString(prefix = "`", separator = "`, `", postfix = "`"))
            addField(translations.t("embeds.help.usage"), formatCommandUsage(command))
            addField(translations.t("embeds.help.permission"), command.permission.name)
            val subCommands = command.registeredCommands.map { formatSubCommandUsage(it, translations) }
            if (subCommands.isNotEmpty()) {
                addField(translations.t("embeds.help.sub_commands"), subCommands.joinToString("\n"))
            }
        }
    }

    private fun formatCommandUsage(command: AbstractCommand): String =
            "${Constants.prefix}${command.name} ${command.usage}"

    private fun formatSubCommandUsage(command: AbstractSubCommand, translations: I18Next): String {
        val builder = StringBuilder(Constants.prefix)
        builder.append(command.name).append(' ').append(command.usage.replace("\n", "\\n"))

        val prefix = " ${command.parent.name} "
        builder.insert(Constants.prefix.length, prefix)
        builder.append(" - ").append(translations.t(command.description))

        return builder.toString()
    }

    private fun EmbedConvention.title(emote: String, title: String) = title("$emote $title")
}
