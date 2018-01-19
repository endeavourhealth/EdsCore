package org.endeavourhealth.core.csv;

import org.apache.commons.csv.CSVParser;

import java.util.Map;

public class CsvHelper {

    public static void validateCsvHeaders(CSVParser parser, String filePath, String[] expectedHeaders) throws IllegalArgumentException {

        String[] headers = getHeaderMapAsArray(parser);

        if (headers.length != expectedHeaders.length) {
            throw new IllegalArgumentException("Mismatch in number of CSV columns in " + filePath + " expected " + expectedHeaders.length + " but found " + headers.length);
        }

        for (int i = 0; i < expectedHeaders.length; i++) {
            String expectedHeader = expectedHeaders[i];
            String actualHeader = headers[i];

            if (!expectedHeader.equals(actualHeader)) {
                throw new IllegalArgumentException("Column mismatch at column " + i + ": expected [" + expectedHeader + "] but found [" + actualHeader + "]");
            }
        }
    }

    public static String[] getHeaderMapAsArray(CSVParser parser) {
        Map<String, Integer> headerMap = parser.getHeaderMap();

        String[] ret = new String[headerMap.size()];

        for (String col: headerMap.keySet()) {
            Integer colIndex = headerMap.get(col);
            ret[colIndex.intValue()] = col;
        }

        return ret;
    }
}
