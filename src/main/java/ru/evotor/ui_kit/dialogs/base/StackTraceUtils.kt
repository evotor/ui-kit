package ru.evotor.ui_kit.dialogs.base

internal object StackTraceUtils {

    fun getStackTrace(): String {
        return try {
            throw RuntimeException()
        } catch (e: RuntimeException) {
            if (e.stackTrace.size > 11) {
                e.stackTrace.foldIndexed(StringBuilder()) { index, acc, st ->
                    if (index in 11..(e.stackTrace.size.coerceAtMost(e.stackTrace.size))) {
                        acc.append("${st.className}.${st.methodName}(${st.fileName}:${st.lineNumber})\n")
                    } else {
                        acc
                    }
                }.toString()
            } else "[NO_STACKTRACE]"
        }
    }
}