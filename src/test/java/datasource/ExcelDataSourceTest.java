package datasource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.savior.rateprojection.datasource.DataSource;
import ru.savior.rateprojection.core.entity.ProjectionDataResponse;
import ru.savior.rateprojection.datasource.excel.ExcelCurrencyTexts;
import ru.savior.rateprojection.datasource.excel.ExcelDataSource;

import java.io.File;
import java.net.URL;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ExcelDataSourceTest {

    @Test
    public void excelLoadFileTest() {
        DataSource excelDataSource = new ExcelDataSource(new ExcelCurrencyTexts());
        Map<String,String> settings = new HashMap<>();
        File file = new File("src/main/resources");
        settings.put("excelFilesFolder", file.getAbsolutePath());
        excelDataSource.configure(settings);
        ProjectionDataResponse sourceResponse = excelDataSource.provideData();
        assertEquals(5204 + 5402 + 5402, sourceResponse.getProvidedData().size());
        assertTrue(sourceResponse.isSuccessful());

    }
    @Test
    public void excelLoadFileTestDirectoryNotFound() {
        DataSource excelDataSource = new ExcelDataSource(new ExcelCurrencyTexts());
        Map<String,String> settings = new HashMap<>();
        File file = new File("src/main/resources2");
        settings.put("excelFilesFolder", file.getAbsolutePath());
        assertThrows( RuntimeException.class, () -> excelDataSource.configure(settings));
    }
}
