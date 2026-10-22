package zed.rainxch.core.domain.model.settings

object AppLanguages {
    /**
     * The app ships two UI languages: English (the default resource set) and Simplified Chinese.
     * Anything else renders English, both for the resources and for the in-app picker.
     */
    val ALL: List<AppLanguage> =
        listOf(
            AppLanguage("en", "English"),
            AppLanguage("zh-CN", "简体中文"),
        )

    fun findByTag(tag: String?): AppLanguage? = if (tag.isNullOrBlank()) null else ALL.find { it.tag == tag }

    fun containsTag(tag: String?): Boolean = findByTag(tag) != null
}
