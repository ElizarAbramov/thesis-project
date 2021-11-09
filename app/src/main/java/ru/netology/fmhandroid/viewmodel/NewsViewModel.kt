package ru.netology.fmhandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.netology.fmhandroid.dto.News
import ru.netology.fmhandroid.dto.NewsWithCreators
import ru.netology.fmhandroid.repository.newsRepository.NewsRepository
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val sortDirection = MutableStateFlow(SortDirection.ASC)

    val newsItemCreatedEvent = MutableSharedFlow<Unit>()
    val loadNewsExceptionEvent = MutableSharedFlow<Unit>()
    val saveNewsItemExceptionEvent = MutableSharedFlow<Unit>()
    val editNewsItemSavedEvent = MutableSharedFlow<Unit>()
    val editNewsItemExceptionEvent = MutableSharedFlow<Unit>()
    val removeNewsItemExceptionEvent = MutableSharedFlow<Unit>()
    val loadNewsCategoriesExceptionEvent = MutableSharedFlow<Unit>()

    init {
        viewModelScope.launch {
            newsRepository.saveCategories()
            newsRepository.refreshNews()
        }
    }

    val data: Flow<List<NewsWithCreators>> by lazy {
        newsRepository.getAllNews(
            viewModelScope,
            publishEnabled = true,
            // Вынести в Utils
            publishDateBefore = LocalDateTime.now().atZone(
                ZoneId.systemDefault()
            ).toInstant().toEpochMilli()
        ).combine(sortDirection) { news, sortDirection ->
            when(sortDirection) {
                SortDirection.ASC -> news
                SortDirection.DESC -> news.reversed()
            }
        }
    }

    fun onRefresh() {
        viewModelScope.launch {
            try {
                newsRepository.refreshNews()
            } catch (e: Exception) {
                e.printStackTrace()
                loadNewsExceptionEvent.emit(Unit)
            }
        }
    }

    fun onSortDirectionButtonClicked() {
        sortDirection.value = sortDirection.value.reverse()
    }

    fun save(newsItem: News) {
        viewModelScope.launch {
            try {
                newsRepository.saveNewsItem(newsItem)
                newsItemCreatedEvent.emit(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                saveNewsItemExceptionEvent.emit(Unit)
            }
        }
    }

    fun edit(newsItem: News) {
        viewModelScope.launch {
            try {
                newsRepository.editNewsItem(newsItem)
                editNewsItemSavedEvent.emit(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                editNewsItemExceptionEvent.emit(Unit)
            }
        }
    }

    fun remove(id: Int) {
        viewModelScope.launch {
            try {
                newsRepository.removeNewsItemById(id)
            } catch (e: Exception) {
                e.printStackTrace()
                removeNewsItemExceptionEvent.emit(Unit)
            }
        }
    }

    suspend fun getAllNewsCategories() =
        newsRepository.getAllNewsCategories()
            .catch { e ->
                e.printStackTrace()
                loadNewsCategoriesExceptionEvent.emit(Unit)
            }

    suspend fun filterNewsByCategory(newsCategoryId: Int) =
        newsRepository.filterNewsByCategory(newsCategoryId)
            .catch { e ->
                e.printStackTrace()
                loadNewsExceptionEvent.emit(Unit)
            }

    suspend fun filterNewsByPublishDate(dateStart: Long, dateEnd: Long) =
        newsRepository.filterNewsByPublishDate(dateStart, dateEnd)
            .catch { e ->
                e.printStackTrace()
                loadNewsExceptionEvent.emit(Unit)
            }

    suspend fun filterNewsByCategoryAndPublishDate(
        newsCategoryId: Int,
        dateStart: Long,
        dateEnd: Long
    ) = newsRepository.filterNewsByCategoryAndPublishDate(
        newsCategoryId, dateStart, dateEnd
    ).catch { e ->
        e.printStackTrace()
        loadNewsExceptionEvent.emit(Unit)
    }

    enum class SortDirection {
        ASC,
        DESC;
        fun reverse() = when(this) {
            ASC -> DESC
            DESC -> ASC
        }
    }
}