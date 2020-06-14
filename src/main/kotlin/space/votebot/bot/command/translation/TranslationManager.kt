package space.votebot.bot.command.translation

import com.i18next.java.I18Next
import space.votebot.bot.database.VoteBotUser
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

object TranslationManager {

    private val languageStorage: Map<String, I18Next>
    private val defaultLanguage
        get() = languageStorage["en"] ?: error("Missing default locale")
    val supportedLanguages: Set<String>

    init {
        languageStorage = File("locales").listFiles()?.map { file ->
            val json = BufferedReader(FileReader(file)).use { it.readText() }
            val i18n = I18Next().apply { loader().from(json).load() }
            val languageName = file.nameWithoutExtension
            languageName to i18n
        }?.toMap() ?: error("Missing language files")
        supportedLanguages = languageStorage.keys
    }

    fun forUser(user: VoteBotUser) = forLanguage(user.locale)

    fun forLanguage(lang: String) = languageStorage[lang] ?: defaultLanguage
}
