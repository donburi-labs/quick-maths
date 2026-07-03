package com.jaidensiu.quickMaths.data

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognitionModelIdentifier
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognizerOptions
import com.google.mlkit.vision.digitalink.recognition.Ink
import com.google.mlkit.vision.digitalink.recognition.RecognitionContext
import com.google.mlkit.vision.digitalink.recognition.WritingArea
import com.jaidensiu.quickMaths.domain.HandwritingPoint
import kotlinx.coroutines.tasks.await

class NumberRecognizer {
    private val model = DigitalInkRecognitionModel
        .builder(DigitalInkRecognitionModelIdentifier.EN_US)
        .build()
    private val recognizer = DigitalInkRecognition.getClient(
        DigitalInkRecognizerOptions.builder(model).build(),
    )

    suspend fun prepare() {
        RemoteModelManager.getInstance()
            .download(model, DownloadConditions.Builder().build())
            .await()
        warmUp()
    }

    suspend fun recognize(
        strokes: List<List<HandwritingPoint>>,
        writingAreaWidth: Float? = null,
        writingAreaHeight: Float? = null,
    ): String {
        val ink = Ink.builder().apply {
            strokes.forEach { points ->
                addStroke(
                    Ink.Stroke.builder().apply {
                        points.forEach {
                            addPoint(Ink.Point.create(it.x, it.y, it.timeMillis))
                        }
                    }.build()
                )
            }
        }.build()
        val context = RecognitionContext.builder()
            .setPreContext("")
            .apply {
                if (writingAreaWidth != null && writingAreaHeight != null) {
                    setWritingArea(WritingArea(writingAreaWidth, writingAreaHeight))
                }
            }
            .build()
        val candidates = recognizer.recognize(ink, context).await().candidates
        return candidates.firstNotNullOfOrNull { candidate ->
            candidate.text.trim().takeIf { it.matches(regex = NUMBER_REGEX) }
        } ?: candidates.firstOrNull()?.text?.filter(Char::isDigit).orEmpty()
    }

    fun close() {
        recognizer.close()
    }

    private suspend fun warmUp() {
        val stroke = Ink.Stroke.builder().apply {
            addPoint(Ink.Point.create(0f, 0f, 0L))
            addPoint(Ink.Point.create(1f, 1f, 16L))
        }.build()
        recognizer.recognize(Ink.builder().addStroke(stroke).build()).await()
    }

    private companion object {
        val NUMBER_REGEX = Regex(pattern = "-?\\d+([.,]\\d+)?")
    }
}
