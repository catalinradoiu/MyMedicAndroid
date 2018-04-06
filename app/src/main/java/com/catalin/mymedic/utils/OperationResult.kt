package com.catalin.mymedic.utils

/**
 * @author catalinradoiu
 * @since 4/6/2018
 */
sealed class OperationResult {
    data class Error(val message: String) : OperationResult()
    data class Success(val message: String = "") : OperationResult()
    object NoOperation : OperationResult()
}