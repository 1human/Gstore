package zed.rainxch.details.data.translation

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import zed.rainxch.details.domain.model.TranslationResult
import java.security.MessageDigest
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class BaiduTranslator(
    private val httpClient: () -> HttpClient,
    private val json: Json,
    private val appId: String,
    private val appSecret: String,
) : Translator {
    // The API rejects a q over 6000 bytes; CJK text is three bytes per character.
    override val maxChunkSize: Int = 1800

    override suspend fun translate(
        text: String,
        targetLanguage: String,
        sourceLanguage: String,
    ): TranslationResult {
        if (appId.isBlank() || appSecret.isBlank()) {
            throw TranslationProviderNotConfiguredException(
                "Baidu appId/appSecret not configured",
            )
        }

        val salt =
            Clock.System
                .now()
                .toEpochMilliseconds()
                .toString()
        val sign = md5Hex(appId + text + salt + appSecret)

        val response =
            httpClient().submitForm(
                url = "https://fanyi-api.baidu.com/api/trans/vip/translate",
                formParameters =
                    Parameters.build {
                        append("q", text)
                        append("from", mapLanguageCode(sourceLanguage))
                        append("to", mapLanguageCode(targetLanguage))
                        append("appid", appId)
                        append("salt", salt)
                        append("sign", sign)
                    },
            )

        val root = json.parseToJsonElement(response.bodyAsText()).jsonObject
        val errorCode = root["error_code"]?.jsonPrimitive?.content
        if (errorCode != null) {
            val errorMessage = root["error_msg"]?.jsonPrimitive?.content.orEmpty()
            throw TranslationProviderException("Baidu translate error $errorCode: $errorMessage")
        }

        val translation =
            root["trans_result"]
                ?.jsonArray
                ?.joinToString("\n") {
                    it.jsonObject["dst"]
                        ?.jsonPrimitive
                        ?.content
                        .orEmpty()
                }.orEmpty()
        if (translation.isBlank()) {
            throw TranslationProviderException("Baidu translate returned no text")
        }

        val detected =
            root["from"]
                ?.jsonPrimitive
                ?.content
                ?.takeIf { it.isNotBlank() }
                ?.let { reverseMapLanguageCode(it) }

        return TranslationResult(
            translatedText = translation,
            detectedSourceLanguage = detected,
        )
    }

    private fun md5Hex(input: String): String {
        val digest = MessageDigest.getInstance("MD5").digest(input.encodeToByteArray())
        return buildString(digest.size * HEX_CHARS_PER_BYTE) {
            for (byte in digest) {
                val v = byte.toInt() and BYTE_MASK
                if (v < HEX_PADDING_BOUND) append('0')
                append(v.toString(HEX_RADIX))
            }
        }
    }

    private fun mapLanguageCode(code: String): String =
        when (code.lowercase()) {
            "auto", "" -> "auto"
            "zh", "zh-cn", "zh-hans" -> "zh"
            "zh-tw", "zh-hant" -> "cht"
            "ja", "jp" -> "jp"
            "ko", "kor" -> "kor"
            "fr", "fra" -> "fra"
            "es", "spa" -> "spa"
            "ar", "ara" -> "ara"
            "vi", "vie" -> "vie"
            else -> code
        }

    private fun reverseMapLanguageCode(code: String): String =
        when (code) {
            "cht" -> "zh-TW"
            "jp" -> "ja"
            "kor" -> "ko"
            "fra" -> "fr"
            "spa" -> "es"
            "ara" -> "ar"
            "vie" -> "vi"
            else -> code
        }
}

private const val HEX_RADIX = 16
private const val HEX_CHARS_PER_BYTE = 2
private const val HEX_PADDING_BOUND = 0x10
private const val BYTE_MASK = 0xff
