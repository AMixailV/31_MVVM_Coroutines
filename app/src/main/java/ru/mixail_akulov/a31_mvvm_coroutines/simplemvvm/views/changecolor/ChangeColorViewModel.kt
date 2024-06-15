package ru.mixail_akulov.a31_mvvm_coroutines.simplemvvm.views.changecolor

import androidx.lifecycle.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import ru.mixail_akulov.a31_mvvm_coroutines.R
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.model.PendingResult
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.model.SuccessResult
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.navigator.Navigator
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.resources.Resources
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.sideeffects.toasts.Toasts
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.views.BaseViewModel
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.views.LiveResult
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.views.MediatorLiveResult
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.views.MutableLiveResult
import ru.mixail_akulov.a31_mvvm_coroutines.simplemvvm.model.colors.ColorsRepository
import ru.mixail_akulov.a31_mvvm_coroutines.simplemvvm.model.colors.NamedColor
import ru.mixail_akulov.a31_mvvm_coroutines.simplemvvm.views.changecolor.ChangeColorFragment.Screen

class ChangeColorViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val toasts: Toasts,
    private val resources: Resources,
    private val colorsRepository: ColorsRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(), ColorsAdapter.Listener {

    // input sources
    private val _availableColors = MutableLiveResult<List<NamedColor>>(PendingResult())
    private val _currentColorId = savedStateHandle.getLiveData("currentColorId", screen.currentColorId)
    private val _saveInProgress = MutableLiveData(false)

    // основной пункт назначения (содержит объединенные значения from _availableColors & _currentColorId)
    private val _viewState = MediatorLiveResult<ViewState>()
    val viewState: LiveResult<ViewState> = _viewState

    // побочное назначение, также тот же результат может быть достигнут с использованием Transformations.map() function.
    val screenTitle: LiveData<String> = Transformations.map(viewState) { result ->
        if (result is SuccessResult) {
            val currentColor = result.data.colorsList.first { it.selected }
            resources.getString(R.string.change_color_screen_title, currentColor.namedColor.name)
        } else {
            resources.getString(R.string.change_color_screen_title_simple)
        }
    }

    init {
        load()

        // initializing MediatorLiveData
        _viewState.addSource(_availableColors) { mergeSources() }
        _viewState.addSource(_currentColorId) { mergeSources() }
        _viewState.addSource(_saveInProgress) { mergeSources() }
    }

    override fun onColorChosen(namedColor: NamedColor) {
        if (_saveInProgress.value == true) return
        _currentColorId.value = namedColor.id
    }

    fun onSavePressed() = viewModelScope.launch {
        try {
            _saveInProgress.postValue(true)

            // this code is launched asynchronously in other thread
            val currentColorId = _currentColorId.value ?: throw IllegalStateException("Color ID should not be NULL")
            val currentColor = colorsRepository.getById(currentColorId)
            colorsRepository.setCurrentColor(currentColor)

            navigator.goBack(currentColor)
        } catch (e: Exception) {
            if (e is CancellationException) toasts.toast(resources.getString(R.string.error_happened))
        } finally {
            _saveInProgress.value = false
        }
    }


    fun onCancelPressed() {
        navigator.goBack()
    }

    fun tryAgain() {
        load()
    }

    /**
     * [MediatorLiveData] может прослушивать другие экземпляры LiveData (даже более 1) и комбинировать их значения.
     *
     * Здесь мы слушаем список доступных цветов ([_availableColors] live-data) + current color id
     * ([_currentColorId] live-data), затем мы используем оба этих значения для создания списка
     * [NamedColorListItem], это список, который будет отображаться в RecyclerView.
     */
    private fun mergeSources() {
        val colors = _availableColors.value ?: return
        val currentColorId = _currentColorId.value ?: return
        val saveInProgress = _saveInProgress.value ?: return

        // map Result<List<NamedColor>> to Result<ViewState>
        _viewState.value = colors.map { colorsList ->
            ViewState(
                // map List<NamedColor> to List<NamedColorListItem>
                colorsList = colorsList.map { NamedColorListItem(it, currentColorId == it.id) },
                showSaveButton = !saveInProgress,
                showCancelButton = !saveInProgress,
                showSaveProgressBar = saveInProgress
            )
        }
    }

    private fun load() = into(_availableColors) {
        colorsRepository.getAvailableColors()
    }

    data class ViewState(
        val colorsList: List<NamedColorListItem>,
        val showSaveButton: Boolean,
        val showCancelButton: Boolean,
        val showSaveProgressBar: Boolean
    )
}