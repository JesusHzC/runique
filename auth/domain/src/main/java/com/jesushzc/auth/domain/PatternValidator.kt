package com.jesushzc.auth.domain

interface PatternValidator {
    fun matches(value: String): Boolean
}

