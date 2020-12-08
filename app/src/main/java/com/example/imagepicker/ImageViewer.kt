package com.example.imagepicker

abstract class ImageViewer {
    abstract var filePath: String
    abstract var fileName: String
    abstract var fileType: String
    abstract var fileSize: Int
    abstract var croppedFilePath: String
    abstract var isCropped: Boolean

    fun resetFilePath() {
        if (croppedFilePath.isNotEmpty())
            filePath = croppedFilePath
    }
}