package com.smartshaped.chameleon.ml.blackbox;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.ml.blackbox.exception.BlackboxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BlackboxTest {

  @Mock Dataset<Row> dataset;
  @Mock FileSystem fileSystem;
  @Mock ProcessBuilder processBuilder;
  @Mock Process process;
  List<Dataset<Row>> datasets = new ArrayList<>();

  @BeforeEach
  void setUp() {
    datasets.add(dataset);
  }

  @Test
  void testConstructor() {
    assertDoesNotThrow(BlackboxExample::new);
  }

  @Test
  void testStartInputMismatch() throws ConfigurationException {
    BlackboxExample blackBox = new BlackboxExample();
    datasets.add(dataset);
    assertThrows(BlackboxException.class, () -> blackBox.start(datasets));
  }

  @Test
  void testStart() throws ConfigurationException {
    BlackboxExample blackBox = new BlackboxExample();
    assertDoesNotThrow(() -> blackBox.start(datasets));
  }

  @Test
  void testDeleteHdfsFolderDeleteFailed() throws ConfigurationException, IOException {

    try (MockedConstruction<Configuration> ignored = mockConstruction(Configuration.class);
        MockedStatic<FileSystem> mockedStaticFileSystem = mockStatic(FileSystem.class);
        MockedConstruction<Path> ignored1 = mockConstruction(Path.class)) {

      mockedStaticFileSystem
          .when(() -> FileSystem.get(any(Configuration.class)))
          .thenReturn(fileSystem);

      when(fileSystem.exists(any(Path.class))).thenReturn(true);

      BlackboxExample blackBox = new BlackboxExample();
      assertDoesNotThrow(() -> blackBox.deleteHdfsFolder("testPath"));
    }
  }

  @Test
  void testDeleteHdfsFolderSuccess() throws ConfigurationException, IOException {

    try (MockedConstruction<Configuration> ignored = mockConstruction(Configuration.class);
        MockedStatic<FileSystem> mockedStaticFileSystem = mockStatic(FileSystem.class);
        MockedConstruction<Path> ignored1 = mockConstruction(Path.class)) {

      mockedStaticFileSystem
          .when(() -> FileSystem.get(any(Configuration.class)))
          .thenReturn(fileSystem);

      when(fileSystem.exists(any(Path.class))).thenReturn(true);
      when(fileSystem.delete(any(Path.class), anyBoolean())).thenReturn(true);

      BlackboxExample blackBox = new BlackboxExample();
      assertDoesNotThrow(() -> blackBox.deleteHdfsFolder("testPath"));
    }
  }

  @Test
  void testDeleteHdfsFolderException() throws ConfigurationException, IOException {

    try (MockedConstruction<Configuration> ignored = mockConstruction(Configuration.class);
        MockedStatic<FileSystem> mockedStaticFileSystem = mockStatic(FileSystem.class);
        MockedConstruction<Path> ignored1 = mockConstruction(Path.class)) {

      mockedStaticFileSystem
          .when(() -> FileSystem.get(any(Configuration.class)))
          .thenReturn(fileSystem);

      when(fileSystem.exists(any(Path.class))).thenReturn(true);
      when(fileSystem.delete(any(Path.class), anyBoolean())).thenThrow(IOException.class);

      BlackboxExample blackBox = new BlackboxExample();
      assertThrows(BlackboxException.class, () -> blackBox.deleteHdfsFolder("testPath"));
    }
  }

  @Test
  void testRunCommandSuccess() throws ConfigurationException, IOException {

    try (MockedConstruction<InputStreamReader> ignored = mockConstruction(InputStreamReader.class);
        MockedConstruction<BufferedReader> ignored1 = mockConstruction(BufferedReader.class)) {

      when(processBuilder.start()).thenReturn(process);

      BlackboxExample blackBox = new BlackboxExample();
      assertDoesNotThrow(() -> blackBox.runCommand(processBuilder));
    }
  }

  @Test
  void testRunCommandFailureExitCode()
      throws ConfigurationException, IOException, InterruptedException {

    try (MockedConstruction<InputStreamReader> ignored = mockConstruction(InputStreamReader.class);
        MockedConstruction<BufferedReader> ignored1 = mockConstruction(BufferedReader.class)) {

      when(processBuilder.start()).thenReturn(process);
      when(process.waitFor()).thenReturn(1);

      BlackboxExample blackBox = new BlackboxExample();
      assertThrows(BlackboxException.class, (() -> blackBox.runCommand(processBuilder)));
    }
  }

  @Test
  void testRunCommandInterruptedException()
      throws ConfigurationException, IOException, InterruptedException {

    try (MockedConstruction<InputStreamReader> ignored = mockConstruction(InputStreamReader.class);
        MockedConstruction<BufferedReader> ignored1 = mockConstruction(BufferedReader.class)) {

      when(processBuilder.start()).thenReturn(process);
      when(process.waitFor()).thenThrow(InterruptedException.class);

      BlackboxExample blackBox = new BlackboxExample();
      assertThrows(BlackboxException.class, (() -> blackBox.runCommand(processBuilder)));
    }
  }
}
