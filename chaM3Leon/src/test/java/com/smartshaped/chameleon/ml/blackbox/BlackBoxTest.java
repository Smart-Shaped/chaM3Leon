package com.smartshaped.chameleon.ml.blackbox;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.ml.blackbox.exception.BlackBoxException;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlackBoxTest {

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
    assertDoesNotThrow(BlackBoxExample::new);
  }

  @Test
  void testStartInputMismatch() throws ConfigurationException {
    BlackBoxExample blackBox = new BlackBoxExample();
    datasets.add(dataset);
    assertThrows(BlackBoxException.class, () -> blackBox.start(datasets));
  }

  @Test
  void testStart() throws ConfigurationException {
    BlackBoxExample blackBox = new BlackBoxExample();
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

      BlackBoxExample blackBox = new BlackBoxExample();
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

      BlackBoxExample blackBox = new BlackBoxExample();
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

      BlackBoxExample blackBox = new BlackBoxExample();
      assertThrows(BlackBoxException.class, () -> blackBox.deleteHdfsFolder("testPath"));
    }
  }

  @Test
  void testRunCommandSuccess() throws ConfigurationException, IOException {

    try (MockedConstruction<InputStreamReader> ignored = mockConstruction(InputStreamReader.class);
        MockedConstruction<BufferedReader> ignored1 = mockConstruction(BufferedReader.class)) {

      when(processBuilder.start()).thenReturn(process);

      BlackBoxExample blackBox = new BlackBoxExample();
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

      BlackBoxExample blackBox = new BlackBoxExample();
      assertThrows(BlackBoxException.class, (() -> blackBox.runCommand(processBuilder)));
    }
  }

  @Test
  void testRunCommandInterruptedException()
      throws ConfigurationException, IOException, InterruptedException {

    try (MockedConstruction<InputStreamReader> ignored = mockConstruction(InputStreamReader.class);
        MockedConstruction<BufferedReader> ignored1 = mockConstruction(BufferedReader.class)) {

      when(processBuilder.start()).thenReturn(process);
      when(process.waitFor()).thenThrow(InterruptedException.class);

      BlackBoxExample blackBox = new BlackBoxExample();
      assertThrows(BlackBoxException.class, (() -> blackBox.runCommand(processBuilder)));
    }
  }
}
