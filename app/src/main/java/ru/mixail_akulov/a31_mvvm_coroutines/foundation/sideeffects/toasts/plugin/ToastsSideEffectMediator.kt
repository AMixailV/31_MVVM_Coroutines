package ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.toasts.plugin

import android.content.Context
import android.widget.Toast
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.SideEffectMediator
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.toasts.Toasts
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.utils.MainThreadExecutor

/**
 * Android-реализация [Toasts]. Отображение простого всплывающего сообщения и получение строки из ресурсов.
 */
class ToastsSideEffectMediator(
    private val appContext: Context
) : SideEffectMediator<Nothing>(), Toasts {

    private val executor = MainThreadExecutor()

    override fun toast(message: String) {
        executor.execute {
            Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
        }
    }

}
