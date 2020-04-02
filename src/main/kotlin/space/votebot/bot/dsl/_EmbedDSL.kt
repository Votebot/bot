

@file:Suppress("unused")

package space.votebot.bot.dsl

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.requests.restaction.MessageAction
import java.awt.Color
import java.time.Instant
import java.time.temporal.TemporalAccessor

/**
 * DSL creator of an [MessageEmbed].
 * @see embed
 */
typealias EmbedCreator = EmbedConvention.() -> Unit

/**
 * Alias to [MessageEmbed.Field] for more clearance.
 */
typealias EmbedField = MessageEmbed.Field

/**
 * DSL builder for [MessageEmbed] titles.
 */
typealias TitleBuilder = EmbedConvention.TitleConvention.() -> Unit

/**
 * DSL builder for [MessageEmbed] authors.
 */
typealias AuthorBuilder = EmbedConvention.AuthorConvention.() -> Unit

/**
 * DSL builder for [MessageEmbed] footers.
 */
typealias FooterBuilder = EmbedConvention.FooterConvention.() -> Unit

/**
 * DSL builder for [MessageEmbed]s.
 * @see EmbedCreator
 */
fun embed(builder: EmbedCreator): EmbedConvention = EmbedConvention().apply(builder)

/**
 * DSL Convention for creating embeds.
 * @property description description of the embed
 * @property color color of the embed
 * @property thumbnail thumbnail url of the embed
 * @property image image url
 * @property timeStamp timestamp of the embed
 */
@Suppress("MemberVisibilityCanBePrivate") // Is supposed to be accessible as DSL
class
EmbedConvention {

    private val fields = mutableListOf<MessageEmbed.Field>()
    private var author: AuthorConvention? = null
    private var title: TitleConvention? = null
    private var footer: FooterConvention? = null

    var description: CharSequence? = null
    var color: Color? = null
    var thumbnail: String? = null
    var image: String? = null
    var timeStamp: TemporalAccessor? = null

    /**
     * Sets the value of the title of the embed to [title].
     */
    fun title(title: String?) {
        this.title = TitleConvention(title)
    }

    /**
     * Sets the title of the embed to the title provided by the [builder].
     * @see TitleBuilder
     * @see TitleConvention
     */
    fun title(builder: TitleBuilder) {
        this.title = TitleConvention().apply(builder)
    }

    /**
     * Sets the name of the author of the embed to [name].
     */
    fun author(name: String?) {
        this.author = AuthorConvention(name)
    }

    /**
     * Sets the author of the embed to the author provided by the [builder].
     * @see AuthorBuilder
     * @see AuthorConvention
     */
    fun author(builder: AuthorBuilder) {
        this.author = AuthorConvention().apply(builder)
    }

    /**
     * Set's the text of the footer to [text].
     */
    fun footer(text: String) {
        this.footer = FooterConvention(text)
    }

    /**
     * Sets the footer of the embed to the author provided by the [builder].
     * @see FooterBuilder
     * @see FooterConvention
     */
    fun footer(builder: FooterBuilder) {
        this.footer = FooterConvention().apply(builder)
    }

    /**
     * Adds the [field] to the embed.
     * @return whether the field was added or not
     */
    fun addField(field: EmbedField): Boolean = fields.add(field)

    /**
     * Adds a field with the specified [name], [value] and [inline] to the embed.
     * @return whether the field was added or not
     */
    fun addField(name: String?, value: String?, inline: Boolean = false): Boolean =
        addField(EmbedField(name, value, inline))

    /**
     * Adds a blank field to the embed.
     * @param inline whether the field should be inlined or not
     * @see EmbedBuilder.ZERO_WIDTH_SPACE
     * @return whether the field was added or not
     */
    fun addBlankField(inline: Boolean = false): Boolean =
        addField(EmbedField(EmbedBuilder.ZERO_WIDTH_SPACE, EmbedBuilder.ZERO_WIDTH_SPACE, inline))

    /**
     * Returns a [Color] for the specified [rgb].
     * @see Color
     */
    fun color(rgb: Int): Color = Color(rgb)

    /**
     * Returns a [TemporalAccessor] for the current timestamp.
     * @see Instant
     */
    fun now(): TemporalAccessor = Instant.now()

    /**
     * Converts the [EmbedConvention] into a [EmbedBuilder].
     */
    fun toEmbedBuilder(): EmbedBuilder = EmbedBuilder().apply {
        author?.let { author -> setAuthor(author.name, author.url, author.iconUrl) }
        title?.let { title -> setTitle(title.title, title.url) }
        footer?.let { footer -> setFooter(footer.text, footer.url) }
        setDescription(description)
        setColor(color)
        setThumbnail(thumbnail)
        setImage(image)
        setTimestamp(timeStamp)
        this@EmbedConvention.fields.forEach { addField(it) }
    }

    /**
     * Represents a DSL convention for embed titles.
     * @property title the text of the title
     * @property url the url the title links to
     */
    class TitleConvention(var title: String? = null, var url: String? = null)

    /**
     * Represents a DSL convention for embed authors.
     * @property name the name of the author
     * @property url the url the author links to
     * @property iconUrl the url to the authors' icon/avatar
     */
    class AuthorConvention(var name: String? = null, var url: String? = null, var iconUrl: String? = null)

    /**
     * Represents a DSL convention for embed footers.
     * @property text the text of the footer
     * @property url the url the footer links to
     */
    class FooterConvention(var text: String? = null, var url: String? = null)
}

/**
 * Sends embed defined by an [EmbedConvention] into the channel.
 * @see embed
 */
fun MessageChannel.sendMessage(embedConvention: EmbedConvention): MessageAction =
        this.sendMessage(embedConvention.toEmbedBuilder().build())

/**
 * Edits message to embed defined by an [EmbedConvention] into the channel.
 * @see embed
 */
fun Message.editMessage(embedConvention: EmbedConvention): MessageAction =
    editMessage(embedConvention.toEmbedBuilder().build())
