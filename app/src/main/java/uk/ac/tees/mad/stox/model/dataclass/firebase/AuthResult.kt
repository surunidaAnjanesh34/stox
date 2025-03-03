package uk.ac.tees.mad.stox.model.dataclass.firebase

sealed class AuthResult<out T> {
    data object Loading : AuthResult<Nothing>()
    data class Success<out T>(val data: T) : AuthResult<T>()
    data class Error(val exception: Exception) : AuthResult<Nothing>()
}