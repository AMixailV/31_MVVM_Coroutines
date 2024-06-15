package ru.mixail_akulov.a31_mvvm_coroutines.simplemvvm.views.changecolor

import ru.mixail_akulov.a31_mvvm_coroutines.simplemvvm.model.colors.NamedColor

/**
 * Представляет элемент списка для цвета; его можно выбрать или нет
 */
data class NamedColorListItem(
    val namedColor: NamedColor,
    val selected: Boolean
)