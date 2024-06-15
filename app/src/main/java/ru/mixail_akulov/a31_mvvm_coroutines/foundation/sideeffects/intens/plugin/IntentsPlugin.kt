package ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.intens.plugin

import android.content.Context
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.intens.Intents
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.SideEffectMediator
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.SideEffectPlugin

/**
    Плагин для запуска системных активностей из view-моделей.
    Позволяет добавить интерфейс [Intents] в конструктор модели представления.
 */
class IntentsPlugin : SideEffectPlugin<Intents, Nothing> {

    override val mediatorClass: Class<Intents>
        get() = Intents::class.java

    override fun createMediator(applicationContext: Context): SideEffectMediator<Nothing> {
        return IntentsSideEffectMediator(applicationContext)
    }

}