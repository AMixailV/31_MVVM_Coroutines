package ru.mixail_akulov.a31_mvvm_coroutines.simplemvvm.views.currentcolor

import android.Manifest
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.mixail_akulov.a31_mvvm_coroutines.R
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.model.PendingResult
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.model.SuccessResult
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.model.takeSuccess
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.dialogs.Dialogs
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.dialogs.plugin.DialogConfig
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.intens.Intents
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.navigator.Navigator
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.permissions.Permissions
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.permissions.plugin.PermissionStatus
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.resources.Resources
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.toasts.Toasts
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.views.BaseViewModel
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.views.LiveResult
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.views.MutableLiveResult
import ru.mixail_akulov.a31_mvvm_coroutines.simplemvvm.model.colors.ColorListener
import ru.mixail_akulov.a31_mvvm_coroutines.simplemvvm.model.colors.ColorsRepository
import ru.mixail_akulov.a31_mvvm_coroutines.simplemvvm.model.colors.NamedColor
import ru.mixail_akulov.a31_mvvm_coroutines.simplemvvm.views.changecolor.ChangeColorFragment

class CurrentColorViewModel(
    private val navigator: Navigator,
    private val toasts: Toasts,
    private val resources: Resources,
    private val permissions: Permissions,
    private val intents: Intents,
    private val dialogs: Dialogs,
    private val colorsRepository: ColorsRepository
) : BaseViewModel() {

    private val _currentColor = MutableLiveResult<NamedColor>(PendingResult())
    val currentColor: LiveResult<NamedColor> = _currentColor

    private val colorListener: ColorListener = {
        _currentColor.postValue(SuccessResult(it))
    }

    // --- пример результатов прослушивания через модельный слой

    init {
        colorsRepository.addListener(colorListener)
        load()
    }

    override fun onCleared() {
        super.onCleared()
        colorsRepository.removeListener(colorListener)
    }

    // --- пример прослушивания результатов прямо с экрана

    override fun onResult(result: Any) {
        super.onResult(result)
        if (result is NamedColor) {
            val message = resources.getString(R.string.changed_color, result.name)
            toasts.toast(message)
        }
    }

    // ---

    fun changeColor() {
        val currentColor = currentColor.value.takeSuccess() ?: return
        val screen = ChangeColorFragment.Screen(currentColor.id)
        navigator.launch(screen)
    }

    /**
     * Пример использования плагинов побочных эффектов
     */
    fun requestPermission() = viewModelScope.launch {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val hasPermission = permissions.hasPermissions(permission)
        if (hasPermission) {
            dialogs.show(createPermissionAlreadyGrantedDialog())
        } else {
            when (permissions.requestPermission(permission)) {
                PermissionStatus.GRANTED -> {
                    toasts.toast(resources.getString(R.string.permissions_grated))
                }
                PermissionStatus.DENIED -> {
                    toasts.toast(resources.getString(R.string.permissions_denied))
                }
                PermissionStatus.DENIED_FOREVER -> {
                    if (dialogs.show(createAskForLaunchingAppSettingsDialog())) {
                        intents.openAppSettings()
                    }
                }
            }
        }
    }

    fun tryAgain() {
        load()
    }

    private fun load() = into(_currentColor) { colorsRepository.getCurrentColor() }

    private fun createPermissionAlreadyGrantedDialog() = DialogConfig(
        title = resources.getString(R.string.dialog_permissions_title),
        message = resources.getString(R.string.permissions_already_granted),
        positiveButton = resources.getString(R.string.action_ok)
    )

    private fun createAskForLaunchingAppSettingsDialog() = DialogConfig(
        title = resources.getString(R.string.dialog_permissions_title),
        message = resources.getString(R.string.open_app_settings_message),
        positiveButton = resources.getString(R.string.action_open),
        negativeButton = resources.getString(R.string.action_cancel)
    )
}