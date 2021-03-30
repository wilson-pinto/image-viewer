package com.wilsonpinto.imageviewerdemo

data class DemoMedia(
    var testField: String,
    override var filePath: String = "",
    override var fileName: String,
    override var fileType: String = "",
    override var fileSize: Int = 0,
    override var croppedFilePath: String = "",
    override var isCropped: Boolean = true,
    override var isLocal: Boolean = true

) : ImageViewer()