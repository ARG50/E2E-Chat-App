package com.chat.e2e;

import java.util.HashMap;

public class IconMapper {
    private static HashMap<String, String> documentTypeToIconFileMappings = new HashMap<>();

    static {
        documentTypeToIconFileMappings.put("aac", "aac-file.png");
        documentTypeToIconFileMappings.put("avi", "avi-file.png");
        documentTypeToIconFileMappings.put("bmp", "bmp-file.png");
        documentTypeToIconFileMappings.put("doc", "doc-file.png");
        documentTypeToIconFileMappings.put("docx", "docx-file.png");
        documentTypeToIconFileMappings.put("exe", "exe-file.png");
        documentTypeToIconFileMappings.put("flac", "flac-file.png");
        documentTypeToIconFileMappings.put("gif", "gif-file.png");
        documentTypeToIconFileMappings.put("html", "html-file.png");
        documentTypeToIconFileMappings.put("jpeg", "jpeg-file.png");
        documentTypeToIconFileMappings.put("jpg", "jpg-file.png");
        documentTypeToIconFileMappings.put("m4a", "m4a-file.png");
        documentTypeToIconFileMappings.put("mkv", "mkv-file.png");
        documentTypeToIconFileMappings.put("mov", "mov-file.png");
        documentTypeToIconFileMappings.put("mp3", "mp3-file.png");
        documentTypeToIconFileMappings.put("mp4", "mp4-file.png");
        documentTypeToIconFileMappings.put("pdf", "pdf-file.png");
        documentTypeToIconFileMappings.put("png", "png-file.png");
        documentTypeToIconFileMappings.put("psd", "psd-file.png");
        documentTypeToIconFileMappings.put("tiff", "tiff-file.png");
        documentTypeToIconFileMappings.put("txt", "txt-file.png");
        documentTypeToIconFileMappings.put("wav", "wav-file.png");
        documentTypeToIconFileMappings.put("webm", "webm-file.png");
        documentTypeToIconFileMappings.put("xls", "xls-file.png");
        documentTypeToIconFileMappings.put("xlsx", "xlsx-file.png");
        documentTypeToIconFileMappings.put("xml", "xml-file.png");
        documentTypeToIconFileMappings.put("c", "c-file.png");
        documentTypeToIconFileMappings.put("java", "java-file.png");
        documentTypeToIconFileMappings.put("css", "css-file.png");
        documentTypeToIconFileMappings.put("js", "js-file.png");
        documentTypeToIconFileMappings.put("cpp", "cpp-file.png");
        documentTypeToIconFileMappings.put("py", "py-file.png");
        documentTypeToIconFileMappings.put("cs", "cs-file.png");
        documentTypeToIconFileMappings.put("asm", "asm-file.png");
        documentTypeToIconFileMappings.put("json", "json-file.png");
        documentTypeToIconFileMappings.put("bat", "bat-file.png");
        documentTypeToIconFileMappings.put("bin", "bin-file.png");
        documentTypeToIconFileMappings.put("csv", "csv-file.png");
        documentTypeToIconFileMappings.put("db", "db-file.png");
        documentTypeToIconFileMappings.put("pptx", "pptx-file.png");
        documentTypeToIconFileMappings.put("ppt", "ppt-file.png");
        documentTypeToIconFileMappings.put("dll", "dll-file.png");
        documentTypeToIconFileMappings.put("7z", "7z-file.png");
        documentTypeToIconFileMappings.put("rar", "rar-file.png");
        documentTypeToIconFileMappings.put("apk", "apk-file.png");
        documentTypeToIconFileMappings.put("msi", "msi-file.png");
        documentTypeToIconFileMappings.put("zip", "zip-file.png");
        documentTypeToIconFileMappings.put("sys", "sys-file.png");
        documentTypeToIconFileMappings.put("ini", "ini-file.png");
        documentTypeToIconFileMappings.put("ico", "ico-file.png");
        documentTypeToIconFileMappings.put("mpeg", "mpeg-file.png");
        documentTypeToIconFileMappings.put("mpg", "mpg-file.png");
        documentTypeToIconFileMappings.put("midi", "midi-file.png");
        documentTypeToIconFileMappings.put("php", "php-file.png");
        documentTypeToIconFileMappings.put("vb", "vb-file.png");
        documentTypeToIconFileMappings.put("com", "com-file.png");
        documentTypeToIconFileMappings.put("asp", "asp-file.png");
        documentTypeToIconFileMappings.put("aspx", "aspx-file.png");
        documentTypeToIconFileMappings.put("swift", "swift-file.png");
        documentTypeToIconFileMappings.put("rb", "rb-file.png");
        documentTypeToIconFileMappings.put("r", "r-file.png");
        documentTypeToIconFileMappings.put("ts", "ts-file.png");
        documentTypeToIconFileMappings.put("sh", "sh-file.png");
        documentTypeToIconFileMappings.put("sql", "sql-file.png");
        documentTypeToIconFileMappings.put("tar", "tar-file.png");
        documentTypeToIconFileMappings.put("deb", "deb-file.png");
        documentTypeToIconFileMappings.put("iso", "iso-file.png");
        documentTypeToIconFileMappings.put("gz", "gz-file.png");
        documentTypeToIconFileMappings.put("jar", "jar-file.png");
    }

    public static String getIconFileNameForParticularDocumentType(String documentType)
    {
        documentType = documentType.toLowerCase();
        return documentTypeToIconFileMappings.getOrDefault(documentType, "unknown-file.png");
    }
}
