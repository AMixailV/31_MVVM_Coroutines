package ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.toasts

/**
 * Интерфейс для показа всплывающих сообщений пользователю из моделей представления.
 * Вам нужно добавить [ToastsPlugin] в свою активность, прежде чем использовать эту функцию.
 */
interface Toasts {

    /**
     * Показать простое всплывающее сообщение.
     */
    fun toast(message: String)

}