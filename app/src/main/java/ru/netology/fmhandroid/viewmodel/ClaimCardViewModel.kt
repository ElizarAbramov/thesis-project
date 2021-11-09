package ru.netology.fmhandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.netology.fmhandroid.R
import ru.netology.fmhandroid.adapter.OnClaimCommentItemClickListener
import ru.netology.fmhandroid.dto.Claim
import ru.netology.fmhandroid.dto.ClaimComment
import ru.netology.fmhandroid.dto.ClaimCommentWithCreator
import ru.netology.fmhandroid.dto.FullClaim
import ru.netology.fmhandroid.repository.claimRepository.ClaimRepository
import ru.netology.fmhandroid.ui.OpenClaimFragmentDirections
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class ClaimCardViewModel @Inject constructor(
    private val claimRepository: ClaimRepository
) : ViewModel(), OnClaimCommentItemClickListener {
    private var claimId by Delegates.notNull<Int>()

    val dataFullClaim: Flow<FullClaim> by lazy {
        claimRepository.getClaimById(claimId)
    }

    val claimStatusChangedEvent = MutableSharedFlow<Unit>()
    val claimStatusChangeExceptionEvent = MutableSharedFlow<Unit>()
    val claimUpdateExceptionEvent = MutableSharedFlow<Unit>()
    val claimUpdatedEvent = MutableSharedFlow<Unit>()
    val claimCreatedEvent = MutableSharedFlow<Unit>()
    val createClaimExceptionEvent = MutableSharedFlow<Unit>()
    private val claimCommentsLoadExceptionEvent = MutableSharedFlow<Unit>()
    val claimCommentsLoadedEvent = MutableSharedFlow<Unit>()
    val claimCommentCreatedEvent = MutableSharedFlow<Unit>()
    val claimCommentUpdatedEvent = MutableSharedFlow<Unit>()
    val claimCommentCreateExceptionEvent = MutableSharedFlow<Unit>()
    val updateClaimCommentExceptionEvent = MutableSharedFlow<Unit>()
    val showNoCommentEditingRightsError = MutableSharedFlow<Unit>()

    fun createClaimComment(claimComment: ClaimComment) {
        viewModelScope.launch {
            try {
                claimComment.claimId?.let { claimRepository.saveClaimComment(it, claimComment) }
                claimCommentCreatedEvent.emit(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                claimCommentCreateExceptionEvent.emit(Unit)
            }
        }
    }

    fun updateClaimComment(comment: ClaimComment) {
        viewModelScope.launch {
            try {
                claimRepository.changeClaimComment(comment)
                claimCommentUpdatedEvent.emit(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                updateClaimCommentExceptionEvent.emit(Unit)
            }
        }
    }

    fun save(claim: Claim) {
        viewModelScope.launch {
            try {
                claimRepository.saveClaim(claim)
                claimCreatedEvent.emit(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                createClaimExceptionEvent.emit(Unit)
            }
        }
    }

    fun updateClaim(updatedClaim: Claim) {
        viewModelScope.launch {
            try {
                claimRepository.editClaim(updatedClaim)
                claimUpdatedEvent.emit(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                claimUpdateExceptionEvent.emit(Unit)
            }
        }
    }

    fun getAllClaimComments(id: Int) {
        viewModelScope.launch {
            try {
                claimRepository.getAllCommentsForClaim(id)
                claimCommentsLoadedEvent.emit(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                claimCommentsLoadExceptionEvent.emit(Unit)
            }
        }
    }

    fun changeClaimStatus(
        claimId: Int,
        newClaimStatus: Claim.Status,
        executorId: Int?,
        claimComment: ClaimComment
    ) {
        viewModelScope.launch {
            try {
                claimRepository.changeClaimStatus(
                    claimId,
                    newClaimStatus,
                    executorId,
                    claimComment
                )
                claimStatusChangedEvent.emit(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                claimStatusChangeExceptionEvent.emit(Unit)
            }
        }
    }

    fun init(claimId: Int) {
        this.claimId = claimId
    }

    // region OnClaimCommentItemClickListener
    override fun onCard(claimComment: ClaimCommentWithCreator) {
//        if (user.id == claimComment.creator.id) {
//            val action = OpenClaimFragmentDirections
//                .actionOpenClaimFragmentToCreateEditClaimCommentFragment(
//                    claimComment,
//                    claim.claim.id!!
//                )
//            findNavController().navigate(action)
//        } else viewModelScope.launch {
//            showNoCommentEditingRightsError.emit(Unit)
//        }
    }
// endregion OnClaimCommentItemClickListener
}
