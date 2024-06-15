package ru.mixail_akulov.a31_mvvm_coroutines.foundation.views

import androidx.lifecycle.*
import kotlinx.coroutines.*
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.model.ErrorResult
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.utils.Event
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.model.Result
import ru.mixail_akulov.a31_mvvm_coroutines.foundation.model.SuccessResult

// Альтернативные записи для сокращени кода
typealias LiveEvent<T> = LiveData<Event<T>>
typealias MutableLiveEvent<T> = MutableLiveData<Event<T>>
typealias LiveResult<T> = LiveData<Result<T>>

typealias MutableLiveResult<T> = MutableLiveData<Result<T>>
typealias MediatorLiveResult<T> = MediatorLiveData<Result<T>>

/**
 * Base class for all view-models.
 */

open class BaseViewModel() : ViewModel() {

    private val coroutineContext = SupervisorJob() + Dispatchers.Main.immediate
    protected val viewModelScope: CoroutineScope = CoroutineScope(coroutineContext)

    override fun onCleared() {
        super.onCleared()
        clearViewModelScope()
    }

    /**
     * Переопределите этот метод в дочерних классах, если вы хотите прослушивать результаты с других экранов.
     */
    open fun onResult(result: Any) {

    }

    /**
     * Переопределите этот метод в дочерних классах, если вы хотите контролировать поведение возврата.
     * Верните `true`, если вы хотите прервать закрытие этого экрана
     */
    open fun onBackPressed(): Boolean {
        clearViewModelScope()
        return false
    }

    /**
     * Запустить задачу асинхронно и сопоставить ее результат с указанным
     * [liveResult].
     * Задача автоматически отменяется, если модель представления будет уничтожена.
     */
    fun <T> into(liveResult: MutableLiveResult<T>, block: suspend () -> T) {
        viewModelScope.launch {
            try {
                liveResult.postValue(SuccessResult(block()))
            } catch (e: Exception) {
                liveResult.postValue(ErrorResult(e))
            }
        }
    }

    private fun clearViewModelScope() {
        viewModelScope.cancel()
    }

}

