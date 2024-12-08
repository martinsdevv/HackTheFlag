package com.bytecrash.terminal;

import com.bytecrash.filesystem.FileSystem;

public interface FileSystemAwareCommand extends Command {
    void setFileSystem(FileSystem fileSystem);
}
