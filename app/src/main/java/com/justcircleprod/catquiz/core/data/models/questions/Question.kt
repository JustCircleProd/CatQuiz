package com.justcircleprod.catquiz.core.data.models.questions

interface Question {
    val id: Int
    var firstOptionStringResId: Int
    var secondOptionStringResId: Int
    var thirdOptionStringResId: Int
    var fourthOptionStringResId: Int
    val answerNumStringResId: Int
}