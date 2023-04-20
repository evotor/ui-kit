package ru.evotor.ui_kit.dialogs.base

object StackTraceUtils {

    fun getStackTrace(): String {
        return try {
            throw RuntimeException()
        } catch (e: RuntimeException) {
            if (e.stackTrace.size > 3) {
                e.stackTrace.foldIndexed(StringBuilder()) { index, acc, st ->
                    if (index in 3..(9.coerceAtMost(e.stackTrace.size))) {
                        acc.append("${st.className}.${st.methodName}(${st.fileName}:${st.lineNumber})\n")
                    } else {
                        acc
                    }
                }.toString()
            } else "[NO_STACKTRACE]"
        }
    }
}