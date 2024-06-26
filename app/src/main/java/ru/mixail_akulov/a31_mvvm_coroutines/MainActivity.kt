package ru.mixail_akulov.a31_mvvm_coroutines

import android.os.Bundle
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.dialogs.plugin.DialogsPlugin
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.intens.plugin.IntentsPlugin
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.navigator.plugin.NavigatorPlugin
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.navigator.plugin.StackFragmentNavigator
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.SideEffectPluginsManager
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.permissions.plugin.PermissionsPlugin
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.resources.plugin.ResourcesPlugin
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.toasts.plugin.ToastsPlugin
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.views.activity.BaseActivity
import ru.mixail_akulov.a31_mvvm_coroutines.simplemvvm.views.currentcolor.CurrentColorFragment

/**
 * Это приложение представляет собой приложение с одним действием.
 * MainActivity — это контейнер для всех экранов.
 */
class MainActivity : BaseActivity() {

    override fun registerPlugins(manager: SideEffectPluginsManager) = with (manager) {
        val navigator = createNavigator()
        register(plugin = ToastsPlugin())
        register(ResourcesPlugin())
        register(NavigatorPlugin(navigator))
        register(PermissionsPlugin())
        register(DialogsPlugin())
        register(IntentsPlugin())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

        private fun createNavigator() = StackFragmentNavigator(
            containerId = R.id.fragmentContainer,
            defaultTitle = getString(R.string.app_name),
            animations = StackFragmentNavigator.Animations(
                enterAnim = R.anim.enter,
                exitAnim = R.anim.exit,
                popEnterAnim = R.anim.pop_enter,
                popExitAnim = R.anim.pop_exit
            ),
            initialScreenCreator = { CurrentColorFragment.Screen() }
        )
}
