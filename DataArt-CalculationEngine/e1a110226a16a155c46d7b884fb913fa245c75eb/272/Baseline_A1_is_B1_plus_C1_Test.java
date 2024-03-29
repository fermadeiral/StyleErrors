package com.dataart.spreadsheetanalytics.baseline;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import com.dataart.spreadsheetanalytics.BenchmarkTestParent;
import com.dataart.spreadsheetanalytics.api.engine.IEvaluator;
import com.dataart.spreadsheetanalytics.api.model.ICellAddress;
import com.dataart.spreadsheetanalytics.api.model.ICellValue;
import com.dataart.spreadsheetanalytics.api.model.IDataModel;
import com.dataart.spreadsheetanalytics.api.model.IEvaluationResult;
import com.dataart.spreadsheetanalytics.engine.Converters;
import com.dataart.spreadsheetanalytics.engine.SpreadsheetEvaluator;
import com.dataart.spreadsheetanalytics.model.A1Address;

public class Baseline_A1_is_B1_plus_C1_Test extends BenchmarkTestParent {

    @Benchmark
    public void evaluate_ExcelDataModel_ExecutionTimeIsOk(BenchmarkStateEvaluator state, Blackhole bh) {
        IEvaluationResult<ICellValue> value = state.evaluator.evaluate(state.address);
        assertThat(value.getResult().get()).isEqualTo(state.expectedValue); /* comment for better performance */
        bh.consume(value);
    }
   
    @State(Scope.Benchmark)
    public static class BenchmarkStateEvaluator {
        String excelFile = "src/test/resources/datamodel/baseline/A1_is_B1_plus_C1.xlsx";
        Object expectedValue = new Double(5.0);
        String column = "A";
        int iterations = 1;

        IDataModel dataModel;
        IEvaluator evaluator;
        ICellAddress address = A1Address.fromA1Address(column + iterations);

        @Setup(Level.Trial)
        public void initialize() throws Exception {
            this.dataModel = Converters.toDataModel(new XSSFWorkbook(excelFile));
            this.evaluator = new SpreadsheetEvaluator(dataModel);
        }
    }

}
