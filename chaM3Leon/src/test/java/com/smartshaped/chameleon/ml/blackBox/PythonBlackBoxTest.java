package com.smartshaped.chameleon.ml.blackBox;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.ml.blackBox.exception.BlackBoxException;
import com.smartshaped.chameleon.ml.utils.MLConfigurationUtils;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import scala.Option;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PythonBlackBoxTest {

    ArrayList<Dataset<Row>> datasets = new ArrayList<>();
    @Mock
    Dataset<Row> dataset;
    @Mock
    Option<SparkSession> option;
    @Mock
    SparkSession sparkSession;
    @Mock
    MLConfigurationUtils mlConfigurationUtils;

    @BeforeEach
    void setUp() {
        datasets.add(dataset);
    }

    private void resetSingleton() throws NoSuchFieldException, IllegalAccessException {

        Field instance = MLConfigurationUtils.class.getDeclaredField("configuration");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void testConstructor() {
        assertDoesNotThrow(PythonBlackBoxExample::new);
    }

    @Test
    void testExtraPreparation() throws ConfigurationException, NoSuchFieldException, IllegalAccessException {

        resetSingleton();
        PythonBlackBoxExample blackBox = new PythonBlackBoxExample();

        try (MockedConstruction<JavaSparkContext> mockedConstruction = mockConstruction(JavaSparkContext.class);
             MockedStatic<SparkSession> mockedStatic = mockStatic(SparkSession.class)) {
            mockedStatic.when((MockedStatic.Verification) SparkSession.getActiveSession()).thenReturn(option);
            when(option.get()).thenReturn(sparkSession);
            assertDoesNotThrow(blackBox::extraPreparation);
        }
    }

    @Test
    void testValidateParams() {

        try (MockedStatic<MLConfigurationUtils> mockedStatic = mockStatic(MLConfigurationUtils.class)) {

            mockedStatic.when(MLConfigurationUtils::getMlConf).thenReturn(mlConfigurationUtils);

            when(mlConfigurationUtils.getBlackBoxPythonScriptPath()).thenReturn("");

            assertThrows(BlackBoxException.class, () -> new PythonBlackBoxExample().validateParams());
        }
    }

    @Test
    void testCleanBlackBoxFolderSuccess() throws ConfigurationException {

        PythonBlackBoxExample blackBox = new PythonBlackBoxExample();
        assertDoesNotThrow(blackBox::cleanBlackBoxFolder);
    }

    @Test
    void testCleanBlackBoxFolderFailure() throws ConfigurationException {

        PythonBlackBoxExample blackBox = new PythonBlackBoxExample();

        try (MockedStatic<Files> mockedStatic = mockStatic(Files.class)) {

            mockedStatic.when(() -> Files.deleteIfExists(any(Path.class))).thenThrow(IOException.class);
            assertThrows(BlackBoxException.class, (blackBox::cleanBlackBoxFolder));
        }
    }

    @Test
    void testRunML() throws ConfigurationException {
        PythonBlackBoxExample blackBox = new PythonBlackBoxExample();
        assertThrows(BlackBoxException.class, (blackBox::runML));
    }

    @Test
    void testReadOutput() throws ConfigurationException {
        PythonBlackBoxExample blackBox = new PythonBlackBoxExample();

        try (MockedStatic<SparkSession> mockedStaticSpark = mockStatic(SparkSession.class)) {
            mockedStaticSpark.when(SparkSession::getActiveSession).thenReturn(option);
            when(option.get()).thenReturn(sparkSession);

            assertDoesNotThrow(() -> blackBox.readOutput("test"));
        }
    }

    @Test
    void testMakeDatasetAccessible() throws ConfigurationException {
        PythonBlackBoxExample blackBox = new PythonBlackBoxExample();
        assertDoesNotThrow(() -> blackBox.makeDatasetAccessible(dataset, "test"));
    }
}
