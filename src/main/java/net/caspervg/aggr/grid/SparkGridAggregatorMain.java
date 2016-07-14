package net.caspervg.aggr.grid;

import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.core.bean.aggregation.GridAggregation;
import net.caspervg.aggr.core.bean.aggregation.TimeAggregation;
import net.caspervg.aggr.core.read.CsvAggrReader;
import net.caspervg.aggr.core.util.AggrContext;
import net.caspervg.aggr.core.write.AggrWriter;
import net.caspervg.aggr.core.write.Rdf4jAggrWriter;
import net.caspervg.aggr.time.SparkTimeAggregator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

import java.util.HashMap;
import java.util.Map;

public class SparkGridAggregatorMain {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("KMeansSpark").setMaster("local[4]");
        JavaSparkContext sparkCtx = new JavaSparkContext(conf);
        Map<String, String> params = new HashMap<>();
        params.put("input_path", SparkGridAggregatorMain.class.getResource("/measurements.csv").getPath());
        AggrContext ctx = new AggrContext(
                params,
                sparkCtx
        );

        Dataset dataset = new Dataset("test_dataset1");
        Iterable<Measurement> meas = new CsvAggrReader().read(ctx);
        Iterable<AggregationResult<GridAggregation, Measurement>> results = new SparkGridAggregator().aggregate(dataset, meas, ctx);

        AggrWriter writer = new Rdf4jAggrWriter(new SPARQLRepository("http://localhost:8890/sparql"));
        for (AggregationResult<GridAggregation, Measurement> res : results) {
            GridAggregation gag = res.getAggregation();
            Iterable<Measurement> resmeas = res.getResults();

            writer.writeAggregation(gag, ctx);
            resmeas.forEach((measurement) -> writer.writeMeasurement(measurement, ctx));
        }

        writer.writeDataset(dataset, ctx);
    }
}
